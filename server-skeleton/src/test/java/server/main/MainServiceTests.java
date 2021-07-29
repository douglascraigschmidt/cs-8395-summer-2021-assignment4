package server.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;
import server.common.Components;
import server.common.model.TransformedImage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static server.main.ImageFactory.randomImageBytes;
import static server.main.ImageFactory.randomTransformedImages;

/**
 * These use mocking to isolate and test only the service component.
 */
@AutoConfigureMockMvc
@SpringBootTest
@ContextConfiguration(classes = {Components.class, MainApplication.class, MainService.class})
public class MainServiceTests {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DiscoveryClient discoveryClientMock;

    private MockWebServer mockBackEnd;
    private MainService service;

    @BeforeEach
    void beforeEach() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        service = new MainService();
        service.baseUrl = String.format("http://localhost:%d/", mockBackEnd.getPort());
        service.webClient = WebClient.builder().build();
        service.discoveryClient = discoveryClientMock;
    }

    @AfterEach
    void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    public void testApplyTransforms() throws Exception {
        byte[] imageBytes = randomImageBytes();
        List<TransformedImage> expected = randomTransformedImages(3);
        String fileName = "foobar.png";

        List<String> transforms =
                expected.stream()
                        .map(TransformedImage::getTransformName)
                        .collect(Collectors.toUnmodifiableList());

        when(discoveryClientMock.getServices()).thenReturn(
                transforms
                        .stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.toUnmodifiableList())
        );

        for (TransformedImage transformedImage : expected) {
            mockBackEnd.enqueue(new MockResponse()
                    .setBody(objectMapper.writeValueAsString(transformedImage))
                    .addHeader("Content-Type", "application/json"));
        }

        // The service uses a parallel scheduler to forward requests to
        // microservices in an arbitrary order and therefore the resulting
        // Flux of Flight objects will also have an arbitrary order. The
        // StepVerifier only provides a consumeNextSequence which does not
        // support unordered comparisons. Therefore, it's necessary to check
        // each Flight individually by using a loop too add the correct
        // number of consumeWithNext() calls.
        StepVerifier.FirstStep<TransformedImage> stepVerifier =
                StepVerifier.create(
                        service.applyTransforms(
                                transforms,
                                fileName,
                                imageBytes));

        for (int i = 0; i < expected.size(); i++) {
            stepVerifier.consumeNextWith(transformedImage -> {
                assertThat(transformedImage).isIn(expected);
                assertThat(expected.remove(transformedImage)).isTrue();
            });
        }

        stepVerifier.verifyComplete();


        // Ensure that all expected flights were received.
        assertThat(expected).isEmpty();
    }
}
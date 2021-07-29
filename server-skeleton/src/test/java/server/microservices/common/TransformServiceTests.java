package server.microservices.common;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.test.context.ContextConfiguration;
import server.common.Components;
import server.common.model.TransformedImage;
import server.main.MainApplication;
import server.main.MainService;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;
import static server.common.Constants.Service.*;
import static server.main.ImageFactory.randomImageBytes;
import static server.main.ImageFactory.randomTransformedImages;

/**
 * These use mocking to isolate and test only the service component.
 */
@EnableDiscoveryClient(autoRegister = false)
@AutoConfigureMockMvc
@SpringBootTest
@ContextConfiguration(classes = {
        Components.class,
        MainApplication.class,
        MainService.class})
public class TransformServiceTests {
    @MockBean
    Transforms transforms;

    @Test
    public void testApplyTransform() {
        byte[] imageBytes = randomImageBytes();
        List<TransformedImage> expected = randomTransformedImages(3);
        expected.get(0).setTransformName(GRAYSCALE_TRANSFORM);
        expected.get(1).setTransformName(SEPIA_TRANSFORM);
        expected.get(2).setTransformName(TINT_TRANSFORM);

        TransformService service = new TransformService();
        service.transforms = transforms;

        doNothing().when(transforms).grayScale(any(int[].class), anyBoolean());
        doNothing().when(transforms).sepia(any(int[].class), anyBoolean());
        doNothing().when(transforms).tint(
                any(int[].class), anyBoolean(), anyFloat(), anyFloat(), anyFloat());

        for (TransformedImage transformedImage : expected) {
            TransformedImage result = service.applyTransform(
                    transformedImage.getImageName(),
                    transformedImage.getTransformName(),
                    imageBytes);

            TransformedImage e =
                    new TransformedImage(
                            transformedImage.getImageName(),
                            transformedImage.getTransformName(),
                            imageBytes);

            assertThat(result).isEqualTo(e);
        }

        verify(transforms, times(1)).grayScale(any(int[].class), anyBoolean());
        verify(transforms, times(1)).sepia(any(int[].class), anyBoolean());
        verify(transforms, times(1)).tint(
                any(int[].class), anyBoolean(), anyFloat(), anyFloat(), anyFloat());
    }
}
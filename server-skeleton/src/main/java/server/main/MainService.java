package server.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import server.common.model.TransformedImage;

import java.util.List;
import java.util.Objects;

/**
 * This class defines implementation methods that are called by the
 * {@link MainController}, which serves as the main "front-end"
 * app gateway entry point for remote clients that want an image
 * to be processed by a set of image transforms.
 * <p>
 * A {@link DiscoveryClient} is used to redirect calls to the
 * appropriate microservices, which can run in processes that are
 * deployed to other computers in a cluster.
 * <p>
 * This class is annotated as a Spring {@code @Service}, which enables
 * the auto-detection and wiring of dependent implementation classes
 * via classpath scanning.
 */
@Service
public class MainService {
    /**
     * This auto-wired field connects the {@link MainService} to
     * a {@link WebClient} used to redirect all HTTP requests to
     * the appropriate microservices.
     */
    @Autowired
    WebClient webClient;

    /**
     * This auto-wired field connects the {@link MainService} to
     * the {@link DiscoveryClient} used to find all registered
     * microservices.
     */
    @Autowired
    DiscoveryClient discoveryClient;

    /**
     * Tests can set this value for mocking a back-end server.
     */
    String baseUrl = "http://";

    /**
     * Apply the given {@link List} of {@code transforms} to the given
     * image and return a {@link Flux} that emits the {@link
     * TransformedImage} objects.
     *
     * @param transforms A list of transforms to apply.
     * @param fileName   Image file name.
     * @param imageBytes Image content bytes.
     * @return A list of transformed images.
     */

    public Flux<TransformedImage> applyTransforms(List<String> transforms,
                                                  String fileName,
                                                  byte[] imageBytes) {
        // Create a multi-value map containing the image filename and bytes.
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("filename", fileName);

        // Byte arrays in multi value maps require overriding
        // getFileName() in a ByteArrayResource and adding that
        // resource into the value map.
        ByteArrayResource byteArrayResource =
            new ByteArrayResource(imageBytes) {
                @Override
                public String getFilename() {
                    return fileName;
                }
            };
        map.add("image", byteArrayResource);

        // Get the list of all transform microservices.
        // TODO -- you fill in here, replacing return null
        // with the proper code.

        // Return a Flux stream of transformed images.
        return null;
            // Create a Flux from the list of transforms.

            // Only call microservices that match passed transforms.

            // POST the request to the service via the WebClient and
            // extract the body from the returned ResponseEntity.
    }

    /**
     * Uses the Eureka discovery client to finds all matching image
     * transforming microservices.
     *
     * @return A {@link List} of all registered image transforming
     * microservices.
     */
    private List<String> getTransformMicroServices() {
        // TODO -- you fill in here, replacing return null
        // with the proper code.
        return null;
    }
}

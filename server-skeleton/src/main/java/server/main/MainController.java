package server.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import server.common.model.TransformedImage;

import java.io.IOException;
import java.util.List;

import static server.common.Constants.EndPoint.APPLY_TRANSFORMS;

/**
 * This Spring controller is the main entry point for remote clients.
 * It demonstrates how Spring can be used to handle an HTTP GET
 * request synchronously via functional programming.  This request
 * apply all the given {@code transforms} and return a {@link Mono}
 * that emits a list of {@link TransformedImage} objects.
 * <p>
 * In Spring's approach to building RESTful web services, HTTP
 * requests are handled by a controller (identified by the
 * {@code @RestController} annotation) that defines the endpoints (aka
 * routes) for each supported operation, i.e., {@code @GetMapping},
 * {@code @PostMapping}, {@code @PutMapping}, and
 * {@code @DeleteMapping}, which correspond to the HTTP GET, POST,
 * PUT, and DELETE calls, respectively.
 * <p>
 * Spring uses the {@code @GetMapping} annotation to map HTTP GET
 * requests onto methods in the {@link MainController}.  GET requests
 * invoked from any HTTP web client (e.g., a web browser or Android
 * app) or command-line utility (e.g., Curl or Postman).
 */
@RestController
@ResponseBody
public class MainController {
    /**
     * This auto-wired field connects the {@link MainController} to
     * the {@link MainService}.
     */
    @Autowired
    MainService imageService;

    /**
     * Apply the given {@link List} of {@code transforms} to the given
     * image and return a {@link Flux} that emits the {@link
     * TransformedImage} objects.
     *
     * @param transforms A {@link List} of transforms to apply
     * @param image      The contents of the image as a {@link MultipartFile}
     * @return A {@link Mono} that emits a list of {@link TransformedImage}
     * objects
     */
    // TODO -- you fill in here by adding the appropriate annotations
    // to create a Spring WebFlux endpoint method.
    public Mono<List<TransformedImage>> applyTransforms
        (List<String> transforms,
         MultipartFile image) throws IOException {
        // Apply all transforms and return a Mono list.
        return imageService
            // Call service get a Flux stream of transformed images.
            .applyTransforms(transforms,
                             image.getOriginalFilename(),
                             image.getBytes())

            // Downgrade Flux<T> to a Mono<List<T>> for REST
            // compatibility.
            .collectList();
    }
}

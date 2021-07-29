package server.microservices.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import server.common.ImageUtils;
import server.common.model.TransformedImage;

import java.awt.image.BufferedImage;

import static server.common.Constants.Service.*;

/**
 * This class defines the {@link TransformService#applyTransform}
 * method called by the {@link TransformController} to perform an
 * image transform operation.
 * <p>
 * This class is annotated as a Spring {@code @Service}, which enables
 * the auto-detection and wiring of dependent implementation classes
 * via classpath scanning.
 */
@Service
public class TransformService {
    /**
     * This auto-wired field connects the {@link TransformService} to
     * the {@link Transforms}.
     */
    @Autowired
    Transforms transforms;

    /**
     * Applies the named transform to the passed byte array image and
     * returns the result as a {@link Mono<TransformedImage>}.
     *
     * @param transform  TransformUtils name to apply.
     * @param fileName   Original image file name.
     * @param imageBytes Image bytes to transform.
     * @return A Mono containing the transformed image.
     */
    public TransformedImage applyTransform
        (String fileName,
         String transform,
         byte[] imageBytes) {
        // Convert the image bytes into a BufferedImage.
        BufferedImage bufferedImage =
            ImageUtils.toBufferedImage(imageBytes);

        // Get BufferedImage pixels.
        int[] pixels = bufferedImage
            .getRGB(0, 0,
                    bufferedImage.getWidth(),
                    bufferedImage.getHeight(),
                    null, 0,
                    bufferedImage.getWidth());

        // Perform the appropriate transformation on the pixels array.
        switch (transform) {
        case GRAYSCALE_TRANSFORM:
            transforms.grayScale(pixels,
                                 bufferedImage.getColorModel().hasAlpha());
            break;
        case SEPIA_TRANSFORM:
            transforms.sepia(pixels,
                             bufferedImage.getColorModel().hasAlpha());
            break;
        case TINT_TRANSFORM:
            transforms.tint(pixels,
                            bufferedImage.getColorModel().hasAlpha(),
                            0.0f, 0.0f, 0.9f);
            break;
        default:
            throw new IllegalStateException("Unsupported transform type: "
                                            + transform);
        }

        // Set the output BufferedImage pixels to the transformed
        // pixels array.
        bufferedImage
            .setRGB(0, 0,
                    bufferedImage.getWidth(),
                    bufferedImage.getHeight(),
                    pixels, 0,
                    bufferedImage.getWidth());

        // Return the transformed image within a DTO wrapper.

        // TODO -- you fill in here, replacing return null with
        // the appropriate code.
        return null;
    }
}

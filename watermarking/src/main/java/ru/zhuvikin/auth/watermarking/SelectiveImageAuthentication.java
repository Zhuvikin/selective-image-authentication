package ru.zhuvikin.auth.watermarking;

import java.awt.image.BufferedImage;

import static java.awt.image.BufferedImage.TYPE_BYTE_GRAY;

public final class SelectiveImageAuthentication {

    public static BufferedImage watermark(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        // todo: 1. Filter image with gaussian filter

        // todo: 2. Find central finite differences

        // todo: 3. Find original size feature matrix

        // todo: 4. Average downsampling to small size feature matrix

        // todo: 5. Convert to feature vector

        // todo: 6. Apply 3-bit quantization

        // todo: 7. Sign quantized features

        // todo: 8. Concatenate signature and 3-bit quantization perturbations

        // todo: 9. Encode with LDPC-code

        // todo: 10. Embed by means of Haar Wavelet Transform

        return new BufferedImage(width, height, TYPE_BYTE_GRAY);
    }

    public static boolean authenticate(BufferedImage image) {

        // todo: 1. Extract by means of Haar Wavelet Transform

        // todo: 2. Decode with LDPC-code

        // todo: 3. Separate signature and 3-bit quantization perturbations

        // todo: 4. Filter authenticated image with gaussian filter

        // todo: 5. Find central finite differences of authenticated image

        // todo: 6. Find original size feature matrix of authenticated image

        // todo: 7. Average downsampling to small size feature matrix of authenticated image

        // todo: 8. Convert to feature vector of authenticated image

        // todo: 9. Restore feature vector with 3-bit quantization

        // todo: 10. Verify signature

        return false;
    }

}

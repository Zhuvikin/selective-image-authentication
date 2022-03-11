package ru.zhuvikin.auth.watermarking;

import lombok.SneakyThrows;
import org.junit.Test;
import ru.zhuvikin.auth.security.RsaKeys;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertTrue;
import static ru.zhuvikin.auth.watermarking.TestUtility.saveJPEG;

public class SelectiveImageAuthenticationTest {

    private static final String PASSWORD = "password";
    private static final int SIGNATURE_LENGTH = 1024;
    private static final int SIGNATURE_LENGTH_2 = 512;

    private static final RsaKeys RSA_KEYS = new RsaKeys(PASSWORD, SIGNATURE_LENGTH);
    private static final RsaKeys RSA_KEYS_512 = new RsaKeys(PASSWORD, SIGNATURE_LENGTH_2);

    private static final String ORIGINAL_NAME = "lena.jpg";
    private static final String ORIGINAL_NAME_256 = "lena_256.jpg";
    private static final String WATERMARKED_NAME_1 = "lena_watermarked_1.jpg";
    private static final String WATERMARKED_NAME_2 = "lena_watermarked_2.jpg";
    private static final String WATERMARKED_NAME_3 = "lena_watermarked_3.jpg";
    private static final String WATERMARKED_NAME_256 = "lena_watermarked_256.jpg";

    private static final ClassLoader CLASS_LOADER = SelectiveImageAuthenticationTest.class.getClassLoader();

    private static final URL LENA_URL = CLASS_LOADER.getResource(ORIGINAL_NAME);
    private static final URL LENA_256_URL = CLASS_LOADER.getResource(ORIGINAL_NAME_256);
    private static final URL LENA_WM1_URL = CLASS_LOADER.getResource(WATERMARKED_NAME_1);
    private static final URL LENA_WM2_URL = CLASS_LOADER.getResource(WATERMARKED_NAME_2);
    private static final URL LENA_WM3_URL = CLASS_LOADER.getResource(WATERMARKED_NAME_3);
    private static final URL LENA_WM256_URL = CLASS_LOADER.getResource(WATERMARKED_NAME_256);

    private static final WatermarkingParameters WATERMARKING_PARAMETERS_1 = WatermarkingParameters.builder()
            .eccCodeRate(0.5)
            .gamma(1.55)
            .build();

    private static final WatermarkingParameters WATERMARKING_PARAMETERS_2 = WatermarkingParameters.builder()
            .eccCodeRate(0.25)
            .gamma(1.875)
            .build();

    private static final WatermarkingParameters WATERMARKING_PARAMETERS_3 = WatermarkingParameters.builder()
            .eccCodeRate(0.75)
            .gamma(1.25)
            .build();

    @Test
    @SneakyThrows
    public void testWatermark1() {
        BufferedImage image = ImageIO.read(LENA_URL);

        BufferedImage watermarked = SelectiveImageAuthentication
                .watermark(image, WATERMARKING_PARAMETERS_1, RSA_KEYS.getPrivateKey());

        File outputFile = new File(WATERMARKED_NAME_1);
        saveJPEG(watermarked, outputFile, 1f);
    }

    @Test
    @SneakyThrows
    public void testAuthenticate1() {
        BufferedImage image = ImageIO.read(LENA_WM1_URL);

        boolean authentic = SelectiveImageAuthentication.authenticate(image, WATERMARKING_PARAMETERS_1, RSA_KEYS.getPublicKey());

        assertTrue(authentic);
    }

    @Test
    @SneakyThrows
    public void testWatermark2() {
        BufferedImage image = ImageIO.read(LENA_URL);

        BufferedImage watermarked = SelectiveImageAuthentication
                .watermark(image, WATERMARKING_PARAMETERS_2, RSA_KEYS.getPrivateKey());

        File outputFile = new File(WATERMARKED_NAME_2);
        saveJPEG(watermarked, outputFile, 1f);
    }

    @Test
    @SneakyThrows
    public void testAuthenticate2() {
        BufferedImage image = ImageIO.read(LENA_WM2_URL);

        boolean authentic = SelectiveImageAuthentication.authenticate(image, WATERMARKING_PARAMETERS_2, RSA_KEYS.getPublicKey());

        assertTrue(authentic);
    }

    @Test
    @SneakyThrows
    public void testWatermark3() {
        BufferedImage image = ImageIO.read(LENA_URL);

        BufferedImage watermarked = SelectiveImageAuthentication
                .watermark(image, WATERMARKING_PARAMETERS_3, RSA_KEYS.getPrivateKey());

        File outputFile = new File(WATERMARKED_NAME_3);
        saveJPEG(watermarked, outputFile, 1f);
    }

    @Test
    @SneakyThrows
    public void testAuthenticate3() {
        BufferedImage image = ImageIO.read(LENA_WM3_URL);

        boolean authentic = SelectiveImageAuthentication.authenticate(image, WATERMARKING_PARAMETERS_3, RSA_KEYS.getPublicKey());

        assertTrue(authentic);
    }

    @Test
    @SneakyThrows
    public void testWatermark4() {
        BufferedImage image = ImageIO.read(LENA_256_URL);

        BufferedImage watermarked = SelectiveImageAuthentication
                .watermark(image, WATERMARKING_PARAMETERS_1, RSA_KEYS_512.getPrivateKey());

        File outputFile = new File(WATERMARKED_NAME_256);
        saveJPEG(watermarked, outputFile, 1f);
    }

    @Test
    @SneakyThrows
    public void testAuthenticate4() {
        BufferedImage image = ImageIO.read(LENA_WM256_URL);

        boolean authentic = SelectiveImageAuthentication.authenticate(image, WATERMARKING_PARAMETERS_1, RSA_KEYS_512.getPublicKey());

        assertTrue(authentic);
    }

}
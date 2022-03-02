package ru.zhuvikin.auth.watermarking;

import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;
import ru.zhuvikin.auth.security.RsaKeys;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

import static ru.zhuvikin.auth.watermarking.TestUtility.saveJPEG;

public class SelectiveImageAuthenticationTest {

    private static final String PASSWORD =  "password";
    private static final int SIGNATURE_LENGTH = 1024;

    private static final RsaKeys RSA_KEYS = new RsaKeys(PASSWORD, SIGNATURE_LENGTH);

    private static final String ORIGINAL_NAME = "lena.jpg";
    private static final String WATERMARKED_NAME = "lena_watermarked.jpg";

    private static final URL LENA_URL = FeaturesCalculatorTest.class.getClassLoader().getResource(ORIGINAL_NAME);
    private static final URL LENA_WM_URL = FeaturesCalculatorTest.class.getClassLoader().getResource(WATERMARKED_NAME);

    private static final WatermarkingParameters WATERMARKING_PARAMETERS = WatermarkingParameters.builder().build();

    @Test
    @SneakyThrows
    public void testWatermark() {
        BufferedImage image = ImageIO.read(LENA_URL);

        BufferedImage watermarked = SelectiveImageAuthentication
                .watermark(image, WATERMARKING_PARAMETERS, RSA_KEYS.getPrivateKey());

        File outputFile = new File(WATERMARKED_NAME);
        saveJPEG(watermarked, outputFile, 1f);
    }

    @Test
    @SneakyThrows
    public void testAuthenticate() {
        BufferedImage image = ImageIO.read(LENA_WM_URL);

        boolean authentic = SelectiveImageAuthentication.authenticate(image, WATERMARKING_PARAMETERS, RSA_KEYS.getPublicKey());

        Assert.assertTrue(authentic);
    }

}
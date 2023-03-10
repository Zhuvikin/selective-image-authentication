package ru.zhuvikin.auth.watermarking;

import org.junit.Test;
import ru.zhuvikin.auth.security.RsaKeys;

import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.zhuvikin.auth.watermarking.TestUtility.testImageWatermarkingAndAuthentication;

public class SelectiveImageAuthenticationTest {

    private static final ClassLoader CLASS_LOADER = SelectiveImageAuthenticationTest.class.getClassLoader();

    private static final RsaKeys RSA_KEYS_1024 = new RsaKeys("password", 1024);
    private static final RsaKeys RSA_KEYS_512 = new RsaKeys("password", 512);

    private static final URL LENA_512_URL = CLASS_LOADER.getResource("lena_512.jpg");
    private static final URL LENA_256_URL = CLASS_LOADER.getResource("lena_256.jpg");

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
    public void testWatermarkingAndAuthentication1() {
        AuthenticationResult result = testImageWatermarkingAndAuthentication(
                LENA_512_URL,
                WATERMARKING_PARAMETERS_1,
                RSA_KEYS_1024,
                "lena_watermarked_1.jpg");

        assertTrue(result.isAuthentic());
    }

    @Test
    public void testWatermarkingAndAuthentication2() {
        AuthenticationResult result = testImageWatermarkingAndAuthentication(
                LENA_512_URL,
                WATERMARKING_PARAMETERS_2,
                RSA_KEYS_1024,
                "lena_watermarked_2.jpg");

        assertTrue(result.isAuthentic());
    }

    @Test
    public void testWatermarkingAndAuthentication3() {
        AuthenticationResult result = testImageWatermarkingAndAuthentication(
                LENA_512_URL,
                WATERMARKING_PARAMETERS_3,
                RSA_KEYS_1024,
                "lena_watermarked_3.jpg");

        assertTrue(result.isAuthentic());
    }

    @Test
    public void testWatermarkingAndAuthentication4() {
        AuthenticationResult result = testImageWatermarkingAndAuthentication(
                LENA_256_URL,
                WATERMARKING_PARAMETERS_1,
                RSA_KEYS_512,
                "lena_watermarked_4.jpg");

        assertTrue(result.isAuthentic());
    }

    @Test
    public void testWatermarkingAndAuthenticationWithName() {
        String name = "?????????? ??????-???????? ????????????????";
        AuthenticationResult result = testImageWatermarkingAndAuthentication(
                LENA_512_URL,
                WATERMARKING_PARAMETERS_1,
                RSA_KEYS_1024,
                "lena_watermarked_with_name.jpg",
                name);

        assertTrue(result.isAuthentic());
        assertEquals(name.toUpperCase(), result.getName());
    }

}
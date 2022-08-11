package ru.zhuvikin.auth.watermarking;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FeaturesCalculatorTest {

    private static final int LENGTH = 1024;
    private static final double SIGMA = 10;

    private static final String ORIGINAL_NAME = "lena_512.jpg";
    private static final String WATERMARKED_NAME = "lena_watermarked.jpg";

    private static final ClassLoader CLASS_LOADER = FeaturesCalculatorTest.class.getClassLoader();

    private static final URL LENA_URL = CLASS_LOADER.getResource(ORIGINAL_NAME);
    private static final URL LENA_WM_URL = CLASS_LOADER.getResource(WATERMARKED_NAME);

    @Test
    public void testFeatures1() throws Exception {
        BufferedImage image = ImageIO.read(LENA_URL);

        List<Double> features1 = FeaturesCalculator.features(image, SIGMA, LENGTH);

        assertEquals(LENGTH, features1.size());

        BufferedImage watermarkedImage = ImageIO.read(LENA_WM_URL);

        List<Double> features2 = FeaturesCalculator.features(watermarkedImage, SIGMA, LENGTH);

        assertEquals(LENGTH, features2.size());

        for (int i = 0; i < features1.size(); i++) {
            assertEquals(features1.get(i), features2.get(i), 6);
        }
    }

}
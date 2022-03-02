package ru.zhuvikin.auth.watermarking;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FeaturesCalculatorTest {

    private static final int LENGTH = 1024;
    private static final double SIGMA = 2;

    @Test
    public void features() throws Exception {
        BufferedImage image = ImageIO.read(FeaturesCalculatorTest.class.getClassLoader().getResourceAsStream("lena.jpg"));

        List<Double> features = FeaturesCalculator.features(image, SIGMA, LENGTH);

        assertEquals(LENGTH, features.size());
    }

}
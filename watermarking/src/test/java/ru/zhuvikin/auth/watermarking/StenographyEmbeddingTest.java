package ru.zhuvikin.auth.watermarking;

import org.junit.Assert;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.BitSet;

import static org.junit.Assert.assertEquals;
import static ru.zhuvikin.auth.watermarking.TestUtility.saveJPEG;

public class StenographyEmbeddingTest {

    private static final double GAMMA = 2;

    private static final URL LENA_URL = FeaturesCalculatorTest.class.getClassLoader().getResource("lena_512.jpg");

    @Test
    public void testEmbedAndExtract() throws IOException {
        BufferedImage image = ImageIO.read(LENA_URL);

        BitSet bitSet = new BitSet();
        bitSet.set(1);
        bitSet.set(3);
        bitSet.set(5);
        bitSet.set(7);
        bitSet.set(9);
        bitSet.set(11);

        BufferedImage coverImage = StenographyEmbedding.embed(image, bitSet, 12, GAMMA);

        assertEquals(image.getWidth(), coverImage.getWidth());
        assertEquals(image.getHeight(), coverImage.getHeight());

        File outputFile = new File("lena_embedded.jpg");
        saveJPEG(coverImage, outputFile, 1f);

        BufferedImage readImage = ImageIO.read(outputFile);

        BitSet extractedBitSet = StenographyEmbedding.extract(readImage, 12, GAMMA);

        Assert.assertEquals(bitSet, extractedBitSet);
    }

}
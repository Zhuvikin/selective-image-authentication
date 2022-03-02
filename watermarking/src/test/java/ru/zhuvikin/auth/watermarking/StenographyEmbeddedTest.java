package ru.zhuvikin.auth.watermarking;

import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.BitSet;

import static org.junit.Assert.assertEquals;

public class StenographyEmbeddedTest {

    private static final double GAMMA = 2;

    private static final String LENA_JPG = "lena.jpg";
    private static final URL LENA_URL = FeaturesCalculatorTest.class.getClassLoader().getResource(LENA_JPG);
    private static final String LENA_WM_JPG = "lena_wm.jpg";
    private static final URL LENA_WM_URL = FeaturesCalculatorTest.class.getClassLoader().getResource(LENA_WM_JPG);

    @Test
    public void embed() throws IOException {
        BufferedImage image = ImageIO.read(LENA_URL);

        BitSet bitSet = new BitSet();
        bitSet.set(1);
        bitSet.set(3);
        bitSet.set(5);
        bitSet.set(7);
        bitSet.set(9);
        bitSet.set(11);

        BufferedImage coverImage = StenographyEmbedded.embed(image, bitSet, 12, GAMMA);

        assertEquals(image.getWidth(), coverImage.getWidth());
        assertEquals(image.getHeight(), coverImage.getHeight());

        File outputFile = new File(LENA_WM_JPG);
        saveJPEG(coverImage, outputFile, 1f);
    }

    @Test
    @SneakyThrows
    public void extract() {
        BufferedImage image = ImageIO.read(LENA_WM_URL);

        BitSet bitSet = StenographyEmbedded.extract(image, 12, GAMMA);

        BitSet expected = new BitSet();
        expected.set(1);
        expected.set(3);
        expected.set(5);
        expected.set(7);
        expected.set(9);
        expected.set(11);

        Assert.assertEquals(expected, bitSet);
    }

    @SneakyThrows
    private static void saveJPEG(BufferedImage image, File file, float quality) {
        JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
        jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        jpegParams.setCompressionQuality(quality);

        final ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();

        writer.setOutput(new FileImageOutputStream(file));
        writer.write(null, new IIOImage(image, null, null), jpegParams);
    }

}
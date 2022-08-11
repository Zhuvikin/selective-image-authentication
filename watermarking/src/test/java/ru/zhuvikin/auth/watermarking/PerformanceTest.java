package ru.zhuvikin.auth.watermarking;

import lombok.SneakyThrows;
import org.junit.Test;
import ru.zhuvikin.auth.security.RsaKeys;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static ru.zhuvikin.auth.watermarking.TestUtility.authenticate;
import static ru.zhuvikin.auth.watermarking.TestUtility.saveJPEG;
import static ru.zhuvikin.auth.watermarking.TestUtility.watermark;

public class PerformanceTest {

    private static final ClassLoader CLASS_LOADER = PerformanceTest.class.getClassLoader();

    private static final RsaKeys KEYS = new RsaKeys("password", 1024);

    private static final URL URL = CLASS_LOADER.getResource("lena_512.jpg");

    private static final WatermarkingParameters PARAMETERS = WatermarkingParameters.builder()
            .eccCodeRate(0.5)
            .gamma(1.55)
            .build();

    private static final int SPEED_ITERATIONS = 10;
    private static final int SIZE_ITERATIONS = 5;

    /**
     * On my MacBook 2015 Core i7 with 16Gb RAM test gives:
     * <p>
     * Mean watermarking time: 1.4794770767 sec.
     * Mean authentication time: 1.8536793724 sec.
     */
    @Test
    public void testWatermarkingAndAuthenticationSpeed() {
        List<File> files = new ArrayList<>();

        long startTime = System.nanoTime();
        IntStream.range(0, SPEED_ITERATIONS).forEachOrdered(i ->
                files.add(watermark(URL, PARAMETERS, KEYS, "performance_test_" + i + ".jpg", null)));
        double estimatedTime = ((double) (System.nanoTime() - startTime) / (double) SPEED_ITERATIONS) / 1000.0 / 1000.0 / 1000.0;
        System.out.println("Mean watermarking time: " + estimatedTime + " sec.");

        startTime = System.nanoTime();
        IntStream.range(0, SPEED_ITERATIONS).forEachOrdered(i -> authenticate(PARAMETERS, KEYS, files.get(i)));
        estimatedTime = ((double) (System.nanoTime() - startTime) / (double) SPEED_ITERATIONS) / 1000.0 / 1000.0 / 1000.0;
        System.out.println("Mean authentication time: " + estimatedTime + " sec.");

        files.forEach(File::delete);
    }

    /**
     * My result for this test is:
     * <p>
     * -----------------------------------------------------------------
     * | Image  | original size     | size after          | Ratio      |
     * | index  | after JPEG, Bytes | watermarking, Bytes |            |
     * |---------------------------------------------------------------|
     * |      0 |             44205 |               44217 | 1.0002714  |
     * |      1 |             33966 |               33983 | 1.0005005  |
     * |      2 |             34779 |               34800 | 1.000603   |
     * |      3 |             25036 |               25066 | 1.001198   |
     * |      4 |             23582 |               23595 | 1.0005512  |
     * -----------------------------------------------------------------
     */
    @Test
    @SneakyThrows
    public void testSizeBeforeAndAfterWatermarking() {
        List<File> originalFiles = new ArrayList<>();
        final float jpegQuality = 0.5f;

        for (int i = 0; i < SIZE_ITERATIONS; i++) {
            String name = "fingerprint_" + i + ".jpg";
            URL url = CLASS_LOADER.getResource(name);
            BufferedImage image = ImageIO.read(url);
            File file = new File(name);
            saveJPEG(image, file, jpegQuality);
            originalFiles.add(file);
        }

        List<File> watermarkedFiles = new ArrayList<>();
        for (int i = 0; i < SIZE_ITERATIONS; i++) {
            String name = "fingerprint_" + i + "_wm.jpg";
            watermarkedFiles.add(watermark(originalFiles.get(i).toURL(), PARAMETERS, KEYS, name, null, jpegQuality));
        }

        for (int i = 0; i < SIZE_ITERATIONS; i++) {
            long originalSize = originalFiles.get(i).length();
            long watermarkedSize = watermarkedFiles.get(i).length();
            System.out.println("Image " + i + " - original size after JPEG: " + originalSize + " Bytes; size after watermarking: " + watermarkedSize + " Bytes; ratio: " + ((double) watermarkedSize / (double) originalSize));
        }
    }

}

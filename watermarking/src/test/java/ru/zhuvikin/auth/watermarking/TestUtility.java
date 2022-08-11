package ru.zhuvikin.auth.watermarking;

import lombok.SneakyThrows;
import ru.zhuvikin.auth.security.RsaKeys;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

public final class TestUtility {

    @SneakyThrows
    static void saveJPEG(BufferedImage image, File file, float quality) {
        JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
        jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        jpegParams.setCompressionQuality(quality);

        final ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();

        writer.setOutput(new FileImageOutputStream(file));
        writer.write(null, new IIOImage(image, null, null), jpegParams);
    }

    static AuthenticationResult testImageWatermarkingAndAuthentication(
            URL sourceImage, WatermarkingParameters parameters, RsaKeys rsaKeys, String watermarkedName) {
        return testImageWatermarkingAndAuthentication(sourceImage, parameters, rsaKeys, watermarkedName, null);
    }

    @SneakyThrows
    static AuthenticationResult testImageWatermarkingAndAuthentication(
            URL sourceImage, WatermarkingParameters parameters, RsaKeys rsaKeys, String watermarkedName, String username) {
        File outputFile = watermark(sourceImage, parameters, rsaKeys, watermarkedName, username);
        return authenticate(parameters, rsaKeys, outputFile);
    }

    @SneakyThrows
    static File watermark(URL sourceImage, WatermarkingParameters parameters, RsaKeys rsaKeys, String watermarkedName, String username) {
        return watermark(sourceImage, parameters, rsaKeys, watermarkedName, username, 1.0f);
    }

    @SneakyThrows
    static File watermark(URL sourceImage, WatermarkingParameters parameters, RsaKeys rsaKeys, String watermarkedName, String username, Float jpegQuality) {
        BufferedImage image = ImageIO.read(sourceImage);
        BufferedImage watermarked = SelectiveImageAuthentication.watermark(username, image, parameters, rsaKeys.getPrivateKey());
        File outputFile = new File(watermarkedName);
        return saveWatermarkedImage(watermarked, outputFile, jpegQuality);
    }

    private static File saveWatermarkedImage(BufferedImage watermarked, File outputFile, Float jpegQuality) {
        saveJPEG(watermarked, outputFile, jpegQuality);
        return outputFile;
    }

    @SneakyThrows
    static AuthenticationResult authenticate(WatermarkingParameters parameters, RsaKeys rsaKeys, File file) {
        BufferedImage watermarkedImage = ImageIO.read(file);
        return SelectiveImageAuthentication.authenticate(watermarkedImage, parameters, rsaKeys.getPublicKey());
    }
}

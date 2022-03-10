package ru.zhuvikin.auth.app;

import lombok.SneakyThrows;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import ru.zhuvikin.auth.security.RsaKeys;
import ru.zhuvikin.auth.watermarking.SelectiveImageAuthentication;
import ru.zhuvikin.auth.watermarking.WatermarkingParameters;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;

public class SelectiveAuthentication {

    @SneakyThrows
    public static void main(String[] args) {

        WatermarkingParameters parameters = WatermarkingParameters.builder().build();

        Options options = new Options();
        options.addOption("e", true, "Error correction code rate");
        options.addOption("s", true, "Sigma");
        options.addOption("g", true, "Gamma");
        options.addOption("d", true, "Delta");
        options.addOption("f", true, "Source image file path");
        options.addOption("o", true, "Output image file path");
        options.addOption("a", true, "Authenticate");
        options.addOption("w", true, "Watermark");
        options.addOption("p", true, "Passphrase");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        boolean authentication = false;
        String output = null;
        RsaKeys rsaKeys = null;

        if (cmd.hasOption("a") && cmd.hasOption("w")) {
            System.err.println("Use either flag -a (authentication) or -w (watermarking) to specify mode.");
            System.exit(1);
        }

        if (cmd.hasOption("a")) {
            authentication = true;
        }

        if (cmd.hasOption("w")) {
            authentication = false;
            if (cmd.hasOption("o")) {
                output = cmd.getOptionValue("o");
                System.out.println("Output image file path is " + output);
            } else {
                System.err.println("Specify output image file path with flag -o");
                System.exit(1);
            }
        }

        BufferedImage image = null;
        if (cmd.hasOption("f")) {
            String path = cmd.getOptionValue("f");
            image = ImageIO.read(new File(path));
            System.out.println("Image file path is " + path);
        } else {
            System.err.println("The image file path should be specified. Use option -f in order to specify image file path.");
            System.exit(1);
        }

        if (cmd.hasOption("e")) {
            double rate = Double.valueOf(cmd.getOptionValue("e"));
            System.out.println("Error correction code rate is " + rate);
            parameters.setSigma(rate);
        }

        if (cmd.hasOption("s")) {
            double sigma = Double.valueOf(cmd.getOptionValue("s"));
            System.out.println("Sigma is " + sigma);
            parameters.setSigma(sigma);
        }

        if (cmd.hasOption("g")) {
            double gamma = Double.valueOf(cmd.getOptionValue("g"));
            System.out.println("Gamma is " + gamma);
            parameters.setSigma(gamma);
        }

        if (cmd.hasOption("d")) {
            double delta = Double.valueOf(cmd.getOptionValue("d"));
            System.out.println("Delta is " + delta);
            parameters.setSigma(delta);
        }

        if (cmd.hasOption("p")) {
            String passphrase = cmd.getOptionValue("p");
            rsaKeys = new RsaKeys(passphrase, 1024);
        } else {
            System.err.println("Specify passphrase with flag -p.");
            System.exit(1);
        }

        if (authentication) {
            boolean authentic = SelectiveImageAuthentication.authenticate(image, parameters, rsaKeys.getPublicKey());
            System.out.println("The image is " + (authentic ? "authentic" : "fake"));
        } else {
            BufferedImage watermarked = SelectiveImageAuthentication.watermark(image, parameters, rsaKeys.getPrivateKey());
            saveJPEG(watermarked, new File(output), 1.0f);
            System.out.println("Watermarked image is saved under path " + output);
        }
    }

    @SneakyThrows
    static void saveJPEG(BufferedImage image, File file, float quality) {
        JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
        jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        jpegParams.setCompressionQuality(quality);

        final ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();

        writer.setOutput(new FileImageOutputStream(file));
        writer.write(null, new IIOImage(image, null, null), jpegParams);
    }

}

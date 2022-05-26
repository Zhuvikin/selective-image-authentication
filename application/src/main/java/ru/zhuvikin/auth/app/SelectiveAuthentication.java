package ru.zhuvikin.auth.app;

import lombok.SneakyThrows;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FilenameUtils;
import ru.zhuvikin.auth.security.RsaKeys;
import ru.zhuvikin.auth.watermarking.AuthenticationResult;
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
import java.util.Objects;

public class SelectiveAuthentication {

    @SneakyThrows
    public static void main(String[] args) {
        WatermarkingParameters parameters = WatermarkingParameters.builder().build();

        Options options = new Options();
        options.addOption("e", "--error-correction-code-rate", true, "Error correction code rate");
        options.addOption("s", "--sigma", true, "Sigma");
        options.addOption("g", "--gamma", true, "Gamma");
        options.addOption("d", "--delta", true, "Delta");
        options.addOption("f", "--source-path", true, "Source image file path");
        options.addOption("o", "--output-path", true, "Output image file path. Default path is the same as the input one but with 'wm' suffix");
        options.addOption("a", "--authentication-mode", false, "Authenticate");
        options.addOption("w", "--watermarking-mode", false, "Watermark");
        options.addOption("p", "--passphrase", true, "Passphrase");
        options.addOption("n", "--name", true, "Name");
        options.addOption("m", "--max-name-length", true, "The maximum name length");

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

        File input = null;
        BufferedImage image = null;
        if (cmd.hasOption("f")) {
            String path = cmd.getOptionValue("f");
            input = new File(path);
            image = ImageIO.read(input);
            System.out.println("Image file path is " + path);
        } else {
            System.err.println("The image file path should be specified. Use option -f in order to specify image file path.");
            System.exit(1);
        }

        if (cmd.hasOption("w")) {
            authentication = false;
            if (cmd.hasOption("o")) {
                output = cmd.getOptionValue("o");
            } else {
                String name = input.getName();
                String outputName = FilenameUtils.getBaseName(name) + "_wm." + FilenameUtils.getExtension(name);
                output = FilenameUtils.separatorsToSystem(input.getParentFile().toPath() + "/" + outputName);
            }
            System.out.println("Output image file path is " + output);
        }

        if (cmd.hasOption("e")) {
            double rate = Double.valueOf(cmd.getOptionValue("e"));
            System.out.println("Error correction code rate is " + rate);
            parameters.setEccCodeRate(rate);
        }

        if (cmd.hasOption("s")) {
            double sigma = Double.valueOf(cmd.getOptionValue("s"));
            System.out.println("Sigma is " + sigma);
            parameters.setSigma(sigma);
        }

        if (cmd.hasOption("g")) {
            double gamma = Double.valueOf(cmd.getOptionValue("g"));
            System.out.println("Gamma is " + gamma);
            parameters.setGamma(gamma);
        }

        if (cmd.hasOption("d")) {
            double delta = Double.valueOf(cmd.getOptionValue("d"));
            System.out.println("Delta is " + delta);
            parameters.setDelta(delta);
        }

        String name = cmd.getOptionValue("n");
        if (cmd.hasOption("n")) {
            System.out.println("Name is " + name);
        }

        if (cmd.hasOption("m")) {
            int maxNameLength = Integer.valueOf(cmd.getOptionValue("m"));
            System.out.println("Maximum name length is " + maxNameLength);
            parameters.setMaximumNameLength(maxNameLength);
        }

        if (cmd.hasOption("p")) {
            String passphrase = cmd.getOptionValue("p");
            rsaKeys = new RsaKeys(passphrase, 1024);
        } else {
            System.err.println("Specify passphrase with flag -p.");
            System.exit(1);
        }

        if (authentication) {
            AuthenticationResult result = SelectiveImageAuthentication.authenticate(image, parameters, rsaKeys.getPublicKey());
            boolean authentic = result.isAuthentic();
            System.out.println("The image is " + (authentic ? "authentic" : "fake"));
            if (authentic) {
                String resultName = result.getName();
                if (Objects.equals(resultName, "")) {
                    System.out.println("User name is not specified");
                } else {
                    System.out.println("User name is '" + resultName + "'");
                }
            }
        } else {
            BufferedImage watermarked = SelectiveImageAuthentication.watermark(name, image, parameters, rsaKeys.getPrivateKey());
            saveJPEG(watermarked, new File(output), 1.0f);
            System.out.println("Watermarked image is saved under path " + output);
        }
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

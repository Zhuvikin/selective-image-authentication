package ru.zhuvikin.auth.app;

import lombok.SneakyThrows;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.awt.image.BufferedImage;

import static java.awt.image.BufferedImage.TYPE_BYTE_GRAY;
import static ru.zhuvikin.auth.watermarking.SelectiveImageAuthentication.authenticate;
import static ru.zhuvikin.auth.watermarking.SelectiveImageAuthentication.watermark;

public class SelectiveAuthentication {

    @SneakyThrows
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("t", true, "Threshold value");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        System.out.println("Run selective authentication with parameters!");
        if (cmd.hasOption("t")) {
            String t = cmd.getOptionValue("t");
            System.out.println("t parameter is " + t);
        }

        BufferedImage image = new BufferedImage(1, 1, TYPE_BYTE_GRAY);
        //BufferedImage watermarked = watermark(image);

        //boolean authentic = authenticate(watermarked);
        //System.out.println("The image is " + (authentic ? "authentic" : "fake"));
    }

}

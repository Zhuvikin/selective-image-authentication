package ru.zhuvikin.auth.app;

import lombok.SneakyThrows;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import ru.zhuvikin.auth.watermarking.WatermarkingParameters;

public class SelectiveAuthentication {

    @SneakyThrows
    public static void main(String[] args) {

        WatermarkingParameters parameters = WatermarkingParameters.builder().build();

        Options options = new Options();
        options.addOption("e", true, "Error correction code rate");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        /*
           eccCodeRate = 2;
           sigma = 10;
           gamma = 3;
           delta = 10;
         */

        if (cmd.hasOption("e")) {
            String t = cmd.getOptionValue("e");
            System.out.println("t parameter is " + t);
        }


        //BufferedImage watermarked = watermark(image);

        //boolean authentic = authenticate(watermarked);
        //System.out.println("The image is " + (authentic ? "authentic" : "fake"));
    }

}

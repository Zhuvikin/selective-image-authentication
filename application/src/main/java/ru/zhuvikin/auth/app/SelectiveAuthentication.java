package ru.zhuvikin.auth.app;

import lombok.SneakyThrows;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

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

    }

}

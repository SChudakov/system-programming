package com.sschudakov;

import org.apache.commons.cli.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class Main {
    private static final String COMMAND_LINE_SYNTAX = "counter";
    private static final String FILE_OPTION_NAME = "f";
    private static final String FILE_ARGUMENT_NAME = "file_path";
    private static final String FILE_OPTION_DESCRIPTION = "file to be read";

    public static void main(String[] args) {
        HelpFormatter formatter = new HelpFormatter();
        Options options = createOptions();

        try {
            CommandLine commandLine = parseArguments(args, options);

            if (commandLine.hasOption(FILE_OPTION_NAME)) {
                String filePath = commandLine.getOptionValue(FILE_OPTION_NAME);
                MaxLengthWords maxLengthWords = new MaxLengthWords();
                List<String> longestWords = maxLengthWords.wordsWithMaxLength(filePath);
                longestWords.forEach(System.out::println);
            } else {
                formatter.printHelp(COMMAND_LINE_SYNTAX, options);
            }
        } catch (ParseException | FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Options createOptions() {
        Option fileOption = Option.builder(FILE_OPTION_NAME)
                .argName(FILE_ARGUMENT_NAME)
                .hasArg()
                .desc(FILE_OPTION_DESCRIPTION)
                .build();
        Options options = new Options();
        options.addOption(fileOption);
        return options;
    }

    private static CommandLine parseArguments(String[] args, Options options) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }
}

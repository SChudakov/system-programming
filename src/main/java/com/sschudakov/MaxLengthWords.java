package com.sschudakov;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MaxLengthWords {
    private static final String WORD_REGEXP = "\\w{1,30}";
    private Pattern wordPattern;

    public MaxLengthWords() {
        wordPattern = Pattern.compile(WORD_REGEXP);
    }

    public Set<String> wordsWithMaxLength(String filePath) throws IOException {
        return wordsWithMaxLength(new File(filePath));
    }

    public Set<String> wordsWithMaxLength(File file) throws IOException {
        Set<String> words = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = wordPattern.matcher(line);
                while (matcher.find()) {
                    words.add(line.substring(matcher.start(), matcher.end()));
                }
            }
        }
        int maxLength = words.stream().mapToInt(String::length).max().orElse(0);
        return words.stream().filter(word -> word.length() == maxLength).collect(Collectors.toSet());
    }
}

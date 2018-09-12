package com.sschudakov;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MaxLengthWords {
    private static final String WORD_REGEXP = "(\\w|\\p{IsCyrillic}){1,30}";
    private Pattern wordPattern;

    public MaxLengthWords() {
        wordPattern = Pattern.compile(WORD_REGEXP);
    }

    public List<String> wordsWithMaxLength(String filePath) throws IOException {
        return wordsWithMaxLength(new File(filePath));
    }

    public List<String> wordsWithMaxLength(File file) throws IOException {
        Map<String, Integer> words = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = wordPattern.matcher(line);
                while (matcher.find()) {
                    String word = line.substring(matcher.start(), matcher.end());
                    words.putIfAbsent(word, 0);
                    words.compute(word, (key, value) -> value + 1);
                }
            }
        }
        int maxAmount = words.entrySet().stream().mapToInt(Map.Entry::getValue).max().orElse(0);
        return words.entrySet()
                .stream()
                .filter(entry -> entry.getValue() == maxAmount)
                .map(Map.Entry::getKey).collect(Collectors.toList());
    }
}

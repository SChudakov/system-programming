package com.sschudakov;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class MaxLengthWords {
    public Set<String> wordsWithMaxLength(String filePath) throws FileNotFoundException {
        return wordsWithMaxLength(new File(filePath));
    }

    public Set<String> wordsWithMaxLength(File file) throws FileNotFoundException {
        Set<String> words = new HashSet<>();
        Scanner scanner = new Scanner(file);
        while (scanner.hasNext()) {
            words.add(scanner.next());
        }
        int maxLength = words.stream().mapToInt(String::length).max().orElse(0);
        return words.stream().filter(word -> word.length() == maxLength).collect(Collectors.toSet());
    }
}

package com.sschudakov.analysis;

import java.io.IOException;

public class Main {
    private static final String CODE_PATH = "C:\\Users\\Semen\\D\\workspace.cpp\\crypto\\arithmetic\\main.cpp";

    public static void main(String[] args) {
        CPPLexer lexer = new CPPLexer();
        try {
            for (CPPLexer.Lexeme lexeme : lexer.analyze(CODE_PATH)) {
                System.out.println(lexeme);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

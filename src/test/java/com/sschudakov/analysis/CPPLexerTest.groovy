package com.sschudakov.analysis

import spock.lang.Specification

class CPPLexerTest extends Specification {
    private CPPLexer lexer = new CPPLexer()

    def "test analyze 1"() {
        when:
        List<CPPLexer.Lexeme> lexems = lexer.analyze("C:\\Users\\Semen\\D\\workspace.java\\system-programming\\src\\test\\resources\\lexeme\\test-numbers-lexer.txt")
        lexems.each {
            println it
        }
        then:
        notThrown(Exception)
    }

    def "test analyze 2"() {
        when:
        List<CPPLexer.Lexeme> lexems = lexer.analyze("C:\\Users\\Semen\\D\\workspace.cpp\\crypto\\arithmetic\\main.cpp")
        lexems.each {
            println it
        }
        then:
        notThrown(Exception)
    }
}

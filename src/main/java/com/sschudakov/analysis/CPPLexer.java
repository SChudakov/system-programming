package com.sschudakov.analysis;

import com.sschudakov.automaton.io.CSVAutomatonImporter;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CPPLexer {
    private static final String RESERVED_WORDS_FILE_PATH = "C:\\Users\\Semen\\D\\workspace.java\\system-programming\\src\\main\\resources\\lexeme\\cpp-reserved-words.txt";
    private static final String PROCESSOR_DIRECTIVES_FILE_PATH = "C:\\Users\\Semen\\D\\workspace.java\\system-programming\\src\\main\\resources\\lexeme\\cpp-preprocessor-derectives.txt";

    private static final String SEPARATORS_FILE_PATH = "C:\\Users\\Semen\\D\\workspace.java\\system-programming\\src\\main\\resources\\lexeme\\cpp-separators.txt";
    private static final String NUMBER_LEXER_AUTOMATON = "C:\\Users\\Semen\\D\\workspace.java\\system-programming\\src\\main\\resources\\lexeme\\number-lexer-automaton.txt";

    private static Set<String> cppReservedWords;
    private static Set<String> cppPreprocessorDirectives;

    private static Set<Character> cppSeparators;
    private static LexerAutomaton numberLexerAutomaton;


    private static final char POINT = '.';
    private static final char PLUS = '+';
    private static final char MINUS = '-';
    private static final char SINGLE_QUOTE = '\'';
    private static final char DOUBLE_QUOTE = '\"';
    private static final char SLASH = '\\';
    private static final String HEXADECIMAL_PREFIX = "0x";
    private static final String HEXADECIMAL_CAPITAL_PREFIX = "0X";

    static {
        cppReservedWords = readFileLines(RESERVED_WORDS_FILE_PATH);
        cppPreprocessorDirectives = readFileLines(PROCESSOR_DIRECTIVES_FILE_PATH);
        cppSeparators = readFileCharacters();
        numberLexerAutomaton = readLexerAutomaton();
    }

    private static Set<String> readFileLines(String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            Set<String> result = new HashSet<>();
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(line.trim());
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Set<Character> readFileCharacters() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CPPLexer.SEPARATORS_FILE_PATH))) {
            Set<Character> result = new HashSet<>();
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(line.charAt(0));
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static LexerAutomaton readLexerAutomaton() {
        LexerAutomatonImporter importer = new LexerAutomatonImporter(new CSVAutomatonImporter());
        try {
            return importer.importAutomaton(NUMBER_LEXER_AUTOMATON);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<Lexeme> analyze(String path) throws IOException {
        return analyze(new File(path));
    }

    public List<Lexeme> analyze(File file) throws IOException {
        PushbackInputStream stream = new PushbackInputStream(new FileInputStream(file));
        List<Lexeme> lexemes = new ArrayList<>();
        char ch;
        Lexeme lexeme = null;
        boolean parsed;
        while (!isEOF(ch = read(stream))) {
            parsed = false;
            if (Character.isSpaceChar(ch) || isEOL(ch)) {
                continue;
            }
            unreadIfNecessary(stream, ch);
            if (isSeparator(ch)) {
                parsed = true;
                lexeme = getSeparator(stream);
            }
            if (Character.isJavaIdentifierStart(ch)) {
                parsed = true;
                lexeme = getIdentifier(stream);
            }
            if (Character.isDigit(ch) || ch == PLUS || ch == MINUS) {
                parsed = true;
                lexeme = getNumber(stream);
            }
            if (isBackSlash(ch)) {
                parsed = true;
                lexeme = getComment(stream);
            }
            if (isSingleQuote(ch)) {
                parsed = true;
                lexeme = getCharLiteral(stream);
            }
            if (isDoubleQuote(ch)) {
                parsed = true;
                lexeme = getStringLiteral(stream);
            }
            if (isSharp(ch)) {
                parsed = true;
                lexeme = getPreprocessorDirective(stream);
            }
            if (!parsed) {
                lexeme = getLexicalMistake(stream, new StringBuilder());
            }
            lexemes.add(lexeme);
        }
        return lexemes;
    }

    private Lexeme getSeparator(PushbackInputStream stream) throws IOException {
        return Lexeme.of(String.valueOf(read(stream)), LexemeType.SEPARATOR);
    }

    private Lexeme getIdentifier(PushbackInputStream stream) throws IOException {
        StringBuilder lexemeBuilder = new StringBuilder();

        char ch;
        while (!isEOF(ch = read(stream)) && Character.isJavaIdentifierPart(ch)) {
            lexemeBuilder.append(ch);
        }

        if (!isLexemeEnd(ch)) {
            lexemeBuilder.append(ch);
            return getLexicalMistake(stream, lexemeBuilder);
        }

        unreadIfNecessary(stream, ch);
        String identifier = lexemeBuilder.toString();

        if (isReservedWord(identifier)) {
            return Lexeme.of(identifier, LexemeType.RESERVED_WORD);
        } else {
            return Lexeme.of(identifier, LexemeType.IDENTIFIER);
        }
    }

    private Lexeme getNumber(PushbackInputStream stream) throws IOException {
        numberLexerAutomaton.resetToBegin();
        StringBuilder lexemeBuilder = new StringBuilder();
        char ch = 0;
        while (!isEOF(ch) && numberLexerAutomaton.getCurrentState() != null) {
            ch = read(stream);
            lexemeBuilder.append(ch);
            numberLexerAutomaton.readSymbol(String.valueOf(ch));
        }
        unreadIfNecessary(stream, ch);
        lexemeBuilder.deleteCharAt(lexemeBuilder.length() - 1);

        if (numberLexerAutomaton.getPreviousState().isFinalState()) {
            String number = lexemeBuilder.toString();
            if ((number.startsWith(HEXADECIMAL_PREFIX) || number.startsWith(HEXADECIMAL_CAPITAL_PREFIX))
                    && isLexemeEnd(ch)) {
                return Lexeme.of(number, LexemeType.HEXADECIMAL);
            }
            if (Character.isAlphabetic(ch)) {
                Lexeme typeLexeme = getIdentifier(stream);
                String typeIdentifier = typeLexeme.getLexeme();
                lexemeBuilder.append(typeIdentifier);
                if (typeLexeme.getType().equals(LexemeType.LEXICAL_MISTAKE)) {
                    return Lexeme.of(lexemeBuilder.toString(), LexemeType.LEXICAL_MISTAKE);
                }
            }
            if (number.contains(String.valueOf(POINT))) {
                return Lexeme.of(lexemeBuilder.toString(), LexemeType.DOUBLE);
            }
            return Lexeme.of(lexemeBuilder.toString(), LexemeType.INTEGER);
        }
        return getLexicalMistake(stream, lexemeBuilder);
    }

    private Lexeme getComment(PushbackInputStream stream) throws IOException {
        StringBuilder lexemeBuilder = new StringBuilder();
        char ch = read(stream);
        lexemeBuilder.append(ch);
        ch = read(stream);
        if (isBackSlash(ch)) {
            lexemeBuilder.append(ch);
            return getSingleLineComment(stream, lexemeBuilder);
        }
        if (isStar(ch)) {
            lexemeBuilder.append(ch);
            return getMultilineComment(stream, lexemeBuilder);
        }
        unreadIfNecessary(stream, ch);
        return getLexicalMistake(stream, lexemeBuilder);
    }

    private Lexeme getSingleLineComment(PushbackInputStream stream, StringBuilder lexemeBuilder) throws IOException {
        char ch;
        while (!isEOF(ch = read(stream)) && !isEOL(ch)) {
            lexemeBuilder.append(ch);
        }
        return Lexeme.of(lexemeBuilder.toString(), LexemeType.COMMENT);
    }

    private Lexeme getMultilineComment(PushbackInputStream stream, StringBuilder lexemeBuilder) throws IOException {
        char ch = 0;
        while (!isEOF(ch)) {
            ch = read(stream);
            lexemeBuilder.append(ch);
            if (isStar(ch)) {
                char chn = read(stream);
                if (isEOF(chn)) {
                    return Lexeme.of(lexemeBuilder.toString(), LexemeType.LEXICAL_MISTAKE);
                }
                lexemeBuilder.append(chn);
                if (isBackSlash(chn)) {
                    return Lexeme.of(lexemeBuilder.toString(), LexemeType.COMMENT);
                }
            }
        }
        return Lexeme.of(lexemeBuilder.toString(), LexemeType.LEXICAL_MISTAKE);
    }

    private Lexeme getCharLiteral(PushbackInputStream stream) throws IOException {
        String literal = readLiteral(stream, SINGLE_QUOTE);
        if (literal.length() > 2 && literal.charAt(literal.length() - 1) == SINGLE_QUOTE && literal.charAt(literal.length() - 2) != SLASH) {
            return Lexeme.of(literal, LexemeType.CHAR_LITERAL);
        }
        return Lexeme.of(literal, LexemeType.LEXICAL_MISTAKE);
    }

    private Lexeme getStringLiteral(PushbackInputStream stream) throws IOException {
        String literal = readLiteral(stream, DOUBLE_QUOTE);
        if (literal.length() > 1 && literal.charAt(literal.length() - 1) == DOUBLE_QUOTE && literal.charAt(literal.length() - 2) != SLASH) {
            return Lexeme.of(literal, LexemeType.STRING_LITERAL);
        }
        return Lexeme.of(literal, LexemeType.LEXICAL_MISTAKE);
    }

    private String readLiteral(PushbackInputStream stream, char terminationChar) throws IOException {
        StringBuilder lexemeBuilder = new StringBuilder();
        lexemeBuilder.append(read(stream));
        char ch = 0;
        char chn;
        while (!isEOF(chn = read(stream))) {
            lexemeBuilder.append(chn);
            if (chn == terminationChar) {
                if (!isSlash(ch)) {
                    break;
                }
            }
            ch = chn;
        }
        return lexemeBuilder.toString();
    }

    private Lexeme getPreprocessorDirective(PushbackInputStream stream) throws IOException {
        StringBuilder lexemeBuilder = new StringBuilder();

        lexemeBuilder.append(read(stream));

        Lexeme identifier = getIdentifier(stream);
        lexemeBuilder.append(identifier.getLexeme());

        if (identifier.getType().equals(LexemeType.LEXICAL_MISTAKE) || !isPreprocessorDirective(lexemeBuilder.toString())) {
            return Lexeme.of(lexemeBuilder.toString(), LexemeType.LEXICAL_MISTAKE);
        }
        return Lexeme.of(lexemeBuilder.toString(), LexemeType.PREPROCESSOR_DIRECTIVE);
    }

    private Lexeme getLexicalMistake(PushbackInputStream stream, StringBuilder lexemeBuilder) throws IOException {
        char ch;
        while (!isEOF(ch = read(stream)) && !isEOL(ch) && !Character.isSpaceChar(ch) && !isSeparator(ch)) {
            lexemeBuilder.append(ch);
        }
        unreadIfNecessary(stream, ch);
        return Lexeme.of(lexemeBuilder.toString(), LexemeType.LEXICAL_MISTAKE);
    }


    private boolean isSlash(char ch) {
        return ch == SLASH;
    }

    private boolean isBackSlash(char ch) {
        return ch == '/';
    }

    private boolean isStar(char ch) {
        return ch == '*';
    }

    private boolean isDoubleQuote(char ch) {
        return ch == DOUBLE_QUOTE;
    }

    private boolean isSingleQuote(char ch) {
        return ch == SINGLE_QUOTE;
    }

    private boolean isSharp(char ch) {
        return ch == '#';
    }

    private boolean isEOL(char ch) {
        return ch == '\n' || ch == '\r';
    }

    private boolean isEOF(char ch) {
        return ch == Character.MAX_VALUE;
    }

    private boolean isLexemeEnd(char ch) {
        return isEOF(ch) || Character.isSpaceChar(ch) || isSeparator(ch) || isEOL(ch);
    }

    private boolean isSeparator(char character) {
        return cppSeparators.contains(character);
    }

    private boolean isPreprocessorDirective(String lexeme) {
        return cppPreprocessorDirectives.contains(lexeme);
    }

    private boolean isReservedWord(String lexeme) {
        return cppReservedWords.contains(lexeme);
    }


    private char read(InputStream stream) throws IOException {
        return (char) stream.read();
    }

    private void unreadIfNecessary(PushbackInputStream stream, char ch) throws IOException {
        if (!isEOF(ch)) {
            stream.unread(ch);
        }
    }


    public static class Lexeme {
        private final String lexeme;
        private final LexemeType type;

        Lexeme(String lexeme, LexemeType type) {
            this.lexeme = lexeme;
            this.type = type;
        }

        String getLexeme() {
            return lexeme;
        }

        LexemeType getType() {
            return type;
        }

        static Lexeme of(String lexeme, LexemeType type) {
            return new Lexeme(lexeme, type);
        }

        @Override
        public String toString() {
            return "[" + lexeme + ',' + type + ']';
        }
    }

    enum LexemeType {
        INTEGER,
        DOUBLE,
        HEXADECIMAL,
        STRING_LITERAL,
        CHAR_LITERAL,
        PREPROCESSOR_DIRECTIVE,
        COMMENT,
        RESERVED_WORD,
        IDENTIFIER,
        SEPARATOR,
        LEXICAL_MISTAKE
    }
}

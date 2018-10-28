package com.sschudakov.analysis;

import com.sschudakov.automaton.Automaton;
import com.sschudakov.automaton.io.AutomatonImporter;

import java.io.File;
import java.io.FileNotFoundException;

public class LexerAutomatonImporter {
    private AutomatonImporter importer;

    public LexerAutomatonImporter(AutomatonImporter importer) {
        this.importer = importer;
    }

    public LexerAutomaton importAutomaton(String path) throws FileNotFoundException {
        return importAutomaton(new File(path));
    }

    public LexerAutomaton importAutomaton(File file) throws FileNotFoundException {
        Automaton automaton = this.importer.importAutomaton(file);
        return new LexerAutomaton(automaton);
    }
}

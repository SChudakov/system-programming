package com.sschudakov.automaton.io;

import com.sschudakov.automaton.Automaton;

import java.io.File;
import java.io.FileNotFoundException;

public interface AutomatonImporter {

    default Automaton importAutomaton(String path) throws FileNotFoundException {
        return importAutomaton(new File(path));
    }

    Automaton importAutomaton(File file) throws FileNotFoundException;
}

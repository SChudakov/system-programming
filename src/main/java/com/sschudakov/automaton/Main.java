package com.sschudakov.automaton;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        AutomatonImporter importer = new CSVAutomatonImporter();
        try {
            Automaton automaton = importer.importAutomaton("C:\\Users\\Semen\\D\\workspace.java\\system-programming\\src\\main\\resources\\automaton\\automaton_1.txt");
            System.out.println(automaton);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

package com.sschudakov.automaton;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        CSVAutomatonImporter importer = new CSVAutomatonImporter();

        try {
            Automaton automaton = importer.importAutomaton(
                    "C:\\Users\\Semen\\D\\workspace.java\\system-programming\\src\\main\\resources\\automaton\\test_algorithm_3.txt");
            System.out.println("imported: " + automaton);

            Algorithm algorithm = new Algorithm();
            System.out.println(algorithm.execute(automaton));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

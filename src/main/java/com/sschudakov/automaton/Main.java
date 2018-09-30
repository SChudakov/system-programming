package com.sschudakov.automaton;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        CSVAutomatonImporter importer = new CSVAutomatonImporter();
        AutomatonDeterminator determinator = new AutomatonDeterminator();
        AutomatonUnifier unifier = new AutomatonUnifier();
        AutomatonMinimizer minimizer = new AutomatonMinimizer();
        try {
            Automaton automaton = importer.importAutomaton(
                    "C:\\Users\\Semen\\D\\workspace.java\\system-programming\\src\\main\\resources\\automaton\\det_min_1.txt");
            System.out.println("imported: " + automaton);

            Automaton determined = determinator.determinate(automaton);
            System.out.println("determined: " + determined);

            Automaton unified = unifier.unifySymbols(determined);
            System.out.println("unified: " + unified);

            Automaton minimized = minimizer.minimize(unified);
            System.out.println("minimized: " + minimized);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

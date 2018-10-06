package com.sschudakov.automaton.algorithm;

import com.sschudakov.automaton.Automaton;

public class Algorithm {
    private AutomatonDeterminator determinator = new AutomatonDeterminator();
    private AutomatonUnifier unifier = new AutomatonUnifier();
    private AutomatonMinimizer minimizer = new AutomatonMinimizer();
    private OddWordsRecognition recognition = new OddWordsRecognition();


    public boolean execute(Automaton automaton) {
        Automaton determined = determinator.determinate(automaton);
        System.out.println("determined: " + determined);

        Automaton unified = unifier.unifySymbols(determined);
        System.out.println("unified: " + unified);

        Automaton minimized = minimizer.minimize(unified);
        System.out.println("minimized: " + minimized);

        return recognition.recognizes(minimized);
    }
}

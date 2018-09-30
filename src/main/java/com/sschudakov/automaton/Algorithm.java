package com.sschudakov.automaton;

public class Algorithm {
    private AutomatonDeterminator determinator = new AutomatonDeterminator();
    private AutomatonUnifier unifier = new AutomatonUnifier();
    private AutomatonMinimizer minimizer = new AutomatonMinimizer();


    public boolean execute(Automaton automaton) {
        Automaton determined = determinator.determinate(automaton);
        System.out.println("determined: " + determined);

        Automaton unified = unifier.unifySymbols(determined);
        System.out.println("unified: " + unified);

        Automaton minimized = minimizer.minimize(unified);
        System.out.println("minimized: " + minimized);

        OddWordsRecognition recognition = new OddWordsRecognition();
        return recognition.recognizes(minimized);
    }
}

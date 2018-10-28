package com.sschudakov.automaton.io;

import com.sschudakov.automaton.Automaton;
import com.sschudakov.automaton.AutomatonEdge;
import com.sschudakov.automaton.AutomatonState;
import org.jgrapht.graph.DirectedPseudograph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class CSVAutomatonImporter implements AutomatonImporter {
    private static final String WHITESPACE_REGEXP = "[ \\t]+";

    @Override
    public Automaton importAutomaton(File file) throws FileNotFoundException {
        DirectedPseudograph<AutomatonState, AutomatonEdge> automatonGraph
                = new DirectedPseudograph<>(AutomatonEdge.class);

        Scanner scanner = new Scanner(file);
        int alphabetSize = readAlphabetSize(scanner);
        int statesSetSize = readStatesSetSize(scanner);
        int initialStateIndex = readInitialState(scanner);
        Set<Integer> finalStates = readFinalStates(scanner);

        addStates(automatonGraph, statesSetSize, finalStates);
        addTransitions(scanner, automatonGraph, finalStates);

        AutomatonState initialState = new AutomatonState(initialStateIndex, finalStates.contains(initialStateIndex));

        return new Automaton(automatonGraph, initialState);
    }

    private int readAlphabetSize(Scanner scanner) {
        return Integer.valueOf(scanner.nextLine());
    }

    private int readStatesSetSize(Scanner scanner) {
        return Integer.valueOf(scanner.nextLine());
    }

    private int readInitialState(Scanner scanner) {
        return Integer.valueOf(scanner.nextLine());
    }

    private Set<Integer> readFinalStates(Scanner scanner) {
        return Arrays.stream(scanner.nextLine().split(WHITESPACE_REGEXP)).map(Integer::valueOf).collect(Collectors.toCollection(HashSet::new));
    }

    private void addStates(DirectedPseudograph<AutomatonState, AutomatonEdge> automatonGraph, int statesSetSize, Set<Integer> finalStates) {
        for (int i = 0; i < statesSetSize; i++) {
            automatonGraph.addVertex(new AutomatonState(i, finalStates.contains(i)));
        }
    }


    private void addTransitions(Scanner scanner, DirectedPseudograph<AutomatonState, AutomatonEdge> automatonGraph, Set<Integer> finalStates) {
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (!line.trim().equals("")) {
                String[] parts = line.split(WHITESPACE_REGEXP);
                int sourceStateIndex;
                String symbol;
                int targetStateIndex;

                if (parts.length == 3) {
                    sourceStateIndex = Integer.valueOf(parts[0]);
                    symbol = parts[1];
                    targetStateIndex = Integer.valueOf(parts[2]);
                } else {
                    sourceStateIndex = Integer.valueOf(parts[0]);
                    symbol = "";
                    targetStateIndex = Integer.valueOf(parts[1]);
                }

                AutomatonState sourceState = new AutomatonState(sourceStateIndex, finalStates.contains(sourceStateIndex));
                AutomatonState targetState = new AutomatonState(targetStateIndex, finalStates.contains(targetStateIndex));

                automatonGraph.addVertex(sourceState);
                automatonGraph.addVertex(targetState);
                automatonGraph.addEdge(sourceState, targetState, new AutomatonEdge(symbol));
            }
        }
    }
}

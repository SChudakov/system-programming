package com.sschudakov.automaton;

import org.jgrapht.Graphs;
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

        try (Scanner scanner = new Scanner(file)) {
            int alphabetSize = readAlphabetSize(scanner);
            int statesSetSize = readStatesSetSize(scanner);
            int initialState = readInitialState(scanner);
            Set<Integer> finalStates = readFinalStates(scanner);
            addTransitions(scanner, automatonGraph, finalStates);

            return new Automaton(automatonGraph, initialState);
        } catch (Exception e) {
            throw new RuntimeException("Illegal automaton format", e);
        }
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

    private void addTransitions(Scanner scanner, DirectedPseudograph<AutomatonState, AutomatonEdge> automaton, Set<Integer> finalStates) {
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(WHITESPACE_REGEXP);
            int beginStateIndex = Integer.valueOf(parts[0]);
            AutomatonState beginState = new AutomatonState(beginStateIndex, finalStates.contains(beginStateIndex));
            String symbol = parts[1];
            int targetStateIndex = Integer.valueOf(parts[2]);
            AutomatonState targetState = new AutomatonState(beginStateIndex, finalStates.contains(targetStateIndex));
            Graphs.addEdgeWithVertices(automaton, beginState, targetState);
            automaton.getEdge(beginState, targetState).setSymbol(symbol);
        }
    }
}

package com.sschudakov.automaton;

import org.jgrapht.graph.DirectedPseudograph;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Automaton {
    private DirectedPseudograph<AutomatonState, AutomatonEdge> automatonGraph;
    private AutomatonState initialState;


    public Automaton(DirectedPseudograph<AutomatonState, AutomatonEdge> automatonGraph, AutomatonState initialState) {
        this.automatonGraph = automatonGraph;
        this.initialState = initialState;
    }

    public DirectedPseudograph<AutomatonState, AutomatonEdge> getAutomatonGraph() {
        return automatonGraph;
    }

    public AutomatonState getInitialState() {
        return initialState;
    }

    public Set<AutomatonState> finalStates() {
        return automatonGraph.vertexSet().stream().filter(AutomatonState::isFinalState).collect(Collectors.toSet());
    }

    public Set<AutomatonState> notFinalStates() {
        Set<AutomatonState> result = new HashSet<>(automatonGraph.vertexSet());
        result.removeAll(finalStates());
        return result;
    }

    public Set<String> alphabet() {
        return automatonGraph.edgeSet().stream()
                .map(AutomatonEdge::getSymbol).collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        List<AutomatonState> finalStates = automatonGraph.vertexSet().stream()
                .filter(AutomatonState::isFinalState).sorted(Comparator.comparingInt(AutomatonState::getIndex)).collect(Collectors.toList());
        return new StringBuilder()
                .append("initial: ")
                .append(initialState)
                .append(", ")
                .append("final: ")
                .append(finalStates)
                .append(", ")
                .append(automatonGraph)
                .toString();
    }
}

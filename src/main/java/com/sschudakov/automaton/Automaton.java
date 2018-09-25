package com.sschudakov.automaton;

import org.jgrapht.graph.DirectedPseudograph;

public class Automaton {
    private DirectedPseudograph<AutomatonState, AutomatonEdge> automatonGraph;
    private int initialState;


    public Automaton(DirectedPseudograph<AutomatonState, AutomatonEdge> automatonGraph, int initialState) {
        this.automatonGraph = automatonGraph;
        this.initialState = initialState;
    }

    public int getInitialState() {
        return initialState;
    }

    @Override
    public String toString() {
        return initialState + " " + automatonGraph;
    }
}

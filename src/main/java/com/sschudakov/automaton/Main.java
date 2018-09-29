package com.sschudakov.automaton;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DirectedPseudograph;

public class Main {
    public static void main(String[] args) {
        DirectedPseudograph<AutomatonState, AutomatonEdge> pseudograph = new DirectedPseudograph<>(AutomatonEdge.class);
        AutomatonState state_1 = new AutomatonState(1, false);
        AutomatonState state_2 = new AutomatonState(2, false);
        pseudograph.addVertex(state_1);
        pseudograph.addVertex(state_2);
        pseudograph.addEdge(state_1, state_2, new AutomatonEdge("s"));
        pseudograph.addEdge(state_1, state_2, new AutomatonEdge("t"));
        System.out.println(pseudograph);
    }
}

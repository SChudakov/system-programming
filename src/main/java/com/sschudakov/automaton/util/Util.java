package com.sschudakov.automaton.util;

import com.sschudakov.automaton.Automaton;
import com.sschudakov.automaton.AutomatonEdge;
import com.sschudakov.automaton.AutomatonState;
import org.jgrapht.graph.DirectedPseudograph;

public class Util {
    public static Automaton copyAutomaton(Automaton automaton) {
        DirectedPseudograph<AutomatonState, AutomatonEdge> graph = automaton.getAutomatonGraph();
        AutomatonState initialState = automaton.getInitialState();

        DirectedPseudograph<AutomatonState, AutomatonEdge> graphCopy = new DirectedPseudograph<>(AutomatonEdge.class);
        for (AutomatonState state : graph.vertexSet()) {
            graphCopy.addVertex(new AutomatonState(state.getIndex(), state.isFinalState()));
        }
        for (AutomatonEdge edge : graph.edgeSet()) {
            AutomatonState source = graph.getEdgeSource(edge);
            AutomatonState target = graph.getEdgeTarget(edge);
            graphCopy.addEdge(source, target, new AutomatonEdge(edge.getSymbol()));
        }
        return new Automaton(graphCopy, new AutomatonState(initialState.getIndex(), initialState.isFinalState()));
    }
}

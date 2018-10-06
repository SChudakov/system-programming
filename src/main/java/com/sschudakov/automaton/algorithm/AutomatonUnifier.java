package com.sschudakov.automaton.algorithm;

import com.sschudakov.automaton.Automaton;
import com.sschudakov.automaton.AutomatonEdge;
import com.sschudakov.automaton.AutomatonState;
import org.jgrapht.graph.DirectedPseudograph;

public class AutomatonUnifier {
    private static final String UNIFIED_SYMBOL = "a";

    public Automaton unifySymbols(Automaton automaton) {
        DirectedPseudograph<AutomatonState, AutomatonEdge> automatonGraph = automaton.getAutomatonGraph();
        DirectedPseudograph<AutomatonState, AutomatonEdge> pseudograph =
                new DirectedPseudograph<>(AutomatonEdge.class);
        for (AutomatonState state : automatonGraph.vertexSet()) {
            pseudograph.addVertex(new AutomatonState(state.getIndex(), state.isFinalState()));
        }
        for (AutomatonEdge edge : automatonGraph.edgeSet()) {
            pseudograph.addEdge(automatonGraph.getEdgeSource(edge),
                    automatonGraph.getEdgeTarget(edge),
                    new AutomatonEdge(UNIFIED_SYMBOL));
        }
        AutomatonState initialState = new AutomatonState(automaton.getInitialState().getIndex(),
                automaton.getInitialState().isFinalState());
        return new Automaton(pseudograph, initialState);
    }
}

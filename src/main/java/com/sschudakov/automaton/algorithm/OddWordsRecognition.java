package com.sschudakov.automaton.algorithm;

import com.sschudakov.automaton.Automaton;
import com.sschudakov.automaton.AutomatonEdge;
import com.sschudakov.automaton.AutomatonState;
import org.jgrapht.alg.cycle.TarjanSimpleCycles;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DirectedPseudograph;

import java.util.List;
import java.util.stream.Collectors;

public class OddWordsRecognition {

    public boolean recognizes(Automaton automaton) {
        return checkFinalStatesSelfLoops(automaton) || checkLoopsOfLengthTwo(automaton);
    }

    private boolean checkFinalStatesSelfLoops(Automaton automaton) {
        DirectedPseudograph<AutomatonState, AutomatonEdge> pseudograph = automaton.getAutomatonGraph();
        for (AutomatonState state : automaton.finalStates()) {
            for (AutomatonEdge edge : pseudograph.outgoingEdgesOf(state)) {
                if (pseudograph.getEdgeTarget(edge).equals(state) && containsPathOfFinalStates(automaton, state)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkLoopsOfLengthTwo(Automaton automaton) {
        TarjanSimpleCycles<AutomatonState, AutomatonEdge> tarjanSimpleCycles =
                new TarjanSimpleCycles<>(automaton.getAutomatonGraph());
        List<List<AutomatonState>> cycles = tarjanSimpleCycles.findSimpleCycles();
        List<List<AutomatonState>> cyclesOfLengthTwo = cycles.stream()
                .filter(cycle -> cycle.size() == 2).collect(Collectors.toList());

        for (List<AutomatonState> cycle : cyclesOfLengthTwo) {
            if (cycle.get(0).isFinalState()) {
                if (!automaton.getInitialState().equals(cycle.get(0))) {
                    if (containsPathOfFinalStates(automaton, cycle.get(0))) {
                        return true;
                    }
                }

            }
            if (cycle.get(1).isFinalState()) {
                if (!automaton.getInitialState().equals(cycle.get(1))) {
                    if (containsPathOfFinalStates(automaton, cycle.get(1))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean containsPathOfFinalStates(Automaton automaton, AutomatonState state) {
        if (automaton.getInitialState().equals(state)) {
            return true;
        }
        DijkstraShortestPath<AutomatonState, AutomatonEdge> path =
                new DijkstraShortestPath<>(automaton.getAutomatonGraph());
        List<AutomatonState> pathVertices = path.getPath(automaton.getInitialState(), state).getVertexList();
        boolean result = true;
        if (pathVertices.size() % 2 == 0) {
            for (int i = 1; i < pathVertices.size(); i += 2) {
                if (!pathVertices.get(i).isFinalState()) {
                    result = false;
                }
            }
        } else {
            result = false;
        }
        return result;
    }
}

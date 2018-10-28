package com.sschudakov.analysis;

import com.sschudakov.automaton.Automaton;
import com.sschudakov.automaton.AutomatonEdge;
import com.sschudakov.automaton.AutomatonState;

import java.util.Map;
import java.util.stream.Collectors;

public class LexerAutomaton {
    private Automaton automaton;
    private AutomatonState previousState;
    private AutomatonState currentState;

    public LexerAutomaton(Automaton automaton) {
        this.automaton = automaton;
        this.currentState = automaton.getInitialState();
    }

    public Automaton getAutomaton() {
        return automaton;
    }

    public AutomatonState getPreviousState() {
        return previousState;
    }

    public AutomatonState getCurrentState() {
        return currentState;
    }

    public void readSymbol(String symbol) {
        previousState = currentState;
        Map<String, AutomatonEdge> transitionMap = automaton.getAutomatonGraph().outgoingEdgesOf(currentState)
                .stream().collect(Collectors.toMap(AutomatonEdge::getSymbol, edge -> edge));
        if (transitionMap.containsKey(symbol)) {
            currentState = automaton.getAutomatonGraph().getEdgeTarget(transitionMap.get(symbol));
        } else {
            currentState = null;
        }
    }

    public void resetToBegin() {
        currentState = automaton.getInitialState();
        previousState = null;
    }
}

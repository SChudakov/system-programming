package com.sschudakov.automaton;

import org.jgrapht.graph.DefaultEdge;

public class AutomatonEdge extends DefaultEdge {
    private String symbol;

    public AutomatonEdge() {
    }

    public AutomatonEdge(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return "(" + getSource() + " : " + getTarget() + " : " + symbol + ")";
    }
}

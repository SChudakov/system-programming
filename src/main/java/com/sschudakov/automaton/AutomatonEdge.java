package com.sschudakov.automaton;

import org.jgrapht.graph.DefaultEdge;

import java.util.Objects;

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

    @Override
    public String toString() {
        return "(" + getSource() + " : " + getTarget() + " : " + symbol + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AutomatonEdge edge = (AutomatonEdge) o;
        return Objects.equals(symbol, edge.symbol) &&
                Objects.equals(getSource(), edge.getSource()) &&
                Objects.equals(getTarget(), edge.getTarget());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSymbol());
    }
}

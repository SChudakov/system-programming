package com.sschudakov.automaton;

public class AutomatonState {
    private Integer index;
    private boolean finalState;

    public AutomatonState(Integer index, boolean finalState) {
        this.index = index;
        this.finalState = finalState;
    }

    public Integer getIndex() {
        return index;
    }

    public boolean isFinalState() {
        return finalState;
    }

    @Override
    public String toString() {
        if (finalState) {
            return index + " : final";
        } else {
            return index+"";
        }
    }
}

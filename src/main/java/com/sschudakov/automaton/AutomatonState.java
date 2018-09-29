package com.sschudakov.automaton;

import java.util.Objects;

public class AutomatonState {
    private int index;
    private boolean finalState;

    public AutomatonState(int index, boolean finalState) {
        this.index = index;
        this.finalState = finalState;
    }

    public int getIndex() {
        return index;
    }

    public boolean isFinalState() {
        return finalState;
    }

    public void setFinalState(boolean finalState) {
        this.finalState = finalState;
    }

    @Override
    public String toString() {
        return String.valueOf(index);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AutomatonState that = (AutomatonState) o;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }
}

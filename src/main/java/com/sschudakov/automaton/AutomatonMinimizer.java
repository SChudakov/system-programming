package com.sschudakov.automaton;

import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AutomatonMinimizer {
    public Automaton minimize(Automaton automaton) {
        Automaton copy = Util.copyAutomaton(automaton);
        removeUnreachableStates(copy);
        removeDeadEndStates(copy);
        Set<Set<AutomatonState>> equivalentStates = equivalentStates(copy);
        mergeEquivalentStates(copy.getAutomatonGraph(), equivalentStates);
        return copy;
    }

    private void removeUnreachableStates(Automaton automaton) {
        DirectedPseudograph<AutomatonState, AutomatonEdge> pseudograph = automaton.getAutomatonGraph();

        DepthFirstIterator<AutomatonState, AutomatonEdge> iterator = new DepthFirstIterator<>(pseudograph,
                automaton.getInitialState());

        Set<AutomatonState> reachableStates = new HashSet<>();
        while (iterator.hasNext()) {
            reachableStates.add(iterator.next());
        }

        Set<AutomatonState> unreachableState = new HashSet<>(pseudograph.vertexSet());
        unreachableState.removeAll(reachableStates);

        for (AutomatonState state : unreachableState) {
            pseudograph.removeVertex(state);
        }
    }

    private void removeDeadEndStates(Automaton automaton) {
        DirectedPseudograph<AutomatonState, AutomatonEdge> pseudograph = automaton.getAutomatonGraph();

        EdgeReversedGraph<AutomatonState, AutomatonEdge> reversedGraph =
                new EdgeReversedGraph<>(pseudograph);

        DepthFirstIterator<AutomatonState, AutomatonEdge> iterator =
                new DepthFirstIterator<>(reversedGraph, automaton.finalStates());

        Set<AutomatonState> nonDeadEndStates = new HashSet<>();
        while (iterator.hasNext()) {
            nonDeadEndStates.add(iterator.next());
        }

        Set<AutomatonState> deadEndStates = new HashSet<>(pseudograph.vertexSet());
        deadEndStates.removeAll(nonDeadEndStates);

        for (AutomatonState state : deadEndStates) {
            pseudograph.removeVertex(state);
        }
    }

    private Set<Set<AutomatonState>> equivalentStates(Automaton automaton) {
        Set<String> alphabet = automaton.alphabet();

        Set<AutomatonState> finalStates = automaton.finalStates();
        Set<AutomatonState> notFinalStates = automaton.notFinalStates();

        Set<Set<AutomatonState>> partitions = new HashSet<>();
        partitions.add(finalStates);
        partitions.add(notFinalStates);

        List<Pair<Set<AutomatonState>, String>> statesSymbolPairs = new ArrayList<>();

        for (String s : alphabet) {
            statesSymbolPairs.add(Pair.of(finalStates, s));
            statesSymbolPairs.add(Pair.of(notFinalStates, s));
        }
        while (!statesSymbolPairs.isEmpty()) {
            Pair<Set<AutomatonState>, String> stateSymbolPair = statesSymbolPairs.remove(statesSymbolPairs.size() - 1);
            Set<AutomatonState> c = stateSymbolPair.getFirst();
            String a = stateSymbolPair.getSecond();

            Set<Set<AutomatonState>> partitionsToBeAdded = new HashSet<>();
            Set<Set<AutomatonState>> partitionsToBeRemoved = new HashSet<>();

            for (Set<AutomatonState> partition : partitions) {
                Pair<Set<AutomatonState>, Set<AutomatonState>> split = split(
                        automaton.getAutomatonGraph(),
                        partition,
                        stateSymbolPair.getFirst(),
                        stateSymbolPair.getSecond());

                Set<AutomatonState> firstSplit = split.getFirst();
                Set<AutomatonState> secondSplit = split.getSecond();
                if (!(firstSplit.isEmpty() || secondSplit.isEmpty())) {
                    partitionsToBeRemoved.add(partition);
                    partitionsToBeAdded.add(firstSplit);
                    partitionsToBeAdded.add(secondSplit);

                    Pair<Set<AutomatonState>, String> partitionPair = Pair.of(partition, stateSymbolPair.getSecond());
                    if (statesSymbolPairs.contains(partitionPair)) {
                        statesSymbolPairs.remove(partitionPair);
                        statesSymbolPairs.add(Pair.of(firstSplit, a));
                        statesSymbolPairs.add(Pair.of(secondSplit, a));
                    } else {
                        if (firstSplit.size() > secondSplit.size()) {
                            statesSymbolPairs.add(Pair.of(firstSplit, a));
                        } else {
                            statesSymbolPairs.add(Pair.of(secondSplit, a));
                        }
                    }
                }
            }
            partitions.removeAll(partitionsToBeRemoved);
            partitions.addAll(partitionsToBeAdded);
        }
        System.out.println("partitions: " + partitions);
        return partitions;
    }

    private Pair<Set<AutomatonState>, Set<AutomatonState>> split(DirectedPseudograph<AutomatonState, AutomatonEdge> graph,
                                                                 Set<AutomatonState> r, Set<AutomatonState> c, String a) {
        Set<AutomatonState> withTransitionToC = new HashSet<>();
        Set<AutomatonState> noTransitionToC = new HashSet<>();

        for (AutomatonState state : r) {
            Set<AutomatonEdge> transitions = graph.outgoingEdgesOf(state);
            AutomatonEdge edge = null;
            for (AutomatonEdge transition : transitions) {
                if (transition.getSymbol().equals(a)) {
                    edge = transition;
                }
            }
            if (edge != null && c.contains(graph.getEdgeTarget(edge))) {
                withTransitionToC.add(state);
            } else {
                noTransitionToC.add(state);
            }
        }
        return Pair.of(withTransitionToC, noTransitionToC);
    }

    private void mergeEquivalentStates(DirectedPseudograph<AutomatonState, AutomatonEdge> graph,
                                       Set<Set<AutomatonState>> equivalentStates) {
        for (Set<AutomatonState> equivalentState : equivalentStates) {
            if (equivalentState.size() > 1) {
                mergeStates(graph, equivalentState);
            }
        }
    }

    private void mergeStates(DirectedPseudograph<AutomatonState, AutomatonEdge> graph,
                             Set<AutomatonState> states) {
        Set<Pair<AutomatonState, String>> incomingTransition = new HashSet<>();
        Set<Pair<AutomatonState, String>> outgoingTransitions = new HashSet<>();
        Set<Pair<AutomatonState, String>> outgoingInternalTransitions = new HashSet<>();

        for (AutomatonState state : states) {
            for (AutomatonEdge edge : graph.incomingEdgesOf(state)) {
                if (!states.contains(graph.getEdgeSource(edge))) {
                    incomingTransition.add(Pair.of(graph.getEdgeSource(edge),
                            edge.getSymbol()));
                }
            }
            for (AutomatonEdge edge : graph.outgoingEdgesOf(state)) {
                Pair<AutomatonState, String> transition = Pair.of(graph.getEdgeTarget(edge), edge.getSymbol());
                if (!states.contains(transition.getFirst())) {
                    outgoingTransitions.add(transition);
                } else {
                    outgoingInternalTransitions.add(transition);
                }
            }
        }

        int mergedStateIndex = graph.vertexSet().stream().mapToInt(AutomatonState::getIndex).max().getAsInt() + 1;
        boolean mergedStateIsFinal = states.stream().allMatch(AutomatonState::isFinalState);
        for (AutomatonState state : states) {
            graph.removeVertex(state);
        }
        AutomatonState mergedState = new AutomatonState(mergedStateIndex, mergedStateIsFinal);

        graph.addVertex(mergedState);

        for (Pair<AutomatonState, String> transition : incomingTransition) {
            graph.addEdge(transition.getFirst(), mergedState, new AutomatonEdge(transition.getSecond()));
        }
        for (Pair<AutomatonState, String> transition : outgoingTransitions) {
            graph.addEdge(mergedState, transition.getFirst(), new AutomatonEdge(transition.getSecond()));
        }
        for (Pair<AutomatonState, String> transition : outgoingInternalTransitions) {
            graph.addEdge(mergedState, mergedState, new AutomatonEdge(transition.getSecond()));
        }
    }
}

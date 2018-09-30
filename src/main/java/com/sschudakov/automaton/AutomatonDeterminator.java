package com.sschudakov.automaton;

import org.jgrapht.graph.DirectedPseudograph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AutomatonDeterminator {
    private static String LAMBDA = "";

    public Automaton determinate(Automaton automaton) {
        if (isDeterministic(automaton)) {
            return automaton;
        } else {
            Automaton copy = Util.copyAutomaton(automaton);
            removeLambdaTransition(copy);
            return deterministicAutomaton(copy);
        }
    }

    private boolean isDeterministic(Automaton automaton) {
        DirectedPseudograph<AutomatonState, AutomatonEdge> automatonGraph = automaton.getAutomatonGraph();
        for (AutomatonState state : automatonGraph.vertexSet()) {
            Map<String, Long> grouped = automatonGraph.outgoingEdgesOf(state).stream().collect(Collectors.groupingBy(AutomatonEdge::getSymbol, Collectors.counting()));
            if (!grouped.values().stream().filter(value -> value > 1).collect(Collectors.toList()).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private Automaton deterministicAutomaton(Automaton automaton) {
        Set<Set<AutomatonState>> subsets = allSubsets(automaton.getAutomatonGraph().vertexSet().toArray());

        Set<AutomatonState> finalStates = automaton.finalStates();

        Map<Set<AutomatonState>, AutomatonState> subsetToStateMap = new HashMap<>();
        DirectedPseudograph<AutomatonState, AutomatonEdge> deterministicAutomatonGraph =
                new DirectedPseudograph<>(AutomatonEdge.class);

        int i = 0;
        for (Set<AutomatonState> subset : subsets) {
            boolean isFinalState = !(intersection(subset, finalStates).isEmpty());
            AutomatonState state = new AutomatonState(i++, isFinalState);
            deterministicAutomatonGraph.addVertex(state);
            subsetToStateMap.put(subset, state);
        }

        for (Set<AutomatonState> subset : subsets) {
            Map<String, Set<AutomatonState>> symbolsSubsets = new HashMap<>();
            for (AutomatonState state : subset) {
                for (AutomatonEdge automatonEdge : automaton.getAutomatonGraph().outgoingEdgesOf(state)) {
                    symbolsSubsets.putIfAbsent(automatonEdge.getSymbol(), new HashSet<>());
                    symbolsSubsets.get(automatonEdge.getSymbol())
                            .add(automaton.getAutomatonGraph().getEdgeTarget(automatonEdge));
                }
            }
            for (Map.Entry<String, Set<AutomatonState>> entry : symbolsSubsets.entrySet()) {
                deterministicAutomatonGraph.addEdge(subsetToStateMap.get(subset),
                        subsetToStateMap.get(entry.getValue()), new AutomatonEdge(entry.getKey()));
            }
        }
        Set<AutomatonState> initialStateSubset = new HashSet<>();
        initialStateSubset.add(automaton.getInitialState());
        return new Automaton(deterministicAutomatonGraph, subsetToStateMap.get(initialStateSubset));
    }

    private Set<AutomatonState> intersection(Set<AutomatonState> a, Set<AutomatonState> b) {
        if (a.size() > b.size()) {
            return intersection(b, a);
        }
        Set<AutomatonState> results = new HashSet<>();
        for (AutomatonState element : a) {
            if (b.contains(element)) {
                results.add(element);
            }
        }
        return results;
    }

    private static Set<Set<AutomatonState>> allSubsets(Object[] states) {
        int n = states.length;

        Set<Set<AutomatonState>> subsets = new HashSet<>();
        for (int i = 0; i < (1 << n); i++) {
            Set<AutomatonState> currentSubset = new HashSet<>();
            for (int j = 0; j < n; j++) {
                if ((i & (1 << j)) > 0) {
                    currentSubset.add((AutomatonState) states[j]);
                }
            }
            subsets.add(currentSubset);
        }
        return subsets;
    }

    private void removeLambdaTransition(Automaton automaton) {
        DirectedPseudograph<AutomatonState, AutomatonEdge> automatonGraph = automaton.getAutomatonGraph();
        Set<AutomatonEdge> lambdaTransitions = automatonGraph.edgeSet().stream()
                .filter(a -> a.getSymbol().equals(LAMBDA)).collect(Collectors.toSet());

        Set<AutomatonState> statesTobeRemoved = lambdaTransitions.stream().map(automatonGraph::getEdgeTarget).collect(Collectors.toSet())
                .stream().filter(target -> hasOnlyLambdaTransition(automatonGraph, target)).collect(Collectors.toSet());

        boolean lambdaTransitionRelaxed = true;
        while (lambdaTransitionRelaxed) {
            lambdaTransitionRelaxed = false;
            for (AutomatonEdge lambdaTransition : lambdaTransitions) {
                lambdaTransitionRelaxed = relaxLambdaTransition(automatonGraph, lambdaTransition);
            }
        }
        for (AutomatonEdge lambdaTransition : lambdaTransitions) {
            automatonGraph.removeEdge(lambdaTransition);
        }
        if (statesTobeRemoved.contains(automaton.getInitialState())) {
            throw new IllegalArgumentException("Initial state of the automaton has only lambda incoming transition");
        }
        for (AutomatonState state : statesTobeRemoved) {
            automatonGraph.removeVertex(state);
        }
    }

    private boolean relaxLambdaTransition(DirectedPseudograph<AutomatonState, AutomatonEdge> automatonGraph,
                                          AutomatonEdge transition) {
        boolean result = false;

        AutomatonState sourceState = automatonGraph.getEdgeSource(transition);
        AutomatonState targetState = automatonGraph.getEdgeTarget(transition);
        Set<AutomatonEdge> outgoingEdges = new HashSet<>(automatonGraph.outgoingEdgesOf(targetState));

        for (AutomatonEdge outgoingEdge : outgoingEdges) {

            String symbol = outgoingEdge.getSymbol();
            AutomatonState outgoingEdgeTarget = automatonGraph.getEdgeTarget(outgoingEdge);

            Set<AutomatonEdge> sourceOutgoingTargetEdges = automatonGraph.getAllEdges(sourceState, outgoingEdgeTarget);
            Set<String> sourceOutgoingTargetEdgesSymbols = sourceOutgoingTargetEdges.stream()
                    .map(AutomatonEdge::getSymbol).collect(Collectors.toSet());

            if (!sourceOutgoingTargetEdgesSymbols.contains(symbol)) {
                result = true;

                automatonGraph.addEdge(sourceState, outgoingEdgeTarget, new AutomatonEdge(outgoingEdge.getSymbol()));

                if (targetState.isFinalState()) {
                    sourceState.setFinalState(true);
                }
            }
        }
        return result;
    }


    private boolean hasOnlyLambdaTransition(DirectedPseudograph<AutomatonState, AutomatonEdge> automatonGraph, AutomatonState state) {
        Set<String> symbols = automatonGraph.incomingEdgesOf(state).stream().map(AutomatonEdge::getSymbol).collect(Collectors.toSet());
        return symbols.size() == 1 && symbols.contains(LAMBDA);
    }
}

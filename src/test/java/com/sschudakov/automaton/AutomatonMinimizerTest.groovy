package com.sschudakov.automaton

import com.sschudakov.automaton.algorithm.AutomatonMinimizer
import com.sschudakov.automaton.io.CSVAutomatonImporter
import spock.lang.Specification

class AutomatonMinimizerTest extends Specification {
    private AutomatonMinimizer minimizer = new AutomatonMinimizer()
    private CSVAutomatonImporter importer = new CSVAutomatonImporter()

    def "test minimize"() {
        given:
        Automaton automaton = importer.importAutomaton(
                "C:\\Users\\Semen\\D\\workspace.java\\system-programming\\src\\main\\resources\\automaton\\minimization_test_1.txt")
        println "imported: ${automaton}"
        when:
        Automaton minimized = minimizer.minimize(automaton)
        println "minimized: ${minimized}"
        then:
        notThrown(Exception)
    }
}

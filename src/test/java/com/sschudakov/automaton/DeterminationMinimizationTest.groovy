package com.sschudakov.automaton

import com.sschudakov.automaton.algorithm.AutomatonDeterminator
import com.sschudakov.automaton.algorithm.AutomatonMinimizer
import com.sschudakov.automaton.io.CSVAutomatonImporter
import spock.lang.Specification

class DeterminationMinimizationTest extends Specification {
    private AutomatonDeterminator determinator = new AutomatonDeterminator()
    private AutomatonMinimizer minimizer = new AutomatonMinimizer()
    private CSVAutomatonImporter importer = new CSVAutomatonImporter()

    def "test 1"() {
        given:
        Automaton automaton = importer.importAutomaton(
                "C:\\Users\\Semen\\D\\workspace.java\\system-programming\\src\\main\\resources\\automaton\\det_min_1.txt")
        println "imported: ${automaton}"
        when:
        Automaton determined = determinator.determinate(automaton)
        println "determined: ${determined}"
        Automaton minimized = minimizer.minimize(determined)
        println "minimized: ${minimized}"
        then:
        notThrown(Exception)
    }
}

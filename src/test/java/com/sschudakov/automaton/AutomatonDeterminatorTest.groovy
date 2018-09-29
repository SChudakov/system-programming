package com.sschudakov.automaton

import spock.lang.Specification

class AutomatonDeterminatorTest extends Specification {

    private AutomatonDeterminator determinator = new AutomatonDeterminator()
    private AutomatonImporter importer = new CSVAutomatonImporter()

    def "test determinate"() {
        given:
        Automaton automaton = importer.importAutomaton(
                "C:\\Users\\Semen\\D\\workspace.java\\system-programming\\src\\main\\resources\\automaton\\determinization_test_1.txt")
        println "imported: ${automaton}"
        when:
        Automaton determinized = determinator.determinate(automaton)
        println determinized
        then:
        notThrown(Exception)
    }
}

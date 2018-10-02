package com.sschudakov.automaton

import spock.lang.Specification

class AlgorithmTest extends Specification {
    CSVAutomatonImporter importer = new CSVAutomatonImporter();
    Algorithm algorithm = new Algorithm();

    def "test execute 1"() {
        when:
        Automaton automaton = importer.importAutomaton("C:\\Users\\Semen\\D\\workspace.java\\system-programming\\src\\main\\resources\\automaton\\test_algorithm_1.txt")
        then:
        !algorithm.execute(automaton)
    }

    def "test execute 2"() {
        when:
        Automaton automaton = importer.importAutomaton("C:\\Users\\Semen\\D\\workspace.java\\system-programming\\src\\main\\resources\\automaton\\test_algorithm_2.txt")
        then:
        algorithm.execute(automaton)
    }

    def "test execute 3"() {
        when:
        Automaton automaton = importer.importAutomaton("C:\\Users\\Semen\\D\\workspace.java\\system-programming\\src\\main\\resources\\automaton\\test_algorithm_3.txt")
        then:
        !algorithm.execute(automaton)
    }

    def "test execute 4"() {
        when:
        Automaton automaton = importer.importAutomaton("C:\\Users\\Semen\\D\\workspace.java\\system-programming\\src\\main\\resources\\automaton\\test_algorithm_4.txt")
        then:
        algorithm.execute(automaton)
    }

    def "test execute 5"() {
        when:
        Automaton automaton = importer.importAutomaton("C:\\Users\\Semen\\D\\workspace.java\\system-programming\\src\\main\\resources\\automaton\\test_algorithm_5.txt")
        then:
        !algorithm.execute(automaton)
    }

    def "test execute 6"() {
        when:
        Automaton automaton = importer.importAutomaton("C:\\Users\\Semen\\D\\workspace.java\\system-programming\\src\\main\\resources\\automaton\\test_algorithm_6.txt")
        then:
        algorithm.execute(automaton)
    }

    def "test execute 7"() {
        when:
        Automaton automaton = importer.importAutomaton("C:\\Users\\Semen\\D\\workspace.java\\system-programming\\src\\main\\resources\\automaton\\test_algorithm_7.txt")
        then:
        !algorithm.execute(automaton)
    }

    def "test execute 8"() {
        when:
        Automaton automaton = importer.importAutomaton("C:\\Users\\Semen\\D\\workspace.java\\system-programming\\src\\main\\resources\\automaton\\test_algorithm_8.txt")
        then:
        algorithm.execute(automaton)
    }

    def "test execute 9"() {
        when:
        Automaton automaton = importer.importAutomaton("C:\\Users\\Semen\\D\\workspace.java\\system-programming\\src\\main\\resources\\automaton\\test_algorithm_9.txt")
        then:
        algorithm.execute(automaton)
    }
}

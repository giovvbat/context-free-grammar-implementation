package model;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class Grammar {
    private Set<Variable> variables;
    private final Set<AlphabetSymbol> alphabet;
    private final Variable start;
    private Rules rules;

    public Grammar(Set<Variable> variables, Set<AlphabetSymbol> alphabet, Variable start, Rules rules) {
        this.variables = variables;
        this.alphabet = alphabet;
        this.start = start;
        this.rules = rules;

        verifyStructure();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("VARIABLES: {");

        for (Variable variable : variables) {
            sb.append(" ").append(variable);
        }

        sb.append(" }\n").append("ALPHABET: {");

        for (AlphabetSymbol symbol : alphabet) {
            if (symbol.getValue().isEmpty()) {
                continue;
            }

            sb.append(" ").append(symbol);
        }

        sb.append(" }\n").append("STARTING VARIABLE: ").append(start).append("\n");
        sb.append("PRODUCTION RULES:\n").append(rules.getFormattedRules(start)).append("\n");

        return sb.toString();
    }

    private void verifyStructure() {
        verifySymbols(variables, alphabet);
        verifyStart(variables, start);
        verifyRules(variables, alphabet, rules);
    }

    // verifies if all variables and alphabet symbols have (different) representations
    private void verifySymbols(Set<Variable> variables, Set<AlphabetSymbol> alphabet) {
        if (variables == null || variables.isEmpty()) {
            throw new IllegalArgumentException("ERROR: grammar variables set must not be empty!");
        }

        if (alphabet == null || alphabet.isEmpty()) {
            throw new IllegalArgumentException("ERROR: grammar alphabet set must not be empty!");
        }

        for (Variable variable : variables) {
            for (AlphabetSymbol symbol : alphabet) {
                if (variable.getValue().equals(symbol.getValue())) {
                    throw new IllegalArgumentException("ERROR: variables and alphabet symbols must all have different representations!");
                }
            }
        }
    }

    // verifies if starting variable exists and belongs to grammar variables' set
    private void verifyStart(Set<Variable> variables, Variable startingVariable) {
        if (startingVariable == null) {
            throw new IllegalArgumentException("ERROR: grammar starting variable must not be null!");
        }

        if (!variables.contains(startingVariable)) {
            throw new IllegalArgumentException("ERROR: starting variable " + startingVariable + " does not exist in grammar!");
        }
    }

    // verifies some if production rules follow some logic principles
    private void verifyRules(Set<Variable> variables, Set<AlphabetSymbol> alphabet, Rules rules) {
        boolean foundAlphabetSymbol = false;
        List<Variable> alreadyUsedVariables = new ArrayList<>();

        if (rules == null || rules.getValue() == null) {
            throw new IllegalArgumentException("ERROR: grammar production rules must not be null!");
        }

        if (!rules.getValue().containsKey(start)) {
            throw new IllegalArgumentException("ERROR: starting variable must present at least one production rule!");
        }

        for (Map.Entry<Variable, List<List<GrammarSymbol>>> entry : rules.getValue().entrySet()) {
            if (!variables.contains(entry.getKey())) {
                throw new IllegalArgumentException("ERROR: variable " + entry.getKey() + " does not exist in grammar!");
            }

            for (List<GrammarSymbol> symbols : entry.getValue()) {
                if (symbols.isEmpty()) {
                    throw new IllegalArgumentException("ERROR: production rule " + entry.getKey() + " must not be empty!");
                }

                for (GrammarSymbol symbol : symbols) {
                    if (symbol instanceof AlphabetSymbol) {
                        foundAlphabetSymbol = true;
                    } else if (symbol instanceof Variable) {
                        alreadyUsedVariables.add((Variable) symbol);
                    }
                }
            }
        }

        if (!foundAlphabetSymbol) {
            throw new IllegalArgumentException("ERROR: no production rule in grammar contains non terminal alphabet symbols!");
        }

        for (Variable symbol : alreadyUsedVariables) {
            if (rules.getRulesByLeft(symbol).isEmpty()) {
                throw new IllegalArgumentException("ERROR: production rule for variable " + symbol + " is not present in grammar!");
            }
        }

        for (Variable variable : variables) {
            if (!rules.getValue().containsKey(variable)) {
                rules.getValue().put(variable, new ArrayList<>());
            }
        }
    }
}

package model;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class Grammar {
    private Set<Variable> variables;
    private Set<AlphabetSymbol> alphabet;
    private Variable start;
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
        sb.append("PRODUCTION RULES:\n").append(rules).append("\n");

        return sb.toString();
    }

    private void verifyStructure() {
        verifySymbols(variables, alphabet);
        verifyStart(variables, start);
        verifyRules(variables, alphabet, rules);
    }

    // verifies if variables and alphabet symbols have all different representations
    private void verifySymbols(Set<Variable> variables, Set<AlphabetSymbol> alphabet) {
        for (Variable variable : variables) {
            for (AlphabetSymbol symbol : alphabet) {
                if (variable.getValue().equals(symbol.getValue())) {
                    throw new IllegalArgumentException("variables and alphabet symbols must all have different representations!");
                }
            }
        }
    }

    // verifies if starting variable belongs to grammar variables' set
    private void verifyStart(Set<Variable> variables, Variable startingVariable) {
        if (!variables.contains(startingVariable)) {
            throw new IllegalArgumentException("variable " + startingVariable + " does not exist in grammar!");
        }
    }

    // verifies some if production rules follow some logic principles
    private void verifyRules(Set<Variable> variables, Set<AlphabetSymbol> alphabet, Rules rules) {
        boolean foundAlphabetSymbol = false;
        List<GrammarSymbol> alreadyUsedVariables = new ArrayList<>();

        if (rules == null || rules.getValue() == null) {
            throw new IllegalArgumentException("production rules cannot be null!");
        }

        if (!rules.getValue().containsKey(start)) {
            throw new IllegalArgumentException("production rules must contain starting variable!");
        }

        for (Map.Entry<Variable, Set<List<GrammarSymbol>>> entry : rules.getValue().entrySet()) {
            if (!variables.contains(entry.getKey())) {
                throw new IllegalArgumentException("variable " + entry.getKey() + " does not exist in grammar!");
            }

            for (List<GrammarSymbol> symbols : entry.getValue()) {
                if (symbols.isEmpty()) {
                    throw new IllegalArgumentException("production rule " + entry.getKey() + " cannot be empty!");
                }

                if (alreadyUsedVariables.contains(new AlphabetSymbol("")) && symbols.size() != 1) {
                    throw new IllegalArgumentException("lambda production rules cannot contain more than one symbol!");
                }

                for (GrammarSymbol symbol : symbols) {
                    if (symbol instanceof AlphabetSymbol) {
                        foundAlphabetSymbol = true;

                        if (!alphabet.contains(symbol)) {
                            throw new IllegalArgumentException("alphabet symbol " + symbol + " does not exist in grammar!");
                        }
                    } else if (symbol instanceof Variable) {
                        alreadyUsedVariables.add(symbol);

                        if (!variables.contains(symbol)) {
                            throw new IllegalArgumentException("variable " + symbol + " does not exist in grammar!");
                        }
                    }
                }
            }
        }

        if (!foundAlphabetSymbol) {
            throw new IllegalArgumentException("no production rule in grammar contains non terminal alphabet symbols!");
        }

        for (GrammarSymbol symbol : alreadyUsedVariables) {
            if (!rules.getValue().containsKey((Variable) symbol)) {
                throw new IllegalArgumentException("production rule for variable " + symbol + " is not present in grammar!");
            }
        }

        for (Variable variable : variables) {
            if (!rules.getValue().containsKey(variable)) {
                rules.getValue().put(variable, new HashSet<>());
            }
        }
    }
}

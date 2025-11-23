package grammar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Grammar {
    private final Set<Variable> variables;
    private final Set<AlphabetSymbol> alphabetSymbols;
    private final Variable startingVariable;
    private final ProductionRules productionRules;

    public Grammar(Set<Variable> variables, Set<AlphabetSymbol> alphabetSymbols, Variable startingVariable, ProductionRules productionRules) {
        this.variables = variables;
        this.alphabetSymbols = alphabetSymbols;
        this.startingVariable = startingVariable;
        this.productionRules = productionRules;

        verifyGrammarStructure();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("VARIABLES: {");

        for (Variable variable : variables) {
            sb.append(" ").append(variable);
        }

        sb.append(" }\n").append("ALPHABET: {");

        for (AlphabetSymbol alphabetSymbol : alphabetSymbols) {
            sb.append(" ").append(alphabetSymbol);
        }

        sb.append(" }\n").append("STARTING VARIABLE: ").append(startingVariable).append("\n");
        sb.append("PRODUCTION RULES:\n").append(productionRules).append("\n");

        return sb.toString();
    }

    private void verifyGrammarStructure() {
        verifyGrammarSymbols(variables, alphabetSymbols);
        verifyStartingVariable(variables, startingVariable);
        verifyProductionRules(variables, alphabetSymbols, productionRules);
    }

    private void verifyGrammarSymbols(Set<Variable> variables, Set<AlphabetSymbol> alphabetSymbols) {
        for (Variable variable : variables) {
            for (AlphabetSymbol alphabetSymbol : alphabetSymbols) {
                if (variable.getGrammarSymbol().equals(alphabetSymbol.getGrammarSymbol())) {
                    throw new IllegalArgumentException("variables and alphabet symbols must all have different representations!");
                }
            }
        }
    }

    private void verifyStartingVariable(Set<Variable> variables, Variable startingVariable) {
        if (!variables.contains(startingVariable)) {
            throw new IllegalArgumentException("variable " + startingVariable + " does not exist in grammar!");
        }
    }

    private void verifyProductionRules(Set<Variable> variables, Set<AlphabetSymbol> alphabetSymbols, ProductionRules productionRules) {
        boolean foundAlphabetSymbol = false;
        List<GrammarSymbol> alreadyUsedVariables = new ArrayList<>();

        if (productionRules == null) {
            throw new IllegalArgumentException("production rules cannot be null!");
        }

        for (Map.Entry<Variable, Set<List<GrammarSymbol>>> entry : productionRules.productionRules().entrySet()) {
            if (!variables.contains(entry.getKey())) {
                throw new IllegalArgumentException("variable " + entry.getKey() + " does not exist in grammar!");
            }

            if (entry.getValue().isEmpty()) {
                throw new IllegalArgumentException("production rule " + entry.getKey() + " cannot be empty!");
            }

            for (List<GrammarSymbol> grammarSymbols : entry.getValue()) {
                for (GrammarSymbol grammarSymbol : grammarSymbols) {
                    if (grammarSymbol instanceof AlphabetSymbol) {
                        foundAlphabetSymbol = true;

                        if (!alphabetSymbols.contains(grammarSymbol)) {
                            throw new IllegalArgumentException("alphabet symbol " + grammarSymbol + " does not exist in grammar!");
                        }
                    } else if (grammarSymbol instanceof Variable) {
                        alreadyUsedVariables.add(grammarSymbol);

                        if (!variables.contains(grammarSymbol)) {
                            throw new IllegalArgumentException("variable " + grammarSymbol + " does not exist in grammar!");
                        }
                    }
                }
            }
        }

        if (!foundAlphabetSymbol) {
            throw new IllegalArgumentException("no production rule in grammar contains non terminal alphabet symbols!");
        }

        for (GrammarSymbol grammarSymbol : alreadyUsedVariables) {
            if (!productionRules.productionRules().containsKey((Variable) grammarSymbol)) {
                throw new IllegalArgumentException("production rule " + grammarSymbol + " is not valid in grammar!");
            }
        }
    }
}

package service;

import model.AlphabetSymbol;
import model.Grammar;

import model.GrammarSymbol;
import model.Variable;

import java.util.*;

public class GrammarService {

    public static void removeLeftRecursion(Grammar grammar){
        List<Variable> orderedVariables = new ArrayList<>(grammar.getVariables());

        orderedVariables.sort((v1, v2) -> {
            if(v1.equals(grammar.getStart())) {
                return -1;
            }
            if(v2.equals(grammar.getStart())) {
                return 1;
            }
            return v1.getValue().compareTo(v2.getValue());
        });

        // Paull's algorithm to remove left recursion
        for(int i = 0; i< orderedVariables.size(); i++){
            Variable Ai = orderedVariables.get(i);
            for(int j = 0; j<i; j++){
                Variable Aj = orderedVariables.get(j);
                replaceProductions(grammar, Ai, Aj);
                }
            removeDirectLeftRecursion(grammar, Ai);
        }
    }
    // replaces Ai -> Aj... with rules from Aj
    private static void replaceProductions(Grammar grammar, Variable Ai, Variable Aj) {
        List<List<GrammarSymbol>> newRules = new ArrayList<>();
        List<List<GrammarSymbol>> rulesAj = grammar.getRules().getRulesByLeft(Aj);
        boolean changed = false;

        for (List<GrammarSymbol> ruleAi : grammar.getRules().getRulesByLeft(Ai)) {
            if (!ruleAi.isEmpty() && ruleAi.getFirst().equals(Aj)) {
                changed = true;
                List<GrammarSymbol> rest = ruleAi.subList(1, ruleAi.size());

                for (List<GrammarSymbol> ruleAj : rulesAj) {
                    List<GrammarSymbol> combined = new ArrayList<>(ruleAj);

                    // Aj -> &
                    if (combined.size() == 1 && combined.getFirst() instanceof AlphabetSymbol && combined.getFirst().getValue().isEmpty()) {
                        combined.clear();
                    }

                    combined.addAll(rest);

                    if (combined.isEmpty()) {
                        combined.add(new AlphabetSymbol(""));
                    }

                    newRules.add(combined);
                }
            } else {
                newRules.add(ruleAi);
            }
        }
        if (changed) {
            grammar.getRules().getValue().put(Ai, newRules);
        }
    }
    private static void removeDirectLeftRecursion(Grammar grammar, Variable variable) {

        List<List<GrammarSymbol>> recursive = new ArrayList<>();
        List<List<GrammarSymbol>> nonRecursive = new ArrayList<>();

        List<List<GrammarSymbol>> rules = grammar.getRules().getRulesByLeft(variable);

        if(rules == null) {
            return;
        }
        rules.removeIf(rule -> rule.size() == 1 && rule.getFirst().equals(variable));

        for(List<GrammarSymbol> rule : rules) {

            if(!rule.isEmpty() && rule.getFirst().equals(variable)) {
                recursive.add(rule);
            }else{
                nonRecursive.add(rule);
            }

        }
        if(!recursive.isEmpty()) {
            Variable current = Variable.nextRepresentationAvailable(grammar.getVariables());
            grammar.getVariables().add(current);
            grammar.getRules().getValue().put(current, new ArrayList<>());


            List<List<GrammarSymbol>> newRulesForVariable = new ArrayList<>();

            for(List<GrammarSymbol> rule : nonRecursive) {
                List<GrammarSymbol> newRule = new ArrayList<>(rule);

                if(newRule.size() == 1 && newRule.getFirst() instanceof AlphabetSymbol && newRule.getFirst().getValue().isEmpty()) {
                    newRule.clear();
                }
                newRule.add(current);
                newRulesForVariable.add(newRule);


                newRulesForVariable.add(new ArrayList<>(rule));
            }
            grammar.getRules().getValue().put(variable, newRulesForVariable);

            for(List<GrammarSymbol> rule : recursive) {
                List<GrammarSymbol> newRule = new ArrayList<>(rule.subList(1, rule.size()));
                newRule.removeFirst();

                List<GrammarSymbol> recursiveStep  = new ArrayList<>(newRule);

                recursiveStep.add(current);
                grammar.getRules().addRule(current, recursiveStep);

                grammar.getRules().addRule(current, rule);
            }
        }

    }

    public static void removeUselessVariables(Grammar grammar) {
        removeUnreachableVariables(grammar);
        removeNonGenerativeVariables(grammar);
        removeEmptyVariables(grammar);
    }

    public static void removeUnitaryRules(Grammar grammar) {
        for (Variable variable : grammar.getVariables()) {
            Set<Variable> reachable = computeUnitClosure(grammar, variable);
            List<List<GrammarSymbol>> added = new ArrayList<>();

            for (Variable reachableVariable : reachable) {
                for (List<GrammarSymbol> rule : grammar.getRules().getRulesByLeft(reachableVariable)) {
                    if (rule.size() > 1 || (rule.size() == 1 && rule.getFirst() instanceof AlphabetSymbol)) {
                        added.add(rule);
                    }
                }
            }

            for (List<GrammarSymbol> rule : added) {
                grammar.getRules().addRule(variable, rule);
            }
        }

        for (Map.Entry<Variable, List<List<GrammarSymbol>>> entry : grammar.getRules().getValue().entrySet()) {
            entry.getValue().removeIf(symbols -> symbols.size() == 1 && symbols.getFirst() instanceof Variable);
        }
    }

    public static void removeLambdaRules(Grammar grammar)  {
        Set<Variable> empty = new HashSet<>();
        boolean isEmptyWorded = isEmptyWorded(grammar);

        for (Variable variable : grammar.getVariables()) {
            for (List<GrammarSymbol> rule : grammar.getRules().getRulesByLeft(variable)) {
                if (rule.size() == 1 && rule.getFirst() instanceof AlphabetSymbol && rule.getFirst().getValue().isEmpty()) {
                    empty.add(variable);
                    grammar.getRules().getRulesByLeft(variable).remove(rule);

                    break;
                }
            }
        }

        for (Variable variable : empty) {
            for (Variable current : grammar.getVariables()) {
                suitLambdaRules(grammar, current, variable);
            }
        }

        if (isEmptyWorded) {
            List<GrammarSymbol> emptyRule = new ArrayList<>();
            emptyRule.add(new AlphabetSymbol(""));

            grammar.getRules().addRule(grammar.getStart(), emptyRule);
        }
    }

    private static void suitLambdaRules(Grammar grammar, Variable current, Variable variable) {
        List<List<GrammarSymbol>> added = new ArrayList<>();

        for (List<GrammarSymbol> rule : grammar.getRules().getRulesByLeft(current)) {
            for (GrammarSymbol symbol : rule) {
                if (symbol.getValue().equals(variable.getValue())) {
                    List<GrammarSymbol> newRule = new ArrayList<>(rule);
                    newRule.remove(symbol);

                    if (!newRule.isEmpty()) {
                        added.add(newRule);
                    }
                }
            }
        }

        for (List<GrammarSymbol> rule : added) {
            grammar.getRules().addRule(current, rule);
        }
    }

    public static void removeEmptyVariables(Grammar grammar) {
        Set<Variable> removed = new HashSet<>();
        List<List<GrammarSymbol>> rules = new ArrayList<>();

        while (hasEmptyVariables(grammar)) {
            removed.clear();

            for (Variable variable : grammar.getVariables()) {
                if (grammar.getRules().getRulesByLeft(variable).isEmpty()) {
                    removed.add(variable);
                    grammar.getRules().getValue().remove(variable);
                }
            }

            for (Variable variable : removed) {
                grammar.getVariables().remove(variable);
            }

            for (Variable empty : removed) {
                for (Variable variable : grammar.getVariables()) {
                    rules.clear();

                    for (List<GrammarSymbol> rule : grammar.getRules().getRulesByLeft(variable)) {
                        for (GrammarSymbol symbol : rule) {
                            if (symbol.equals(empty)) {
                                rules.add(rule);
                            }
                        }
                    }

                    for (List<GrammarSymbol> rule : rules) {
                        grammar.getRules().getRulesByLeft(variable).remove(rule);
                    }
                }
            }
        }

        if (grammar.getVariables().isEmpty()) {
            throw new RuntimeException("during removal of empty variables, grammar went variable-less");
        }
    }

    public static void removeUnreachableVariables(Grammar grammar) {
        Set<Variable> reachable = computeClosure(grammar, grammar.getStart());
        Queue<Variable> removed = new LinkedList<>();

        for (Variable variable : grammar.getVariables()) {
            if (!reachable.contains(variable)) {
                removed.add(variable);
            }
        }

        while (!removed.isEmpty()) {
            Variable variable = removed.poll();
            grammar.getRules().getRulesByLeft(variable).clear();
        }
    }

    public static void removeNonGenerativeVariables(Grammar grammar) {
        Set<Variable> generative = retrieveGenerativeVariables(grammar);
        Queue<Variable> removed = new LinkedList<>();

        if (!retrieveGenerativeVariables(grammar).contains(grammar.getStart())) {
            throw new RuntimeException("during removal non-generative variables, grammar went variable-less");
        }

        for (Variable variable : grammar.getVariables()) {
            if (!generative.contains(variable)) {
                removed.add(variable);
            }
        }

        for (Variable variable : removed) {
            grammar.getRules().getRulesByLeft(variable).clear();
        }
    }

    public static boolean hasInvalidLambdaRules(Grammar grammar) {
        for (Variable variable : grammar.getVariables()) {
            for (List<GrammarSymbol> rule : grammar.getRules().getRulesByLeft(variable)) {
                if (!(variable.equals(grammar.getStart())) && rule.size() == 1 && rule.getFirst() instanceof AlphabetSymbol && rule.getFirst().getValue().isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean hasUnitaryRules(Grammar grammar) {
        for (Variable variable : grammar.getVariables()) {
            for (List<GrammarSymbol> rule : grammar.getRules().getRulesByLeft(variable)) {
                if (rule.size() == 1 && rule.getFirst() instanceof Variable) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean hasUselessVariables(Grammar grammar) {
        return hasUnreachableVariables(grammar) || hasNonGenerativeVariables(grammar) || hasEmptyVariables(grammar);
    }

    private static boolean hasEmptyVariables(Grammar grammar) {
        for (Variable variable : grammar.getVariables()) {
            if (grammar.getRules().getRulesByLeft(variable).isEmpty()) {
                return true;
            }
        }

        return false;
    }

    private static boolean hasUnreachableVariables(Grammar grammar) {
        for (Variable variable : grammar.getVariables()) {
            if (!computeClosure(grammar, grammar.getStart()).contains(variable)) {
                return true;
            }
        }

        return false;
    }

    private static boolean hasNonGenerativeVariables(Grammar grammar) {
        Set<Variable> generative = retrieveGenerativeVariables(grammar);

        for (Variable variable : grammar.getVariables()) {
            if (!generative.contains(variable)) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasLeftRecursion(Grammar grammar) {
        for (Variable variable : grammar.getVariables()) {

            if(canReachSelf(grammar, variable, variable, new HashSet<>())) {
                return true;
            }
        }
        return false;
    }


    private static boolean canReachSelf(Grammar grammar, Variable current, Variable target, Set<Variable> visited) {

        visited.add(current);

        List<List<GrammarSymbol>> rules = grammar.getRules().getRulesByLeft(current);

        if (rules == null) {
            return false;
        }

        for (List<GrammarSymbol> rule : rules) {
            if(!rule.isEmpty() && rule.getFirst() instanceof Variable) {
                Variable next = (Variable) rule.getFirst();
                // find cycles
                if (next.equals(target)) {
                    return true;
                }
                //recursive search for cycles
                if(!visited.contains(next)) {
                    if (canReachSelf(grammar, next, target, visited)) {
                        return true;
                    }
                }

            }
            }
        return false;
        }



    private static Set<Variable> retrieveGenerativeVariables(Grammar grammar) {
        Set<Variable> generative = new HashSet<>();
        boolean hasVariable;
        boolean hasAdded;

        // identifies variables with at least one rule who contains no variables
        for (Variable variable : grammar.getVariables()) {
            for (List<GrammarSymbol> rule : grammar.getRules().getRulesByLeft(variable)) {
                hasVariable = false;

                for (GrammarSymbol symbol : rule) {
                    if (symbol instanceof Variable) {
                        hasVariable = true;
                        break;
                    }
                }

                if (!hasVariable) {
                    generative.add(variable);
                    break;
                }
            }
        }

        do {
            hasAdded = false;

            for (Variable variable : grammar.getVariables()) {
                if (!generative.contains(variable)) {
                    for (List<GrammarSymbol> rule : grammar.getRules().getRulesByLeft(variable)) {
                        boolean isRuleGenerative = true;

                        for (GrammarSymbol symbol : rule) {
                            if (symbol instanceof Variable && !generative.contains(symbol)) {
                                isRuleGenerative = false;
                                break;
                            }
                        }

                        if (isRuleGenerative) {
                            hasAdded = true;
                            generative.add(variable);
                            break;
                        }
                    }
                }
            }

        } while (hasAdded);

        return generative;
    }

    private static List<Variable> retrieveLeftRecursiveVariables(Grammar grammar) {
        List<Variable> recursive = new ArrayList<>();

        for (Variable variable : grammar.getVariables()) {
            for (List<GrammarSymbol> rule : grammar.getRules().getRulesByLeft(variable)) {
                if (variable.equals(rule.getFirst())) {
                    recursive.add(variable);
                }
            }
        }

        return recursive;
    }

    // verifies if grammar generates empty word
    private static boolean isEmptyWorded(Grammar grammar) {
        Set<Variable> remaining = new HashSet<>(grammar.getVariables());
        Set<Variable> removed = new HashSet<>();

        // identifies variables with rules such as -> &
        for (Variable variable : grammar.getVariables()) {
            for (List<GrammarSymbol> rule : grammar.getRules().getRulesByLeft(variable)) {
                if (rule.size() == 1 && rule.getFirst() instanceof AlphabetSymbol && rule.getFirst().getValue().isEmpty()) {
                    if (variable.equals(grammar.getStart())) {
                        return true;
                    }

                    remaining.remove(variable);
                }
            }
        }

        while (true) {
            for (Variable variable : remaining) {
                for (List<GrammarSymbol> rule : grammar.getRules().getRulesByLeft(variable)) {
                    boolean isRuleEmpty = true;

                    for (GrammarSymbol symbol : rule) {
                        if (symbol instanceof AlphabetSymbol || (symbol instanceof Variable && remaining.contains((Variable) symbol))) {
                            isRuleEmpty = false;
                            break;
                        }
                    }

                    if (isRuleEmpty) {
                        removed.add(variable);
                        break;
                    }
                }
            }

            if (!removed.isEmpty()) {
                if (removed.contains(grammar.getStart())) {
                    return true;
                }

                remaining.removeAll(removed);
                removed.clear();
            } else {
                break;
            }
        }

        return false;
    }

    private static Set<Variable> computeClosure(Grammar grammar, Variable variable) {
        Set<Variable> closure = new HashSet<>(Set.of(variable));
        Queue<Variable> queue = new LinkedList<>();
        queue.add(variable);

        while (!queue.isEmpty()) {
            Variable current = queue.poll();

            for (List<GrammarSymbol> rule : grammar.getRules().getRulesByLeft(current)) {
                for (GrammarSymbol symbol : rule) {
                    if (symbol instanceof Variable && !closure.contains(symbol)) {
                        closure.add((Variable) symbol);
                        queue.add((Variable) symbol);
                    }
                }
            }
        }

        return closure;
    }

    private static Set<Variable> computeUnitClosure(Grammar grammar, Variable variable) {
        Set<Variable> closure = new HashSet<>(Set.of(variable));
        Queue<Variable> queue = new LinkedList<>();
        queue.add(variable);

        while (!queue.isEmpty()) {
            Variable current = queue.poll();

            for (List<GrammarSymbol> rule : grammar.getRules().getRulesByLeft(current)) {
                if (rule.size() == 1 && rule.getFirst() instanceof Variable && !closure.contains((Variable) rule.getFirst())) {
                    closure.add((Variable) rule.getFirst());
                    queue.add((Variable) rule.getFirst());
                }
            }
        }

        return closure;
    }
    public static void convertTerminalsToVariables(Grammar grammar){
        Map<String, Variable> terminalToVariable = new HashMap<>();

        List<Variable> variables = new ArrayList<>(grammar.getVariables());

        for (Variable variable : variables) {
            for (List<GrammarSymbol> rule : grammar.getRules().getRulesByLeft(variable)) {
                if(rule.size()>=2){
                    for(int i = 0; i<rule.size();i++){
                        GrammarSymbol symbol = rule.get(i);

                        if(symbol instanceof AlphabetSymbol){
                            String val = symbol.getValue();
                            Variable replacement;

                            if (terminalToVariable.containsKey(val)) {
                                replacement = terminalToVariable.get(val);
                            } else{
                                replacement = Variable.nextRepresentationAvailable(grammar.getVariables());
                                grammar.getVariables().add(replacement);
                                grammar.getRules().getValue().put(replacement, new ArrayList<>());

                                List<GrammarSymbol> terminalRule = new ArrayList<>();
                                terminalRule.add(symbol);
                                grammar.getRules().addRule(replacement, terminalRule);
                                terminalToVariable.put(val, replacement);

                            }
                            rule.set(i, replacement);
                        }
                    }
                }
            }
        }
    }

    public static void breakLongProductions(Grammar grammar){
        boolean hasLongRules = true;

        // Cache system to avoid creating duplicate productions
        Map<List<GrammarSymbol>, Variable> productionCache = new HashMap<>();

        for (Map.Entry<Variable, List<List<GrammarSymbol>>> entry : grammar.getRules().getValue().entrySet()) {
            Variable var = entry.getKey();
            for (List<GrammarSymbol> rule : entry.getValue()) {
                if (!rule.isEmpty()) {
                    productionCache.putIfAbsent(new ArrayList<>(rule), var);
                }
            }
        }
        while (hasLongRules) {
            hasLongRules = false;
            List<Variable> variables = new ArrayList<>(grammar.getVariables());

            for(Variable variable : variables){
                List<List<GrammarSymbol>> rules = grammar.getRules().getRulesByLeft(variable);

                for (List<GrammarSymbol> rule : rules) {
                    if (rule.size() > 2) {
                        hasLongRules = true;
                        GrammarSymbol first = rule.getFirst();

                        List<GrammarSymbol> rest = new ArrayList<>(rule.subList(1, rule.size()));

                        Variable targetVar;

                        if (productionCache.containsKey(rest)) {
                            targetVar = productionCache.get(rest);
                        }else{
                            targetVar = Variable.nextRepresentationAvailable(grammar.getVariables());
                            grammar.getVariables().add(targetVar);
                            grammar.getRules().getValue().put(targetVar, new ArrayList<>());
                            grammar.getRules().addRule(targetVar, rest);

                            productionCache.put(rest, targetVar);
                        }
                        rule.clear();
                        rule.add(first);
                        rule.add(targetVar);

                        productionCache.put(new ArrayList<>(rule), variable);
                    }
                }
            }
        }
    }

    private static int getVariableIndex(Variable variable) {
        String variableValue = variable.getValue();
        return Integer.parseInt(variableValue);
    }

    private static boolean violatesOrder(Variable left, List<GrammarSymbol> rule) {
        GrammarSymbol first = rule.getFirst();
        if (!(first instanceof Variable variable)) {
            return false;
        }

        int i = getVariableIndex(left);
        int j = getVariableIndex(variable);

        return i >= j;
    }
}

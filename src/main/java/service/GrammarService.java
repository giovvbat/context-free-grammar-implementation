package service;

import model.AlphabetSymbol;
import model.Grammar;

import model.GrammarSymbol;
import model.Variable;

import java.util.*;

public class GrammarService {
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
            grammar.getVariables().remove(variable);
            grammar.getRules().getValue().remove(variable);
        }
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

            grammar.getRules().getRulesByLeft(variable).addAll(added);
        }

        for (Map.Entry<Variable, Set<List<GrammarSymbol>>> entry : grammar.getRules().getValue().entrySet()) {
            entry.getValue().removeIf(symbols -> symbols.size() == 1 && symbols.getFirst() instanceof Variable);
        }
    }

    public static void removeEmptyRules(Grammar grammar) {
        Set<Variable> empty = new HashSet<>();
        Set<List<GrammarSymbol>> removed = new HashSet<>();
        boolean isEmptyWorded = isEmptyWorded(grammar);

        for (Variable variable : grammar.getVariables()) {
            for (List<GrammarSymbol> rule : grammar.getRules().getRulesByLeft(variable)) {
                if (rule.size() == 1 && rule.getFirst() instanceof AlphabetSymbol && rule.getFirst().getValue().isEmpty()) {
                    empty.add(variable);
                    removed.add(rule);
                }
            }

            for (List<GrammarSymbol> rule : removed) {
                grammar.getRules().getRulesByLeft(variable).remove(rule);
            }

            removed.clear();
        }

        for (Variable variable : empty) {
            for (Variable current : grammar.getVariables()) {
                adaptEmptyRules(grammar, grammar.getRules().getRulesByLeft(current), variable);
            }
        }

        if (isEmptyWorded) {
            List<GrammarSymbol> emptyRule = new ArrayList<>();
            emptyRule.add(new AlphabetSymbol(""));

            grammar.getRules().getRulesByLeft(grammar.getStart()).add(emptyRule);
        }
    }

    private static void adaptEmptyRules(Grammar grammar, Set<List<GrammarSymbol>> rules, Variable variable) {
        if (grammar.getRules().getRulesByLeft(variable).isEmpty()) {
            for (List<GrammarSymbol> rule : rules) {
                rule.removeIf(symbol -> symbol instanceof Variable && symbol.getValue().equals(variable.getValue()));
            }

            rules.removeIf(List::isEmpty);
        } else {
            Set<List<GrammarSymbol>> added = new HashSet<>();

            for (List<GrammarSymbol> rule : rules) {
                for (GrammarSymbol symbol : rule) {
                    if (symbol.getValue().equals(variable.getValue())) {
                        List<GrammarSymbol> newRule = new ArrayList<>(rule);
                        newRule.remove(symbol);
                        added.add(newRule);
                    }
                }
            }

            for (List<GrammarSymbol> rule : added) {
                if (!rule.isEmpty()) {
                    rules.add(rule);
                }
            }
        }
    }

    // verifies if grammar generates empty word
    private static boolean isEmptyWorded(Grammar grammar) {
        Queue<List<Variable>> reachable = new LinkedList<>();
        reachable.offer(List.of(grammar.getStart()));

        while (!reachable.isEmpty()) {
            boolean isRuleSetEmpty = true;
            List<Variable> variables = reachable.poll();

            for (Variable variable : variables) {
                boolean isVariableEmpty = false;

                for (List<GrammarSymbol> rule : grammar.getRules().getRulesByLeft(variable)) {
                    if (rule.size() == 1 && rule.getFirst() instanceof AlphabetSymbol && rule.getFirst().getValue().isEmpty()) {
                        isVariableEmpty = true;
                    }
                }

                if (!isVariableEmpty) {
                    isRuleSetEmpty = false;
                    break;
                }
            }

            if (isRuleSetEmpty) {
                return true;
            }

            for (Variable v : variables) {
                for (List<GrammarSymbol> rule : grammar.getRules().getRulesByLeft(v)) {
                    boolean hasAlphabetSymbol = false;
                    List<Variable> added = new ArrayList<>();

                    for (GrammarSymbol symbol : rule) {
                        if (symbol instanceof AlphabetSymbol) {
                            hasAlphabetSymbol = true;
                            break;
                        } else {
                            added.add((Variable) symbol);
                        }
                    }

                    if (!hasAlphabetSymbol) {
                        reachable.add(added);
                    }
                }
            }
        }

        return false;
    }

    public static boolean hasInvalidEmptyRules(Grammar grammar) {
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

    public static boolean hasUnreachableVariables(Grammar grammar) {
        for (Variable variable : grammar.getVariables()) {
            if (!computeClosure(grammar, grammar.getStart()).contains(variable)) {
                return true;
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
}

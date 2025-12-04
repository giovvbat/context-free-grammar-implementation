package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class Rules {
    private Map<Variable, List<List<GrammarSymbol>>> value;

    public List<List<GrammarSymbol>> getRulesByLeft(Variable variable) {
        return value.get(variable);
    }

    public void addRule(Variable variable, List<GrammarSymbol> rule) {
        if (value.get(variable).stream().noneMatch(existing -> existing.equals(rule))) {
            value.get(variable).add(rule);
        }
    }

    public String getFormattedRules(Variable startVariable) {
        StringBuilder sb = new StringBuilder();


        if (value.containsKey(startVariable)) {
            sb.append(formatRuleLine(startVariable, value.get(startVariable)));
        }

        List<Variable> otherVariables = new ArrayList<>(value.keySet());
        otherVariables.remove(startVariable);
        otherVariables.sort(Comparator.comparing(GrammarSymbol::getValue));

        for (Variable var : otherVariables) {
            sb.append(formatRuleLine(var, value.get(var)));
        }

        return sb.toString();
    }


    private String formatRuleLine(Variable variable, List<List<GrammarSymbol>> alternatives) {
        if (alternatives.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        sb.append(variable).append(" -> ");

        String rightSide = alternatives.stream().map(rule -> {
            if (rule.size() == 1 && rule.getFirst().toString().isEmpty()) {
                return "&";
            }
            return rule.stream().map(symbol -> {
                String symbolStr = symbol.toString();

                if (symbol instanceof Variable && symbolStr.matches(".*\\d.*")) {
                    return "(" + symbolStr + ")";
                }
                return symbolStr;
            }).collect(Collectors.joining(""));
        }).collect(Collectors.joining(" | "));

        sb.append(rightSide).append("\n");
        return sb.toString();
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        List<Variable> sorted = new ArrayList<>(value.keySet());
        sorted.sort(Comparator.comparing(GrammarSymbol::getValue));

        for (Variable var : sorted) {
            sb.append(formatRuleLine(var, value.get(var)));
        }
        return sb.toString();
    }
}


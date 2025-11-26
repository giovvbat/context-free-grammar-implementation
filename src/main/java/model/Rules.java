package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<Variable, List<List<GrammarSymbol>>> entry : value.entrySet()) {
            Variable variable = entry.getKey();
            List<List<GrammarSymbol>> alternatives = entry.getValue();

            if (alternatives.isEmpty()) {
                continue;
            }

            sb.append(variable).append(" -> ");

            String rightSide = alternatives.stream().map(rule -> {
                if (rule.size() == 1 && rule.getFirst().toString().isEmpty()) {
                    return "& ";
                }

                return rule.stream().map(Object::toString).collect(Collectors.joining(""));
            }).collect(Collectors.joining(" | "));

            sb.append(rightSide).append("\n");
        }

        return sb.toString();
    }
}

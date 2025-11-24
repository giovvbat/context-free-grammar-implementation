package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class Rules {
    private Map<Variable, Set<List<GrammarSymbol>>> value;

    public Set<List<GrammarSymbol>> getRulesByLeft(Variable variable) {
        return value.get(variable);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<Variable, Set<List<GrammarSymbol>>> entry : value.entrySet()) {
            Variable variable = entry.getKey();
            Set<List<GrammarSymbol>> alternatives = entry.getValue();

            if (alternatives.isEmpty()) {
                continue;
            }

            sb.append(variable).append(" -> ");

            String rightSide = alternatives.stream().map(rule -> {
                if (rule.size() == 1 && rule.getFirst().toString().isEmpty()) {
                    return "*";
                }

                return rule.stream().map(Object::toString).collect(Collectors.joining(""));
            }).collect(Collectors.joining(" | "));

            sb.append(rightSide).append("\n");
        }

        return sb.toString();
    }
}

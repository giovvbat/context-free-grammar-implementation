package grammar;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record ProductionRules(Map<Variable, Set<List<GrammarSymbol>>> productionRules) {
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<Variable, Set<List<GrammarSymbol>>> entry : productionRules.entrySet()) {
            Variable variable = entry.getKey();
            Set<List<GrammarSymbol>> alternatives = entry.getValue();

            sb.append(variable).append(" -> ");
            sb.append(alternatives.stream().map(prod -> prod.stream().map(Object::toString).collect(Collectors.joining(""))).collect(Collectors.joining(" | "))).append("\n");
        }

        return sb.toString();
    }
}

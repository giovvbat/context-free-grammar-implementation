import grammar.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        Variable S = new Variable("S");
        Variable A = new Variable("A");
        Variable B = new Variable("B");

        AlphabetSymbol a = new AlphabetSymbol("a");
        AlphabetSymbol b = new AlphabetSymbol("b");
        AlphabetSymbol c = new AlphabetSymbol("c");
        AlphabetSymbol d = new AlphabetSymbol("d");

        Set<Variable> variables = Set.of(S, A, B);
        Set<AlphabetSymbol> alphabetSymbols = Set.of(a, b, c, d);

        ProductionRules productionRules = new ProductionRules(Map.of(S, Set.of(List.of(a, A)), A, Set.of(List.of(a, c, d, S), List.of(b, d))));

        Grammar grammar = new Grammar(variables, alphabetSymbols, S, productionRules);
        System.out.println(grammar);
    }
}

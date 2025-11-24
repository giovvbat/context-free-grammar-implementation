import model.*;
import service.NormalizationService;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Grammar grammar = setGrammarUp();
        System.out.println(grammar);
        NormalizationService.normalizeChomsky(grammar);
        System.out.println(grammar);
    }

    private static Grammar setGrammarUp() {
        Variable S = new Variable("S");
        Variable A = new Variable("A");
        Variable B = new Variable("B");
        Variable C = new Variable("C");
        Variable D = new Variable("D");
        Variable E = new Variable("E");

        AlphabetSymbol a = new AlphabetSymbol("a");
        AlphabetSymbol b = new AlphabetSymbol("b");
        AlphabetSymbol empty = new AlphabetSymbol("");

        Set<Variable> variables = new HashSet<>();
        Set<AlphabetSymbol> alphabetSymbols = new HashSet<>();
        HashMap<Variable, Set<List<GrammarSymbol>>> map = new HashMap<>();

        variables.add(S);
        variables.add(A);
        variables.add(B);
        variables.add(C);
        variables.add(D);
        variables.add(E);

        alphabetSymbols.add(a);
        alphabetSymbols.add(b);
        alphabetSymbols.add(empty);

        List<GrammarSymbol> S_to_A = new ArrayList<>();
        S_to_A.add(A);

        List<GrammarSymbol> S_to_b = new ArrayList<>();
        S_to_b.add(b);

        List<GrammarSymbol> S_to_BC = new ArrayList<>();
        S_to_BC.add(B);
        S_to_BC.add(C);

        Set<List<GrammarSymbol>> Sset = new HashSet<>();
        Sset.add(S_to_A);
        Sset.add(S_to_b);
        Sset.add(S_to_BC);

        List<GrammarSymbol> A_to_B = new ArrayList<>();
        A_to_B.add(B);

        List<GrammarSymbol> A_to_a = new ArrayList<>();
        A_to_a.add(a);

        Set<List<GrammarSymbol>> Aset = new HashSet<>();
        Aset.add(A_to_B);
        Aset.add(A_to_a);

        List<GrammarSymbol> B_to_C = new ArrayList<>();
        B_to_C.add(C);

        Set<List<GrammarSymbol>> Bset = new HashSet<>();
        Bset.add(B_to_C);

        List<GrammarSymbol> C_to_empty = new ArrayList<>();
        C_to_empty.add(empty);

        Set<List<GrammarSymbol>> Cset = new HashSet<>();
        Cset.add(C_to_empty);

        List<GrammarSymbol> D_to_a = new ArrayList<>();
        D_to_a.add(a);

        Set<List<GrammarSymbol>> Dset = new HashSet<>();
        Dset.add(D_to_a);

        List<GrammarSymbol> E_to_b = new ArrayList<>();
        E_to_b.add(b);

        Set<List<GrammarSymbol>> Eset = new HashSet<>();
        Eset.add(E_to_b);

        map.put(S, Sset);
        map.put(A, Aset);
        map.put(B, Bset);
        map.put(C, Cset);
        map.put(D, Dset);
        map.put(E, Eset);

        return new Grammar(variables, alphabetSymbols, S, new Rules(map));
    }
}

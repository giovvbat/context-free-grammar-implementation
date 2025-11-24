package service;

import model.Grammar;

public class NormalisationService {
    public static void normatizeChomsky(Grammar grammar) {
        GrammarService.removeUnitaryRules(grammar);
        GrammarService.removeUnreachableVariables(grammar);
        GrammarService.removeEmptyRules(grammar);
    }
}

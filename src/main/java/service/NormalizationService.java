package service;

import model.Grammar;

public class NormalizationService {
    public static void normalizeChomsky(Grammar grammar) {
        GrammarService.removeUnitaryRules(grammar);
        GrammarService.removeUnreachableVariables(grammar);
        GrammarService.removeEmptyRules(grammar);
    }
}

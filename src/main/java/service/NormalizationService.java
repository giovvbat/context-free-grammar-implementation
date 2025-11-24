package service;

import model.Grammar;

public class NormalizationService {
    public static void normalizeChomsky(Grammar grammar) {
        while (true) {
            if (GrammarService.hasInvalidEmptyRules(grammar)) {
                GrammarService.removeEmptyRules(grammar);
            } else if (GrammarService.hasUnitaryRules(grammar)) {
                GrammarService.removeUnitaryRules(grammar);
            } else if (GrammarService.hasUnreachableVariables(grammar)) {
                GrammarService.removeUnreachableVariables(grammar);
            } else {
                break;
            }
        }
    }
}

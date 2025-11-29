package service;

import model.Grammar;

public class NormalizationService {
    public static void normalizeChomsky(Grammar grammar) {
        while (true) {
            if (GrammarService.hasLeftRecursion(grammar)) {
                GrammarService.removeLeftRecursion(grammar);
            } else if (GrammarService.hasInvalidLambdaRules(grammar)) {
                GrammarService.removeLambdaRules(grammar);
            } else if (GrammarService.hasUnitaryRules(grammar)) {
                GrammarService.removeUnitaryRules(grammar);
            } else if (GrammarService.hasUselessVariables(grammar)) {
                GrammarService.removeUselessVariables(grammar);
            } else {
                break;
            }
        }
    }

    public static void normalizeGreibach(Grammar grammar) {
        while (true) {

        }
    }
}

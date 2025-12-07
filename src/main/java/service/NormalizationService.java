package service;

import model.Grammar;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NormalizationService {
    private static int stepCounter = 1;

    public static void normalizeChomsky(Grammar grammar, String originalFilePath) {
        stepCounter = 1;

        String outputFileName = generateOutputFilePath(originalFilePath, "_chomsky");

        initializeLogFile(outputFileName);
        logCustomMessage(outputFileName, "INITIAL GRAMMAR", grammar.toString());

        while (true) {
            if (GrammarService.hasLeftRecursion(grammar)) {
                executeAndLogAction(grammar, GrammarService::removeLeftRecursion, "Removing Left Recursion", outputFileName);
            } else if (GrammarService.hasInvalidLambdaRules(grammar)) {
                executeAndLogAction(grammar, GrammarService::removeLambdaRules, "Removing Lambda Rules", outputFileName);
            } else if (GrammarService.hasUnitaryRules(grammar)) {
                executeAndLogAction(grammar, GrammarService::removeUnitaryRules, "Removing Unitary Rules", outputFileName);
            } else if (GrammarService.hasUselessVariables(grammar)) {
                executeAndLogAction(grammar, GrammarService::removeUselessVariables, "Removing Useless Variables", outputFileName);
            } else {
                break;
            }
        }

        executeAndLogAction(grammar, GrammarService::convertTerminalsToVariables, "Converting Terminals to Variables", outputFileName);
        executeAndLogAction(grammar, GrammarService::breakLongProductions, "Breaking Long Productions", outputFileName);

        logCustomMessage(outputFileName, "FINAL GRAMMAR IN CHOMSKY NORMAL FORM", grammar.toString());

        System.out.println("Normalization complete. Detailed steps logged in: " + outputFileName);

    }

    public static void normalizeGreibach(Grammar grammar, String originalFilePath) {
        stepCounter = 1;

        String outputFileName = generateOutputFilePath(originalFilePath, "_greibach");

        initializeLogFile(outputFileName);
        logCustomMessage(outputFileName, "INITIAL GRAMMAR", grammar.toString());

        while (true) {
            if (GrammarService.hasLeftRecursion(grammar)) {
                executeAndLogAction(grammar, GrammarService::removeLeftRecursion, "Removing Left Recursion", outputFileName);
            } else if (GrammarService.hasInvalidLambdaRules(grammar)) {
                executeAndLogAction(grammar, GrammarService::removeLambdaRules, "Removing Lambda Rules", outputFileName);
            } else if (GrammarService.hasUnitaryRules(grammar)) {
                executeAndLogAction(grammar, GrammarService::removeUnitaryRules, "Removing Unitary Rules", outputFileName);
            } else if (GrammarService.hasUselessVariables(grammar)) {
                executeAndLogAction(grammar, GrammarService::removeUselessVariables, "Removing Useless Variables", outputFileName);
            } else {
                break;
            }
        }

        executeAndLogAction(grammar, GrammarService::convertTerminalsToVariables, "Converting Terminals to Variables", outputFileName);

        executeAndLogAction(grammar, GrammarService::convertToGreibach, "Converting to Greibach Normal Form", outputFileName);

        logCustomMessage(outputFileName, "FINAL GRAMMAR IN GREIBACH NORMAL FORM", grammar.toString());

        System.out.println("Normalization complete. Detailed steps logged in: " + outputFileName);

    }

    @FunctionalInterface
    interface GrammarAction {
        void apply(Grammar grammar);
    }
    private static String generateOutputFilePath(String originalFilePath, String suffix) {
        Path file = Paths.get(originalFilePath);
        String fileName = file.getFileName().toString();

        int dotIndex = fileName.lastIndexOf('.');
        if(dotIndex > 0){
             fileName = fileName.substring(0, dotIndex);
        }

        Path parentDir = file.getParent();
        if (parentDir != null) {
            return parentDir.resolve("normalization_" + fileName + "_output" + suffix + ".txt").toString();
        } else {
            return "normalization_" + fileName + "_output.txt";
            }
    }

    private static void executeAndLogAction(Grammar grammar, GrammarAction action, String actionDescription, String outputFileName) {
        String beforeAction = grammar.toString();
        action.apply(grammar);
        String afterAction = grammar.toString();

        if(!beforeAction.equals(afterAction)){
            appendStepToLogFile(outputFileName, actionDescription, beforeAction, afterAction);
        }
    }
    private  static void initializeLogFile(String filePath) {
        try{
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
            Files.createFile(path);

        } catch(IOException e){
            System.err.println("Error: " + e.getMessage());
        }

    }
    private static void appendStepToLogFile(String outputFileName, String action, String before, String after){
        StringBuilder sb = new  StringBuilder();

        sb.append("\n==================================================================\n");
        sb.append("STEP ").append(stepCounter++).append(": ").append(action).append("\n");
        sb.append("\n==================================================================\n");

        sb.append("\n ------------- BEFORE -------------\n");
        sb.append(before).append("\n");
        sb.append("\n ------------- AFTER -------------\n");
        sb.append(after).append("\n");

        try{
            Files.writeString(Paths.get(outputFileName), sb.toString(), java.nio.file.StandardOpenOption.APPEND);
        } catch(IOException e){
            System.err.println("Error: " + e.getMessage());
        }
    }
    private static void logCustomMessage(String outputFileName, String title, String content){
       StringBuilder sb = new  StringBuilder();
         sb.append("\n#################################################\n");
        sb.append("# ").append(title).append("\n");
        sb.append("#################################################\n");
        sb.append(content).append("\n");
        try{
            Files.writeString(Paths.get(outputFileName), sb.toString(), java.nio.file.StandardOpenOption.APPEND);
        } catch(IOException e){
            System.err.println("Error: " + e.getMessage());
        }
    }
}

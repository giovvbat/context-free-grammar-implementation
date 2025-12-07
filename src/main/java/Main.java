import java.util.Scanner;
import service.GrammarLoaderService;
import service.NormalizationService;
import model.Grammar;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Grammar grammar = null;
        String inputFileName = "";

        while (grammar == null) {
            System.out.print("Enter grammar file path (e.g., assets/indirect.txt): ");
            inputFileName = scanner.nextLine().trim();

            if (inputFileName.isEmpty()) {
                System.out.println("Filename cannot be empty.");
                continue;
            }

            try {
                System.out.println("Attempting to load grammar from: " + inputFileName);
                grammar = GrammarLoaderService.parseFromFile(inputFileName);
                System.out.println("Grammar loaded successfully!\n");
            } catch (Exception e) {
                System.err.println("\n[ERROR] Failed to load grammar.");
                System.err.println("Details: " + e.getMessage());
                System.out.println("Please check the file path and try again.\n");
            }
        }

        System.out.println("-------------------------------------------------");
        System.out.println(grammar);
        System.out.println("-------------------------------------------------");

        boolean validChoice = false;
        while (!validChoice) {
            System.out.print("Enter 'c' for Chomsky (CNF) or 'g' for Greibach (GNF): ");
            String choice = scanner.nextLine().trim().toLowerCase();

            if (choice.equals("c")) {
                System.out.println("Mode: Chomsky Normal Form (CNF) selected.");
                System.out.println("Starting normalization process...");

                try {
                    NormalizationService.normalizeChomsky(grammar, inputFileName);
                    validChoice = true;
                } catch (Exception e) {
                    System.err.println("Error during Chomsky normalization: " + e.getMessage());
                    validChoice = true;
                }

            } else if (choice.equals("g")) {
                System.out.println("Mode: Greibach Normal Form (GNF) selected.");
                System.out.println("Starting normalization process...");

                try {
                    NormalizationService.normalizeGreibach(grammar, inputFileName);
                    validChoice = true;
                } catch (Exception e) {
                    System.err.println("Error during Greibach normalization: " + e.getMessage());
                    validChoice = true;
                }

            } else {
                System.out.println("Invalid choice. Please enter only 'c' or 'g'.");
            }
        }

        scanner.close();

        System.out.println("\nNormalization process completed.");
        System.out.println("Final normalized grammar:\n");
        System.out.println(grammar);
    }
}
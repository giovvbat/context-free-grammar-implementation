import model.*;
import service.GrammarLoaderService;
import service.*;


public class Main {

    public static void main(String[] args) throws Exception {
        String inputFileName   = "assets/chomsky.txt";

        System.out.println("Loading grammar from file: " + inputFileName);
        Grammar grammar = GrammarLoaderService.parseFromFile(inputFileName);
        System.out.println("Grammar loaded successfully:\n");
        System.out.println(grammar);

        System.out.println("Starting normalization process...");
        NormalizationService.normalizeChomsky(grammar, inputFileName);
        System.out.println("Normalization process completed.");
        System.out.println("Final normalized grammar:\n");
        System.out.println(grammar);

    }
}

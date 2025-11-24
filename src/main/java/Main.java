import model.*;
import service.GrammarLoader;
import service.NormalizationService;

import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws Exception {
        Grammar grammar = GrammarLoader.parseFromFile(Paths.get(System.getProperty("user.dir"), "assets", "input.txt").toString());
        NormalizationService.normalizeChomsky(grammar);

        System.out.println(grammar);
    }
}

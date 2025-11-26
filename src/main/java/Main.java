import model.*;
import service.GrammarLoaderService;
import service.*;

import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws Exception {
        Grammar grammar = GrammarLoaderService.parseFromFile(Paths.get(System.getProperty("user.dir"), "assets", "input.txt").toString());
        NormalizationService.normalizeChomsky(grammar);

        System.out.println(grammar);
    }
}

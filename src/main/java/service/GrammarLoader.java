package service;

import model.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class GrammarLoader {
    public static Grammar parseFromFile(String path) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String line;

        Set<Variable> variables = new HashSet<>();
        Set<AlphabetSymbol> alphabet = new HashSet<>();
        Variable start = null;
        Map<Variable, Set<List<GrammarSymbol>>> rules = new HashMap<>();

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.startsWith("VARIABLES:")) {
                String inside = extractBetweenBraces(line);

                for (String value : inside.split(" ")) {
                    if (!value.isEmpty()) {
                        variables.add(new Variable(value));
                    }
                }
            }

            else if (line.startsWith("ALPHABET:")) {
                String inside = extractBetweenBraces(line);

                for (String value : inside.split(" ")) {
                    if (!value.isEmpty()) {
                        alphabet.add(new AlphabetSymbol(value));
                    }
                }
            }

            else if (line.startsWith("STARTING VARIABLE:")) {
                String[] parts = line.split(":");
                start = new Variable(parts[1].trim());
            }

            else if (line.startsWith("PRODUCTION RULES:")) {
                break;
            }
        }

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split("->");
            String left = parts[0].trim();
            Variable key = new Variable(left);

            if (!rules.containsKey(key)) {
                rules.put(key, new HashSet<>());
            }

            String right = parts[1].trim();
            String[] productions = right.split("\\|");

            for (String rule : productions) {
                rule = rule.trim();
                List<GrammarSymbol> list = new ArrayList<>();

                if (rule.equals("*")) {
                    list.add(new AlphabetSymbol(""));
                } else {
                    List<String> tokens = tokenize(rule);

                    for (String token : tokens) {
                        if (token.equals("*")) {
                            throw new IllegalArgumentException("lambda production rules cannot contain more than one symbol!");
                        }

                        if (isVariable(token, variables)) {
                            list.add(new Variable(token));
                        } else if (isAlphabetSymbol(token, alphabet)) {
                            list.add(new AlphabetSymbol(token));
                        } else {
                            throw new IllegalArgumentException("grammar symbol " + token + " was not defined in neither alphabet or variable set!");
                        }
                    }
                }

                rules.get(key).add(list);
            }
        }

        reader.close();

        for (Variable variable : variables) {
            if (!rules.containsKey(variable)) {
                rules.put(variable, new HashSet<>());
            }
        }

        return new Grammar(variables, alphabet, start, new Rules(rules));
    }

    private static String extractBetweenBraces(String line) {
        int start = line.indexOf("{");
        int end = line.indexOf("}");

        return line.substring(start + 1, end).trim();
    }

    private static boolean isVariable(String token, Set<Variable> variables) {
        for (Variable variable : variables) {
            if (variable.getValue().equals(token)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isAlphabetSymbol(String token, Set<AlphabetSymbol> alphabet) {
        for (AlphabetSymbol symbol : alphabet) {
            if (symbol.getValue().equals(token)) {
                return true;
            }
        }

        return false;
    }

    private static List<String> tokenize(String rule) {
        rule = rule.replace(" ", "");
        List<String> result = new ArrayList<>();

        for (int i = 0; i < rule.length(); i++) {
            result.add(String.valueOf(rule.charAt(i)));
        }

        return result;
    }
}

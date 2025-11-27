package service;

import model.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class GrammarLoaderService {

    public static Grammar parseFromFile(String path) throws Exception {

        List<String> lines = Files.readAllLines(Paths.get(path));

        Set<Variable> variables = new HashSet<>();
        Set<AlphabetSymbol> alphabet = new HashSet<>();
        Variable start = null;
        Rules rules = new Rules(new HashMap<>());

        boolean autoDiscover = true;

        for (String line : lines) {
            line = cleanLine(line);
            if (line.isEmpty()) continue;

            if (matchesHeader(line, "VARIABLES") || matchesHeader(line, "VARIAVEIS")) {
                String inside = extractBetweenBraces(line);

                String[] parts = inside.split("[,\\s]+");

                for (String value : parts) {
                    if (!value.isEmpty()) {
                        variables.add(new Variable(value));
                    }
                }
                autoDiscover = false;
            }

            else if (matchesHeader(line, "ALFABETO") || matchesHeader(line, "ALPHABET")) {
                String inside = extractBetweenBraces(line);

                String[] parts = inside.split("[,\\s]+");

                for (String value : parts) {
                    if (!value.isEmpty()) {
                        alphabet.add(new AlphabetSymbol(value));
                    }
                }
            }
            // Detecta INICIAL
            else if (matchesHeader(line, "START") || matchesHeader(line, "INICIAL")) {
                String[] parts = line.split("[:=]");
                if (parts.length >1){
                    start = new Variable(parts[1].trim());
                }
            }
        }

        if (variables.isEmpty()) {
            autoDiscover = true;
        }

        for (String line : lines) {
            line = cleanLine(line);
            if (line.isEmpty() || isHeaderLine(line)) continue;
            if (!line.contains("->")) continue;

            String[] parts = line.split("->");
            String left = parts[0].trim();
            Variable key = new Variable(left);

            if (autoDiscover) variables.add(key);

            if (start == null) {
                start = key;
            } else if (!start.getValue().equals("S") && key.getValue().equals("S")) {
                start = key;
            }

            if (!rules.getValue().containsKey(key)) {
                rules.getValue().put(key, new ArrayList<>());
            }

            String right = parts.length > 1 ? parts[1].trim() : "";

            String[] productions = right.split("\\|");

            for (String rule : productions) {
                rule = rule.trim();
                if (rule.isEmpty()) continue;

                List<GrammarSymbol> list = new ArrayList<>();

                if (rule.equals("&")) {
                    list.add(new AlphabetSymbol(""));
                } else {

                    List<String> tokens = tokenize(rule);

                    for (String token : tokens) {
                        boolean isVar = isVariable(token, variables);
                        boolean isAlph = isAlphabetSymbol(token, alphabet);

                        if (isVar) {
                            list.add(new Variable(token));
                        } else if (isAlph) {
                            list.add(new AlphabetSymbol(token));
                        } else if (autoDiscover) {

                            if (token.matches("[A-Z]")) {
                                Variable v = new Variable(token);
                                variables.add(v);
                                list.add(v);
                            } else {
                                AlphabetSymbol s = new AlphabetSymbol(token);
                                alphabet.add(s);
                                list.add(s);
                            }
                        } else {
                            throw new IllegalArgumentException("Símbolo desconhecido: " + token);
                        }
                    }
                }
                rules.addRule(key, list);
            }
        }

        if (start == null) throw new IllegalArgumentException("Não foi possível identificar a variável inicial.");


        for (Variable variable : variables) {
            rules.getValue().putIfAbsent(variable, new ArrayList<>());
        }

        return new Grammar(variables, alphabet, start, rules);
    }



    private static String extractBetweenBraces(String line) {
        int start = line.indexOf("{");
        int end = line.indexOf("}");

        return line.substring(start + 1, end).trim();
        }
    private static String cleanLine(String line) {
        return line.replaceAll("\\[.*?]", "").trim();
    }

    private static boolean matchesHeader(String line, String keyword) {
        return line.toUpperCase().startsWith(keyword);
    }

    private static boolean isHeaderLine(String line) {
        return matchesHeader(line, "VARIABLE") || matchesHeader(line, "ALFABETO") ||
                matchesHeader(line, "ALPHABET") || matchesHeader(line, "START") ||
                matchesHeader(line, "INICIAL");
    }


    private static boolean isVariable(String token, Set<Variable> variables) {
        return variables.stream().anyMatch(v -> v.getValue().equals(token));
    }

    private static boolean isAlphabetSymbol(String token, Set<AlphabetSymbol> alphabet) {
        return alphabet.stream().anyMatch(a -> a.getValue().equals(token));
    }

    private static List<String> tokenize(String rule) {
        rule = rule.replaceAll("\\s+", "");
        List<String> res = new ArrayList<>();
        for (char c : rule.toCharArray()) res.add(String.valueOf(c));
        return res;
    }
}
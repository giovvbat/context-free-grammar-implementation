package model;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class Variable extends GrammarSymbol {
    public Variable(String value) {
        super(validateVariable(value));
    }

    private static String validateVariable(String value) throws IllegalArgumentException {
        for (char character = 'A'; character <= 'Z'; character++) {
            String letter = String.valueOf(character);

            if (letter.equals(value)) {
                return value;
            }
        }

        throw new IllegalArgumentException("ERROR: variable values must contain single character represented as majuscule letter");
    }

    public static Variable nextRepresentationAvailable(Set<Variable> variables) {
        for (char character = 'A'; character <= 'Z'; character++) {
            String letter = String.valueOf(character);

            if (!variables.contains(new Variable(letter))) {
                return new Variable(letter);
            }
        }

        throw new RuntimeException("ERROR: grammar reached maximum number of variables allowed");
    }
}

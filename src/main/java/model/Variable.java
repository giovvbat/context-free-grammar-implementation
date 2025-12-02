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
        if (value.matches("^[A-Z][A-Z0-9]*")) {
            return value;
        }

        throw new IllegalArgumentException("ERROR: variable values must start with an uppercase letter and contain only uppercase letters or digits. Received: " + value);
    }

    public static Variable nextRepresentationAvailable(Set<Variable> variables) {
        for (char character = 'A'; character <= 'Z'; character++) {
            String letter = String.valueOf(character);

            if (!variables.contains(new Variable(letter))) {
                return new Variable(letter);
            }
        }
        int counter = 0;
        while(true){
            String candidate = "V" + counter;
            if (!variables.contains(new Variable(candidate))) {
                return new Variable(candidate);
            }

            if (counter >= 1000) {
                throw new RuntimeException("ERROR: grammar reached maximum number of variables allowed");
            }
            counter++;

        }
    }
}

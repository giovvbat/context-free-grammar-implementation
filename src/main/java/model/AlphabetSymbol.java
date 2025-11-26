package model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlphabetSymbol extends GrammarSymbol {
    public AlphabetSymbol(String value) {
        super(validateAlphabetSymbol(value));
    }

    private static String validateAlphabetSymbol(String value) throws IllegalArgumentException {
        if (value.isEmpty()) {
            return value;
        }

        for (char character = 'a'; character <= 'z'; character++) {
            String letter = String.valueOf(character);

            if (letter.equals(value)) {
                return value;
            }
        }

        throw new IllegalArgumentException("ERROR: alphabet symbols must contain single character represented as majuscule letter");
    }
}

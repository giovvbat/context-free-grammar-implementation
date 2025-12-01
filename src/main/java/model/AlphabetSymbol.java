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

        if (value.length() == 1) {
            char character = value.charAt(0);

            if (character >= 'a' && character <= 'z' || character >= '0' && character <= '9') {
                return value;
            }
        }

        throw new IllegalArgumentException("ERROR: alphabet symbols must contain single character represented as majuscule letter");
    }
}

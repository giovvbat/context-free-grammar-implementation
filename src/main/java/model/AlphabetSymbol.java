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

        if(value.length() == 1){
            char c = value.charAt(0);
            if(c>='a' && c<='z' || c>='0' && c<='9'){
                return value;
            }
        }

        throw new IllegalArgumentException("ERROR: alphabet symbols must contain single character represented as majuscule letter");
    }
}

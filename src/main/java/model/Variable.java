package model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Variable extends GrammarSymbol {
    public Variable(String value) {
        super(validateVariable(value));
    }

    private static String validateVariable(String value) throws IllegalArgumentException {
        if (value.isEmpty()) {
            throw new IllegalArgumentException("variable values cannot be defined as empty");
        }

        return value;
    }
}

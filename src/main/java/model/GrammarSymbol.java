package model;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
public abstract class GrammarSymbol {
    private final String value;

    public GrammarSymbol(String value) {
        if (value.equals("&")) {
            throw new IllegalArgumentException("grammar symbol values cannot be stored as &");
        }

        if (value.equals("(") || value.equals(")")) {
            throw new IllegalArgumentException("grammar symbol values cannot be stored as ( or )");
        }

        this.value = value;
    }

    @Override
    public String toString() {
        if (value.isEmpty()) {
            return "&";
        }

        return value;
    }
}

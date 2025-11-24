package model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public abstract class GrammarSymbol {
    private String value;

    @Override
    public String toString() {
        if (value.isEmpty()) {
            return "*";
        }

        return value;
    }
}

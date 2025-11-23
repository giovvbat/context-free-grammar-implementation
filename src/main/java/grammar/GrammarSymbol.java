package grammar;

import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public abstract class GrammarSymbol {
    private final String grammarSymbol;

    @Override
    public String toString() {
        return grammarSymbol;
    }
}

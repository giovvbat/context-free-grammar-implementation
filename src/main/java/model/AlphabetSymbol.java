package model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlphabetSymbol extends GrammarSymbol {
    public AlphabetSymbol(String value) {
        super(value);
    }
}

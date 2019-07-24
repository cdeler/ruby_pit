package cdeler.highlight;

public enum TokenType {
    keyword,
    identifier,
    string,
    word,
    symbol,
    constant,
    unknown;

    public static TokenType getEnum(String val) {
        switch (val) {
            case "identifier":
                return TokenType.identifier;
            case "string":
                return TokenType.string;
            case "constant":
                return TokenType.constant;
            case "symbol":
                return TokenType.symbol;
            default:
                return TokenType.unknown;
        }
    }
}

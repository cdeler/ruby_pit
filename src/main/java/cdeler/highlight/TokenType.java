package cdeler.highlight;

public enum TokenType {
    KEYWORD,
    IDENTIFIER,
    STRING,
    WORD;

    public static TokenType getEnum(String val) {
        switch (val) {
            case "identifier":
                return TokenType.IDENTIFIER;
            case "string":
                return TokenType.STRING;
            default:
                return TokenType.WORD;
        }
    }
}

package cdeler.highlight.token;

public enum TokenType {
    identifier,
    string,
    symbol,
    constant,
    program,
    method_call,
    argument_list,
    comment,
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
            case "program":
                return TokenType.program;
            case "method_call":
                return TokenType.method_call;
            case "argument_list":
                return TokenType.argument_list;
            case "comment":
                return TokenType.comment;
            default:
                return TokenType.unknown;
        }
    }
}

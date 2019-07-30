package cdeler.highlight;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public enum TokenType {
    identifier,
    string,
    symbol,
    constant,
    program,
    method_call,
    argument_list,
    unknown;

    private static final List<TokenType> HIGHLIGHTED_TOKENS = Arrays.asList(
            identifier,
            string
    );

    public static boolean isHighlightedToken(TokenType ttype) {
        return (ttype != null) && HIGHLIGHTED_TOKENS.contains(ttype);
    }

    public static Collection<TokenType> getHighlightedTokens() {
        return HIGHLIGHTED_TOKENS;
    }

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
            default:
                return TokenType.unknown;
        }
    }
}

package cdeler.highlight.token;

public interface Token {

    TokenType getTokenType();

    TokenLocation getLocation();
}

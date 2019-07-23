package cdeler.highlight;

public interface Token {

    TokenType getTokenType();

    TokenLocation getLocation();
}

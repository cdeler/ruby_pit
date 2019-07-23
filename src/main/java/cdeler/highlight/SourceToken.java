package cdeler.highlight;

public class SourceToken implements Token {
    private final TokenType tokenType;
    private final TokenLocation tokenLocation;

    public SourceToken(TokenType tokenType, TokenLocation tokenLocation) {
        this.tokenType = tokenType;
        this.tokenLocation = tokenLocation;
    }

    @Override
    public TokenType getTokenType() {
        return tokenType;
    }

    @Override
    public TokenLocation getLocation() {
        return tokenLocation;
    }

}

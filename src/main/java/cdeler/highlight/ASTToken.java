package cdeler.highlight;

public class ASTToken implements Token {

    private final TokenType type;
    private final TokenLocation location;

    public ASTToken(TokenType type, TokenLocation location) {
        this.type = type;
        this.location = location;
    }

    @Override
    public TokenType getTokenType() {
        return null;
    }

    @Override
    public TokenLocation getLocation() {
        return null;
    }
}

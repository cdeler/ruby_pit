package cdeler.highlight;

import java.util.Objects;

import cdeler.core.ui.UIUtils;

public class SourceToken implements Token {
    private final TokenType tokenType;
    private final TokenLocation tokenLocation;

    public SourceToken(TokenType tokenType, TokenLocation tokenLocation) {
        this.tokenType = tokenType;
        this.tokenLocation = tokenLocation;
    }

    public SourceToken(TokenType tokenType, int beginLine, int beginColumn, int endLine, int endColumn) {
        this.tokenType = tokenType;
        this.tokenLocation = new TokenLocation(beginLine, beginColumn, endLine, endColumn);
    }

    @Override
    public TokenType getTokenType() {
        return tokenType;
    }

    @Override
    public TokenLocation getLocation() {
        return tokenLocation;
    }

    @Override
    public String toString() {
        return "SourceToken<" + getTokenType() + ", " + getLocation() + ">";
    }

    @Override
    public int hashCode() {
        return UIUtils.hashCombine(Objects.hashCode(tokenType), Objects.hashCode(tokenLocation));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SourceToken)) {
            return false;
        }

        return Objects.equals(tokenType, ((SourceToken) obj).tokenType)
                && Objects.equals(tokenLocation, ((SourceToken) obj).tokenLocation);
    }

}

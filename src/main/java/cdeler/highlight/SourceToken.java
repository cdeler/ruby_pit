package cdeler.highlight;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SourceToken implements Token {
    private static final Pattern SOURCE_PATTERN =
            Pattern.compile("(?:\\s+)?\\(([a-z]+) \\[(\\d+), (\\d+)] - \\[(\\d+), (\\d+)].*");
    private final TokenType tokenType;
    private final TokenLocation tokenLocation;

    public SourceToken(TokenType tokenType, TokenLocation tokenLocation) {
        this.tokenType = tokenType;
        this.tokenLocation = tokenLocation;
    }

    public static Optional<SourceToken> fromTreeSitterLine(final String treeSitterOutput) {
        final Matcher matcher = SOURCE_PATTERN.matcher(treeSitterOutput);

        if (matcher.matches()) {
            var tokenType = TokenType.getEnum(matcher.group(1));
            int beginLine = Integer.valueOf(matcher.group(2));
            int beginColumn = Integer.valueOf(matcher.group(3));
            int endLine = Integer.valueOf(matcher.group(4));
            int endColumn = Integer.valueOf(matcher.group(5));

            return Optional.of(new SourceToken(tokenType,
                    new TokenLocation(beginLine, beginColumn, endLine, endColumn)));
        }

        return Optional.empty();
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

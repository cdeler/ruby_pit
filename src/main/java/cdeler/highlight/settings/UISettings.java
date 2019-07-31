package cdeler.highlight.settings;

import java.util.Map;
import java.util.Objects;

import cdeler.highlight.token.TokenType;

public class UISettings {
    private final String name;
    private final String fontName;
    private final int fontSize;
    private final Map<TokenType, TokenStyle> tokenStyle;

    public UISettings(String name, String fontName, int fontSize, Map<TokenType, TokenStyle> tokenStyle) {
        this.name = name;
        this.fontName = fontName;
        this.fontSize = fontSize;
        this.tokenStyle = tokenStyle;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.name);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof UISettings)) {
            return false;
        }

        return Objects.equals(((UISettings) obj).getName(), getName());
    }

    public String getName() {
        return name;
    }

    public String getFontName() {
        return fontName;
    }

    public int getFontSize() {
        return fontSize;
    }

    public Map<TokenType, TokenStyle> getTokenStyle() {
        return tokenStyle;
    }
}

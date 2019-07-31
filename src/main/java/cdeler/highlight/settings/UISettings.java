package cdeler.highlight.settings;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import cdeler.highlight.TokenType;

public class UISettings {
    private static final String DEFAULT_SETTINGS_NAME = "default";
    private String name;
    private String fontName;
    private int fontSize;
    private Map<TokenType, TokenStyle> tokenStyle;

    public static UISettings getDefaultSettings() {
        UISettings result = new UISettings();

        result.name = getDefaultSettingsName();
        result.fontName = "iosevka-regular";
        result.fontSize = 20;

        result.tokenStyle = Arrays.stream(TokenType.values())
                .collect(Collectors.toMap(
                        it -> it,
                        it -> TokenStyle.getDefaultTokenStyle()
                ));

        return result;
    }

    public static String getDefaultSettingsName() {
        return DEFAULT_SETTINGS_NAME;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.name);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof UISettings)) {
            return false;
        }

        return Objects.equals(obj, this);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public Map<TokenType, TokenStyle> getTokenStyle() {
        return tokenStyle;
    }

    public void setTokenStyle(Map<TokenType, TokenStyle> tokenStyle) {
        this.tokenStyle = tokenStyle;
    }
}

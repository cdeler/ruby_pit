package cdeler.highlight.settings;

import java.awt.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import cdeler.highlight.token.TokenType;

public class UISettings {
    private static final Logger LOGGER = LoggerFactory.getLogger(UISettings.class);
    private static final String DEFAULT_FONT_NAME = "iosevka-regular";
    private static final int DEFAULT_FONT_SIZE = 20;
    private static final String DEFAULT_BACKGROUND_COLOR = "#FFFFFF";
    private static final String DEFAULT_LINE_NUMBER_COLOR = "#FFFF00";

    private final String name;
    private final String fontName;
    private final int fontSize;
    private final Color backgroundColor;
    private final Color lineNumberColor;
    private final TokenStyle defaultFontSettings;
    private final Map<TokenType, TokenStyle> tokenStyle;

    public UISettings(String name, String fontName, int fontSize,
                      String backgroundColor,
                      String lineNumberColor,
                      TokenStyle defaultFontSettings,
                      Map<TokenType, TokenStyle> tokenStyle) {
        this.name = name;
        this.fontName = fontName;
        this.fontSize = fontSize;
        this.defaultFontSettings = defaultFontSettings;
        this.backgroundColor = Color.decode(backgroundColor);
        this.lineNumberColor = Color.decode(lineNumberColor);

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

    public Color getBackgroundColor() {
        return backgroundColor;
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

    public TokenStyle getDefaultFontSettings() {
        return defaultFontSettings;
    }

    public Color getLineNumberColor() {
        return lineNumberColor;
    }

    public static class UISettingSerializer implements JsonDeserializer<UISettings> {

        @Override
        public UISettings deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return deserializeInternal(json);
            } catch (ClassCastException | IllegalStateException | NoSuchElementException | NullPointerException e) {
                throw new JsonParseException("Error happened during json processing " + json, e);
            }
        }

        private static Optional<TokenStyle> deserializeTokenStyle(JsonObject tokenStylesObject) {
            try {
                Optional<String> color = getStringValueSave(tokenStylesObject, "color");

                Optional<Boolean> bold = getBooleanValueSave(tokenStylesObject, "bold");
                Optional<Boolean> italic = getBooleanValueSave(tokenStylesObject, "italic");

                return Optional.of(
                        new TokenStyle(color.orElse(TokenStyle.getDefaultFontColor()),
                                bold.orElse(false),
                                italic.orElse(false))
                );


            } catch (ClassCastException | IllegalStateException | NoSuchElementException | NullPointerException e) {

            }

            return Optional.empty();
        }

        private UISettings deserializeInternal(JsonElement json) {
            JsonObject jsonObject = json.getAsJsonObject();

            // name is required field in settings file
            String name = getStringValueSave(jsonObject, "name").orElseThrow();
            TokenStyle defaultFontSettings = deserializeTokenStyle(jsonObject.getAsJsonObject("defaultFontSettings"))
                    .orElse(TokenStyle.getFallbackTokenStyle());
            String backgroundColor = getStringValueSave(jsonObject, "backgroundColor").orElse(DEFAULT_BACKGROUND_COLOR);
            String lineNumberColor = getStringValueSave(jsonObject, "lineNumberColor").orElse(DEFAULT_LINE_NUMBER_COLOR);
            String fontName = getStringValueSave(jsonObject, "fontName").orElse(DEFAULT_FONT_NAME);
            int fontSize = getIntValueSave(jsonObject, "fontSize").orElse(DEFAULT_FONT_SIZE);

            JsonObject tokenStylesObject = jsonObject.getAsJsonObject("tokenStyle");

            Map<TokenType, TokenStyle> tokenStyles = new HashMap<>();
            for (var tokenStyle : tokenStylesObject.entrySet()) {
                try {
                    TokenType tokenName = TokenType.getEnum(tokenStyle.getKey());
                    Optional<TokenStyle> style = deserializeTokenStyle(tokenStyle.getValue().getAsJsonObject());

                    if (tokenName != TokenType.unknown) {
                        tokenStyles.put(tokenName, style.orElseThrow());
                    } else {
                        LOGGER.error("Unknown token type " + tokenStyle.getKey());
                    }
                } catch (ClassCastException | IllegalStateException | NoSuchElementException e) {
                    LOGGER.error("Error during parsing settings file: " + tokenStyle.toString(), e);
                }
            }

            return new UISettings(name, fontName, fontSize, backgroundColor, lineNumberColor, defaultFontSettings,
                    tokenStyles);
        }

        private static Optional<Boolean> getBooleanValueSave(JsonObject object, String key) {
            try {
                return Optional.of(object.get(key).getAsBoolean());
            } catch (ClassCastException | IllegalStateException | NullPointerException e) {
            }

            return Optional.empty();
        }

        private static Optional<String> getStringValueSave(JsonObject object, String key) {
            try {
                return Optional.ofNullable(object.get(key).getAsString());
            } catch (ClassCastException | IllegalStateException | NullPointerException e) {
            }

            return Optional.empty();
        }

        private static Optional<Integer> getIntValueSave(JsonObject object, String key) {
            try {
                return Optional.of(object.get(key).getAsInt());
            } catch (ClassCastException | IllegalStateException | NullPointerException e) {
            }

            return Optional.empty();
        }

    }
}

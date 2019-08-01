package cdeler.highlight.settings;

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

    public static class UISettingSerializer implements JsonDeserializer<UISettings> {

        @Override
        public UISettings deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return deserializeInternal(json);
            } catch (ClassCastException | IllegalStateException | NoSuchElementException | NullPointerException e) {
                throw new JsonParseException("Error happened during json processing " + json, e);
            }
        }

        private UISettings deserializeInternal(JsonElement json) {
            JsonObject jsonObject = json.getAsJsonObject();

            // name is required field in settings file
            String name = getStringValueSave(jsonObject, "name").orElseThrow();

            String fontName = getStringValueSave(jsonObject, "fontName").orElse(DEFAULT_FONT_NAME);
            int fontSize = getIntValueSave(jsonObject, "fontSize").orElse(DEFAULT_FONT_SIZE);

            JsonObject tokenStylesObject = jsonObject.getAsJsonObject("tokenStyle");

            Map<TokenType, TokenStyle> tokenStyles = new HashMap<>();
            for (var tokenStyle : tokenStylesObject.entrySet()) {
                try {
                    TokenType tokenName = TokenType.getEnum(tokenStyle.getKey());

                    if (tokenName != TokenType.unknown) {
                        JsonObject tokenStyleObject = tokenStyle.getValue().getAsJsonObject();

                        Optional<String> color = getStringValueSave(tokenStyleObject, "color");

                        Optional<Boolean> bold = getBooleanValueSave(tokenStyleObject, "bold");
                        Optional<Boolean> italic = getBooleanValueSave(tokenStyleObject, "italic");

                        tokenStyles.put(tokenName,
                                new TokenStyle(color.orElseThrow(), bold.orElse(false), italic.orElse(false)));
                    } else {
                        LOGGER.error("Unknown token type " + tokenStyle.getKey());
                    }
                } catch (ClassCastException | IllegalStateException | NoSuchElementException e) {
                    LOGGER.error("Error during parsing settings file: " + tokenStyle.toString(), e);
                }
            }

            return new UISettings(name, fontName, fontSize, tokenStyles);
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

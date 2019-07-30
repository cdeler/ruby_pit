package cdeler.core;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FontLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(FontLoader.class);
    private static final int DEFAULT_FONT_SIZE = 20;

    public static Font load(String fontName) {
        return load(fontName, DEFAULT_FONT_SIZE, false, false);
    }

    public static Font load(String fontName, int fontSize) {
        return load(fontName, fontSize, false, false);
    }

    public static Font load(String fontName, int fontSize, boolean bold, boolean italic) {
        try (InputStream is = FontLoader.class.getResourceAsStream("/fonts/" + fontName + ".ttf")) {
            var font = Font.createFont(Font.TRUETYPE_FONT, is);
            Map attributes = font.getAttributes();
            attributes.put(TextAttribute.LIGATURES, TextAttribute.LIGATURES_ON);

            if (bold) {
                attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
            }

            if (italic) {
                attributes.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
            }

            font = font.deriveFont(attributes);
            return font.deriveFont((float) fontSize);
        } catch (FontFormatException | IOException e) {
            LOGGER.error("Cannot load font " + fontName, e);
        }

        return new Font("Serif", Font.PLAIN, fontSize);
    }
}

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

    public static Font load(String fontName, int fontSize) {
        try (InputStream is = FontLoader.class.getResourceAsStream("/fonts/" + fontName + ".ttf")) {
            var font = Font.createFont(Font.TRUETYPE_FONT, is);
            Map attributes = font.getAttributes();
            attributes.put(TextAttribute.LIGATURES, TextAttribute.LIGATURES_ON);
            font = font.deriveFont(attributes);
            return font.deriveFont((float) fontSize);
        } catch (FontFormatException | IOException e) {
            LOGGER.error("Cannot load font " + fontName, e);
        }

        return new Font("Serif", Font.PLAIN, fontSize);
    }
}

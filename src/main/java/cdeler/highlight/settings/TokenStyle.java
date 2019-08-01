package cdeler.highlight.settings;

import java.awt.*;

import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class TokenStyle {
    private static final String DEFAULT_FONT_COLOR = "#000000";
    private static final TokenStyle FALLBACK_TOKEN_STYLE = new TokenStyle("#000000", false, false);

    private final boolean bold;
    private final boolean italic;
    private final Color color;
    private final AttributeSet highlightedAttributeSet;


    public TokenStyle(String color, boolean bold, boolean italic) {
        this(Color.decode(color), bold, italic);
    }

    public TokenStyle(String color) {
        this(color, false, false);
    }

    public TokenStyle(Color color, boolean bold, boolean italic) {
        this.color = color;
        this.bold = bold;
        this.italic = italic;

        MutableAttributeSet attributes = new SimpleAttributeSet();
        attributes.addAttribute(StyleConstants.Foreground, this.color);
        StyleConstants.setBold(attributes, this.bold);
        StyleConstants.setItalic(attributes, this.italic);

        this.highlightedAttributeSet = attributes;
    }

    public TokenStyle(Color color) {
        this(color, false, false);
    }

    public boolean isBold() {
        return bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public Color getColor() {
        return color;
    }

    public static TokenStyle getFallbackTokenStyle() {
        return FALLBACK_TOKEN_STYLE;
    }

    public AttributeSet asAttributeSet() {
        return highlightedAttributeSet;
    }

    public static String getDefaultFontColor() {
        return DEFAULT_FONT_COLOR;
    }
}

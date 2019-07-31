package cdeler.highlight.settings;

import java.awt.*;

import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class TokenStyle {
    private static final TokenStyle FALLBACK_TOKEN_STYLE = new TokenStyle("000000", false, false);
    private static final AttributeSet FALLBACK_ATTRIBUTE_SET;

    static {
        var style = new SimpleAttributeSet();
        StyleConstants.setItalic(style, FALLBACK_TOKEN_STYLE.isItalic());
        StyleConstants.setBold(style, FALLBACK_TOKEN_STYLE.isBold());
        StyleConstants.setForeground(style, FALLBACK_TOKEN_STYLE.getColor());
        FALLBACK_ATTRIBUTE_SET = style;
    }

    private boolean bold;
    private boolean italic;
    private Color color;

    public TokenStyle(String color, boolean bold, boolean italic) {
        this.color = Color.decode(color);
        this.bold = bold;
        this.italic = italic;
    }

    public TokenStyle(String color) {
        this(color, false, false);
    }

    public TokenStyle(Color color, boolean bold, boolean italic) {
        this.color = color;
        this.bold = bold;
        this.italic = italic;
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

    public static AttributeSet getFallbackAttributeSet() {
        return FALLBACK_ATTRIBUTE_SET;
    }
}

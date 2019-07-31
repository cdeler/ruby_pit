package cdeler.highlight.settings;

import java.awt.*;

import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class TokenStyle {
    private static final TokenStyle DEFAULT_TOKEN_STYLE = new TokenStyle("000000", false, false);
    private static final AttributeSet DEFAULT_ATTRIBUTE_SET;

    static {
        var style = new SimpleAttributeSet();
        StyleConstants.setItalic(style, DEFAULT_TOKEN_STYLE.isItalic());
        StyleConstants.setBold(style, DEFAULT_TOKEN_STYLE.isBold());
        StyleConstants.setForeground(style, Color.decode(DEFAULT_TOKEN_STYLE.getColor()));
        DEFAULT_ATTRIBUTE_SET = style;
    }

    private boolean bold;
    private boolean italic;
    private String color;

    public TokenStyle(String color, boolean bold, boolean italic) {
        this.color = color;
        this.bold = bold;
        this.italic = italic;
    }

    public boolean isBold() {
        return bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public String getColor() {
        return color;
    }

    public static TokenStyle getDefaultTokenStyle() {
        return DEFAULT_TOKEN_STYLE;
    }

    public static AttributeSet getDefaultAttributeSet() {
        return DEFAULT_ATTRIBUTE_SET;
    }
}

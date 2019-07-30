package cdeler.highlight.settings;

public class TokenStyle {
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
}

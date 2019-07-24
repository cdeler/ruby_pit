package cdeler.highlight;

public class HighlightError extends RuntimeException {
    public HighlightError(Exception ex) {
        super(ex);
    }
}

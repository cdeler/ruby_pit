package cdeler.highlight.highlighters;

enum HighlightEventPriority {
    REDRAW_VISIBLE_PART(0),
    REDRAW_ALL(1);

    private final int value;

    HighlightEventPriority(int val) {
        this.value = val;
    }

    public boolean isLessImportant(HighlightEventPriority other) {
        return (this.value - other.value) < 0;
    }

    public boolean isMoreImportant(HighlightEventPriority other) {
        return (this.value - other.value) >= 0;
    }
}

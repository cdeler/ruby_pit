package cdeler.highlight;

public class TokenLocation {
    public final int beginLine;
    public final int beginColumn;

    public final int endLine;
    public final int endColumn;

    public TokenLocation(int beginLine, int beginColumn, int endLine, int endColumn) {
        this.beginLine = beginLine;
        this.endLine = endLine;
        this.beginColumn = beginColumn;
        this.endColumn = endColumn;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TokenLocation)) {
            return false;
        }

        return (beginLine == ((TokenLocation) obj).beginLine)
                && (endLine == ((TokenLocation) obj).endLine)
                && (beginColumn == ((TokenLocation) obj).beginColumn)
                && (endColumn == ((TokenLocation) obj).endColumn);
    }

    // algorithm was taken from boost hash_combine (0x9e3779b9 also came from the boost)
    private static int hashCombine(int seed, int... rest) {
        for (int it : rest) {
            seed ^= (0x9e3779b9 + (it << 6) + (it >> 2));
        }
        return seed;
    }

    @Override
    public int hashCode() {
        return hashCombine(beginLine, endLine, beginColumn, endColumn);
    }
}

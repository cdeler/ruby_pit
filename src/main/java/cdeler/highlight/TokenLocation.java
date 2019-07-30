package cdeler.highlight;

import cdeler.core.ui.UIUtils;

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


    @Override
    public int hashCode() {
        return UIUtils.hashCombine(beginLine, endLine, beginColumn, endColumn);
    }

    @Override
    public String toString() {
        return "TokenLocation[from (" + beginLine + "," + beginColumn + "), to (" + endLine + "," + endColumn + ")]";
    }
}

package cdeler.core;

public class ArraySourceLine implements SourceLine {
    private static final int DEFAULT_CAPACITY = 16;

    int beginPosition;
    int endPosition;
    byte[] line;
    int capacity;

    public ArraySourceLine(int capacity) {
        this.capacity = capacity;
        this.line = new byte[this.capacity];
        this.beginPosition = capacity / 2;
        this.endPosition = capacity / 2;
    }

    public ArraySourceLine() {
        this(DEFAULT_CAPACITY);
    }

    public ArraySourceLine(String line) {
        this.capacity = getLengthWithExtraBuffer(line);
        this.line = new byte[this.capacity];
        this.beginPosition = (this.line.length - line.length()) / 2;
        this.endPosition = this.beginPosition + line.length();

        System.arraycopy(line.getBytes(), 0, this.line, this.beginPosition, line.length());
    }

    @Override
    public void insertAt(int pos, CharSequence data) {

    }

    @Override
    public void insertAt(int pos, char c) {
        preInsertCheck();

        if (pos + beginPosition >= endPosition) {
            line[endPosition] = (byte) c;
            endPosition++;
        } else if (pos <= 0) {
            beginPosition--;
            line[beginPosition] = (byte) c;
        } else {
            if (pos < length() / 2) {
                int copyLength = pos + 1;
                System.arraycopy(line, beginPosition, line, beginPosition - 1, copyLength);
                beginPosition--;

                line[beginPosition + pos] = (byte) c;
            } else {
                int copyLength = length() - pos;
                System.arraycopy(line, beginPosition + pos, line,
                        beginPosition + pos + 1, copyLength);
                endPosition++;

                line[beginPosition + pos] = (byte) c;
            }
        }
    }

    private void preInsertCheck() {
        if (endPosition >= capacity) {
            if (beginPosition < DEFAULT_CAPACITY) {
                enlarge();
            } else {
                center();
            }
        } else if (beginPosition == 0) {
            if (capacity - endPosition < DEFAULT_CAPACITY) {
                enlarge();
            } else {
                center();
            }
        }

    }

    private void center() {
        var oldBeginPosition = beginPosition;
        var length = length();

        beginPosition = (capacity - length) / 2;
        endPosition = beginPosition + length;

        System.arraycopy(line, oldBeginPosition, line, beginPosition, length);
    }

    private void enlarge() {
        var oldBeginPosition = beginPosition;
        var previousLine = line;
        var length = length();

        capacity = getLengthWithExtraBuffer(line);
        line = new byte[capacity];

        beginPosition = (capacity - length) / 2;
        endPosition = beginPosition + length;

        System.arraycopy(previousLine, oldBeginPosition, line, beginPosition, length());
    }

    @Override
    public char charAt(int pos) {
        return (char) line[beginPosition + pos];
    }


    private static int getLengthWithExtraBuffer(CharSequence line) {
        return line.length() * 3 / 2;
    }

    private static int getLengthWithExtraBuffer(byte[] line) {
        return line.length * 3 / 2;
    }

    public int length() {
        return endPosition - beginPosition;
    }

    public String toString() {
        return new String(line, beginPosition, length());
    }
}

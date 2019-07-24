package cdeler.core;

public class ArraySourceLine implements SourceLine {
    int beginPosition;
    int endPosition;
    byte[] line;

    public ArraySourceLine(String line) {
        this.line = new byte[getLengthWithExtraBuffer(line)];
        this.beginPosition = (this.line.length - line.length()) / 2;
        this.endPosition = this.beginPosition + line.length();

        System.arraycopy(line.getBytes(), 0, this.line, this.beginPosition, line.length());
    }

    @Override
    public void insertAt(int pos, CharSequence data) {

    }

    @Override
    public void insertAt(int pos, char c) {

    }

    @Override
    public char charAt(int pos) {
        return (char) line[beginPosition + pos];
    }


    private static int getLengthWithExtraBuffer(CharSequence line) {
        return line.length() * 6 / 5;
    }

    private static int getLengthWithExtraBuffer(char[] line) {
        return line.length * 6 / 5;
    }

    public int length() {
        return endPosition - beginPosition;
    }

    public String toString() {
        return new String(line, beginPosition, length());
    }
}

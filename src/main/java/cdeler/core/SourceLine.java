package cdeler.core;

public interface SourceLine {
    String toString();

    void insertAt(int pos, char c);

    char charAt(int pos);

    int length();

    void removeAt(int pos, int len);
}

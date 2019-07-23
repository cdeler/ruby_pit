package cdeler.core;

public interface SourceStorage {
    char getCharAt(int lineNumber, int position);

    void insertCharAt(int lineNumber, int position);
}

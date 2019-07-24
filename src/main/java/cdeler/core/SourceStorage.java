package cdeler.core;

import java.io.InputStream;
import java.util.List;

public interface SourceStorage {
    char getCharAt(int lineNumber, int position);

    void insertCharAt(int lineNumber, int position, char symbol);

    void load(InputStream is) throws IDEException;

    void deleteLine(int lineNum);

    void insertLine(int lineNum, String sourceLine);

    void insertLine(int lineNum);

    List<String> dump();
}

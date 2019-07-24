package cdeler.core;

import java.io.InputStream;
import java.io.OutputStream;

public interface SourceStorage {
    char getCharAt(int lineNumber, int position);

    void insertCharAt(int lineNumber, int position, char symbol);

    void insertCharAt(int lineNumber, int position, CharSequence symbols);

    void load(InputStream is);

    OutputStream dump();
}

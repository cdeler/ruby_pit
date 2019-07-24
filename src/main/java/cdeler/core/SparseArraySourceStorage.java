package cdeler.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SparseArraySourceStorage implements SourceStorage {
    private List<ArraySourceLine> sourceLines;

    public SparseArraySourceStorage() {
        sourceLines = new ArrayList<>();
    }

    @Override
    public char getCharAt(int lineNumber, int position) {
        return sourceLines.get(lineNumber).charAt(position);
    }

    @Override
    public void insertCharAt(int lineNumber, int position, char symbol) {
        sourceLines.get(lineNumber).insertAt(position, symbol);
    }

    @Override
    public void insertCharAt(int lineNumber, int position, CharSequence symbols) {
        sourceLines.get(lineNumber).insertAt(position, symbols);
    }

    @Override
    public void load(InputStream is) {
        try (var reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
             var linesStream = reader.lines()) {
            sourceLines = linesStream.map(ArraySourceLine::new).collect(Collectors.toList());
        } catch (IOException e) {

        }
    }

    @Override
    public OutputStream dump() {
        return null;
    }
}

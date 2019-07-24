package cdeler.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SparseArraySourceStorage implements SourceStorage {
    private static final Logger LOGGER = LoggerFactory.getLogger(SparseArraySourceStorage.class);

    private List<ArraySourceLine> sourceLines;

    public SparseArraySourceStorage() {
        sourceLines = new ArrayList<>();
    }

    public SparseArraySourceStorage(final List<String> textLines) {
        sourceLines = textLines.stream().map(ArraySourceLine::new).collect(Collectors.toList());
    }

    public SparseArraySourceStorage(InputStream is) throws IDEException {
        this();
        load(is);
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
    public void load(InputStream is) throws IDEException {
        try (var reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
             var linesStream = reader.lines()) {
            sourceLines = linesStream.map(ArraySourceLine::new).collect(Collectors.toList());
        } catch (IOException e) {
            LOGGER.error("Failed to load source file from file", e);
            throw new IDEException(e);
        }
    }

    @Override
    public void deleteLine(int lineNum) {
        if ((0 <= lineNum) && (lineNum < sourceLines.size())) {
            sourceLines.remove(lineNum);
        }
    }

    @Override
    public void insertLine(int lineNum, String sourceLine) {
        sourceLines.add(lineNum, new ArraySourceLine(sourceLine));
    }

    @Override
    public void insertLine(int lineNum) {
        sourceLines.add(lineNum, new ArraySourceLine());
    }

    @Override
    public List<String> dump() {
        return sourceLines.stream().map(ArraySourceLine::toString).collect(Collectors.toList());
    }
}

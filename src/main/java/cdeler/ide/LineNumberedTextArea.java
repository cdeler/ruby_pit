package cdeler.ide;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cdeler.core.FontLoader;
import cdeler.core.UIUtils;

public class LineNumberedTextArea extends JTextArea {
    private static final Logger LOGGER = LoggerFactory.getLogger(LineNumberedTextArea.class);
    private static final int MIN_SYMBOL_WIDTH = 3;

    private final JTextArea textArea;
    private final List<Integer> lineNumbers;

    public LineNumberedTextArea(JTextArea textArea) {
        this.textArea = textArea;
        this.lineNumbers = new ArrayList<>();
        setBackground(Color.LIGHT_GRAY);
        setEditable(false);
        setFont(FontLoader.load("iosevka-regular", 20));
    }

    public synchronized void highlightCaretPosition() {
        if (lineNumbers.isEmpty()) {
            return;
        }

        try {
            int caretPosition = textArea.getCaretPosition();
            int caretLine = textArea.getLineOfOffset(caretPosition);

            var highlightedArea = UIUtils.getHighlightedArea(lineNumbers, caretLine + 1);

            if (highlightedArea.isPresent()) {
                getHighlighter().removeAllHighlights();
                getHighlighter().addHighlight(
                        getLineStartOffset(highlightedArea.get().first()),
                        getLineEndOffset(highlightedArea.get().second()),
                        new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW));
            }
        } catch (BadLocationException e) {
            LOGGER.error("Invalid caret position", e);
        } catch (IndexOutOfBoundsException e) {
            LOGGER.error("Invalid highlight location", e);
        }
    }

    public synchronized void updateLineNumbers() {
        this.lineNumbers.clear();
        this.lineNumbers.addAll(getLineNumbersData());

        setText(UIUtils.formatLineNumbers(this.lineNumbers, MIN_SYMBOL_WIDTH));
    }

    // TODO sync issue when textArea is changing in the loop
    private List<Integer> getLineNumbersData() {
        LOGGER.debug("Trace getLineNumbersText()");

        List<Integer> lineNumbersData = new ArrayList<>(lineNumbers.size());

        try {
            int rowStartOffset = Utilities.getRowStart(textArea, 0);
            int endOffset = textArea.getLineEndOffset(textArea.getLineCount() - 1);

            if (endOffset >= 1) {
                while (rowStartOffset <= endOffset) {
                    int lineNumber = textArea.getLineOfOffset(rowStartOffset) + 1;

                    lineNumbersData.add(lineNumber);
                    rowStartOffset = Utilities.getRowEnd(textArea, rowStartOffset) + 1;
                }
            }
        } catch (BadLocationException e) {
            // we can just ignore it, since this error is raised when (look at line 70)
            // rowStartOffset becomes invalid when we speedly delete text lines from textArea
        }

        return lineNumbersData;
    }
}
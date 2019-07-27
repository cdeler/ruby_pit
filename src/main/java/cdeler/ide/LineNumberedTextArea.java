package cdeler.ide;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cdeler.core.FontLoader;
import cdeler.core.StringUtils;

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
        var text = getText();

        try (Scanner scanner = new Scanner(text).useDelimiter(System.lineSeparator())) {
            int caretPosition = textArea.getCaretPosition();
            int caretLine = textArea.getLineOfOffset(caretPosition);
            int textAreaLineNumber = -1;
            int highlightLineNumber = -1;

            while (scanner.hasNext()) {
                var currentLine = scanner.next();

                highlightLineNumber++;
                if (!currentLine.isBlank())
                    textAreaLineNumber++;

                if (textAreaLineNumber == caretLine) {
                    int beginHighlightedOffset = getLineStartOffset(highlightLineNumber);

                    while (scanner.hasNext()) {
                        var nextLine = scanner.next();

                        if (nextLine.isBlank()) {
                            highlightLineNumber++;
                        } else {
                            break;
                        }
                    }

                    int endHighlightedOffset = getLineEndOffset(highlightLineNumber);
                    getHighlighter().removeAllHighlights();
                    getHighlighter().addHighlight(beginHighlightedOffset, endHighlightedOffset,
                            new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW));

                    return;
                }

            }
        } catch (BadLocationException e) {
        }
    }

    public void updateLineNumbers() {
        this.lineNumbers.clear();
        this.lineNumbers.addAll(getLineNumbersData());

        setText(StringUtils.formatLineNumbers(this.lineNumbers, MIN_SYMBOL_WIDTH));
        highlightCaretPosition();
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
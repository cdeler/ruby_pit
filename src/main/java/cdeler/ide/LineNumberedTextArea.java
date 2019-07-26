package cdeler.ide;

import java.awt.*;
import java.nio.CharBuffer;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cdeler.core.FontLoader;

public class LineNumberedTextArea extends JTextArea {
    private static final Logger LOGGER = LoggerFactory.getLogger(LineNumberedTextArea.class);
    private static final int MIN_SYMBOL_WIDTH = 3;

    private final JTextArea textArea;

    public LineNumberedTextArea(JTextArea textArea) {
        this.textArea = textArea;
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
        String lineNumbersText = getLineNumbersText();
        setText(lineNumbersText);
        highlightCaretPosition();
    }

    // TODO sync issue when textArea is changing in the loop
    private String getLineNumbersText() {
        LOGGER.debug("Trace getLineNumbersText()");

        StringBuilder lineNumbersTextBuilder = new StringBuilder();

        String format = "%" + getWidthInSymbols() + "d";
        String emptyLine = CharBuffer.allocate(getWidthInSymbols()).toString().replace('\0', ' ');

        try {
            int rowStartOffset = Utilities.getRowStart(textArea, 0);
            int endOffset = textArea.getLineEndOffset(textArea.getLineCount() - 1);

            if (endOffset >= 1) {
                int prevLineNumber = -1;
                while (rowStartOffset <= endOffset) {
                    int lineNumber = textArea.getLineOfOffset(rowStartOffset) + 1;

                    if (lineNumber != prevLineNumber) {
                        lineNumbersTextBuilder.append(String.format(format, lineNumber)).append(System.lineSeparator());
                    } else {
                        lineNumbersTextBuilder.append(emptyLine).append(System.lineSeparator());
                    }
                    prevLineNumber = lineNumber;

                    rowStartOffset = Utilities.getRowEnd(textArea, rowStartOffset) + 1;
                }
            }
        } catch (BadLocationException e) {
            // we can just ignore it, since this error is raised when (look at line 70)
            // rowStartOffset becomes invalid when we speedly delete text lines from textArea
        }

        return lineNumbersTextBuilder.toString();
    }

    private int getWidthInSymbols() {
        return Math.max((int) Math.ceil(Math.log10(textArea.getLineCount())), MIN_SYMBOL_WIDTH);
    }
}
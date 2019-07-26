package cdeler.ide;

import java.awt.*;
import java.nio.CharBuffer;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;
import javax.swing.text.Utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cdeler.core.FontLoader;

public class LineNumberedTextPane extends JTextArea {
    private static final Logger LOGGER = LoggerFactory.getLogger(LineNumberedTextPane.class);
    private static final int MIN_SYMBOL_WIDTH = 3;

    private final JTextArea textArea;

    public LineNumberedTextPane(JTextArea textArea) {
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
            int currentComponentLineNumber = -1;
            int toHighlightLineNumber = -1;

            while (scanner.hasNext()) {
                var currentLine = scanner.next();

                if (!currentLine.isBlank())
                    currentComponentLineNumber++;
                toHighlightLineNumber++;

                if (currentComponentLineNumber == caretLine) {
                    int beginOffset = getLineStartOffset(toHighlightLineNumber);
                    int endOffset = getLineEndOffset(toHighlightLineNumber);

                    while (scanner.hasNext()) {
                        var nextLine = scanner.next();

                        if (nextLine.isBlank()) {
                            toHighlightLineNumber++;
                        } else {
                            endOffset = getLineEndOffset(toHighlightLineNumber);

                            break;
                        }
                    }
                    getHighlighter().removeAllHighlights();
                    getHighlighter().addHighlight(beginOffset, endOffset,
                            new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW));

                    return;
                }

            }
        } catch (BadLocationException e) {

        }

        /*
        try {


            int caretPosition = textArea.getCaretPosition();

            if (caretPosition >= 0) {
                int caretLine = textArea.getLineOfOffset(caretPosition);

                int caretLineBeginOffset = Utilities.getRowStart(textArea, caretPosition);
                int caretLineEndOffset = Utilities.getRowEnd(textArea, caretPosition);

                int lineWidthInSymbols = getWidthInSymbols() + System.lineSeparator().length();
                int firstOffset = caretLineBeginOffset; //caretLine * lineWidthInSymbols;
                int lastOffset = caretLineEndOffset; //(caretLine + 1) * lineWidthInSymbols;

                getHighlighter().removeAllHighlights();
                getHighlighter().addHighlight(firstOffset, lastOffset,
                        new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW));
            }

        } catch (BadLocationException e) {
            LOGGER.warn("Unable to highlightCaretPosition", e);
        }*/
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
                    int lineNumber = getTextLineNumber(format, emptyLine, rowStartOffset);

                    if (lineNumber != prevLineNumber) {
                        lineNumbersTextBuilder.append(String.format(format, lineNumber)).append(System.lineSeparator());
                    } else {
                        lineNumbersTextBuilder.append(emptyLine).append(System.lineSeparator());
                    }
                    prevLineNumber = lineNumber;

                    // String lineNumber = getTextLineNumber(format, emptyLine, rowStartOffset);
                    // lineNumbersTextBuilder.append(lineNumber).append(System.lineSeparator());
                    rowStartOffset = Utilities.getRowEnd(textArea, rowStartOffset) + 1;
                }
            }
        } catch (BadLocationException e) {
            // we can just ignore it, since this error is raised when (look at line 70)
            // rowStartOffset becomes invalid when we speedly delete text lines from textArea
        }

        return lineNumbersTextBuilder.toString();
    }

    private int getTextLineNumber(String format, String emptyLineValue, int rowStartOffset) {
        Element root = textArea.getDocument().getDefaultRootElement();
        int index = root.getElementIndex(rowStartOffset);
        Element line = root.getElement(index);

        return index + 1;
        /*
        if (line.getStartOffset() == rowStartOffset) {
            return String.format(format, index + 1);
        } else {
            return emptyLineValue;
        }*/
    }

    private int getWidthInSymbols() {
        return Math.max((int) Math.ceil(Math.log10(textArea.getLineCount())), MIN_SYMBOL_WIDTH);
    }
}
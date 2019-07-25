package cdeler.ide;

import java.awt.*;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cdeler.core.FontLoader;

public class LineNumberingTextArea extends JTextArea {
    private static final Logger LOGGER = LoggerFactory.getLogger(LineNumberingTextArea.class);

    private JTextArea textArea;

    public LineNumberingTextArea(JTextArea textArea) {
        this.textArea = textArea;
        setBackground(Color.LIGHT_GRAY);
        setEditable(false);
        setFont(FontLoader.load("iosevka-regular", 20));
    }

    public void updateLineNumbers() {
        String lineNumbersText = getLineNumbersText();
        setText(lineNumbersText);
    }

    private String getLineNumbersText() {
        LOGGER.debug("Trace getLineNumbersText()");
        int caretPosition = textArea.getDocument().getLength();
        Element root = textArea.getDocument().getDefaultRootElement();
        StringBuilder lineNumbersTextBuilder = new StringBuilder();

        LOGGER.info("Trace getColumns=" + textArea.getColumns() + " getLineCount=" + textArea.getLineCount());
        if (textArea.getGraphics() != null) { // it's null before rendering
            try {
                int rowStartOffset = Utilities.getRowStart(textArea, 0);
                int endOffset = textArea.getLineEndOffset(textArea.getLineCount() - 1);

                while (rowStartOffset <= endOffset) {
                    String lineNumber = getTextLineNumber(rowStartOffset);
                    lineNumbersTextBuilder.append(lineNumber).append(System.lineSeparator());
                    rowStartOffset = Utilities.getRowEnd(textArea, rowStartOffset) + 1;
                }
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
        return lineNumbersTextBuilder.toString();
    }

    private String getTextLineNumber(int rowStartOffset) {
        Element root = textArea.getDocument().getDefaultRootElement();
        int index = root.getElementIndex(rowStartOffset);
        Element line = root.getElement(index);

        if (line.getStartOffset() == rowStartOffset)
            return String.valueOf(index + 1);
        else
            return "";
    }

}
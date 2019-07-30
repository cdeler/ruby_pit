package cdeler.ide;

import java.awt.*;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cdeler.core.FontLoader;
import cdeler.core.ui.UIUtils;

public class LineNumberedTextArea extends JTextArea {
    private static final Logger LOGGER = LoggerFactory.getLogger(LineNumberedTextArea.class);
    private static final int MIN_SYMBOL_WIDTH = 3;

    private final JTextPane textArea;

    public LineNumberedTextArea(JTextPane textArea) {
        this.textArea = textArea;
        setBackground(Color.LIGHT_GRAY);
        setEditable(false);
        setFont(FontLoader.load("iosevka-regular", 20));
    }

    public synchronized void highlightCaretPosition() {
        try {
            int caretPosition = textArea.getCaretPosition();
            Element textAreaRoot = textArea.getDocument().getDefaultRootElement();
            if (textAreaRoot.getEndOffset() > 0) {
                int rowNumber = textAreaRoot.getElementIndex(caretPosition);
                int startHighlightOffset = getLineStartOffset(rowNumber);
                int endHighlightOffset = getLineEndOffset(rowNumber);

                getHighlighter().removeAllHighlights();
                getHighlighter().addHighlight(
                        startHighlightOffset,
                        endHighlightOffset,
                        new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW));
            }
        } catch (IndexOutOfBoundsException | BadLocationException e) {
            LOGGER.error("Unable to highlight line numbers", e);
        }
    }

    public synchronized void updateLineNumbers() {
        int linesCount = textArea.getDocument().getDefaultRootElement().getElementCount();

        setText(UIUtils.formatLineNumbers(1, linesCount + 1, MIN_SYMBOL_WIDTH));
    }

}
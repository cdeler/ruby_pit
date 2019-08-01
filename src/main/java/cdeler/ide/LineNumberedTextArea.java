package cdeler.ide;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cdeler.core.ui.UIUtils;
import cdeler.highlight.settings.UISettingsManager;

class LineNumberedTextArea extends JTextArea {
    private static final Logger LOGGER = LoggerFactory.getLogger(LineNumberedTextArea.class);
    private static final int MIN_SYMBOL_WIDTH = 3;

    private final JTextPane textArea;
    private final UISettingsManager settingsManager;

    LineNumberedTextArea(UISettingsManager uiSettingsManager, JTextPane textArea) {
        this.textArea = textArea;
        this.settingsManager = uiSettingsManager;

        setBackground(settingsManager.getDefaultActiveStyle().getColor());
        setForeground(settingsManager.getActiveBackgroundColor());

        setEditable(false);
        setFont(uiSettingsManager.getActiveFont());
    }

    synchronized void updateColors() {
        setForeground(settingsManager.getActiveBackgroundColor());
        setBackground(settingsManager.getDefaultActiveStyle().getColor());
    }

    synchronized void highlightCaretPosition() {
        try {
            Element textAreaRoot = textArea.getDocument().getDefaultRootElement();

            int caretPosition = textArea.getCaretPosition();
            int textAreaEndOffset = textAreaRoot.getEndOffset();

            if (0 <= caretPosition && caretPosition < textAreaEndOffset) {
                int rowNumber = textAreaRoot.getElementIndex(caretPosition);
                int startHighlightOffset = getLineStartOffset(rowNumber);
                int endHighlightOffset = getLineEndOffset(rowNumber);

                getHighlighter().removeAllHighlights();
                getHighlighter().addHighlight(
                        startHighlightOffset,
                        endHighlightOffset,
                        new DefaultHighlighter.DefaultHighlightPainter(settingsManager.getActiveLineHighlightColor()));
            }
        } catch (IndexOutOfBoundsException | BadLocationException e) {
            // LOGGER.error("Unable to highlight line numbers", e);
        }
    }

    synchronized void updateLineNumbers() {
        int linesCount = textArea.getDocument().getDefaultRootElement().getElementCount();

        setText(UIUtils.formatLineNumbers(1, linesCount + 1, MIN_SYMBOL_WIDTH));
    }

}
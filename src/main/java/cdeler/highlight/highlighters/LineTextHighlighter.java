package cdeler.highlight.highlighters;

import java.awt.*;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cdeler.highlight.settings.UISettingsManager;
import cdeler.highlight.token.TokenType;
import cdeler.highlight.token.Tokenizer;

public class LineTextHighlighter extends BaseTextHighlighter {
    private static final Logger LOGGER = LoggerFactory.getLogger(LineTextHighlighter.class);

    public LineTextHighlighter(Tokenizer tokenizer, UISettingsManager settingManager) {
        super(tokenizer, settingManager);
        LOGGER.warn("LineTextHighlighter doesn't fully support text highlighting");
    }

    @Override
    protected void highlight(JTextPane textArea, TokenType tokenType, int startOffset, int endOffset) {
        try {
            textArea.getHighlighter().addHighlight(startOffset, endOffset,
                    new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW));
        } catch (BadLocationException e) {
            LOGGER.error("Unable to highlight location from " + startOffset + " to " + endOffset, e);
        }
    }

    @Override
    protected void clearHighlight(JTextPane textArea) {
        textArea.getHighlighter().removeAllHighlights();
    }
}

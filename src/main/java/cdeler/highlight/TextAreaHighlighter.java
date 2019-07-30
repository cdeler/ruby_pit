package cdeler.highlight;

import java.awt.*;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextAreaHighlighter {
    private static final Logger LOGGER = LoggerFactory.getLogger(TextAreaHighlighter.class);

    private final Tokenizer tokenizer;

    public TextAreaHighlighter(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public synchronized void highlight(JTextArea textArea) {
        LOGGER.debug("Enter highlight");

        var tokens = tokenizer.harvest(textArea.getText());

        textArea.getHighlighter().removeAllHighlights();
        tokens.forEach(sourceToken -> {
            try {
                var location = sourceToken.getLocation();

                var startOffset = textArea.getLineStartOffset(location.beginLine) + location.beginColumn;
                var endOffset = textArea.getLineStartOffset(location.endLine) + location.endColumn;

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Highlight node " + sourceToken.getTokenType()
                            + " from " + startOffset + " to " + endOffset);
                }

                textArea.getHighlighter().addHighlight(
                        startOffset,
                        endOffset,
                        new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW));
            } catch (BadLocationException e) {
                LOGGER.error("Unable to highlight code", e);
            }
        });

        LOGGER.debug("Leave highlight");
    }
}

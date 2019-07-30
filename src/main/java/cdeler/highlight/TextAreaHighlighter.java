package cdeler.highlight;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextAreaHighlighter {
    private static final Logger LOGGER = LoggerFactory.getLogger(TextAreaHighlighter.class);

    private final Tokenizer tokenizer;

    public TextAreaHighlighter(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public synchronized void highlight(JTextPane textArea) {
        LOGGER.debug("Enter highlight");

        var tokens = tokenizer.harvest(textArea.getText());

        textArea.getHighlighter().removeAllHighlights();
        tokens.forEach(sourceToken -> {
            var location = sourceToken.getLocation();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Location is " + location);
            }

            Element textAreaRoot = textArea.getDocument().getDefaultRootElement();

            if (textAreaRoot != null) {
                var startOffset =
                        textAreaRoot.getElement(location.beginLine).getStartOffset() + location.beginColumn;
                var endOffset = textAreaRoot.getElement(location.endLine).getStartOffset() + location.endColumn;

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.error("Highlighting node " + sourceToken.getTokenType()
                            + " from " + startOffset + " to " + endOffset);
                }

                if (0 <= startOffset && startOffset < endOffset) {
                    highlight(textArea, startOffset, endOffset);
                }
            }
        });

        LOGGER.debug("Leave highlight");
    }

    private static void highlight(JTextPane textArea, int startOffset, int endOffset) {
        StyledDocument doc = (StyledDocument) textArea.getDocument();
        Element element = doc.getCharacterElement(startOffset);
        AttributeSet as = element.getAttributes();
        MutableAttributeSet asNew = new SimpleAttributeSet(as.copyAttributes());
        StyleConstants.setBold(asNew, true);
        doc.setCharacterAttributes(startOffset, endOffset - startOffset, asNew, true);
    }
}

package cdeler.highlight;

import java.awt.*;

import javax.swing.*;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cdeler.highlight.settings.TokenStyle;
import cdeler.highlight.settings.UISettingsManager;

public class StyleTextHighlighter {
    private static final Logger LOGGER = LoggerFactory.getLogger(StyleTextHighlighter.class);

    private final Tokenizer tokenizer;
    private final UISettingsManager settingsManager;

    public StyleTextHighlighter(Tokenizer tokenizer, UISettingsManager settingManager) {
        this.tokenizer = tokenizer;
        this.settingsManager = settingManager;
    }

    public void highlight(JTextPane textArea) {
        LOGGER.debug("Enter highlight");

        var tokens = tokenizer.harvest(textArea.getText());

        // TODO we should rewrite only difference of parts of AST
        clearHighlight(textArea);
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
                    LOGGER.debug("Highlighting node " + sourceToken.getTokenType()
                            + " from " + startOffset + " to " + endOffset);
                }

                if (0 <= startOffset && startOffset < endOffset) {
                    highlight(textArea, sourceToken.getTokenType(), startOffset, endOffset);
                }
            }
        });

        LOGGER.debug("Leave highlight");
    }

    private void clearHighlight(JTextPane textArea) {
        Element textAreaRoot = textArea.getDocument().getDefaultRootElement();
        int length = textAreaRoot.getEndOffset() - textAreaRoot.getStartOffset();

        if (length >= 0) {
            StyledDocument doc = (StyledDocument) textArea.getDocument();
            doc.removeStyle("highlight");
            doc.setCharacterAttributes(0, length, TokenStyle.getDefaultAttributeSet(), true);
        }
    }

    private void highlight(JTextPane textArea, TokenType tokenType, int startOffset, int endOffset) {
        StyledDocument doc = (StyledDocument) textArea.getDocument();

        Element element = doc.getCharacterElement(startOffset);

        TokenStyle tokenStyle = settingsManager.getActiveStyleForTokenType(tokenType);
        var attributes = new SimpleAttributeSet(element.getAttributes());

        StyleConstants.setItalic(attributes, tokenStyle.isItalic());
        StyleConstants.setBold(attributes, tokenStyle.isBold());
        StyleConstants.setForeground(attributes, Color.decode(tokenStyle.getColor()));

        doc.setCharacterAttributes(startOffset, endOffset - startOffset, attributes, true);
    }
}

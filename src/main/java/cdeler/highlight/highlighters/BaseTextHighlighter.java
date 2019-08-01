package cdeler.highlight.highlighters;

import javax.swing.*;
import javax.swing.text.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cdeler.highlight.settings.UISettingsManager;
import cdeler.highlight.token.TokenType;
import cdeler.highlight.token.Tokenizer;

abstract class BaseTextHighlighter implements TextHighlighter {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseTextHighlighter.class);

    protected final Tokenizer tokenizer;
    protected final UISettingsManager settingsManager;

    public BaseTextHighlighter(Tokenizer tokenizer, UISettingsManager settingManager) {
        this.tokenizer = tokenizer;
        this.settingsManager = settingManager;
    }

    @Override
    public void highlight(JTextPane textArea) {
        LOGGER.debug("Enter highlight");

        var tokens = tokenizer.harvest(textArea.getText());

        // TODO we should clear only changed parts of AST
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

    protected abstract void highlight(JTextPane textArea, TokenType tokenType, int startOffset, int endOffset);

    protected abstract void clearHighlight(JTextPane textArea);
}

package cdeler.highlight.highlighters;

import java.awt.*;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyledDocument;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cdeler.highlight.settings.UISettingsManager;
import cdeler.highlight.token.TokenType;
import cdeler.highlight.token.Tokenizer;

class StyleTextHighlighter extends BaseTextHighlighter {
    private static final Logger LOGGER = LoggerFactory.getLogger(StyleTextHighlighter.class);
    private static final int HIGHLIGHT_CHUNK_SIZE = 250;

    public StyleTextHighlighter(Tokenizer tokenizer, UISettingsManager settingManager) {
        super(tokenizer, settingManager);
    }

    private void clearHighlightInternal(@NotNull StyledDocument document, @NotNull AttributeSet cleanAttrs,
                                        int startOffset, int endOffset) {
        int length = endOffset - startOffset;

        if (length > 0) {
            document.setCharacterAttributes(startOffset, length, cleanAttrs, false);
        }
    }

    @Override
    protected void highlight(JTextPane textArea, TokenType tokenType, int startOffset, int endOffset) {
        StyledDocument document = textArea.getStyledDocument();
        AttributeSet attributes = settingsManager.getActiveStyleForTokenType(tokenType).asAttributeSet();

        clearHighlightInternal(document, attributes, startOffset, endOffset);
    }

    private void clearHighlightByChunk(JTextPane textArea, int startOffset, int endOffset) {
        if (0 <= startOffset && startOffset < endOffset) {
            var document = textArea.getStyledDocument();
            var attributes = settingsManager.getDefaultActiveStyle().asAttributeSet();
            int length = endOffset - startOffset;

            while (startOffset < endOffset) {
                int cleanupLength = Math.min(HIGHLIGHT_CHUNK_SIZE, length);

                clearHighlightInternal(document, attributes, startOffset, endOffset);

                startOffset += cleanupLength;
                length = endOffset - startOffset;
            }
        }
    }

    protected void clearHighlight(@NotNull JTextPane textArea, @NotNull Rectangle clearArea) {
        int lineHeight = textArea.getFontMetrics(textArea.getFont()).getHeight();
        var visibleLocationPoint = clearArea.getLocation();

        int startOffset = textArea.viewToModel2D(visibleLocationPoint) - lineHeight;
        visibleLocationPoint.y += clearArea.height - lineHeight;

        int endOffset = textArea.viewToModel2D(visibleLocationPoint);

        clearHighlightByChunk(textArea, Math.max(0, startOffset), endOffset);
    }

}

package cdeler.highlight.highlighters;

import java.awt.*;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cdeler.highlight.settings.UISettingsManager;
import cdeler.highlight.token.TokenType;
import cdeler.highlight.token.Tokenizer;

class StyleTextHighlighter extends BaseTextHighlighter {
    private static final Logger LOGGER = LoggerFactory.getLogger(StyleTextHighlighter.class);

    private final AttributeSet blackAttributeSet;

    public StyleTextHighlighter(Tokenizer tokenizer, UISettingsManager settingManager) {
        super(tokenizer, settingManager);

        StyleContext styleContext = StyleContext.getDefaultStyleContext();
        blackAttributeSet = styleContext.addAttribute(styleContext.getEmptySet(),
                StyleConstants.Foreground,
                Color.BLACK);
    }

    @Override
    protected void clearHighlight(JTextPane textArea) {
        StyledDocument document = textArea.getStyledDocument();
        Element textAreaRoot = document.getDefaultRootElement();
        int length = textAreaRoot.getEndOffset() - textAreaRoot.getStartOffset();

        if (length >= 0) {
            document.setCharacterAttributes(0, textArea.getText().length(), blackAttributeSet, true);
        }
    }

    @Override
    protected void highlight(JTextPane textArea, TokenType tokenType, int startOffset, int endOffset) {
        StyledDocument document = textArea.getStyledDocument();
        AttributeSet attributes = settingsManager.getActiveStyleForTokenType(tokenType).getHighlightedAttributeSet();

        document.setCharacterAttributes(startOffset, endOffset - startOffset, attributes, false);
    }
}

package cdeler.highlight.highlighters;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cdeler.highlight.settings.UISettingsManager;
import cdeler.highlight.token.TokenType;
import cdeler.highlight.token.Tokenizer;

class StyleTextHighlighter extends BaseTextHighlighter {
    private static final Logger LOGGER = LoggerFactory.getLogger(StyleTextHighlighter.class);

    public StyleTextHighlighter(Tokenizer tokenizer, UISettingsManager settingManager) {
        super(tokenizer, settingManager);
    }

    @Override
    protected void clearHighlight(JTextPane textArea) {
        StyledDocument document = textArea.getStyledDocument();
        Element textAreaRoot = document.getDefaultRootElement();
        int length = textAreaRoot.getEndOffset() - textAreaRoot.getStartOffset();

        if (length >= 0) {
            document.setCharacterAttributes(0, textArea.getText().length(),
                    settingsManager.getDefaultActiveStyle().asAttributeSet(), true);
        }
    }

    @Override
    protected void highlight(JTextPane textArea, TokenType tokenType, int startOffset, int endOffset) {
        StyledDocument document = textArea.getStyledDocument();
        AttributeSet attributes = settingsManager.getActiveStyleForTokenType(tokenType).asAttributeSet();

        document.setCharacterAttributes(startOffset, endOffset - startOffset, attributes, false);
    }
}

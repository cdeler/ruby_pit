package cdeler.highlight.highlighters;

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
import cdeler.highlight.token.TokenType;
import cdeler.highlight.token.Tokenizer;

class StyleTextHighlighter extends BaseTextHighlighter {
    private static final Logger LOGGER = LoggerFactory.getLogger(StyleTextHighlighter.class);

    public StyleTextHighlighter(Tokenizer tokenizer, UISettingsManager settingManager) {
        super(tokenizer, settingManager);
    }

    @Override
    protected void clearHighlight(JTextPane textArea) {
        Element textAreaRoot = textArea.getDocument().getDefaultRootElement();
        int length = textAreaRoot.getEndOffset() - textAreaRoot.getStartOffset();

        if (length >= 0) {
            StyledDocument doc = (StyledDocument) textArea.getDocument();
            doc.removeStyle("highlight");
            doc.setCharacterAttributes(0, length, TokenStyle.getDefaultAttributeSet(), true);
        }
    }

    @Override
    protected void highlight(JTextPane textArea, TokenType tokenType, int startOffset, int endOffset) {
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

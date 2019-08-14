package cdeler.highlight.highlighters;

import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cdeler.highlight.settings.UISettingsManager;
import cdeler.highlight.token.TokenLocation;
import cdeler.highlight.token.TokenType;
import cdeler.highlight.token.Tokenizer;

abstract class BaseTextHighlighter implements TextHighlighter {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseTextHighlighter.class);
    private static final int HIGHLIGHT_SLEEP_DELAY_MS = 1000;

    protected final Tokenizer tokenizer;
    protected final UISettingsManager settingsManager;
    private volatile Future<Boolean> event;
    private final ExecutorService executor;

    public BaseTextHighlighter(Tokenizer tokenizer, UISettingsManager settingManager) {
        this.tokenizer = tokenizer;
        this.settingsManager = settingManager;
        this.event = null;
        this.executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void highlight(JTextPane textArea, JScrollPane scrollPane) {
        if (this.event != null && (!this.event.isCancelled() || !this.event.isDone())) {
            this.event.cancel(true);
        }

        var tokens = tokenizer.harvest(textArea.getText());

        event = executor.submit(() -> {
            LOGGER.error("Enter highlight");

            Thread.sleep(HIGHLIGHT_SLEEP_DELAY_MS);

            // TODO we dont need clear highlight if we add all words as a tokens with type = unknown
            // think up.... ^
            // TODO 2 remake clearing
            clearHighlight(textArea);

            var visibleRect = textArea.getVisibleRect();
            int lineHeight = textArea.getFontMetrics(textArea.getFont()).getHeight();

            tokens.forEach(sourceToken -> {
                var location = sourceToken.getLocation();

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Location is " + location);
                }

                var textAreaRoot = textArea.getDocument().getDefaultRootElement();

                if (textAreaRoot != null) {
                    var startOffset =
                            textAreaRoot.getElement(location.beginLine).getStartOffset() + location.beginColumn;
                    var endOffset = textAreaRoot.getElement(location.endLine).getStartOffset() + location.endColumn;


                    if (0 <= startOffset && startOffset < endOffset) {
                        if (isVisibleToken(visibleRect, lineHeight, location)) {
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("Highlighting node " + sourceToken.getTokenType()
                                        + " from " + startOffset + " to " + endOffset);
                            }

                            highlight(textArea, sourceToken.getTokenType(), startOffset, endOffset);
                        }
                    }
                }
            });

            LOGGER.error("Leave highlight");

            return true;
        });

    }

    private static boolean isVisibleToken(Rectangle visibleRect, int lineHeight, TokenLocation location) {
        return visibleRect.intersects(new Rectangle(0, (location.beginLine - 1) * lineHeight, 10,
                (location.endLine - location.beginLine + 1) * lineHeight));
    }

    protected abstract void highlight(JTextPane textArea, TokenType tokenType, int startOffset, int endOffset);

    protected abstract void clearHighlight(JTextPane textArea);
}

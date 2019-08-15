package cdeler.highlight.highlighters;

import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.*;

import org.apache.commons.lang3.concurrent.ConcurrentUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import cdeler.highlight.settings.UISettingsManager;
import cdeler.highlight.token.Token;
import cdeler.highlight.token.TokenLocation;
import cdeler.highlight.token.TokenType;
import cdeler.highlight.token.Tokenizer;

abstract class BaseTextHighlighter implements TextHighlighter {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseTextHighlighter.class);
    private static final int HIGHLIGHT_SLEEP_DELAY_MS = 1000;

    @NotNull
    private final Tokenizer tokenizer;
    @NotNull
    protected final UISettingsManager settingsManager;
    @NotNull
    private volatile Future<Boolean> areaHighlightEvent;
    @NotNull
    private final ExecutorService executor;

    public BaseTextHighlighter(@NotNull Tokenizer tokenizer, @NotNull UISettingsManager settingManager) {
        this.tokenizer = tokenizer;
        this.settingsManager = settingManager;
        this.areaHighlightEvent = ConcurrentUtils.constantFuture(true);

        this.executor = Executors.newSingleThreadExecutor(new CustomizableThreadFactory("highlight-pool-"));
    }

    @Override
    public void highlightAll(JTextPane textArea) {
        if (isVisibleAreaHighlightEventInProgress()) {
            areaHighlightEvent.cancel(true);
        }

        var tokens = tokenizer.harvest(textArea.getText());

        areaHighlightEvent = executor.submit(() -> {
            LOGGER.error("Enter highlightAll");

            Thread.sleep(HIGHLIGHT_SLEEP_DELAY_MS);

            var workArea = new Rectangle(0, 0, textArea.getWidth(), textArea.getHeight());

            highlightInternal(textArea, tokens, workArea);

            LOGGER.error("Leave highlightAll");

            return true;
        });

    }

    @Override
    public void highlightVisible(JTextPane textArea) {
        if (isVisibleAreaHighlightEventInProgress()) {
            areaHighlightEvent.cancel(true);
        }

        var tokens = tokenizer.harvest(textArea.getText());

        areaHighlightEvent = executor.submit(() -> {
            LOGGER.error("Enter highlightVisible");

            Thread.sleep(HIGHLIGHT_SLEEP_DELAY_MS);

            var visibleRect = textArea.getVisibleRect();

            highlightInternal(textArea, tokens, visibleRect);

            LOGGER.error("Leave highlightVisible");

            return true;
        });
    }

    private void highlightInternal(@NotNull JTextPane textArea,
                                   @NotNull List<Token> tokens,
                                   @NotNull Rectangle workingArea) {
        if (workingArea.isEmpty()) {
            LOGGER.error("Cannot proceed with empty working area {}", workingArea);
            return;
        }

        clearHighlight(textArea, workingArea);

        var lineHeight = textArea.getFontMetrics(textArea.getFont()).getHeight();

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
                    if (intersectsWithWorkingArea(workingArea, lineHeight, location)) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Highlighting node " + sourceToken.getTokenType()
                                    + " from " + startOffset + " to " + endOffset);
                        }

                        highlight(textArea, sourceToken.getTokenType(), startOffset, endOffset);
                    }
                }
            }
        });
    }

    private boolean isVisibleAreaHighlightEventInProgress() {
        return !areaHighlightEvent.isCancelled() || !areaHighlightEvent.isDone();
    }

    private static boolean intersectsWithWorkingArea(Rectangle workingArea, int lineHeight, TokenLocation location) {
        var y = location.beginLine * lineHeight;
        var w = workingArea.width;
        var h = (location.endLine - location.beginLine + 1) * lineHeight;

        return workingArea.intersects(new Rectangle(0, y, w, h));
    }

    protected abstract void highlight(JTextPane textArea, TokenType tokenType, int startOffset, int endOffset);

    protected abstract void clearHighlight(JTextPane textArea, Rectangle clearArea);
}

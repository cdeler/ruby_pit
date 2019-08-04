package cdeler.ide.events;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cdeler.core.Event;
import cdeler.core.EventThread;
import cdeler.core.ui.UIEventType;
import cdeler.highlight.highlighters.TextHighlighter;
import cdeler.highlight.settings.UISettingsManager;
import cdeler.ide.Ide;
import cdeler.ide.LineNumberedTextArea;

public class UIEventsManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(UIEventsManager.class);
    @NotNull
    private final LineNumberedTextArea lineNumbers;
    @NotNull
    private final JTextPane textArea;
    @NotNull
    private final EventThread<UIEventType> uiThread;
    @NotNull
    private final EventThread<UIEventType> highlightThread;
    @NotNull
    private final TextHighlighter highlighter;
    @NotNull
    private final JComboBox themeChooseList;
    @NotNull
    private final UISettingsManager settingsManager;
    @NotNull
    private final JPanel textPanel;

    public UIEventsManager(@NotNull Ide ide, @NotNull TextHighlighter highlighter,
                           @NotNull UISettingsManager settingsManager) {
        this.textArea = ide.getTextArea();
        this.lineNumbers = ide.getLineNumbers();
        this.themeChooseList = ide.getThemeChooseList();
        this.textPanel = ide.getTextPanel();
        this.highlighter = highlighter;
        this.settingsManager = settingsManager;

        uiThread = new EventThread<>();
        highlightThread = new EventThread<>();

        uiThread.addConsumers(getLineNumbersEventList());
        highlightThread.addConsumers(getHighlightEvents());

        initializeEventListeners();

        new Thread(uiThread, "ui_event_thread").start();
        new Thread(highlightThread, "highlight_thread").start();

        var initializeCompleted = new Event<>(UIEventType.UI_INITIALIZE);
        uiThread.fire(initializeCompleted);
        highlightThread.fire(initializeCompleted);

        LOGGER.info("UIEventsManager has been initialized");
    }

    private void initializeEventListeners() {
        initializeTextChangeRelatedEvents();
        initializeThemeChooseListEvents();

        textPanel.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                uiThread.fire(new Event<>(UIEventType.WINDOW_RESIZE));
            }

            @Override
            public void componentMoved(ComponentEvent componentEvent) {
            }

            @Override
            public void componentShown(ComponentEvent componentEvent) {
            }

            @Override
            public void componentHidden(ComponentEvent componentEvent) {
            }
        });
    }

    private void initializeTextChangeRelatedEvents() {
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                LOGGER.debug("insertUpdate");
                var event = new Event<>(UIEventType.TEXT_AREA_TEXT_CHANGED);
                uiThread.fire(event);
                highlightThread.fire(event);
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                LOGGER.debug("removeUpdate");
                var event = new Event<>(UIEventType.TEXT_AREA_TEXT_CHANGED);
                uiThread.fire(event);
                highlightThread.fire(event);
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
            }
        });

        textArea.addCaretListener(caretEvent -> uiThread.fire(new Event<>(UIEventType.CARET_UPDATE)));
    }

    private void initializeThemeChooseListEvents() {
        if (settingsManager.getAvailableSettings().length > 1) {
            themeChooseList.setSelectedIndex(0);
            themeChooseList.addItemListener(itemEvent -> {
                if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                    LOGGER.info("Selected theme {}", itemEvent.getItem());
                    settingsManager.setActiveSettingsSet((String) itemEvent.getItem());

                    // change BG and text colors
                    textArea.setBackground(settingsManager.getActiveBackgroundColor());
                    textArea.setForeground(settingsManager.getDefaultActiveStyle().getColor());

                    highlightThread.fire(new Event<>(UIEventType.REDRAW_HIGHLIGHT));
                    uiThread.fire(new Event<>(UIEventType.REDRAW_HIGHLIGHT));
                }
            });
        }
    }

    private Map<UIEventType, Function<List<Event<UIEventType>>, Void>> getLineNumbersEventList() {
        Map<UIEventType, Function<List<Event<UIEventType>>, Void>> result = new HashMap<>();

        result.put(UIEventType.TEXT_AREA_TEXT_CHANGED, uiEvent -> {
            lineNumbers.updateLineNumbers();
            lineNumbers.highlightCaretPosition();

            return null;
        });
        result.put(UIEventType.CARET_UPDATE, uiEvents -> {
            lineNumbers.highlightCaretPosition();

            return null;
        });
        result.put(UIEventType.WINDOW_RESIZE, uiEvents -> {
            lineNumbers.updateLineNumbers();
            lineNumbers.highlightCaretPosition();

            return null;
        });
        result.put(UIEventType.UI_INITIALIZE, uiEvents -> {
            lineNumbers.updateLineNumbers();
            lineNumbers.highlightCaretPosition();

            return null;
        });
        result.put(UIEventType.REDRAW_HIGHLIGHT, uiEvents -> {
            lineNumbers.updateColors();
            lineNumbers.highlightCaretPosition();

            return null;
        });

        return result;
    }

    private Map<UIEventType, Function<List<Event<UIEventType>>, Void>> getHighlightEvents() {
        Map<UIEventType, Function<List<Event<UIEventType>>, Void>> result = new HashMap<>();
        result.put(UIEventType.REDRAW_HIGHLIGHT, uiEvents -> {
            highlighter.highlight(textArea);

            return null;
        });
        result.put(UIEventType.TEXT_AREA_TEXT_CHANGED, uiEvent -> {
            highlighter.highlight(textArea);

            return null;
        });
        result.put(UIEventType.UI_INITIALIZE, uiEvents -> {
            highlighter.highlight(textArea);

            return null;
        });

        return result;
    }

}

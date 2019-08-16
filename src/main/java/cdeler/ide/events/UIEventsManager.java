package cdeler.ide.events;

import java.awt.*;
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
import javax.swing.text.Document;

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
    @NotNull
    private final JScrollPane scrollPane;

    public UIEventsManager(@NotNull Ide ide, @NotNull TextHighlighter highlighter,
                           @NotNull UISettingsManager settingsManager) {
        this.textArea = ide.getTextArea();
        this.lineNumbers = ide.getLineNumbers();
        this.themeChooseList = ide.getThemeChooseList();
        this.textPanel = ide.getTextPanel();
        this.scrollPane = ide.getTextPanelScrollPane();
        this.highlighter = highlighter;
        this.settingsManager = settingsManager;

        uiThread = new EventThread<>();
        highlightThread = new EventThread<>();

        uiThread.addConsumers(getLineNumbersEventList());
        highlightThread.addConsumers(getHighlightEvents());

        initializeEventListeners();

        new Thread(uiThread, "ui_event_thread").start();
        new Thread(highlightThread, "highlight_thread").start();

        EventQueue.invokeLater(this::redrawAll);

        LOGGER.info("UIEventsManager has been initialized");
    }

    void redrawAll() {
        var initializeCompleted = new Event<>(UIEventType.UI_INITIALIZE);
        uiThread.fire(initializeCompleted);
        SwingUtilities.invokeLater(() -> highlightThread.fire(initializeCompleted));
    }

    private void initializeEventListeners() {
        initializeTextChangeRelatedEvents();
        initializeThemeChooseListEvents();

        scrollPane.getViewport().addChangeListener(changeEvent ->
                SwingUtilities.invokeLater(() ->
                        highlightThread.fire(new Event<>(UIEventType.REDRAW_VISIBLE_HIGHLIGHT)))
        );

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
        hookDocumentListeners(textArea.getDocument());

        textArea.addCaretListener(caretEvent -> uiThread.fire(new Event<>(UIEventType.CARET_UPDATE)));
    }

    public void hookDocumentListeners(Document document) {
        document.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                LOGGER.debug("insertUpdate");
                var event = new Event<>(UIEventType.TEXT_AREA_TEXT_CHANGED);
                uiThread.fire(event);
                SwingUtilities.invokeLater(() -> highlightThread.fire(event));
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                LOGGER.debug("removeUpdate");
                var event = new Event<>(UIEventType.TEXT_AREA_TEXT_CHANGED);
                uiThread.fire(event);

                SwingUtilities.invokeLater(() -> highlightThread.fire(event));
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
            }
        });
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

                    var activeFont = settingsManager.getActiveFont();
                    textArea.setFont(activeFont);
                    lineNumbers.setFont(activeFont);

                    var event = new Event<>(UIEventType.REDRAW_HIGHLIGHT);

                    SwingUtilities.invokeLater(() -> highlightThread.fire(event));
                    uiThread.fire(event);
                }
            });
        }
    }

    private Map<UIEventType, Function<List<Event<UIEventType>>, Void>> getLineNumbersEventList() {
        Map<UIEventType, Function<List<Event<UIEventType>>, Void>> result = new HashMap<>();

        result.put(UIEventType.TEXT_AREA_TEXT_CHANGED, uiEvent -> {
            LOGGER.error("UIEventType.TEXT_AREA_TEXT_CHANGED");
            lineNumbers.updateLineNumbers();
            lineNumbers.highlightCaretPosition();

            return null;
        });
        result.put(UIEventType.CARET_UPDATE, uiEvents -> {
            LOGGER.error("UIEventType.CARET_UPDATE");
            lineNumbers.highlightCaretPosition();

            return null;
        });
        result.put(UIEventType.WINDOW_RESIZE, uiEvents -> {
            LOGGER.error("UIEventType.WINDOW_RESIZE");
            lineNumbers.updateLineNumbers();
            lineNumbers.highlightCaretPosition();

            return null;
        });
        result.put(UIEventType.UI_INITIALIZE, uiEvents -> {
            LOGGER.error("UIEventType.UI_INITIALIZE");

            lineNumbers.updateLineNumbers();
            lineNumbers.highlightCaretPosition();

            return null;
        });
        result.put(UIEventType.REDRAW_HIGHLIGHT, uiEvents -> {
            LOGGER.error("UIEventType.REDRAW_HIGHLIGHT");

            lineNumbers.updateColors();
            lineNumbers.highlightCaretPosition();

            return null;
        });

        return result;
    }

    private Map<UIEventType, Function<List<Event<UIEventType>>, Void>> getHighlightEvents() {
        Map<UIEventType, Function<List<Event<UIEventType>>, Void>> result = new HashMap<>();
        result.put(UIEventType.REDRAW_HIGHLIGHT, uiEvents -> {
            LOGGER.error("UIEventType.REDRAW_HIGHLIGHT");

            highlighter.highlightAll(textArea);

            return null;
        });
        result.put(UIEventType.TEXT_AREA_TEXT_CHANGED, uiEvent -> {
            LOGGER.error("UIEventType.TEXT_AREA_TEXT_CHANGED");

            highlighter.highlightVisible(textArea);

            return null;
        });
        result.put(UIEventType.REDRAW_VISIBLE_HIGHLIGHT, uiEvent -> {
            LOGGER.error("UIEventType.REDRAW_VISIBLE_HIGHLIGHT");

            highlighter.highlightVisible(textArea);

            return null;
        });
        result.put(UIEventType.UI_INITIALIZE, uiEvents -> {
            LOGGER.error("UIEventType.UI_INITIALIZE");

            highlighter.highlightAll(textArea);

            return null;
        });

        return result;
    }

}

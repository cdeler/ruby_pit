package cdeler.ide;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultStyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cdeler.core.Event;
import cdeler.core.EventThread;
import cdeler.core.io.IOEventType;
import cdeler.core.ui.UIEventType;
import cdeler.highlight.highlighters.TextHighlighter;
import cdeler.highlight.settings.UISettingsManager;


// created using https://stackoverflow.com/questions/36384683/highlighter-highlights-all-the-textarea-instead-of-a
// -specific-word-and-its-occur
public class Ide extends JFrame {
    private static final Logger LOGGER = LoggerFactory.getLogger(Ide.class);

    private int windowWidth;
    private int windowHeight;
    private String iconPath;
    private String windowTitle;
    private final JTextPane textArea;
    private final LineNumberedTextArea lineNumbers;
    private final EventThread<UIEventType> uiEventThread;
    private final EventThread<IOEventType> ioEventThread;
    private final EventThread<UIEventType> highlightThread;
    private final TextHighlighter highlighter;
    private final UISettingsManager settingsManager;
    private final JComboBox themeChooseList;
    private final JButton saveButton;


    private volatile String fileName = null;

    public Ide(int windowWidth, int windowHeight, String iconPath, String windowTitle,
               TextHighlighter highlighter, UISettingsManager settingsManager) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.iconPath = iconPath;
        this.windowTitle = windowTitle;
        this.textArea = new JTextPane(new DefaultStyledDocument());
        this.lineNumbers = new LineNumberedTextArea(settingsManager, textArea);
        this.uiEventThread = new EventThread<>();
        this.ioEventThread = new EventThread<>();
        this.highlightThread = new EventThread<>();
        this.highlighter = highlighter;
        this.settingsManager = settingsManager;
        this.saveButton = new JButton("\uD83D\uDCBE");

        themeChooseList = new JComboBox(settingsManager.getAvailableSettings());
        uiInitialize();

        this.ioEventThread.addConsumers(getOpenFileEvents());
        this.uiEventThread.addConsumers(getLineNumbersEventList());
        this.highlightThread.addConsumers(getHighlightEvents());

        new Thread(this.uiEventThread, "ui_event_thread").start();
        new Thread(this.ioEventThread, "io_event_thread").start();
        new Thread(this.highlightThread, "highlight_thread").start();


        var initializeCompleted = new Event<>(UIEventType.UI_INITIALIZE);
        uiEventThread.fire(initializeCompleted);
        highlightThread.fire(initializeCompleted);
        LOGGER.info("Ide is initialized");
    }

    private void uiInitialize() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setTitle(windowTitle);
        setSize(windowWidth, windowHeight);
        setLocationRelativeTo(null);

        var webIcon = new ImageIcon(getClass().getResource(iconPath));
        setIconImage(webIcon.getImage());

        initializeTextArea();

        var scrollPane = new JScrollPane(textArea);
        scrollPane.setRowHeaderView(lineNumbers);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        var textPanel = new JPanel(new BorderLayout());

        textPanel.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                uiEventThread.fire(new Event<>(UIEventType.WINDOW_RESIZE));
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

        var openButton = new JButton("\uD83D\uDCC2");
        openButton.addActionListener(actionEvent -> {
            LOGGER.debug("Open button pressed");
            ioEventThread.fire(new Event<>(IOEventType.FILE_OPEN_EVENT));
        });

        if (settingsManager.getAvailableSettings().length > 1) {
            themeChooseList.setSelectedIndex(0);
            themeChooseList.addItemListener(itemEvent -> {
                if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                    LOGGER.info("Selected theme {}", itemEvent.getItem());
                    settingsManager.setActiveSettingsSet((String) itemEvent.getItem());

                    // change BG and text colors
                    //UIUtils.changeTextPaneAttributes(textArea,
                    //        settingsManager.getDefaultActiveStyle().asAttributeSet());
                    textArea.setBackground(settingsManager.getActiveBackgroundColor());
                    textArea.setForeground(settingsManager.getDefaultActiveStyle().getColor());

                    highlightThread.fire(new Event<>(UIEventType.REDRAW_HIGHLIGHT));
                    uiEventThread.fire(new Event<>(UIEventType.REDRAW_HIGHLIGHT));
                }
            });
        }

        var topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.add(openButton);
        topPanel.add(saveButton);
        topPanel.add(themeChooseList);

        textPanel.add(scrollPane);

        add(topPanel, BorderLayout.NORTH);
        add(textPanel, BorderLayout.CENTER);

        setMinimumSize(new Dimension(windowWidth, windowHeight));
        setPreferredSize(new Dimension(windowWidth, windowHeight));
        pack();
    }

    private void initializeTextArea() {
        textArea.setEditable(true);
        textArea.setEditorKit(new NoWrappingEditorKit());
        textArea.setFont(settingsManager.getActiveFont());
        textArea.setBackground(settingsManager.getActiveBackgroundColor());

        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                LOGGER.debug("insertUpdate");
                var event = new Event<>(UIEventType.TEXT_AREA_TEXT_CHANGED);
                uiEventThread.fire(event);
                highlightThread.fire(event);
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                LOGGER.debug("removeUpdate");
                var event = new Event<>(UIEventType.TEXT_AREA_TEXT_CHANGED);
                uiEventThread.fire(event);
                highlightThread.fire(event);
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
            }
        });

        textArea.addCaretListener(caretEvent -> uiEventThread.fire(new Event<>(UIEventType.CARET_UPDATE)));
    }


    private Map<IOEventType, Function<List<Event<IOEventType>>, Void>> getOpenFileEvents() {
        Map<IOEventType, Function<List<Event<IOEventType>>, Void>> result = new HashMap<>();

        result.put(IOEventType.FILE_OPEN_EVENT, uiEvent -> {
            JFileChooser fileOpenDialog = new JFileChooser();
            int ret = fileOpenDialog.showDialog(null, "Open file");
            if (ret == JFileChooser.APPROVE_OPTION) {
                synchronized (this) {
                    File inputFile = fileOpenDialog.getSelectedFile();

                    LOGGER.info("Opening file {}", inputFile.getAbsolutePath());

                    try (var is = new FileInputStream(inputFile);
                         var reader = new BufferedReader(new InputStreamReader(is))) {

                        textArea.setText(reader.lines().collect(Collectors.joining(System.lineSeparator())));

                        var event = new Event<>(UIEventType.TEXT_AREA_TEXT_CHANGED);
                        uiEventThread.fire(event);
                        highlightThread.fire(event);

                        fileName = inputFile.getAbsolutePath();
                    } catch (IOException e) {
                        LOGGER.error("Unable to read file " + inputFile.getAbsolutePath(), e);
                    }
                }
            }

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

    JTextPane getTextArea() {
        return textArea;
    }

    JButton getSaveButton() {
        return saveButton;
    }
}

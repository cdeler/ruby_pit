package cdeler.ide;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cdeler.core.EventProducer;
import cdeler.core.EventThread;
import cdeler.core.FontLoader;
import cdeler.core.UIEvent;
import cdeler.core.UIEventType;


// created using https://stackoverflow.com/questions/36384683/highlighter-highlights-all-the-textarea-instead-of-a
// -specific-word-and-its-occur
public class Ide extends JFrame implements EventProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(Ide.class);

    private int windowWidth;
    private int windowHeight;
    private String iconPath;
    private String windowTitle;
    private final JTextArea textArea;
    private final LineNumberedTextArea lineNumbers;
    private final EventThread eventThread;

    public Ide(int windowWidth, int windowHeight, String iconPath, String windowTitle,
               EventThread eventThread) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.iconPath = iconPath;
        this.windowTitle = windowTitle;
        this.textArea = new JTextArea();
        this.lineNumbers = new LineNumberedTextArea(textArea);
        this.eventThread = eventThread;

        initialize();

        this.eventThread.addConsumers(getLineNumbersEventList());

        new Thread(this.eventThread, "line_numbers_event_thread").start();

        LOGGER.info("Ide is initialized");
    }

    private void initialize() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setTitle(windowTitle);
        setSize(windowWidth, windowHeight);
        setLocationRelativeTo(null);

        var webIcon = new ImageIcon(getClass().getResource(iconPath));
        setIconImage(webIcon.getImage());

        initializeTextArea();


        var scrollPane = new JScrollPane(textArea);
        scrollPane.setRowHeaderView(lineNumbers);

        var textPanel = new JPanel(new BorderLayout());

        textPanel.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                eventThread.fire(new UIEvent(UIEventType.WINDOW_RESIZE));
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
            LOGGER.error("Open button pressed");
        });

        var saveButton = new JButton("\uD83D\uDCBE");
        saveButton.addActionListener(actionEvent -> {
            LOGGER.error("Save button pressed");
        });

        var topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.add(openButton);
        topPanel.add(saveButton);

        textPanel.add(scrollPane);

        add(topPanel, BorderLayout.NORTH);
        add(textPanel, BorderLayout.CENTER);

        lineNumbers.updateLineNumbers();
        pack();
    }


    private void initializeTextArea() {
        textArea.setColumns(80);
        textArea.setRows(30);
        textArea.setLineWrap(true);
        textArea.setEditable(true);
        textArea.setWrapStyleWord(true);

        textArea.setFont(FontLoader.load("iosevka-regular", 20));

        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                LOGGER.debug("insertUpdate");
                eventThread.fire(new UIEvent(UIEventType.TEXT_AREA_TEXT_CHANGED));
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                LOGGER.debug("removeUpdate");
                eventThread.fire(new UIEvent(UIEventType.TEXT_AREA_TEXT_CHANGED));
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                LOGGER.debug("changedUpdate");
                eventThread.fire(new UIEvent(UIEventType.TEXT_AREA_TEXT_CHANGED));
            }
        });

        textArea.addCaretListener(caretEvent -> eventThread.fire(new UIEvent(UIEventType.CARET_UPDATE)));
    }

    @Override
    public Map<UIEventType, Function<List<UIEvent>, Void>> getLineNumbersEventList() {
        Map<UIEventType, Function<List<UIEvent>, Void>> result = new HashMap<>();

        result.put(UIEventType.TEXT_AREA_TEXT_CHANGED, uiEvent -> {
            lineNumbers.updateLineNumbers();
            return null;
        });
        result.put(UIEventType.CARET_UPDATE, uiEvents -> {
            lineNumbers.highlightCaretPosition();
            return null;
        });
        result.put(UIEventType.WINDOW_RESIZE, uiEvents -> {
            lineNumbers.updateLineNumbers();
            return null;
        });

        return result;
    }
}

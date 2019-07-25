package cdeler.ide;

import java.awt.*;
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
    private final LineNumberingTextArea lineNumbers;
    private final EventThread eventThread;

    public Ide(int windowWidth, int windowHeight, String iconPath, String windowTitle,
               EventThread eventThread) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.iconPath = iconPath;
        this.windowTitle = windowTitle;
        this.textArea = new JTextArea();
        this.lineNumbers = new LineNumberingTextArea(textArea);
        this.eventThread = eventThread;

        initialize();

        this.eventThread.addConsumers(getEventList());

        new Thread(this.eventThread, "ui_events_thread").start();

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

        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                LOGGER.debug("insertUpdate");
                eventThread.fire(new UIEvent(UIEventType.INSERT_UPDATE));
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                LOGGER.debug("removeUpdate");
                eventThread.fire(new UIEvent(UIEventType.REMOVE_UPDATE));
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                LOGGER.debug("changedUpdate");
                eventThread.fire(new UIEvent(UIEventType.CHANGE_UPDATE));
            }
        });

        var scrollPane = new JScrollPane(textArea);
        scrollPane.setRowHeaderView(lineNumbers);

        var panel = new JPanel(new BorderLayout());
        panel.add(scrollPane);

        add(panel, BorderLayout.CENTER);

        lineNumbers.updateLineNumbers();
        pack();
    }

    private void initializeTextArea() {
        textArea.setColumns(80);
        textArea.setRows(60);
        textArea.setLineWrap(true);
        textArea.setEditable(true);
        textArea.setWrapStyleWord(true);

        textArea.setFont(FontLoader.load("iosevka-regular", 20));
    }

    @Override
    public Map<UIEventType, Function<List<UIEvent>, Void>> getEventList() {
        Map<UIEventType, Function<List<UIEvent>, Void>> result = new HashMap<>();

        result.put(UIEventType.CHANGE_UPDATE, uiEvent -> {
            lineNumbers.updateLineNumbers();
            return null;
        });
        result.put(UIEventType.INSERT_UPDATE, uiEvent -> {
            lineNumbers.updateLineNumbers();
            return null;
        });
        result.put(UIEventType.REMOVE_UPDATE, uiEvent -> {
            lineNumbers.updateLineNumbers();
            return null;
        });

        return result;
    }
}

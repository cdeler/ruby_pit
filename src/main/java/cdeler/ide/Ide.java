package cdeler.ide;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cdeler.core.FontLoader;

// created using https://stackoverflow.com/questions/36384683/highlighter-highlights-all-the-textarea-instead-of-a
// -specific-word-and-its-occur
public class Ide extends JFrame {
    private static final Logger LOGGER = LoggerFactory.getLogger(Ide.class);

    private int windowWidth;
    private int windowHeight;
    private String iconPath;
    private String windowTitle;


    public Ide(int windowWidth, int windowHeight, String iconPath, String windowTitle) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.iconPath = iconPath;
        this.windowTitle = windowTitle;

        initialize();

        LOGGER.info("Ide is initialized");
    }

    private void initialize() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setTitle(windowTitle);
        setSize(windowWidth, windowHeight);
        setLocationRelativeTo(null);

        var webIcon = new ImageIcon(getClass().getResource(iconPath));
        setIconImage(webIcon.getImage());

        var textArea = createJTextArea();
        var lineNumbers = new LineNumberingTextArea(textArea);

        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                LOGGER.debug("insertUpdate");
                lineNumbers.updateLineNumbers();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                LOGGER.debug("removeUpdate");
                lineNumbers.updateLineNumbers();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                LOGGER.debug("changedUpdate");
                lineNumbers.updateLineNumbers();
            }
        });

        var jScrollPane = new JScrollPane(textArea);
        jScrollPane.setRowHeaderView(lineNumbers);

        var panel = new JPanel(new BorderLayout());
        panel.add(jScrollPane);

        add(panel);

        pack();

        lineNumbers.updateLineNumbers();
    }

    private JTextArea createJTextArea() {
        JTextArea textArea = new JTextArea();
        textArea.setColumns(80);
        textArea.setRows(60);
        textArea.setLineWrap(true);
        textArea.setEditable(true);

        textArea.setFont(FontLoader.load("iosevka-regular", 20));

        return textArea;
    }

}

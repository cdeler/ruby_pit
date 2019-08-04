package cdeler.ide;

import java.awt.*;

import javax.swing.*;
import javax.swing.text.DefaultStyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cdeler.highlight.settings.UISettingsManager;


// created using https://stackoverflow.com/questions/36384683/highlighter-highlights-all-the-textarea-instead-of-a
// -specific-word-and-its-occur
public class Ide extends JFrame {
    private static final Logger LOGGER = LoggerFactory.getLogger(Ide.class);

    private int windowWidth;
    private int windowHeight;
    private String iconPath;
    private final JTextPane textArea;
    private final LineNumberedTextArea lineNumbers;
    private final UISettingsManager settingsManager;
    private final JComboBox themeChooseList;
    private final JButton saveButton;
    private final JButton openButton;
    private final JPanel textPanel;

    public Ide(int windowWidth, int windowHeight, String iconPath, UISettingsManager settingsManager) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.iconPath = iconPath;
        this.textArea = new JTextPane(new DefaultStyledDocument());
        this.lineNumbers = new LineNumberedTextArea(settingsManager, textArea);
        this.settingsManager = settingsManager;
        this.saveButton = new JButton("\uD83D\uDCBE");
        this.openButton = new JButton("\uD83D\uDCC2");
        this.textPanel = new JPanel(new BorderLayout());

        this.themeChooseList = new JComboBox(settingsManager.getAvailableSettings());
        uiInitialize();

        LOGGER.info("Ide has been initialized");
    }

    private void uiInitialize() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setTitle(settingsManager.getIdeTitle());
        setSize(windowWidth, windowHeight);
        setLocationRelativeTo(null);

        var webIcon = new ImageIcon(getClass().getResource(iconPath));
        setIconImage(webIcon.getImage());

        initializeTextArea();

        var scrollPane = new JScrollPane(textArea);
        scrollPane.setRowHeaderView(lineNumbers);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

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
    }

    JTextPane getTextArea() {
        return textArea;
    }

    JButton getSaveButton() {
        return saveButton;
    }

    JButton getOpenButton() {
        return openButton;
    }

    LineNumberedTextArea getLineNumbers() {
        return lineNumbers;
    }

    JComboBox getThemeChooseList() {
        return themeChooseList;
    }

    JPanel getTextPanel() {
        return textPanel;
    }
}

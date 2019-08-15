package cdeler.ide;

import java.awt.*;

import javax.swing.*;
import javax.swing.text.DefaultStyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cdeler.highlight.settings.UISettingsManager;


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
    private final JScrollPane textPanelScrollPane;

    public Ide(int windowWidth, int windowHeight, String iconPath, UISettingsManager settingsManager) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.iconPath = iconPath;
        this.settingsManager = settingsManager;

        textArea = new JTextPane(new DefaultStyledDocument());
        lineNumbers = new LineNumberedTextArea(settingsManager, textArea);
        saveButton = new JButton("\uD83D\uDCBE");
        openButton = new JButton("\uD83D\uDCC2");
        textPanel = new JPanel(new BorderLayout());
        textPanelScrollPane = new JScrollPane(textArea);
        themeChooseList = new JComboBox(settingsManager.getAvailableSettings());

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

        textPanelScrollPane.setRowHeaderView(lineNumbers);
        textPanelScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        textPanelScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        var topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.add(openButton);
        topPanel.add(saveButton);

        if (areAtLeastTwoThemesAvailable()) {
            topPanel.add(themeChooseList);
        }

        textPanel.add(textPanelScrollPane);

        add(topPanel, BorderLayout.NORTH);
        add(textPanel, BorderLayout.CENTER);

        setMinimumSize(new Dimension(windowWidth, windowHeight));
        setPreferredSize(new Dimension(windowWidth, windowHeight));
        pack();
    }

    private boolean areAtLeastTwoThemesAvailable() {
        return settingsManager.getAvailableSettings().length > 1;
    }

    private void initializeTextArea() {
        textArea.setEditable(true);
        textArea.setEditorKit(new NoWrappingEditorKit());
        textArea.setFont(settingsManager.getActiveFont());
        textArea.setBackground(settingsManager.getActiveBackgroundColor());
        textArea.setForeground(settingsManager.getDefaultActiveStyle().getColor());
    }

    public JTextPane getTextArea() {
        return textArea;
    }

    public JButton getSaveButton() {
        return saveButton;
    }

    public JButton getOpenButton() {
        return openButton;
    }

    public LineNumberedTextArea getLineNumbers() {
        return lineNumbers;
    }

    public JComboBox getThemeChooseList() {
        return themeChooseList;
    }

    public JPanel getTextPanel() {
        return textPanel;
    }

    public JScrollPane getTextPanelScrollPane() {
        return textPanelScrollPane;
    }
}

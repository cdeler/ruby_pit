package cdeler.ide;

import javax.swing.*;

public class Ide extends JFrame {
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
    }

    private void initialize() {
        setTitle(windowTitle);
        setSize(windowWidth, windowHeight);
        setLocationRelativeTo(null);

        var webIcon = new ImageIcon(getClass().getResource(iconPath));
        setIconImage(webIcon.getImage());

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}

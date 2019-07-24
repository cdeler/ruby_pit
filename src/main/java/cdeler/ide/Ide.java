package cdeler.ide;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        setTitle(windowTitle);
        setSize(windowWidth, windowHeight);
        setLocationRelativeTo(null);

        var webIcon = new ImageIcon(getClass().getResource(iconPath));
        setIconImage(webIcon.getImage());

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}

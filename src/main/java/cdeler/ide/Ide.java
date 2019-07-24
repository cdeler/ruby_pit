package cdeler.ide;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// created using https://stackoverflow.com/questions/36384683/highlighter-highlights-all-the-textarea-instead-of-a
// -specific-word-and-its-occur
public class Ide extends JFrame {
    private static final Logger LOGGER = LoggerFactory.getLogger(Ide.class);

    private final JPanel jPanel1;
    private final JScrollPane jScrollPane1;
    private final JTextArea textArea;

    private int windowWidth;
    private int windowHeight;
    private String iconPath;
    private String windowTitle;


    public Ide(int windowWidth, int windowHeight, String iconPath, String windowTitle) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.iconPath = iconPath;
        this.windowTitle = windowTitle;
        this.textArea = new JTextArea();
        this.jPanel1 = new JPanel();
        this.jScrollPane1 = new JScrollPane();
        initialize();

        LOGGER.info("Ide is initialized");
    }

    private void initialize() {
        setTitle(windowTitle);
        setSize(windowWidth, windowHeight);
        setLocationRelativeTo(null);

        var webIcon = new ImageIcon(getClass().getResource(iconPath));
        setIconImage(webIcon.getImage());

        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                LOGGER.info("insertUpdate");
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                LOGGER.info("removeUpdate");
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                LOGGER.info("changedUpdate");
            }
        });

        textArea.setColumns(20);
        textArea.setRows(5);
        jScrollPane1.setViewportView(textArea);

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1, GroupLayout.Alignment.TRAILING,
                                GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1, GroupLayout.Alignment.TRAILING,
                                GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}

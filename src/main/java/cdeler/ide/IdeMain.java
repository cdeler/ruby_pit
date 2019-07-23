package cdeler.ide;


import java.awt.*;

import javax.swing.*;

public class IdeMain extends JFrame {
    private static final String IDE_FRAME_NAME = "RubyPit";

    private IdeMain() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle(IDE_FRAME_NAME);
        setSize(800, 600);
        setLocationRelativeTo(null);


        var webIcon = new ImageIcon(getClass().getResource("/icons8-ruby-programming-language-64.png"));
        setIconImage(webIcon.getImage());

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {

            var ex = new IdeMain();
            ex.setVisible(true);
        });
    }
}

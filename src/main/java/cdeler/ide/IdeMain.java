package cdeler.ide;


import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.ImageIcon;

public class IdeMain extends JFrame {
    private static final String IDE_FRAME_NAME = "RubyPit";

    public IdeMain() {
        setTitle(IDE_FRAME_NAME);
        setSize(800, 600);
        setLocationRelativeTo(null);

        var webIcon = new ImageIcon("src/main/resources/icons8-ruby-programming-language-64.png");
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

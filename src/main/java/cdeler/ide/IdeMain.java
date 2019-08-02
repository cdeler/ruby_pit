package cdeler.ide;

import java.awt.*;

import javax.swing.*;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class IdeMain extends JFrame {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("/app-context.xml");

        EventQueue.invokeLater(() -> {
            Ide ide = (Ide) context.getBean("ide_window");

            ide.setVisible(true);
        });
    }
}

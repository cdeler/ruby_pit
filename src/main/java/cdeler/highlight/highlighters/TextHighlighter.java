package cdeler.highlight.highlighters;

import javax.swing.*;

public interface TextHighlighter {
    void highlightVisible(JTextPane textArea);

    void highlightAll(JTextPane textArea);
}

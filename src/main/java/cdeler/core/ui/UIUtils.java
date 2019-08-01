package cdeler.core.ui;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;

import cdeler.core.Pair;

public class UIUtils {
    private UIUtils() {
    }

    public static String formatNumber(int number, int numPos) {
        final StringBuilder sb = new StringBuilder();

        String resultNumber = String.valueOf(number);

        sb.append(" ".repeat(Math.max(0, (numPos - resultNumber.length()))));

        sb.append(resultNumber);

        return sb.toString();
    }

    public static String formatLineNumbers(int firstLineInclusive, int lastLineExclusive, int lineNumberMinLength) {
        return IntStream.range(firstLineInclusive, lastLineExclusive)
                .boxed()
                .map(lineNumber -> formatNumber(lineNumber, lineNumberMinLength))
                .collect(Collectors.joining(System.lineSeparator()));

    }

    public static Optional<Pair<Integer>> getHighlightedArea(List<Integer> lineNumbers, int caretLine) {
        int startHighlightLinePosition = Arrays.binarySearch(lineNumbers.toArray(), caretLine);

        if (0 <= startHighlightLinePosition && startHighlightLinePosition < lineNumbers.size()) {
            if (lineNumbers.get(startHighlightLinePosition).equals(caretLine)) {
                while (startHighlightLinePosition > 0) {
                    if (lineNumbers.get(startHighlightLinePosition).equals(lineNumbers.get(startHighlightLinePosition - 1))) {
                        startHighlightLinePosition--;
                    } else {
                        break;
                    }
                }
            }

            int endHighlightLinePosition = startHighlightLinePosition;

            while (endHighlightLinePosition < lineNumbers.size() - 1 && lineNumbers.get(endHighlightLinePosition + 1).equals(lineNumbers.get(endHighlightLinePosition))) {
                endHighlightLinePosition++;
            }

            return Optional.of(new Pair<>(startHighlightLinePosition, endHighlightLinePosition));
        }

        return Optional.empty();
    }

    // algorithm was taken from boost hash_combine (0x9e3779b9 also came from the boost)
    public static int hashCombine(int seed, int... rest) {
        for (int it : rest) {
            seed ^= (0x9e3779b9 + (it << 6) + (it >> 2));
        }
        return seed;
    }

    public static void changeTextPaneAttributes(JTextPane textArea, AttributeSet newTextAttributes) {
        StyledDocument document = textArea.getStyledDocument();
        Element textAreaRoot = document.getDefaultRootElement();
        int length = textAreaRoot.getEndOffset() - textAreaRoot.getStartOffset();

        if (length >= 0) {
            document.setCharacterAttributes(0, textArea.getText().length(), newTextAttributes, true);
        }

    }

}

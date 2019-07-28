package cdeler.core;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    public static String formatLineNumbers(List<Integer> lineNumbers, int lineNumberMinLength) {
        final StringBuilder sb = new StringBuilder();

        int prevNumber = Integer.MIN_VALUE;
        String emptyLine = " ".repeat(Math.max(0, lineNumberMinLength));

        for (int currentNumber : lineNumbers) {
            if (currentNumber != prevNumber) {
                sb.append(formatNumber(currentNumber, lineNumberMinLength)).append(System.lineSeparator());
            } else {
                sb.append(emptyLine).append(System.lineSeparator());
            }
            prevNumber = currentNumber;
        }

        return sb.toString();
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

}

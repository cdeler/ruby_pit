package cdeler.core;

import java.util.List;

public class StringUtils {
    private StringUtils() {
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
}

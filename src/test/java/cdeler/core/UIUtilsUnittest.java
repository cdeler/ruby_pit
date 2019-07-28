package cdeler.core;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class UIUtilsUnittest {
    @Test
    public void testFormatNumber() {
        // given
        var args = Arrays.asList(
                Arrays.asList(1, 1),
                Arrays.asList(1, 2),
                Arrays.asList(10, 2),
                Arrays.asList(10, 3),
                Arrays.asList(10, 1)
        );
        var expected = Arrays.asList(
                "1",
                " 1",
                "10",
                " 10",
                "10"
        );

        // when
        var actual = args.stream().map((arguments) -> {
            var number = arguments.get(0);
            var numPos = arguments.get(1);

            return UIUtils.formatNumber(number, numPos);
        }).collect(Collectors.toList());

        // then
        assertEquals(expected, actual);
    }

    @Test
    public void testFormatLineNumbers() {
        // given
        var arg = Arrays.asList(1, 1, 2, 3, 4, 4, 4, 4, 5);
        var expected = "  1" + System.lineSeparator()
                + "   " + System.lineSeparator()
                + "  2" + System.lineSeparator()
                + "  3" + System.lineSeparator()
                + "  4" + System.lineSeparator()
                + "   " + System.lineSeparator()
                + "   " + System.lineSeparator()
                + "   " + System.lineSeparator()
                + "  5" + System.lineSeparator();

        // when
        String actual = UIUtils.formatLineNumbers(arg, 3);

        // then
        assertEquals(expected, actual);
    }

    @Test
    public void testForHighlightedAreaSmoke() {
        // given
        var lines = Arrays.asList(1, 2, 3, 4, 5);
        var lookingFor = 2;
        var expected = new Pair<>(1, 1);

        // when
        var actual = UIUtils.getHighlightedArea(lines, lookingFor);

        // then
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }


    @Test
    public void testForHighlightedAreaWithRepeats() {
        // given
        var lines = Arrays.asList(1, 1, 2, 3, 4, 4, 4, 4, 5);
        var lookingFor = 4;
        var expected = new Pair<>(4, 7);

        // when
        var actual = UIUtils.getHighlightedArea(lines, lookingFor);

        // then
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    public void testForHighlightedAreaOneLine() {
        // given
        var lines = Arrays.asList(1);
        var lookingFor = 1;
        var expected = new Pair<>(0, 0);

        // when
        var actual = UIUtils.getHighlightedArea(lines, lookingFor);

        // then
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }
}

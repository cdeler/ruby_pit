package cdeler.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ArraySourceLineUnittest {
    @Test
    public void testItWorks() {
        // given
        String inputString = "echo \"test... test... test...\" | perl -e '$??s:;s:s;;$?::s;;=]=>%-{<-|}<&|`{;;y; " +
                "-/:-@[-`{-};`-{/\" -;;s;;$_;see'";

        // when
        SourceLine line = new ArraySourceLine(inputString);

        // then
        assertEquals(inputString, line.toString());

        assertEquals(inputString.length(), line.length());

        for (int i = 0; i < line.length(); i++) {
            assertEquals(inputString.charAt(i), line.charAt(i));
        }
    }

    @Test
    public void testInsertAtEnd() {
        // given
        String symbols = "1234567890abcdefghijklmnopqrstuvwxyz";
        SourceLine line = new ArraySourceLine();

        // when
        for (int i = 0; i < symbols.length(); i++) {
            line.insertAt(line.length(), symbols.charAt(i));
        }

        // then
        assertEquals(symbols, line.toString());
    }

    @Test
    public void testInsertAtBegin() {
        // given
        String symbols = "1234567890abcdefghijklmnopqrstuvwxyz";
        SourceLine line = new ArraySourceLine();

        // when
        for (int i = symbols.length() - 1; i >= 0; i--) {
            line.insertAt(0, symbols.charAt(i));
        }

        // then
        assertEquals(symbols, line.toString());
    }

    @Test
    public void testInsertIntoMiddle() {
        // given
        String symbols = "1234567890abcdefghijklmnopqrstuvwxyz";
        SourceLine line = new ArraySourceLine("1z");

        // when
        for (int i = symbols.length() - 2; i >= 1; i--) {
            line.insertAt(1, symbols.charAt(i));
        }

        // then
        assertEquals(symbols, line.toString());
    }

    @Test
    public void testRemoveFromEnd() {
        // given
        SourceLine line = new ArraySourceLine("0123456789");

        // when
        line.removeAt(7, 9);

        // then
        assertEquals("0123456", line.toString());
    }

    @Test
    public void testDeleteFromMiddle() {
        // given
        SourceLine line = new ArraySourceLine("0123456789");

        // when
        line.removeAt(1, 8);

        // then
        assertEquals("09", line.toString());
    }

}

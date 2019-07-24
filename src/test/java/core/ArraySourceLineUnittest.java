package core;

import org.junit.Test;

import cdeler.core.ArraySourceLine;
import cdeler.core.SourceLine;

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

}

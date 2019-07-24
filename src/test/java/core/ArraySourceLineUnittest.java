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
}

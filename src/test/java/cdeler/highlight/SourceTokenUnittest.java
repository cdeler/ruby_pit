package cdeler.highlight;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SourceTokenUnittest {

    @Test
    public void testInitializeFromString() {
        // given
        var sourceLine = "       (identifier [1, 2] - [3, 4])";

        // when
        var optionalToken = SourceToken.fromTreeSitterLine(sourceLine);

        // then
        assertTrue(optionalToken.isPresent());

        var token = optionalToken.get();

        assertEquals(TokenType.IDENTIFIER, token.getTokenType());
        assertEquals(1, token.getLocation().beginLine);
        assertEquals(2, token.getLocation().beginColumn);
        assertEquals(3, token.getLocation().endLine);
        assertEquals(4, token.getLocation().endColumn);
    }
}

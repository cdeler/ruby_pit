package cdeler.highlight.token;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JNITokenizerUnittest {
    @Test
    public void testHelloWorld() throws IOException {
        // given
        String inputSource = "puts \"Hello world!\"";
        Tokenizer tokenizer = new JNITokenizer();
        var expected = Arrays.asList(
                new SourceToken(TokenType.string, new TokenLocation(0, 5, 0, 19))
        );

        // when
        List<Token> actual = tokenizer.harvest(inputSource);

        // then
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    public void testSymbolsParsing() {
        // given
        String inputSource = ":test";
        Tokenizer tokenizer = new JNITokenizer();
        var expected = Arrays.asList(
                new SourceToken(TokenType.symbol, new TokenLocation(0, 0, 0, 5))
        );

        // when
        List<Token> actual = tokenizer.harvest(inputSource);

        // then
        assertNotNull(actual);
        assertEquals(expected, actual);
    }
}

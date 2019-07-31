package cdeler.highlight;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import cdeler.highlight.token.JNITokenizer;
import cdeler.highlight.token.SourceToken;
import cdeler.highlight.token.Token;
import cdeler.highlight.token.TokenLocation;
import cdeler.highlight.token.TokenType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JNITokenizerUnittest {
    @Test
    public void testHelloWorld() throws IOException {
        // given
        String inputSource = "puts \"Hello world!\"";
        JNITokenizer tokenizer = new JNITokenizer();
        var expected = Arrays.asList(
                new SourceToken(TokenType.identifier, new TokenLocation(0, 0, 0, 4)),
                new SourceToken(TokenType.string, new TokenLocation(0, 5, 0, 19))
        );

        // when
        List<Token> actual = tokenizer.harvest(inputSource);

        // then
        assertNotNull(actual);
        assertEquals(expected, actual);
    }
}

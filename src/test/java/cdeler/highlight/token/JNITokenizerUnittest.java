package cdeler.highlight.token;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import cdeler.highlight.settings.UISettingsManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JNITokenizerUnittest {
    private static UISettingsManager manager;

    @BeforeClass
    public static void initializeUIManager() {
        manager = Mockito.mock(UISettingsManager.class);
        Stream.of(TokenType.identifier,
                TokenType.program,
                TokenType.method_call,
                TokenType.argument_list,
                TokenType.unknown)
                .forEach(tokenType -> Mockito.doReturn(false).when(manager).isHighlightedToken(tokenType));


        Stream.of(TokenType.string, TokenType.symbol, TokenType.constant, TokenType.comment)
                .forEach(tokenType -> Mockito.doReturn(true).when(manager).isHighlightedToken(tokenType));
    }

    @Test
    public void testHelloWorld() throws IOException {
        // given
        String inputSource = "puts \"Hello world!\"";
        Tokenizer tokenizer = new JNITokenizer(manager);
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
        Tokenizer tokenizer = new JNITokenizer(manager);
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

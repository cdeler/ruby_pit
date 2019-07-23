package cdeler.highlight;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;


public class ClITokenizerUnittest {
    @Test
    public void smallSmokeTestWithHelloWorld() throws IOException {
        /*
            puts "Hello World" ->
            (program [0, 0] - [1, 0]
              (method_call [0, 0] - [0, 18]
                (identifier [0, 0] - [0, 4])
                (argument_list [0, 5] - [0, 18]
                  (string [0, 5] - [0, 18]))))
         */

        // given
        final String helloWorldSource = "puts \"Hello World\"";
        final CLITokenizer tok = spy(new CLITokenizer("/proc/self/exe"));
        doReturn(Arrays.asList(
                "(program [0, 0] - [1, 0]",
                "  (method_call [0, 0] - [0, 18]",
                "    (identifier [0, 0] - [0, 4])",
                "      (string [0, 5] - [0, 18]))))"
        )).when(tok).spawnChildProcess(any(File.class));


        // when
        var tokens = tok.harvest(IOUtils.toInputStream(helloWorldSource));

        // then
        assertEquals(2, tokens.size());

        var firstToken = tokens.get(0);
        assertEquals(firstToken.getTokenType(), TokenType.IDENTIFIER);
        assertEquals(firstToken.getLocation(), new TokenLocation(0, 0, 0, 4));

        var lastToken = tokens.get(1);
        assertEquals(lastToken.getTokenType(), TokenType.STRING);
        assertEquals(lastToken.getLocation(), new TokenLocation(0, 5, 0, 18));
    }
}

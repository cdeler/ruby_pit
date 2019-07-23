package cdeler.highlight;

import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class ClITokenizerUnittest {
    /*
    puts "Hello World" ->
    (program [0, 0] - [1, 0]
      (method_call [0, 0] - [0, 18]
        (identifier [0, 0] - [0, 4])
        (argument_list [0, 5] - [0, 18]
          (string [0, 5] - [0, 18]))))

     */
    @Test
    public void smallSmokeTestWithHelloWorld() {
        // given
        final Tokenizer tok = new CLITokenizer("/proc/self/exe");
        final String helloWorldSource = "puts \"Hello World\"";

        // when
        var tokenStream = tok.harvest(IOUtils.toInputStream(helloWorldSource)).collect(Collectors.toList());

        // then
        assertEquals(2, tokenStream.size());

        var firstToken = tokenStream.get(0);
        assertEquals(firstToken.getTokenType(), TokenType.IDENTIFIER);
        assertEquals(firstToken.getLocation(), new TokenLocation(0, 0, 0, 4));

        var lastToken = tokenStream.get(1);
        assertEquals(lastToken.getTokenType(), TokenType.STRING);
        assertEquals(lastToken.getLocation(), new TokenLocation(0, 5, 0, 18));
    }
}

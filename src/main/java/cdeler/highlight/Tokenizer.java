package cdeler.highlight;

import java.io.InputStream;
import java.util.stream.Stream;

public interface Tokenizer {
    Stream<Token> harvest(InputStream is);
}

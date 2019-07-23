package cdeler.highlight;

import java.io.InputStream;
import java.util.stream.Stream;

public interface Tokenizer {
    void feed(InputStream is);

    Stream<Token> harvest();
}

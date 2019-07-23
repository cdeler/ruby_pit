package cdeler.highlight;

import java.io.InputStream;
import java.util.List;

public interface Tokenizer {
    List<Token> harvest(InputStream is);
}

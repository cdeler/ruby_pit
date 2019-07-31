package cdeler.highlight.token;

import java.util.List;

public interface Tokenizer {
    List<Token> harvest(String inputText);
}

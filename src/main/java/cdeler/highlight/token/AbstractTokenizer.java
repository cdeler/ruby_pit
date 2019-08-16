package cdeler.highlight.token;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cdeler.highlight.HighlightException;

public abstract class AbstractTokenizer<T> implements Tokenizer {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTokenizer.class);

    protected abstract T feed(String inputText) throws HighlightException;

    protected abstract List<Token> build(T data);

    @Override
    final public List<Token> harvest(String inputText) {
        try {
            T intermediateData = feed(inputText);
            return build(intermediateData);
        } catch (HighlightException ex) {
            LOGGER.error("Error happens during harvesting", ex);
        }

        return Collections.emptyList();
    }
}

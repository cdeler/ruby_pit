package cdeler.highlight;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTokenizer implements Tokenizer {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTokenizer.class);

    abstract List<String> feed(InputStream is) throws HighlightException;

    abstract Stream<Token> build(List<String> data);

    @Override
    public Stream<Token> harvest(InputStream is) {
        try {
            List<String> intermediateData = feed(is);
            return build(intermediateData);
        } catch (HighlightException ex) {
            LOGGER.error("Error happens during harvesting", ex);
        }

        return Stream.empty();
    }
}

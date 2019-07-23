package cdeler.highlight;

import java.io.InputStream;
import java.util.stream.Stream;

public class CLITokenizer implements Tokenizer {
    private final String executablePath;

    public CLITokenizer(String executablePath) {
        this.executablePath = executablePath;
    }

    @Override
    public void feed(InputStream is) {

    }

    @Override
    public Stream<Token> harvest() {
        return null;
    }
}

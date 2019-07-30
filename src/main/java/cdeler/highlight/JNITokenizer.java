package cdeler.highlight;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.scijava.nativelib.NativeLoader;


public class JNITokenizer extends AbstractTokenizer<Optional<AST<Token>>> {
    static {
        try {
            NativeLoader.loadLibrary("rubypit");
        } catch (UnsatisfiedLinkError | IOException e) {
            throw new RuntimeException("Highlight will never work", e);
        }
    }

    @Override
    protected Optional<AST<Token>> feed(String inputText) throws HighlightException {
        synchronized (this) {
            var nativeResult = feed_internal(inputText);

            return Optional.ofNullable(nativeResult);
        }
    }

    @Override
    protected List<Token> build(Optional<AST<Token>> data) {
        List<Token> result = new ArrayList<>();

        if (data.isPresent()) {
            var tokenData = data.get();

            tokenData.walk((token -> {
                if (TokenType.isHighlightedToken(token.getTokenType())) {
                    result.add(token);
                }

                return null;
            }));
        }

        return result;
    }

    private native AST<Token> feed_internal(String source);
}

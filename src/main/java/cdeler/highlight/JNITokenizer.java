package cdeler.highlight;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    protected Optional<AST<Token>> feed(InputStream is) throws HighlightException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            var native_result = feed_internal(reader.lines().collect(Collectors.joining()));
            return Optional.ofNullable(native_result);
        } catch (IOException e) {
        }

        return Optional.empty();
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

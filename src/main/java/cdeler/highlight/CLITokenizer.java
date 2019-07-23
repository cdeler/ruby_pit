package cdeler.highlight;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CLITokenizer extends AbstractTokenizer<List<String>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CLITokenizer.class);
    private final String executablePath;

    public CLITokenizer(String executablePath) {
        this.executablePath = executablePath;
    }

    @Override
    public List<String> feed(InputStream is) throws HighlightException {
        File sourceFile = null;

        try {
            sourceFile = getTemporaryFile();

            try (FileWriter writer = new FileWriter(sourceFile);
                 BufferedWriter bw = new BufferedWriter(writer)) {
                IOUtils.copy(is, bw);
            }

            return spawnChildProcess(sourceFile);
        } catch (IOException ex) {
            LOGGER.error("Cannot process feed", ex);
            throw new HighlightException(ex);
        } finally {
            if (sourceFile != null) {
                sourceFile.delete();
            }
        }
    }

    @Override
    List<Token> build(List<String> data) {
        return data.stream().map(SourceToken::fromTreeSitterLine).flatMap(Optional::stream).collect(Collectors.toList());
    }

    List<String> spawnChildProcess(final File sourceFile) throws IOException {
        List<String> result = Collections.emptyList();
        final ProcessBuilder processBuilder =
                new ProcessBuilder(executablePath, "parse", sourceFile.getAbsolutePath());

        final Process process = processBuilder.start();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            result = (List<String>) IOUtils.readLines(in);
        }

        return result;
    }

    private static File getTemporaryFile() throws IOException {
        final File sourceFile = File.createTempFile("ruby_pit", ".tmp");

        sourceFile.deleteOnExit();

        return sourceFile;
    }

}

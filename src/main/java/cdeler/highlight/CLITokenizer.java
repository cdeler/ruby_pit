package cdeler.highlight;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CLITokenizer extends AbstractTokenizer<List<String>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CLITokenizer.class);
    private final String executablePath;
    private final Path treeSitterPath;
    private final Path treeSitterRubyPath;

    public CLITokenizer(String executableFileName, String treeSitterResourcePath, String treeSitterRubyResourcePath) {

        this.treeSitterPath = unpackResourceToTempDir(treeSitterResourcePath).orElseThrow();
        this.treeSitterRubyPath = unpackResourceToTempDir(treeSitterRubyResourcePath).orElseThrow();

        recursiveDeleteOnShutdownHook(this.treeSitterPath, this.treeSitterRubyPath);

        setExecutableBit(executableFileName, treeSitterPath);
        this.executablePath = treeSitterPath.toAbsolutePath() + "/" + executableFileName;

        LOGGER.info("Created " + this);
    }

    public String toString() {
        return getClass().getName() + "("
                + this.executablePath + ", " + this.treeSitterPath + ", " + this.treeSitterRubyPath +
                ")";
    }

    private static void setExecutableBit(String executableName, Path executableDirectory) {
        try {
            Files.walkFileTree(executableDirectory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file,
                                                 @SuppressWarnings("unused") BasicFileAttributes attrs)
                        throws IOException {
                    Set<PosixFilePermission> perms = new HashSet<>();
                    perms.add(PosixFilePermission.OWNER_READ);
                    perms.add(PosixFilePermission.OWNER_WRITE);
                    perms.add(PosixFilePermission.OWNER_EXECUTE);
                    Files.setPosixFilePermissions(file, perms);

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException e)
                        throws IOException {
                    if (e == null) {
                        return FileVisitResult.CONTINUE;
                    }
                    // directory iteration failed
                    throw e;
                }
            });
        } catch (IOException e) {
            LOGGER.error("Unable to set +x bit to file" + executableName, e);
        }
    }

    private static Optional<Path> unpackResourceToTempDir(String resourceName) {
        try {
            Path directoryToUnpack = Files.createTempDirectory(resourceName.substring(1));

            var uri = CLITokenizer.class.getResource(resourceName).toURI();

            if (uri.getScheme().contains("jar")) {
                try (FileSystem jarFS = FileSystems.newFileSystem(uri, Collections.emptyMap(), null)) {
                    Path packedDirectory = jarFS.getPath(resourceName.substring(1));

                    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(packedDirectory)) {
                        for (Path filePath : directoryStream) {
                            var newFilePath = Paths.get(
                                    directoryToUnpack.toString(),
                                    filePath.toString().substring(resourceName.length()));

                            Files.copy(filePath, newFilePath);
                        }
                    }

                }
            } else {
                File inputDirectory = new File(uri);
                FileUtils.copyDirectory(inputDirectory, directoryToUnpack.toFile());
            }

            return Optional.of(directoryToUnpack);
        } catch (IOException | URISyntaxException e) {
            LOGGER.error("Unable to unpack " + resourceName, e);
        }

        return Optional.empty();
    }

    // https://stackoverflow.com/questions/15022219/does-files-createtempdirectory-remove-the-directory-after-jvm
    // -exits-normally
    private static void recursiveDeleteOnShutdownHook(final Path... directories) {
        Runtime.getRuntime().addShutdownHook(new Thread(
                () -> {
                    try {
                        for (var directory : directories) {
                            Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                                @Override
                                public FileVisitResult visitFile(Path file,
                                                                 @SuppressWarnings("unused") BasicFileAttributes attrs)
                                        throws IOException {
                                    Files.delete(file);
                                    return FileVisitResult.CONTINUE;
                                }

                                @Override
                                public FileVisitResult postVisitDirectory(Path dir, IOException e)
                                        throws IOException {
                                    if (e == null) {
                                        Files.delete(dir);
                                        return FileVisitResult.CONTINUE;
                                    }
                                    // directory iteration failed
                                    throw e;
                                }
                            });
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to delete temp directories", e);
                    }
                }));
    }

    @Override
    protected List<String> feed(InputStream is) throws HighlightException {
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
    protected List<Token> build(List<String> data) {
        return data.stream().map(SourceToken::fromTreeSitterLine).flatMap(Optional::stream).collect(Collectors.toList());
    }

    List<String> spawnChildProcess(final File sourceFile) throws IOException {
        List<String> result;

        final ProcessBuilder processBuilder =
                new ProcessBuilder(executablePath, "parse", sourceFile.getAbsolutePath());

        processBuilder.directory(treeSitterRubyPath.toFile());

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

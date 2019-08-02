package cdeler.core.io;

import java.io.IOException;
import java.nio.file.Path;

public interface FileManager {
    void openFile(Path file) throws IOException;

    boolean saveFile(Path file) throws IOException;

    String getFileContent();

    void write(String data) throws IOException;

    boolean saveFile(Path file, boolean override) throws IOException;
}

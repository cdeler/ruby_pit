package cdeler.core.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

public class InMemoryFileManager implements FileManager {
    @NotNull
    private volatile byte[] fileDataStorage;

    public InMemoryFileManager() {
        fileDataStorage = new byte[0];
    }

    @Override
    public void openFile(@NotNull Path file) throws IOException {
        try (var is = new BufferedInputStream(new FileInputStream(file.toFile()));
             var os = new ByteArrayOutputStream(4096)) {
            IOUtils.copy(is, os);
            synchronized (this) {
                fileDataStorage = os.toByteArray();
            }
        }
    }

    @Override
    public boolean saveFile(@NotNull Path file) throws IOException {
        return saveFile(file, true);
    }

    @Override
    @NotNull
    public String getFileContent() {
        return new String(fileDataStorage);
    }

    @Override
    public void write(@NotNull String data) throws IOException {
        try (var is = IOUtils.toInputStream(data);
             var os = new ByteArrayOutputStream(4096)) {
            IOUtils.copy(is, os);
            synchronized (this) {
                fileDataStorage = os.toByteArray();
            }
        }
    }

    public synchronized boolean saveFile(@NotNull Path destFile, boolean override) throws IOException {
        boolean result = false;

        if (!Files.exists(destFile) || override) {
            try (var is = new ByteArrayInputStream(fileDataStorage);
                 var os = new BufferedOutputStream(new FileOutputStream(destFile.toFile()))) {
                IOUtils.copy(is, os);
                result = true;
            }
        }

        return result;
    }
}

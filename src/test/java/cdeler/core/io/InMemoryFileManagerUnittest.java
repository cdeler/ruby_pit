package cdeler.core.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InMemoryFileManagerUnittest {
    private final static String DEFAULT_FILE_CONTEXT = "test...test...test...";
    private Path workFile;

    @Before
    public void setUp() throws IOException {
        workFile = Files.createTempFile("ruby_pit_unittest", ".tst");
        IOUtils.write(DEFAULT_FILE_CONTEXT, Files.newOutputStream(workFile));
        workFile.toFile().deleteOnExit();
    }

    @After
    public void tearDown() throws IOException {
        Files.delete(workFile);
    }

    @Test
    public void testDefaultStringIsEmpty() {
        // given
        FileManager manager = new InMemoryFileManager();
        var expected = "";

        // when
        var actual = manager.getFileContent();

        // then
        assertEquals(expected.length(), actual.length());
    }

    @Test
    public void testWriteRead() throws IOException {
        // given
        FileManager manager = new InMemoryFileManager();
        var expected = "test";

        // when
        manager.write(expected);
        var actual = manager.getFileContent();

        // then
        assertEquals(expected, actual);
    }

    @Test
    public void testSaveFile() throws IOException {
        // given
        FileManager manager = new InMemoryFileManager();
        var text = Stream.of("123", "456", "678").collect(Collectors.joining(System.lineSeparator()));

        // when
        manager.write(text);
        var hasBeenSaved = manager.saveFile(workFile);

        // then
        assertTrue(hasBeenSaved);
        assertEquals(text, manager.getFileContent());
        assertEquals(text, FileUtils.readFileToString(workFile.toFile()));
    }

    @Test
    public void testCoupleOfSaves() throws IOException {
        // given
        FileManager manager = new InMemoryFileManager();
        var textForFirstSave = Stream.of("123", "456", "789").collect(Collectors.joining(System.lineSeparator()));
        var textForSecondSave = Stream.of("987", "654", "321").collect(Collectors.joining(System.lineSeparator()));

        // when
        manager.write(textForFirstSave);
        var hasBeenSaved = manager.saveFile(workFile);
        manager.write(textForSecondSave);
        hasBeenSaved = manager.saveFile(workFile, false);

        // then
        assertFalse(hasBeenSaved);
        assertEquals(textForSecondSave, manager.getFileContent());
        assertEquals(textForFirstSave, FileUtils.readFileToString(workFile.toFile()));
    }

    @Test
    public void testOpenFile() throws IOException {
        // given
        FileManager manager = new InMemoryFileManager();

        // when
        manager.openFile(workFile);

        // then
        assertEquals(DEFAULT_FILE_CONTEXT, manager.getFileContent());
    }
}

package core;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import cdeler.core.IDEException;
import cdeler.core.SparseArraySourceStorage;

import static org.junit.Assert.assertEquals;

public class SparseArraySourceStorageUnittest {
    @Test
    public void testDumpLoad() throws IOException, IDEException {
        try (var sourceFile = getClass().getResourceAsStream("/ruby_sources/lookup_context.rb")) {
            // given
            List<String> sourceTextLines = IOUtils.readLines(sourceFile);

            // when
            SparseArraySourceStorage storage = new SparseArraySourceStorage(sourceTextLines);
            List<String> dumpedLines = storage.dump();

            // then
            assertEquals(sourceTextLines, dumpedLines);
        }
    }
}

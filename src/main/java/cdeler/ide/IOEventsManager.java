package cdeler.ide;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cdeler.core.Event;
import cdeler.core.EventThread;
import cdeler.core.io.FileManager;
import cdeler.core.io.IOEventType;

public class IOEventsManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(IOEventsManager.class);
    @Nullable
    private volatile Path currentFile;
    @NotNull
    private final FileManager manager;
    @NotNull
    private final JTextComponent textArea;
    @NotNull
    private final EventThread<IOEventType> ioEventsThread;

    public IOEventsManager(@NotNull FileManager manager, @NotNull Ide ide) {
        this.manager = manager;
        this.textArea = ide.getTextArea();
        this.currentFile = null;
        this.ioEventsThread = new EventThread<>();
        this.ioEventsThread.addConsumers(getIOEvents());
        initializeEventListeners(ide);

        new Thread(this.ioEventsThread, "io_events_thread2").start();
    }

    private Map<IOEventType, Function<List<Event<IOEventType>>, Void>> getIOEvents() {
        Map<IOEventType, Function<List<Event<IOEventType>>, Void>> result = new HashMap<>();

        result.put(IOEventType.FILE_SAVE_EVENT, events -> {
            saveFile();
            return null;
        });
        return result;
    }

    private void initializeEventListeners(@NotNull Ide ide) {
        var saveButton = ide.getSaveButton();
        saveButton.addActionListener(actionEvent -> {
            LOGGER.error("Save button pressed");

            ioEventsThread.fire(new Event<>(IOEventType.FILE_SAVE_EVENT));
        });

    }

    private void saveFile() {
        Path file = currentFile;
        if (file == null) {
            JFileChooser fileOpenDialog = new JFileChooser();
            int ret = fileOpenDialog.showDialog(null, "Choose file name");
            if (ret == JFileChooser.APPROVE_OPTION) {
                saveFileInternal(fileOpenDialog.getSelectedFile().toPath());
            }
        } else {
            saveFileInternal(file);
        }
    }

    private synchronized void saveFileInternal(@NotNull Path file) {
        try {
            currentFile = file;
            manager.write(textArea.getText());
            manager.saveFile(currentFile);
        } catch (IOException e) {
            LOGGER.error("Unable to open file {}", file, e);
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(new JFrame(),
                        e.getMessage(),
                        "Unable to read file",
                        JOptionPane.ERROR_MESSAGE);
            });
        }

    }
}

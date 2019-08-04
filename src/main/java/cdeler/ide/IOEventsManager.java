package cdeler.ide;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
import cdeler.highlight.settings.UISettingsManager;

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
    @NotNull
    private final JFrame ide;
    @NotNull
    private final UISettingsManager settingsManager;

    public IOEventsManager(@NotNull FileManager manager, @NotNull Ide ide, @NotNull UISettingsManager settingsManager) {
        this.manager = manager;
        this.textArea = ide.getTextArea();
        this.ide = ide;
        this.settingsManager = settingsManager;

        this.currentFile = null;
        this.ioEventsThread = new EventThread<>();
        this.ioEventsThread.addConsumers(getIOEvents());
        initializeEventListeners(ide);

        new Thread(this.ioEventsThread, "io_events_thread").start();

        LOGGER.info("IOEventsManager has been initialized");
    }

    private Map<IOEventType, Function<List<Event<IOEventType>>, Void>> getIOEvents() {
        Map<IOEventType, Function<List<Event<IOEventType>>, Void>> result = new HashMap<>();

        result.put(IOEventType.FILE_SAVE_EVENT, events -> {
            saveFile();
            return null;
        });

        result.put(IOEventType.FILE_OPEN_EVENT, events -> {
            openFile();
            return null;
        });

        return result;
    }

    private void openFile() {
        JFileChooser fileOpenDialog = new JFileChooser();
        int ret = fileOpenDialog.showDialog(null, "Open file");
        if (ret == JFileChooser.APPROVE_OPTION) {
            synchronized (this) {
                File inputFile = fileOpenDialog.getSelectedFile();

                LOGGER.info("Opening file {}", inputFile.getAbsolutePath());

                try (var is = new FileInputStream(inputFile);
                     var reader = new BufferedReader(new InputStreamReader(is))) {

                    textArea.setText(reader.lines().collect(Collectors.joining(System.lineSeparator())));

                    // todo fix it
                    //     test it, might be unnecessary
                    // var event = new Event<>(UIEventType.TEXT_AREA_TEXT_CHANGED);
                    // uiEventThread.fire(event);
                    // highlightThread.fire(event);

                    currentFile = inputFile.getAbsoluteFile().toPath();

                    ide.setTitle(getNewTitle(currentFile));
                } catch (IOException e) {
                    LOGGER.error("Unable to read file " + inputFile.getAbsolutePath(), e);
                }
            }
        }
    }

    private String getNewTitle(@Nullable Path openedFile) {
        if (openedFile != null) {
            Path fileName = openedFile.getFileName();

            if (fileName != null) {
                return settingsManager.getIdeTitle() + " : " + fileName;
            }
        }

        return settingsManager.getIdeTitle();
    }

    private void initializeEventListeners(@NotNull Ide ide) {
        var saveButton = ide.getSaveButton();
        saveButton.addActionListener(actionEvent -> {
            LOGGER.debug("Save button pressed");

            ioEventsThread.fire(new Event<>(IOEventType.FILE_SAVE_EVENT));
        });

        var openButton = ide.getOpenButton();
        openButton.addActionListener(actionEvent -> {
            LOGGER.debug("Open button pressed");
            ioEventsThread.fire(new Event<>(IOEventType.FILE_OPEN_EVENT));
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

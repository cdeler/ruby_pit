package cdeler.ide.events;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
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

import javax.swing.*;
import javax.swing.text.BadLocationException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cdeler.core.Event;
import cdeler.core.EventThread;
import cdeler.core.io.FileManager;
import cdeler.core.io.IOEventType;
import cdeler.highlight.settings.UISettingsManager;
import cdeler.ide.Ide;

public class IOEventsManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(IOEventsManager.class);
    @Nullable
    private volatile Path currentFile;
    @NotNull
    private final FileManager manager;
    @NotNull
    private final JTextPane textArea;
    @NotNull
    private final EventThread<IOEventType> ioEventsThread;
    @NotNull
    private final JFrame ide;
    @NotNull
    private final UISettingsManager settingsManager;
    @NotNull
    private final Map<KeyStroke, Action> handledKeyboardActions;
    @NotNull
    private final JButton openButton;
    @NotNull
    private final UIEventsManager uiEventsManager;

    public IOEventsManager(@NotNull FileManager manager,
                           @NotNull Ide ide,
                           @NotNull UISettingsManager settingsManager,
                           @NotNull UIEventsManager uiEventsManager) {
        this.manager = manager;
        this.openButton = ide.getOpenButton();
        this.textArea = ide.getTextArea();
        this.ide = ide;
        this.settingsManager = settingsManager;
        this.uiEventsManager = uiEventsManager;

        this.handledKeyboardActions = new HashMap<>();
        this.currentFile = null;
        this.ioEventsThread = new EventThread<>();
        this.ioEventsThread.addConsumers(getIOEvents());

        initializeEventListeners(ide);
        setUpSaveKeyboardEvent();

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

    private void setUpSaveKeyboardEvent() {
        KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK);
        handledKeyboardActions.put(key, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                LOGGER.debug("Processing CTRL+S handler");
                saveFile();
            }
        });

        KeyboardFocusManager keyManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        keyManager.addKeyEventDispatcher(e -> {
            KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(e);
            if (handledKeyboardActions.containsKey(keyStroke)) {
                final Action action = handledKeyboardActions.get(keyStroke);
                final ActionEvent actionEvent = new ActionEvent(e.getSource(), e.getID(), null);

                action.actionPerformed(actionEvent);
                return true;
            }
            return false;
        });
    }

    private void openFile() {
        JFileChooser fileOpenDialog = new JFileChooser();
        int ret = fileOpenDialog.showDialog(null, "Open file");
        if (ret == JFileChooser.APPROVE_OPTION) {
            File inputFile = fileOpenDialog.getSelectedFile();

            LOGGER.info("Opening file {}", inputFile.getAbsolutePath());

            try (var is = new FileInputStream(inputFile);
                 var reader = new BufferedReader(new InputStreamReader(is))) {
                openButton.setEnabled(false);

                var newDoc = textArea.getEditorKit().createDefaultDocument();

                var style = settingsManager.getDefaultActiveStyle().asAttributeSet();
                reader.lines().forEachOrdered(line -> {
                    try {
                        var lineToInsert = line + System.lineSeparator();
                        newDoc.insertString(newDoc.getLength(), lineToInsert, style);
                    } catch (BadLocationException ignored) {
                    }
                });

                uiEventsManager.hookDocumentListeners(newDoc);

                textArea.setDocument(newDoc);

                currentFile = inputFile.getAbsoluteFile().toPath();

                ide.setTitle(getNewTitle(currentFile));

                // the "redraw all" event should be fired only if swing draw has been complete
                EventQueue.invokeLater(uiEventsManager::redrawAll);
            } catch (IOException e) {
                LOGGER.error("Unable to read file " + inputFile.getAbsolutePath(), e);
            } finally {
                openButton.setEnabled(true);
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
        LOGGER.info("Saving file {}", file);
        try {
            currentFile = file;
            manager.write(textArea.getText());
            manager.saveFile(currentFile);
            ide.setTitle(getNewTitle(currentFile));
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

package cdeler.highlight.settings;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;


public class UISettingsManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(UISettingsManager.class);

    private final Map<String, UISettings> settingMap;

    public UISettingsManager(String settingsDirectoryPath) {
        this.settingMap = loadSettings(settingsDirectoryPath);
    }

    private static Map<String, UISettings> loadSettings(String settingsPath) {
        try (var walker = Files.walk(Paths.get(settingsPath))) {

            return walker.filter(Files::isRegularFile)
                    .filter(it -> it.endsWith(".json"))
                    .map(UISettingsManager::loadSetting)
                    .flatMap(Optional::stream)
                    .collect(Collectors.toMap(UISettings::getName, it -> it));
        } catch (IOException e) {
            LOGGER.error("Unable to load settings from " + settingsPath, e);
        }

        Map<String, UISettings> defaultResult = new HashMap<>();
        defaultResult.put(UISettings.getDefaultSettings().getName(), UISettings.getDefaultSettings());
        return defaultResult;
    }

    private static Optional<UISettings> loadSetting(Path settingFile) {
        try (
                InputStream is = new FileInputStream(settingFile.toFile());
                Reader reader = new BufferedReader(new InputStreamReader(is))) {
            Gson gson = new Gson();
            return Optional.of(gson.fromJson(reader, UISettings.class));
        } catch (FileNotFoundException e) {
            LOGGER.error("File " + settingFile + " is not found", e);
        } catch (IOException e) {
            LOGGER.error("Unable to load " + settingFile + " file", e);
        }

        return Optional.empty();
    }
}

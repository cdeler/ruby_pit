package cdeler.highlight.settings;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import cdeler.core.FontLoader;


public class UISettingsManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(UISettingsManager.class);

    private final Map<String, UISettings> settingMap;
    private volatile String activeSettingsSet;

    public UISettingsManager(String settingsDirectoryPath) {
        this.settingMap = loadSettings(settingsDirectoryPath);
        this.activeSettingsSet = UISettings.getDefaultSettingsName();
    }

    private static Map<String, UISettings> loadSettings(String settingsPath) {
        Map<String, UISettings> result = new HashMap<>();
        result.put(UISettings.getDefaultSettingsName(), UISettings.getDefaultSettings());

        try (var walker = Files.walk(Paths.get(settingsPath))) {
            var storedSettings = walker.filter(Files::isRegularFile)
                    .filter(it -> it.endsWith(".json"))
                    .map(UISettingsManager::loadSetting)
                    .flatMap(Optional::stream)
                    .collect(Collectors.toMap(UISettings::getName, it -> it));
            result.putAll(storedSettings);
        } catch (NoSuchFileException e) {
            LOGGER.info("Unable to load settings from " + settingsPath);
        } catch (IOException e) {
            LOGGER.error("Unable to load settings from " + settingsPath, e);
        }

        return result;
    }

    private UISettings getActiveSettings() {
        Optional<UISettings> settings = getSettingsByName(activeSettingsSet);

        return settings.orElse(UISettings.getDefaultSettings());
    }

    public Font getActiveFont() {
        var activeSettings = getActiveSettings();

        return FontLoader.load(activeSettings.getFontName(), activeSettings.getFontSize());
    }

    public UISettings getDefaultSettings() {
        return settingMap.getOrDefault(UISettings.getDefaultSettingsName(), UISettings.getDefaultSettings());
    }

    public Optional<UISettings> getSettingsByName(String settingName) {
        return Optional.ofNullable(settingMap.getOrDefault(settingName, null));
    }

    public Set<String> getAvailableSettings() {
        return settingMap.keySet();
    }

    public String getActiveSettingsSet() {
        return activeSettingsSet;
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

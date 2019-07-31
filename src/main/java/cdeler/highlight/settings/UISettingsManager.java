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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import cdeler.core.FontLoader;
import cdeler.highlight.token.TokenType;


public class UISettingsManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(UISettingsManager.class);

    private final Map<String, UISettings> settingMap;
    private final UISettings defaultSettings;
    private final Set<TokenType> highlightedTokens;
    private volatile String activeSettingsSet;

    public UISettingsManager(String settingsDirectoryPath, UISettings defaultSettings,
                             Collection<TokenType> highlightedTokens) {
        this.defaultSettings = defaultSettings;
        this.highlightedTokens = new HashSet<>(highlightedTokens);
        this.settingMap = loadSettings(settingsDirectoryPath, this.defaultSettings);
        this.activeSettingsSet = this.defaultSettings.getName();
    }

    public boolean isHighlightedToken(TokenType type) {
        return (type != null) && highlightedTokens.contains(type);
    }

    private static Map<String, UISettings> loadSettings(String settingsPath, UISettings defaultSettings) {
        Map<String, UISettings> result = new HashMap<>();
        result.put(defaultSettings.getName(), defaultSettings);

        try (var walker = Files.walk(Paths.get(settingsPath))) {
            var storedSettings = walker.filter(Files::isRegularFile)
                    .filter(it -> it.endsWith(".json"))
                    .map(UISettingsManager::loadSetting)
                    .flatMap(Optional::stream)
                    .filter(it -> !defaultSettings.equals(it))
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

        return settings.orElse(getDefaultSettings());
    }

    public TokenStyle getActiveStyleForTokenType(TokenType tokenType) {
        var activeSettings = getActiveSettings();

        return activeSettings.getTokenStyle().getOrDefault(tokenType, TokenStyle.getFallbackTokenStyle());
    }

    public Font getActiveFont() {
        var activeSettings = getActiveSettings();

        return FontLoader.load(activeSettings.getFontName(), activeSettings.getFontSize());
    }

    public UISettings getDefaultSettings() {
        return settingMap.getOrDefault(defaultSettings.getName(), defaultSettings);
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

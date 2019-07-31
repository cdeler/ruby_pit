package cdeler.highlight.settings;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
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
    private static final String SETTINGS_DIRECTORY_NAME = ".ruby_pit";
    private static final String SETTINGS_FILE_NAME = "settings.json";

    private final Map<String, UISettings> settingMap;
    private final UISettings defaultSettings;
    private final Set<TokenType> highlightedTokens;
    private volatile String activeSettingsSet;

    public UISettingsManager(UISettings defaultSettings,
                             Collection<TokenType> highlightedTokens) {
        this.defaultSettings = defaultSettings;
        this.highlightedTokens = new HashSet<>(highlightedTokens);
        this.settingMap = loadSettings(this.defaultSettings);
        this.activeSettingsSet = this.defaultSettings.getName();
    }

    public boolean isHighlightedToken(TokenType type) {
        return (type != null) && highlightedTokens.contains(type);
    }


    private static Path getSettingsFile() {
        return Paths.get(System.getProperty("user.home"), SETTINGS_DIRECTORY_NAME, SETTINGS_FILE_NAME).toAbsolutePath();
    }

    private static Map<String, UISettings> loadSettings(UISettings defaultSettings) {
        Map<String, UISettings> result = new HashMap<>();
        result.put(defaultSettings.getName(), defaultSettings);

        Path settingsFile = getSettingsFile();
        try (InputStream is = new FileInputStream(settingsFile.toFile());
             Reader reader = new BufferedReader(new InputStreamReader(is))) {

            Gson gson = new Gson();
            Map<String, UISettings> settingsFromFile =
                    Arrays.stream(gson.fromJson(reader, UISettings[].class))
                    .filter(it -> !defaultSettings.equals(it))
                    .collect(Collectors.toMap(UISettings::getName, it -> it));

            result.putAll(settingsFromFile);
        } catch (FileNotFoundException e) {
            LOGGER.info("Unable to load settings from " + settingsFile);
            createDefaultSettingsFile(settingsFile);
        } catch (IOException e) {
            LOGGER.error("Unable to load settings from " + settingsFile, e);
        }

        return result;
    }

    private static void createDefaultSettingsFile(Path settingsFile) {
        LOGGER.info("createDefaultSettingsFile(" + settingsFile + ") is not implemented");
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
}

package cdeler.highlight.settings;

import java.awt.*;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import cdeler.core.FontLoader;
import cdeler.highlight.token.TokenType;
import cdeler.ide.UIEventsManager;


public class UISettingsManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(UISettingsManager.class);
    private static final String SETTINGS_DIRECTORY_NAME = ".ruby_pit";
    private static final String SETTINGS_FILE_NAME = "settings.json";

    private final Map<String, UISettings> settingMap;
    private final UISettings defaultSettings;
    private volatile Set<TokenType> highlightedTokens;
    private volatile String activeSettingsSet;
    private final String ideTitle;

    public UISettingsManager(UISettings defaultSettings, String ideTitle) {
        this.defaultSettings = defaultSettings;
        this.ideTitle = ideTitle;
        this.settingMap = loadSettings(this.defaultSettings);
        this.highlightedTokens = new HashSet<>(getActiveSettings().getTokenStyle().keySet());

        this.activeSettingsSet = this.defaultSettings.getName();
    }

    public boolean isHighlightedToken(TokenType type) {
        return (type != null) && highlightedTokens.contains(type);
    }

    public synchronized void setActiveSettingsSet(String settingsSetName) {
        activeSettingsSet = settingsSetName;
        highlightedTokens = new HashSet<>(getActiveSettings().getTokenStyle().keySet());
    }

    @NotNull
    @Contract(" -> new")
    private static InputStream getSettingsFileInputStream() throws FileNotFoundException {
        Path settingsFile =
                Paths.get(System.getProperty("user.home"),
                        SETTINGS_DIRECTORY_NAME,
                        SETTINGS_FILE_NAME).toAbsolutePath();

        if (Files.notExists(settingsFile)) {
            LOGGER.info("Creating the default settings file {}", settingsFile);
            createDefaultSettingsFile(settingsFile);
        }

        LOGGER.info("Loading themes from {}", settingsFile);

        return new FileInputStream(settingsFile.toFile());
    }

    private static void createDefaultSettingsFile(@NotNull Path settingsFile) {
        var parentDirectory = settingsFile.getParent().toFile();
        if (!parentDirectory.exists() && !settingsFile.getParent().toFile().mkdirs()) {
            LOGGER.error("Unable to create directory {} to settings file. You can create it manually and restart IDE",
                    settingsFile.getParent().toFile());
        }

        try (InputStream is = UIEventsManager.class.getResourceAsStream("/defaut_settings.json");
             OutputStream os = new BufferedOutputStream(new FileOutputStream(settingsFile.toFile()))) {

            IOUtils.copy(is, os);
        } catch (IOException e) {
            LOGGER.error("Unable to install default settings file {}", settingsFile, e);
        }
    }


    @NotNull
    @Contract("_ -> new")
    private static Map<String, UISettings> loadSettings(UISettings defaultSettings) {
        // we need to preserve keys order of UISettingsManager#getAvailableSettings
        Map<String, UISettings> result = new LinkedHashMap<>();
        result.put(defaultSettings.getName(), defaultSettings);

        try (InputStream is = getSettingsFileInputStream();
             Reader reader = new BufferedReader(new InputStreamReader(is))) {

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(UISettings.class, new UISettings.UISettingSerializer());

            Gson gson = gsonBuilder.create();
            Map<String, UISettings> settingsFromFile =
                    Arrays.stream(gson.fromJson(reader, UISettings[].class))
                            .filter(it -> !defaultSettings.equals(it))
                            .collect(Collectors.toMap(UISettings::getName, it -> it));

            result.putAll(settingsFromFile);
        } catch (FileNotFoundException e) {
            LOGGER.info("Unable to load settings from settings file", e);
        } catch (JsonParseException e) {
            LOGGER.error("Malformed settings file", e);
        } catch (IOException | NullPointerException e) {
            LOGGER.error("Unable to load settings file", e);
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

    public TokenStyle getDefaultActiveStyle() {
        var activeSettings = getActiveSettings();
        return activeSettings.getDefaultFontSettings();
    }

    public Font getActiveFont() {
        var activeSettings = getActiveSettings();

        return FontLoader.load(activeSettings.getFontName(), activeSettings.getFontSize());
    }

    private UISettings getDefaultSettings() {
        return settingMap.getOrDefault(defaultSettings.getName(), defaultSettings);
    }

    private Optional<UISettings> getSettingsByName(String settingName) {
        return Optional.ofNullable(settingMap.getOrDefault(settingName, null));
    }

    public String[] getAvailableSettings() {
        List<String> availableSettingsList = new ArrayList<>(settingMap.keySet());

        return availableSettingsList.toArray(new String[0]);
    }

    public Color getActiveBackgroundColor() {
        var activeSettings = getActiveSettings();
        return activeSettings.getBackgroundColor();
    }

    public Color getActiveLineHighlightColor() {
        var activeSettings = getActiveSettings();
        return activeSettings.getLineNumberColor();
    }

    public String getIdeTitle() {
        return ideTitle;
    }
}

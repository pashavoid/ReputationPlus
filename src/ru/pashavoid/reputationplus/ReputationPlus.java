package ru.pashavoid.reputationplus;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.pashavoid.reputationplus.utils.Database;
import ru.pashavoid.reputationplus.utils.Log;
import ru.pashavoid.reputationplus.utils.Placeholders;

import java.io.File;
import java.io.IOException;

public class ReputationPlus extends JavaPlugin {

    private ReputationPlus plugin;
    private final String tag = "[Reputation+]";
    private Log log;
    private Database database;
    private Events events;

    private File settings;
    private FileConfiguration settingsConfig;

    private File language;
    private FileConfiguration langConfig;
    private String lang;

    @Override
    public void onEnable() {

        plugin = this;
        log = new Log(this, tag);

        database = new Database(this);
        database.connect();

        log.sendApproved("Successful initialization of the plugin");
        log.sendNote("Version: " + getDescription().getVersion() + " Author: " + getDescription().getAuthors());

        createSettingsFile();
        createLangFile();
        lang = settingsConfig.getString("settings.lang");

        events = new Events(this);
        getCommand("reputation").setExecutor(new Commands(this));
        getServer().getPluginManager().registerEvents(events, this);

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            new Placeholders(this).register();
            log.sendNote("Support enabled PlaceholderAPI");
        }
    }

    @Override
    public void onDisable() {
        log.sendApproved("Disabling the plugin");
        database.disconnect();
    }

    public FileConfiguration getSettings() {
        return this.settingsConfig;
    }
    public String getLang() { return this.lang; }
    public FileConfiguration getLangConfig() { return this.langConfig; }
    public Log getLog() { return this.log; }
    public Database getMysql() { return this.database; }
    public ReputationPlus getPlugin() { return this.plugin; }
    public Events getEvents(){
        return this.events;
    }

    private void createSettingsFile() {
        settings = new File(getDataFolder(), "settings.yml");
        if (!settings.exists()) {
            settings.getParentFile().mkdirs();
            saveResource("settings.yml", false);
        }

        settingsConfig = new YamlConfiguration();
        try {
            settingsConfig.load(settings);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void createLangFile() {
        language = new File(getDataFolder(), "lang.yml");
        if (!language.exists()) {
            boolean file = language.getParentFile().mkdirs();
            saveResource("lang.yml", false);
        }

        langConfig = new YamlConfiguration();
        try {
            langConfig.load(language);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}

package ru.pashavoid.reputationplus;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import ru.pashavoid.reputationplus.utils.Log;
import ru.pashavoid.reputationplus.utils.MySQL;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class ReputationPlus extends JavaPlugin {

    private ReputationPlus plugin;
    private String tag = "[Reputation+]";
    private Log log;
    private MySQL mysql;

    private File settings;
    private FileConfiguration settingsConfig;

    private File language;
    private FileConfiguration langConfig;
    private String lang;

    @Override
    public void onEnable() {

        plugin = this;
        log = new Log(this, tag);

        mysql = new MySQL(this);
        mysql.connect();

        log.sendApproved("Successful initialization of the plugin");
        log.sendNote("Version: " + getDescription().getVersion() + " Author: " + getDescription().getAuthors());

        createSettingsFile();
        createLangFile();
        lang = settingsConfig.getString("settings.lang");

        getCommand("reputation").setExecutor(new Commands(this));
        getServer().getPluginManager().registerEvents(new Events(this), this);

        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    mysql.updateCache();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                plugin.getLog().sendApproved("Successful connection to the database. The plugin cache was cleared");
            }
        }, 0L, 1200L);
    }

    @Override
    public void onDisable() {

        saveResource("database.yml", false);
        saveResource("settings.yml", false);
        saveResource("lang.yml", false);
        log.sendApproved("Save database.yml, settings.yml, lang.yml");

        try {
            mysql.updateCache();
            log.sendApproved("Successful connection to the database. The cache was moved to the database");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        log.sendApproved("Disabling the plugin");
        mysql.disconnect();
    }

    public FileConfiguration getSettings() {
        return this.settingsConfig;
    }
    public String getLang() { return this.lang; }
    public FileConfiguration getLangConfig() { return this.langConfig; }
    public Log getLog() { return this.log; }
    public MySQL getMysql() { return this.mysql; }
    public ReputationPlus getPlugin() { return this.plugin; }

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
            language.getParentFile().mkdirs();
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

package ru.pashavoid.reputationplus;

import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import ru.pashavoid.reputationplus.gui.PlayerGUI;
import ru.pashavoid.reputationplus.utils.Log;
import ru.pashavoid.reputationplus.utils.MySQL;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class ReputationPlus extends JavaPlugin implements Listener, CommandExecutor {

    public static ReputationPlus instance;

    public FileConfiguration config = getConfig();
    private File settings;
    private FileConfiguration settingsConfig;

    @Override
    public void onEnable() {
        MySQL mysql = new MySQL(this);
        instance = this;
        MySQL.connect();
        Log log = new Log(this);
        log.sendApproved("[Reputation+]", "Successful initialization of the plugin");

        getCommand("reputation").setExecutor(new Commands(this));
        getServer().getPluginManager().registerEvents(new Events(), this);

        createSettingsFile();

        log.sendNote("[Reputation+]", "Version: " + getDescription().getVersion() + " Author: " + getDescription().getAuthors());

        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                try {
                    MySQL.updateCache();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                log.sendApproved("[Reputation+]", "Successful connection to the database. The plugin cache was cleared");
                MySQL.cache.clear();
            }
        }, 0L, 1200L);
    }

    @Override
    public void onDisable() {
        Log log = new Log(this);

        log.sendApproved("[Reputation+]", "Save database.yml, settings.yml");

        saveResource("database.yml", false);
        saveResource("settings.yml", false);

        try {
            MySQL.updateCache();
            log.sendApproved("[Reputation+]", "Successful connection to the database. The cache was moved to the database");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        log.sendApproved("[Reputation+]", "Disabling the plugin");
        MySQL.disconnect();
    }

    public FileConfiguration getSettings() {
        return this.settingsConfig;
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
}

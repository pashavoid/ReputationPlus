package ru.pashavoid.reputationplus.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.pashavoid.reputationplus.ReputationPlus;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class MySQL {

    public static Connection connection;
    private static ReputationPlus plugin;
    static ConsoleCommandSender console = Bukkit.getConsoleSender();

    public MySQL (ReputationPlus instance){
        this.plugin = instance;
    }

    private static File database;
    private static FileConfiguration databaseConfig;

    public static HashMap<UUID, Integer> cache = new HashMap<>();

    public static void createDatabaseFile() {
        database = new File(plugin.getDataFolder(), "database.yml");
        if (!database.exists()) {
            database.getParentFile().mkdirs();
            plugin.saveResource("database.yml", false);
        }

        databaseConfig = new YamlConfiguration();
        try {
            databaseConfig.load(database);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getDatabase() {
        return this.databaseConfig;
    }

    public static void connect() {
        if (!isConnected()) {
            try {
                createDatabaseFile();
                connection = DriverManager.getConnection("jdbc:mysql://"
                        + databaseConfig.getString("host")
                        + ":" + databaseConfig.getString("port")
                        + "/" + databaseConfig.getString("database"), databaseConfig.getString("username"), databaseConfig.getString("password"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isConnected() {
        return (connection != null);
    }

    public static Connection getConnection() {
        return connection;
    }


    public static void updateCache() throws SQLException {

        Iterator<Map.Entry<UUID, Integer>> iterator = cache.entrySet().iterator();
        Log log = new Log(plugin);
        for(int i = 0; ; i++){
            if(iterator.hasNext()){
                Map.Entry<UUID, Integer> entry = iterator.next();
                UUID uuid = entry.getKey();
                int Reputation = entry.getValue();

                log.sendApproved("[Test]", String.valueOf(uuid));
                log.sendApproved("[Test]", String.valueOf(Reputation));
                ResultSet rs = searchPlayer(uuid).executeQuery();
                if(!rs.next()){
                    addPlayer(uuid, Reputation);
                    log.sendApproved("", "addPlayer");
                } else {
                    updatePlayer(uuid, Reputation);
                    log.sendApproved("", "updatePlayer");
                }
            } else break;
        }
        cache.clear();
    }

    private static PreparedStatement searchPlayer(UUID uuid) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM reputation WHERE uuid = ?");
        ps.setString(1, String.valueOf(uuid));
        return ps;
    }

    private static void addPlayer(UUID uuid, int reputation) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement("INSERT INTO reputation (id, uuid, rep) VALUES (NULL, ?, ?)");
        ps.setString(1, String.valueOf(uuid));
        ps.setInt(2, reputation);
        ps.executeUpdate();
    }

    private static void updatePlayer(UUID uuid, int reputation) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement("UPDATE reputation SET `rep` = ? WHERE `uuid` = ?");
        ps.setInt(1, reputation);
        ps.setString(2, String.valueOf(uuid));
        ps.executeUpdate();
    }

    public static Integer getReputation(UUID uuid) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM reputation WHERE uuid = ?");
        ps.setString(1, String.valueOf(uuid));
        ResultSet rs = ps.executeQuery();
        if(cache.get(uuid) != null){
            return cache.get(uuid);
        }
        if(rs.next()){
            return rs.getInt("rep");
        }
        return null;
    }

    public static void setReputation(UUID uuid, int rep) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM reputation WHERE uuid = ?");
        ps.setString(1, String.valueOf(uuid));
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            cache.put(uuid, rs.getInt("rep") + rep);
            return;
        }
        if (cache.get(uuid) != null) {
            cache.put(uuid, cache.get(uuid) + rep);
            return;
        } else {
            cache.put(uuid, 0);
        }
    }
}

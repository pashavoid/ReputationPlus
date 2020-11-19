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

    public MySQL(){

    }

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
                if(reputationExist()){
                    createReputationTable();
                }
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

    private static Boolean reputationExist() throws SQLException {
        DatabaseMetaData dbm = connection.getMetaData();
        ResultSet rs = dbm.getTables(null, null, "reputation", null);
        return !rs.next();
    }

    private static void createReputationTable() throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS reputation (id INT NOT NULL AUTO_INCREMENT, UUID VARCHAR(36), rep INT, PRIMARY KEY(`id`))");
        ps.executeUpdate();
    }

    private static Boolean possibleExist() throws SQLException {
        DatabaseMetaData dbm = connection.getMetaData();
        ResultSet rs = dbm.getTables(null, null, "possible", null);
        return !rs.next();
    }

    private static void createPossibleTable() throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS possible (id INT NOT NULL AUTO_INCREMENT, uuidwho VARCHAR(36), uuidwhom VARCHAR(36), thereis BOOLEAN, PRIMARY KEY(`id`))");
        ps.executeUpdate();
    }


    public static void updateCache() throws SQLException {

        Iterator<Map.Entry<UUID, Integer>> iterator = cache.entrySet().iterator();
        Log log = new Log(plugin);
        for(int i = 0; ; i++){
            if(iterator.hasNext()){
                Map.Entry<UUID, Integer> entry = iterator.next();
                UUID uuid = entry.getKey();
                int Reputation = entry.getValue();

                ResultSet rs = searchPlayer(uuid).executeQuery();
                if(!rs.next()){
                    addPlayer(uuid, Reputation);
                } else {
                    updatePlayer(uuid, Reputation);
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

    public static Boolean getDidVote(UUID uuidwho, UUID uuidwhom) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM possible WHERE uuidwho = ? AND uuidwhom = ?");
        ps.setString(1, String.valueOf(uuidwho));
        ps.setString(2, String.valueOf(uuidwhom));
        ResultSet rs = ps.executeQuery();
        if (rs.next()){
            return rs.getBoolean("thereis");
        } else {
            return null;
        }
    }

    public static void setDidVote(UUID uuidwho, UUID uuidwhom, boolean thereis) throws SQLException {
        Bukkit.getConsoleSender().sendMessage(String.valueOf(getDidVote(uuidwho, uuidwhom)));
        if(getDidVote(uuidwho, uuidwhom) == null){
            PreparedStatement ps = getConnection().prepareStatement("INSERT INTO possible (id, uuidwho, uuidwhom, thereis) VALUES (NULL, ?, ?, ?)");
            ps.setString(1, String.valueOf(uuidwho));
            ps.setString(2, String.valueOf(uuidwhom));
            ps.setBoolean(3, thereis);
            ps.executeUpdate();
        }
        PreparedStatement ps = getConnection().prepareStatement("UPDATE possible SET thereis = ? WHERE uuidwho = ? AND uuidwhom = ?");
        ps.setBoolean(1, thereis);
        ps.setString(2, String.valueOf(uuidwho));
        ps.setString(3, String.valueOf(uuidwhom));
    }
}

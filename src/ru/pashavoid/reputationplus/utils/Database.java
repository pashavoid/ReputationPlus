package ru.pashavoid.reputationplus.utils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.pashavoid.reputationplus.ReputationPlus;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;

public class Database {

    private Connection connection;
    private ReputationPlus plugin;

    public Database(ReputationPlus instance){
        this.plugin = instance;
    }

    public Database() {
        //
        // test
    }

    public void connect() {
        if (!isConnected()) {
            try {
                createDatabaseFile();
                if(databaseConfig.getString("type").equals("mysql")){
                    connection = DriverManager.getConnection("jdbc:mysql://"
                            + databaseConfig.getString("host")
                            + ":" + databaseConfig.getString("port")
                            + "/" + databaseConfig.getString("database"), databaseConfig.getString("username"), databaseConfig.getString("password"));
                }

                if(databaseConfig.getString("type").equals("sqlite")){
                    connection = getSQLConnection();
                }

                if(reputationExist()){
                    createReputationTable();
                }
                if(possibleExist()){
                    createPossibleTable();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Connection getSQLConnection() {
        File dataFolder = new File(plugin.getDataFolder(), "reputation.db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: reputation.db");
            }
        }
        try {
            if(connection != null && !connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Put it in /lib folder.");
        }
        return null;
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private File database;
    private FileConfiguration databaseConfig;

    public void createDatabaseFile() {
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

    private void createReputationTable() throws SQLException {
        if(databaseConfig.getString("type").equals("sqlite")){
            PreparedStatement ps = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS reputation (id INTEGER PRIMARY KEY AUTOINCREMENT, UUID VARCHAR(36) NOT NULL, rep INT NOT NULL)");
            ps.executeUpdate();
            return;
        }
        PreparedStatement ps = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS reputation (id INT NOT NULL AUTO_INCREMENT, UUID VARCHAR(36) NOT NULL, rep INT NOT NULL, PRIMARY KEY(`id`))");
        ps.executeUpdate();
    }

    private Boolean possibleExist() throws SQLException {
        DatabaseMetaData dbm = connection.getMetaData();
        ResultSet rs = dbm.getTables(null, null, "possible", null);
        return !rs.next();
    }

    private void createPossibleTable() throws SQLException {
        if(databaseConfig.getString("type").equals("sqlite")){
            PreparedStatement ps = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS possible (id INTEGER PRIMARY KEY AUTOINCREMENT, uuidwho VARCHAR(36) NOT NULL, uuidwhom VARCHAR(36) NOT NULL, yes TINYINT NOT NULL)");
            ps.executeUpdate();
            return;
        }
        PreparedStatement ps = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS possible (id INT NOT NULL AUTO_INCREMENT, uuidwho VARCHAR(36) NOT NULL, uuidwhom VARCHAR(36) NOT NULL, yes TINYINT NOT NULL, PRIMARY KEY(`id`))");
        ps.executeUpdate();
    }

    public FileConfiguration getDatabaseConfig() {
        return this.databaseConfig;
    }
    public boolean isConnected() {
        return (connection != null);
    }
    public Connection getConnection() {
        return connection;
    }
    private Boolean reputationExist() throws SQLException {
        DatabaseMetaData dbm = connection.getMetaData();
        ResultSet rs = dbm.getTables(null, null, "reputation", null);
        return !rs.next();
    }

    private PreparedStatement searchPlayer(UUID uuid) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM reputation WHERE uuid = ?");
        ps.setString(1, String.valueOf(uuid));
        return ps;
    }

    private void addPlayer(UUID uuid, int reputation) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement("INSERT INTO reputation (id, uuid, rep) VALUES (NULL, ?, ?)");
        ps.setString(1, String.valueOf(uuid));
        ps.setInt(2, reputation);
        ps.executeUpdate();
    }

    private void updatePlayer(UUID uuid, int reputation) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement("UPDATE reputation SET `rep` = ? WHERE `uuid` = ?");
        ps.setInt(1, reputation);
        ps.setString(2, String.valueOf(uuid));
        ps.executeUpdate();
    }

    public Integer getReputation(UUID uuid) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM reputation WHERE uuid = ?");
        ps.setString(1, String.valueOf(uuid));
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            return rs.getInt("rep");
        }
        return null;
    }

    public void setReputation(UUID uuid, int rep) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM reputation WHERE uuid = ?");
        ps.setString(1, String.valueOf(uuid));
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            PreparedStatement pst = getConnection().prepareStatement("UPDATE reputation SET rep = ? WHERE uuid = ?");
            pst.setInt(1, rs.getInt("rep") + rep);
            pst.setString(2, String.valueOf(uuid));
            pst.executeUpdate();
        } else {
            PreparedStatement pst = getConnection().prepareStatement("INSERT INTO reputation (id, uuid, rep) VALUES (NULL, ?, ?)");
            pst.setString(1, String.valueOf(uuid));
            pst.setInt(2, rep);
            pst.executeUpdate();
        }
    }

    public Short getDidVote(UUID uuidwho, UUID uuidwhom) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM possible WHERE uuidwho = ? AND uuidwhom = ?");
        ps.setString(1, String.valueOf(uuidwho));
        ps.setString(2, String.valueOf(uuidwhom));
        ResultSet rs = ps.executeQuery();
        if (rs.next()){
            return rs.getShort("yes");
        } else {
            return 2;
        }
    }

    public void setDidVote(UUID uuidwho, UUID uuidwhom, short yes) throws SQLException {
        if(getDidVote(uuidwho, uuidwhom) == 2){
            PreparedStatement ps = getConnection().prepareStatement("INSERT INTO possible (id, uuidwho, uuidwhom, yes) VALUES (NULL, ?, ?, ?)");
            ps.setString(1, String.valueOf(uuidwho));
            ps.setString(2, String.valueOf(uuidwhom));
            ps.setShort(3, yes);
            ps.executeUpdate();
        }
        PreparedStatement ps = getConnection().prepareStatement("UPDATE possible SET yes = ? WHERE uuidwho = ? AND uuidwhom = ?");
        ps.setShort(1, yes);
        ps.setString(2, String.valueOf(uuidwho));
        ps.setString(3, String.valueOf(uuidwhom));
    }
}

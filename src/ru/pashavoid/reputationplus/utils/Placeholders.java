package ru.pashavoid.reputationplus.utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import ru.pashavoid.reputationplus.ReputationPlus;

import java.sql.SQLException;

public class Placeholders extends PlaceholderExpansion {

    private ReputationPlus plugin;

    public Placeholders(ReputationPlus instance) {
        this.plugin = instance;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "repplus";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, final String identifier) {
        if (identifier.equals("playerrep")) {
            try {
                return String.valueOf(plugin.getMysql().getReputation(player.getUniqueId()));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return null;
    }
}

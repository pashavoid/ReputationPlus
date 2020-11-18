package ru.pashavoid.reputationplus.utils;

import org.bukkit.ChatColor;
import ru.pashavoid.reputationplus.ReputationPlus;

public class Log {

    private ReputationPlus plugin;

    public Log(ReputationPlus instance) {
        this.plugin = instance;
    }

    public void sendError(String prefix, String message){
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.DARK_AQUA +  prefix + " " + ChatColor.RED + message + ".");
    }

    public void sendApproved(String prefix, String message){
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.DARK_AQUA +  prefix + " " + ChatColor.GREEN + message + ".");
    }

    public void sendNote(String prefix, String message){
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.DARK_AQUA +  prefix + " " + ChatColor.GRAY + message + ".");
    }
}

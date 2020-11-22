package ru.pashavoid.reputationplus.utils;

import org.bukkit.ChatColor;
import ru.pashavoid.reputationplus.ReputationPlus;

public class Log {

    private ReputationPlus plugin;
    private String prefix;

    public Log(ReputationPlus instance, String tag) {
        plugin = instance;
        prefix = tag;
    }

    public void sendError(String message){
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.DARK_AQUA +  prefix + " " + ChatColor.RED + message + ".");
    }

    public void sendApproved(String message){
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.DARK_AQUA +  prefix + " " + ChatColor.GREEN + message + ".");
    }

    public void sendNote(String message){
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.DARK_AQUA +  prefix + " " + ChatColor.GRAY + message + ".");
    }
}

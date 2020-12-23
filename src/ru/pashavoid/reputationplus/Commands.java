package ru.pashavoid.reputationplus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import ru.pashavoid.reputationplus.gui.ScrollerGUI;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Commands implements CommandExecutor, Listener {

    private ReputationPlus plugin;
    private final List<String> lore = new ArrayList<String>();
    private ArrayList<ItemStack> items = new ArrayList<ItemStack>();
    public ScrollerGUI inv;

    public Commands(ReputationPlus instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if(label.equalsIgnoreCase("reputation")) {
                if(args.length == 0){
                    lore.clear();
                    lore.add(plugin.getLangConfig().getString(plugin.getLang() + ".lore").replace("&", "§"));
                    items.clear();

                    Bukkit.getOnlinePlayers().forEach(pl -> {
                        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
                        if (!items.contains(skull)) {
                            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
                            skullMeta.setDisplayName(pl.getDisplayName());
                            skullMeta.setLore(lore);
                            skullMeta.setOwningPlayer(pl);
                            skull.setItemMeta(skullMeta);
                            items.add(skull);
                        }
                    });
                    inv = new ScrollerGUI(items, "[Reputation+] " + plugin.getLangConfig().getString(plugin.getLang() + ".namemaingui").replace("&", "§"), player, plugin.getPlugin());
                    return true;
                }
                if(args.length == 1){
                    player.sendMessage("Usage: /reputation <like/dislike> <nickname player>");
                    return true;
                }
                if(args.length == 2){
                    UUID put = plugin.getEvents().getPlayers().get(args[1]);
                    UUID give = player.getUniqueId();
                    if(args[0].equalsIgnoreCase("like")){
                        try {
                            if(give != put){
                                if(plugin.getMysql().getDidVote(put, give) != 1){
                                    plugin.getMysql().setReputation(put, 1);
                                    plugin.getMysql().setDidVote(give, put, (short) 1);
                                    String msg = plugin.getLangConfig().getString(plugin.getLang() + ".vote").replace("&", "§");
                                    player.sendMessage(ChatColor.AQUA + "[Reputation+] " + ChatColor.GREEN + msg);
                                } else {
                                    String msg = plugin.getLangConfig().getString(plugin.getLang() + ".alreadyvote").replace("&", "§");
                                    player.sendMessage(ChatColor.AQUA + "[Reputation+] " + ChatColor.RED + msg);
                                }
                            } else {
                                String msg = plugin.getLangConfig().getString(plugin.getLang() + ".itsyou").replace("&", "§");
                                player.sendMessage(ChatColor.AQUA + "[Reputation+] " + ChatColor.RED + msg);
                            }
                        } catch (NullPointerException | SQLException ex){
                            ex.printStackTrace();
                        }
                        return true;
                    }
                    if(args[0].equalsIgnoreCase("dislike")){

                    }
                    player.sendMessage("test");
                    return true;
                }
            }
        }
        return true;
    }
}

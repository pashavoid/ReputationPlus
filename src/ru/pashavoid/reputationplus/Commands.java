package ru.pashavoid.reputationplus;

import org.bukkit.Bukkit;
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
        } else {
            if (args.length == 1 && args[0].equals("clearcache")) {
                try {
                    plugin.getMysql().updateCache();
                    plugin.getLog().sendApproved("Cache was clear");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
}

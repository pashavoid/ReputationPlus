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
import ru.pashavoid.reputationplus.utils.Log;
import ru.pashavoid.reputationplus.utils.MySQL;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Commands implements CommandExecutor, Listener {

    private ReputationPlus plugin;
    private final List<String> lore = new ArrayList<String>();
    private ArrayList<ItemStack> items = new ArrayList<ItemStack>();
    public static ScrollerInventory inv;

    public Commands(ReputationPlus instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            lore.clear();
            lore.add("Click on the player to interact with them");
            items.clear();

            for (Player pl : Bukkit.getOnlinePlayers()) {
                ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
                if(!items.contains(skull)) {
                    SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
                    skullMeta.setDisplayName(pl.getDisplayName());
                    skullMeta.setLore(lore);
                    skullMeta.setOwningPlayer(pl);
                    skull.setItemMeta(skullMeta);
                    items.add(skull);
                }
            }
            inv = new ScrollerInventory(items, "[Reputation+] Players", player);
        } else {
            if (args.length == 1 && args[0].equals("clearcache")) {
                try {
                    MySQL.updateCache();
                    Log log = new Log(plugin);
                    log.sendApproved("[Reputation+]", "Cache was clear");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
}

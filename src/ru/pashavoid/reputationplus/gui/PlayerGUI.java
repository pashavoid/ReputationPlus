package ru.pashavoid.reputationplus.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import ru.pashavoid.reputationplus.ReputationPlus;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlayerGUI {

    private final Inventory inv;
    private final ReputationPlus plugin;

    public PlayerGUI(ItemStack clickedItem, ReputationPlus instance) throws SQLException {
        this.plugin = instance;
        Inventory inv = Bukkit.createInventory(
                null, 9, "[Reputation+] " + plugin.getLangConfig().getString(plugin.getLang() + ".guiplayer").replace("&", "§"));
        List<String> lore = new ArrayList<String>();
        Player o = Bukkit.getPlayer(clickedItem.getItemMeta().getDisplayName());

        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullhead = (SkullMeta) head.getItemMeta();
        skullhead.setDisplayName(clickedItem.getItemMeta().getDisplayName());
        lore.add(plugin.getLangConfig().getString(plugin.getLang() + ".infoplayerrep").replace("&", "§") + ": " + plugin.getMysql().getReputation(o.getUniqueId()));
        skullhead.setLore(lore);
        skullhead.setOwningPlayer(o);
        head.setItemMeta(skullhead);

        ItemStack like = new ItemStack(Material.GREEN_TERRACOTTA, 1);
        ItemMeta likemeta = like.getItemMeta();
        likemeta.setDisplayName(plugin.getLangConfig().getString(plugin.getLang() + ".like").replace("&", "§"));
        like.setItemMeta(likemeta);

        ItemStack dislike = new ItemStack(Material.RED_TERRACOTTA, 1);
        ItemMeta dislikemeta = like.getItemMeta();
        dislikemeta.setDisplayName(plugin.getLangConfig().getString(plugin.getLang() + ".dislike").replace("&", "§"));
        dislike.setItemMeta(dislikemeta);

        inv.setItem(4, head);
        inv.setItem(3, like);
        inv.setItem(5, dislike);

        this.inv = inv;
    }

    public Inventory getInventory(){
        return inv;
    }
}

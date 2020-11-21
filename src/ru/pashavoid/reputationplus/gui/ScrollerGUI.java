package ru.pashavoid.reputationplus.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ScrollerGUI {

    public ArrayList<Inventory> pages = new ArrayList<Inventory>();
    public UUID id;
    public int currpage = 0;
    public static HashMap<UUID, ScrollerGUI> users = new HashMap<UUID, ScrollerGUI>();

    public ScrollerGUI(ArrayList<ItemStack> items, String name, Player p){
        this.id = UUID.randomUUID();
        Inventory page = getBlankPage(name);
        for (ItemStack item : items) {
            if (page.firstEmpty() == 46) {
                pages.add(page);
                page = getBlankPage(name);
                page.addItem(item);
            } else {

                page.addItem(item);
            }
        }
        pages.add(page);

        p.openInventory(pages.get(currpage));
        users.put(p.getUniqueId(), this);
    }

    public static final String nextPageName = ChatColor.AQUA + "Next Page";
    public static final String previousPageName = ChatColor.AQUA + "Previous Page";

    private Inventory getBlankPage(String name){
        Inventory page = Bukkit.createInventory(null, 54, name);

        ItemStack nextpage =  new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1, (byte) 5);
        ItemMeta meta = nextpage.getItemMeta();
        meta.setDisplayName(nextPageName);
        nextpage.setItemMeta(meta);

        ItemStack prevpage = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1, (byte) 2);
        meta = prevpage.getItemMeta();
        meta.setDisplayName(previousPageName);
        prevpage.setItemMeta(meta);


        page.setItem(53, nextpage);
        page.setItem(45, prevpage);
        return page;
    }
}

package ru.pashavoid.reputationplus;

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

public class ScrollerInventory {

    public ArrayList<Inventory> pages = new ArrayList<Inventory>(); // 0
    public UUID id;
    public static int currpage = 1;
    public static HashMap<UUID, ScrollerInventory> users = new HashMap<UUID, ScrollerInventory>();
    private Inventory page;

    public ScrollerInventory(ArrayList<ItemStack> items, String name, Player player){

        createZero();
        this.id = UUID.randomUUID();
        page = getBlankPage(name);

        if(currpage == 1){
            page.getItem(30).setAmount(0);
        }

        for (ItemStack item : items) {
            if (items.size() < 1 * currpage) {
                page.getItem(32).setAmount(0);
                pages.add(page);
                page = getBlankPage(name);
                page.addItem(item);
            } else {
                page.addItem(item);
            }
        }
        pages.add(page);
        player.openInventory(pages.get(currpage));
        users.put(player.getUniqueId(), this);
    }

    public static final String nextPageName = ChatColor.AQUA + "Next Page";
    public static final String previousPageName = ChatColor.AQUA + "Previous Page";

    @Deprecated
    private Inventory getBlankPage(String name){
        Inventory page = Bukkit.createInventory(null, 36, name);

        ItemStack nextpage =  new ItemStack(Material.ACACIA_BOAT, 1, (byte) 5);
        ItemMeta meta = nextpage.getItemMeta();
        meta.setDisplayName(nextPageName);
        nextpage.setItemMeta(meta);
        page.setItem(32, nextpage);

        ItemStack prevpage = new ItemStack(Material.ACACIA_BOAT, 1, (byte) 2);
        meta = prevpage.getItemMeta();
        meta.setDisplayName(previousPageName);
        prevpage.setItemMeta(meta);
        page.setItem(30, prevpage);

        return page;
    }

    private void createZero(){
        if(pages.size() <= 0){
            Inventory inv = null;
            pages.add(inv);
        }
    }
}

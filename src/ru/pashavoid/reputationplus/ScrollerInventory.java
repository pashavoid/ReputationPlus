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

    public ArrayList<Inventory> pages = new ArrayList<Inventory>();
    public UUID id;
    public int currpage = 0;
    public static HashMap<UUID, ScrollerInventory> users = new HashMap<UUID, ScrollerInventory>();
    private Inventory page;

    public ScrollerInventory(ArrayList<ItemStack> items, String name, Player player){

        this.id = UUID.randomUUID();
        page = getBlankPage(name);
        if(!(page.firstEmpty() == 27)){
            page.getItem(32).setAmount(0);
        }
        if(currpage == 0){
            page.getItem(30).setAmount(0);
        }

        for(int i = 0; i < items.size(); i++){
            if(page.firstEmpty() == 27){
                pages.add(page);
                page = getBlankPage(name);
                page.addItem(items.get(i));
            } else{
                page.addItem(items.get(i));
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

        ItemStack prevpage = new ItemStack(Material.ACACIA_BOAT, 1, (byte) 2);
        meta = prevpage.getItemMeta();
        meta.setDisplayName(previousPageName);
        prevpage.setItemMeta(meta);

        page.setItem(30, prevpage);
        page.setItem(32, nextpage);
        return page;
    }
}

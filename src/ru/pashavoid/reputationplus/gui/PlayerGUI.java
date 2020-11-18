package ru.pashavoid.reputationplus.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import ru.pashavoid.reputationplus.utils.MySQL;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlayerGUI {

    private final List<String> lore = new ArrayList<String>();
    private ArrayList<ItemStack> items = new ArrayList<ItemStack>();
    public Inventory inv;

    public PlayerGUI (Player player, ItemStack clickedItem) throws SQLException {

        Inventory inv = Bukkit.createInventory(null, 9, "[Reputation+] Interact player");
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullhead = (SkullMeta) head.getItemMeta();
        skullhead.setDisplayName(clickedItem.getItemMeta().getDisplayName());
        Player o = Bukkit.getPlayer(clickedItem.getItemMeta().getDisplayName());
        List<String> lore = new ArrayList<>();
        lore.add("Player Reputation: " + MySQL.getReputation(o.getUniqueId()));
        skullhead.setLore(lore);
        skullhead.setOwningPlayer(o);
        head.setItemMeta(skullhead);
        inv.addItem(head);

        this.inv = inv;
    }

    public Inventory getInventory(){
        return inv;
    }
}

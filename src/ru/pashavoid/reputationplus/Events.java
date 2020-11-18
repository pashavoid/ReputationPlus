package ru.pashavoid.reputationplus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import ru.pashavoid.reputationplus.gui.PlayerGUI;
import ru.pashavoid.reputationplus.utils.MySQL;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class Events implements Listener {

    public PlayerGUI playerGUI;
    private HashMap<String, UUID> players = new HashMap<String, UUID>();

    @EventHandler
    public void PlayerJoin(PlayerJoinEvent e) throws SQLException {
        UUID uuid = e.getPlayer().getUniqueId();
        String name = e.getPlayer().getDisplayName();
        MySQL.setReputation(uuid, 0);
        players.put(name, uuid);
    }

    @EventHandler
    public void PlayerLeave(PlayerQuitEvent e){
        players.remove(e.getPlayer().getDisplayName());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) throws SQLException {
        if(e.getView().getTitle().equals("[Reputation+] Players")){
            e.setCancelled(true);
            final Player p = (Player) e.getWhoClicked();
            final ItemStack clickedItem = e.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
            playerGUI = new PlayerGUI(p, clickedItem);
            p.openInventory(playerGUI.getInventory());
        }
        if(e.getView().getTitle().equals("[Reputation+] Interact player")){
            String name = e.getCurrentItem().getItemMeta().getDisplayName();
            UUID uuid = players.get(name);

            if(e.getSlot() == 6){
                MySQL.setReputation(uuid, 1);
            }
            if(e.getSlot() == 4){
                MySQL.setReputation(uuid, -1);
            }
            e.setCancelled(true);
        }
    }
}

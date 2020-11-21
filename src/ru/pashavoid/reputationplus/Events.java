package ru.pashavoid.reputationplus;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.pashavoid.reputationplus.gui.PlayerGUI;
import ru.pashavoid.reputationplus.utils.MySQL;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class Events implements Listener {

    public PlayerGUI playerGUI;
    private HashMap<String, UUID> players = new HashMap<String, UUID>();
    private ReputationPlus plugin;

    public Events(ReputationPlus plugin){
        this.plugin = plugin;
    }

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
        if(e.getView().getTitle().equals("[Reputation+] Players")) {
            e.setCancelled(true);
            final Player p = (Player) e.getWhoClicked();
            final ItemStack clickedItem = e.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
            if(e.getCurrentItem().getType().equals(Material.ACACIA_BOAT)){
                ScrollerInventory inv = ScrollerInventory.users.get(p.getUniqueId());
                return;
            }
            playerGUI = new PlayerGUI(p, clickedItem);
            p.openInventory(playerGUI.getInventory());
        }

        if(e.getView().getTitle().equals("[Reputation+] Interact player")){

            String name = e.getView().getItem(5).getItemMeta().getDisplayName();
            UUID uuidwhom = players.get(name);
            UUID uuidwho = e.getWhoClicked().getUniqueId();
            if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)) return;
            short yes = MySQL.getDidVote(uuidwho, uuidwhom);
            Material clickeditem = e.getCurrentItem().getType();

            if(clickeditem.equals(Material.GREEN_TERRACOTTA) || clickeditem.equals(Material.RED_TERRACOTTA) && uuidwho == uuidwhom){
                String itsyou = "You can't vote for yourself.";
                e.getWhoClicked().sendMessage(ChatColor.DARK_AQUA + "[Reputation+] " + ChatColor.RED + itsyou);
                e.setCancelled(true);
                return;
            }
            if(clickeditem.equals(Material.GREEN_TERRACOTTA)){
                if (yes == 1) {
                    String msg = "You have already voted for this player.";
                    e.getWhoClicked().sendMessage(ChatColor.DARK_AQUA + "[Reputation+] " + ChatColor.RED + msg);
                } else {
                    MySQL.setReputation(uuidwhom, 1);
                    MySQL.setDidVote(uuidwho, uuidwhom, (short) 1);
                }
            }
            if(clickeditem.equals(Material.RED_TERRACOTTA)){
                if (yes == 1) {
                    String msg = "You have already voted for this player.";
                    e.getWhoClicked().sendMessage(ChatColor.DARK_AQUA + "[Reputation+] " + ChatColor.RED + msg);
                } else {
                    MySQL.setReputation(uuidwhom, 1);
                    MySQL.setDidVote(uuidwho, uuidwhom, (short) 1);
                }
            }
            e.setCancelled(true);
        }
    }
}

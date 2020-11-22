package ru.pashavoid.reputationplus;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import ru.pashavoid.reputationplus.gui.PlayerGUI;
import ru.pashavoid.reputationplus.gui.ScrollerGUI;

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
        plugin.getMysql().setReputation(uuid, 0);
        players.put(name, uuid);
    }

    @EventHandler
    public void PlayerLeave(PlayerQuitEvent e){
        players.remove(e.getPlayer().getDisplayName());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) throws SQLException {
        if(e.getView().getTitle().equals("[Reputation+] " + plugin.getLangConfig().getString(plugin.getLang() + ".namemaingui").replace("&", "§"))) {
            if(!(e.getWhoClicked() instanceof Player)) return;
            Player p = (Player) e.getWhoClicked();
            if(!ScrollerGUI.users.containsKey(p.getUniqueId())) return;
            ScrollerGUI inv = ScrollerGUI.users.get(p.getUniqueId());
            final ItemStack clickedItem = e.getCurrentItem();
            if(clickedItem == null) return;
            if(clickedItem.getItemMeta() == null || clickedItem.getType() == Material.AIR) return;
            if(clickedItem.getItemMeta().getDisplayName() == null) return;
            if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.AQUA + plugin.getLangConfig().getString(plugin.getLang() + ".nextpage"))){
                e.setCancelled(true);
                if(inv.currpage >= inv.pages.size()-1){
                    return;
                } else {
                    inv.currpage += 1;
                    p.openInventory(inv.pages.get(inv.currpage));
                }
            } else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.AQUA + plugin.getLangConfig().getString(plugin.getLang() + ".prevpage"))){
                e.setCancelled(true);
                if(inv.currpage > 0){
                    inv.currpage -= 1;
                    p.openInventory(inv.pages.get(inv.currpage));
                }
            }
            if(e.getCurrentItem().getType().equals(Material.PLAYER_HEAD)) {
                e.setCancelled(true);
                playerGUI = new PlayerGUI(p, clickedItem, plugin);
                p.openInventory(playerGUI.getInventory());
            }
        }
        if(e.getView().getTitle().equals("[Reputation+] " + plugin.getLangConfig().getString(plugin.getLang() + ".guiplayer").replace("&", "§"))){

            String name = e.getView().getItem(5).getItemMeta().getDisplayName();
            UUID uuidwhom = players.get(name);
            UUID uuidwho = e.getWhoClicked().getUniqueId();
            if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)) return;
            short yes = plugin.getMysql().getDidVote(uuidwho, uuidwhom);
            Material clickeditem = e.getCurrentItem().getType();

            if(clickeditem.equals(Material.GREEN_TERRACOTTA) || clickeditem.equals(Material.RED_TERRACOTTA) && uuidwho == uuidwhom){
                String itsyou = plugin.getLangConfig().getString(plugin.getLang() + ".itsyou").replace("&", "§");
                e.getWhoClicked().sendMessage(ChatColor.AQUA + "[Reputation+] " + ChatColor.RED + itsyou);
                e.setCancelled(true);
                return;
            }
            if(clickeditem.equals(Material.GREEN_TERRACOTTA)){
                if (yes == 1) {
                    String msg = plugin.getLangConfig().getString(plugin.getLang() + ".alreadyvote").replace("&", "§");
                    e.getWhoClicked().sendMessage(ChatColor.AQUA + "[Reputation+] " + ChatColor.RED + msg);
                } else {
                    plugin.getMysql().setReputation(uuidwhom, 1);
                    plugin.getMysql().setDidVote(uuidwho, uuidwhom, (short) 1);
                }
            }
            if(clickeditem.equals(Material.RED_TERRACOTTA)){
                if (yes == 1) {
                    String msg = plugin.getLangConfig().getString(plugin.getLang() + ".alreadyvote").replace("&", "§");
                    e.getWhoClicked().sendMessage(ChatColor.AQUA + "[Reputation+] " + ChatColor.RED + msg);
                } else {
                    plugin.getMysql().setReputation(uuidwhom, 1);
                    plugin.getMysql().setDidVote(uuidwho, uuidwhom, (short) 1);
                }
            }
            e.setCancelled(true);
        }
    }
}

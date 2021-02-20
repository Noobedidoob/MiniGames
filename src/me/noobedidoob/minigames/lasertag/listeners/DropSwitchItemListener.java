package me.noobedidoob.minigames.lasertag.listeners;

import me.noobedidoob.minigames.Minigames;
import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.methods.PlayerZoomer;
import me.noobedidoob.minigames.lasertag.session.Session;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

public class DropSwitchItemListener implements Listener {
	
	public DropSwitchItemListener(Minigames minigames) {
		PluginManager pluginManeger = Bukkit.getPluginManager();
		pluginManeger.registerEvents(this, minigames);
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e){
		if(!Lasertag.isPlayerTesting(e.getPlayer()) && Session.getPlayerSession(e.getPlayer()) == null) return;
		ItemStack item = e.getItemDrop().getItemStack();
		if(item.getItemMeta().getDisplayName().toUpperCase().contains("LASERGUN") | item.getItemMeta().getDisplayName().toUpperCase().contains("SNIPER")) {
			PlayerZoomer.toggleZoom(e.getPlayer());
		}
	}
	
	@EventHandler
	public void onPlayerSwapItem(PlayerSwapHandItemsEvent e) {
		e.setCancelled(true);
		Player p = e.getPlayer();
		Session session = Session.getPlayerSession(p);
		ItemStack item = p.getInventory().getItem(p.getInventory().getHeldItemSlot());
		if(item != null) {
			if(Lasertag.isPlayerTesting(p) | (session != null && session.tagging())) {
				if(item.getItemMeta().getDisplayName().toUpperCase().contains("LASERGUN") | item.getItemMeta().getDisplayName().toUpperCase().contains("SNIPER")) {
					PlayerZoomer.toggleZoom(p);
				}
			}
		}
	}
	
	@EventHandler
	public void onSwitchItem(PlayerItemHeldEvent e) {
		PlayerZoomer.zoomPlayerOut(e.getPlayer());
	}
}

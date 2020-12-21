package me.noobedidoob.minigames.lasertag.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.methods.Game;
import me.noobedidoob.minigames.lasertag.methods.Modifiers;
import me.noobedidoob.minigames.lasertag.methods.PlayerZoomer;
import me.noobedidoob.minigames.main.Minigames;

public class DropSwitchItemListener implements Listener {
	
	public DropSwitchItemListener(Minigames minigames) {
		PluginManager pluginManeger = Bukkit.getPluginManager();
		pluginManeger.registerEvents(this, minigames);
	}
	
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		ItemStack item = e.getItemDrop().getItemStack();
		if (Lasertag.playerTesting.get(p) == null) Lasertag.playerTesting.put(p, false);
		if(((Game.tagging() | Game.waiting()) && Game.isInGame(p)) | Lasertag.playerTesting.get(p)) {
			if (Game.isInGame(p)) {
				if (!Modifiers.multiWeapons) PlayerZoomer.toggleZoom(p);
			} else if(item != null) {
				if(item.getItemMeta().getDisplayName().toUpperCase().contains("LASERGUN")) {
					PlayerZoomer.toggleZoom(p);
				}
			} 
			e.setCancelled(true);
			p.getInventory().setItem(4, new ItemStack(Material.AIR));
		} 
	}
	
	@EventHandler
	public void onPlayerSwapItem(PlayerSwapHandItemsEvent e) {
		Player p = e.getPlayer();
		@SuppressWarnings("deprecation")
		ItemStack item = p.getItemInHand();
		if (Lasertag.playerTesting.get(p) == null) Lasertag.playerTesting.put(p, false);
		if(((Game.tagging() | Game.waiting()) && Game.isInGame(p)) | Lasertag.playerTesting.get(p)) {
			if (Game.isInGame(p)) {
				if (!Modifiers.multiWeapons) PlayerZoomer.toggleZoom(p);
				else if(item != null && item.getItemMeta().getDisplayName().toUpperCase().contains("SNIPER")) PlayerZoomer.toggleZoom(p); 
			} else if(item != null && (item.getItemMeta().getDisplayName().toUpperCase().contains("SNIPER") | item.getItemMeta().getDisplayName().toUpperCase().contains("LASERGUN"))) {
				PlayerZoomer.toggleZoom(p);
			}
			e.setCancelled(true);
		} 
	}
	
	@EventHandler
	public void onSwitchItem(PlayerItemHeldEvent e) {
		PlayerZoomer.zoomPlayerOut(e.getPlayer());
	}
}

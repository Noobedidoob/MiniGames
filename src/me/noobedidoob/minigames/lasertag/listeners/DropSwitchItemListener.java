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
import org.bukkit.scheduler.BukkitRunnable;

import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.methods.PlayerZoomer;
import me.noobedidoob.minigames.lasertag.session.Session;
import me.noobedidoob.minigames.lasertag.session.SessionModifiers.Mod;
import me.noobedidoob.minigames.main.Minigames;

public class DropSwitchItemListener implements Listener {
	
	public DropSwitchItemListener(Minigames minigames) {
		PluginManager pluginManeger = Bukkit.getPluginManager();
		pluginManeger.registerEvents(this, minigames);
	}
	
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		e.setCancelled(true);
		Player p = e.getPlayer();
		Session session = Session.getPlayerSession(p);
		ItemStack item = e.getItemDrop().getItemStack();
		if(item != null) {
			if(session != null && session.tagging()) {
				if(item.getItemMeta().getDisplayName().toUpperCase().contains("LASERGUN") | item.getItemMeta().getDisplayName().toUpperCase().contains("SNIPER")) {
					PlayerZoomer.toggleZoom(p);
					if(item.getItemMeta().getDisplayName().toUpperCase().contains("SNIPER")) {
						new BukkitRunnable() {
							@Override
							public void run() {
								for(int i = 3; i < 35; i++) p.getInventory().setItem(i, new ItemStack(Material.AIR));
								p.getInventory().getItem(2).setAmount(p.getInventory().getItem(2).getAmount()+1);
								if(p.getInventory().getItem(2).getAmount() > session.getIntMod(Mod.SNIPER_AMMO_BEFORE_COOLDOWN)) p.getInventory().getItem(2).setAmount(session.getIntMod(Mod.SNIPER_AMMO_BEFORE_COOLDOWN));
							}
						}.runTaskLaterAsynchronously(Minigames.minigames, 5);
					}
				}
			} else if(Lasertag.playerTesting.get(p) != null && Lasertag.playerTesting.get(p)) {
				if(item.getItemMeta().getDisplayName().toUpperCase().contains("LASERGUN") | item.getItemMeta().getDisplayName().toUpperCase().contains("SNIPER")) {
					PlayerZoomer.toggleZoom(p);
					if(item.getItemMeta().getDisplayName().toUpperCase().contains("SNIPER")) {
						new BukkitRunnable() {
							@Override
							public void run() {
								for(int i = 3; i < 36; i++) p.getInventory().setItem(i, new ItemStack(Material.AIR));
								p.getInventory().getItem(2).setAmount(p.getInventory().getItem(2).getAmount()+1);
								if(p.getInventory().getItem(2).getAmount() > Mod.SNIPER_AMMO_BEFORE_COOLDOWN.getOgInt()) p.getInventory().getItem(2).setAmount(Mod.SNIPER_AMMO_BEFORE_COOLDOWN.getOgInt());
							}
						}.runTaskLaterAsynchronously(Minigames.minigames, 5);
					}
				}
			} 
		}
		
	} 
			
		
		
	
	@EventHandler
	public void onPlayerSwapItem(PlayerSwapHandItemsEvent e) {
		e.setCancelled(true);
		Player p = e.getPlayer();
		Session session = Session.getPlayerSession(p);
		@SuppressWarnings("deprecation")
		ItemStack item = p.getItemInHand();
		if(item != null) {
			if(session != null && session.tagging()) {
				if(item.getItemMeta().getDisplayName().toUpperCase().contains("LASERGUN") | item.getItemMeta().getDisplayName().toUpperCase().contains("SNIPER")) {
					PlayerZoomer.toggleZoom(p);
				}
			} else if(Lasertag.playerTesting.get(p) != null && Lasertag.playerTesting.get(p)) {
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

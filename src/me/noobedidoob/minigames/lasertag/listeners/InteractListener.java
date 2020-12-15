package me.noobedidoob.minigames.lasertag.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginManager;

import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.methods.Game;
import me.noobedidoob.minigames.lasertag.methods.LaserShooter;
import me.noobedidoob.minigames.lasertag.methods.PlayerZoomer;
import me.noobedidoob.minigames.lasertag.methods.Weapons;
import me.noobedidoob.minigames.main.Minigames;

public class InteractListener implements Listener {
	
	public InteractListener(Minigames minigames) {
		PluginManager pluginManeger = Bukkit.getPluginManager();
		pluginManeger.registerEvents(this, minigames);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p  = e.getPlayer();
		if(Game.testing()) {
			if(e.getAction().name().toUpperCase().contains("RIGHT") && e.getItem() != null) {
				if(e.getItem().getItemMeta().getDisplayName().contains("TEST")) {
					LaserShooter.fireTest(p);
				}
			}
		} 
		if(Game.tagging()) {
			Lasertag.isProtected.put(p, false);
			if(e.getAction() == Action.RIGHT_CLICK_AIR | e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if(e.getItem() == null) return;
				try {
					LaserShooter.fire(p, Weapons.getFireWeapon(e.getItem()));
				} catch (Exception e2) {
					return;
				}
//				if(e.getItem().getItemMeta().getDisplayName().toUpperCase().contains("LASERGUN")) {
//					LaserShooter.fire(p, Weapon.LASERGUN);
//					e.setCancelled(true);
//				} else if(e.getItem().getItemMeta().getDisplayName().toUpperCase().contains("MINIGUN")) {
//					LaserShooter.fire(p, Weapon.MINIGUN);
//					e.setCancelled(true);
//				} else if(e.getItem().getItemMeta().getDisplayName().toUpperCase().contains("SNIPER")) {
//					LaserShooter.fire(p, Weapon.SNIPER);
//					e.setCancelled(true);
//				} else if(e.getItem().getItemMeta().getDisplayName().toUpperCase().contains("SHOTGUN")) {
//					LaserShooter.fire(p, Weapon.SHOTGUN);
//					e.setCancelled(true);
//				}
			} else if(e.getAction() == Action.LEFT_CLICK_AIR | e.getAction() == Action.LEFT_CLICK_BLOCK) {
				if(e.getItem() == null) return;
				if(e.getItem().getItemMeta().getDisplayName().toUpperCase().contains("SNIPER")/* | e.getItem().getItemMeta().getDisplayName().toUpperCase().contains("LASERGUN")*/) {
					PlayerZoomer.toggleZoom(p);
				}
			}
		}
	}
}

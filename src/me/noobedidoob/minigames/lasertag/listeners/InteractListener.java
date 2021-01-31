package me.noobedidoob.minigames.lasertag.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginManager;

import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.methods.LaserShooter;
import me.noobedidoob.minigames.lasertag.methods.PlayerZoomer;
import me.noobedidoob.minigames.lasertag.methods.Weapons.Weapon;
import me.noobedidoob.minigames.lasertag.session.Session;
import me.noobedidoob.minigames.main.Minigames;

public class InteractListener implements Listener {
	
	public InteractListener(Minigames minigames) {
		PluginManager pluginManeger = Bukkit.getPluginManager();
		pluginManeger.registerEvents(this, minigames);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p  = e.getPlayer();
		if (Lasertag.playerTesting.get(p) == null) Lasertag.playerTesting.put(p, false);
		if(Lasertag.playerTesting.get(p)) {
			if(e.getAction() == Action.RIGHT_CLICK_AIR | e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if(e.getItem() == null) return;
				try {
					LaserShooter.fireTest(p, Weapon.getWeaponFromItem(e.getItem()));
				} catch (Exception e2) {
					return;
				}
			} else if(e.getAction() == Action.LEFT_CLICK_AIR | e.getAction() == Action.LEFT_CLICK_BLOCK) {
				if(e.getItem() == null) return;
				if(e.getItem().getItemMeta().getDisplayName().toUpperCase().contains("SNIPER")/* | e.getItem().getItemMeta().getDisplayName().toUpperCase().contains("LASERGUN")*/) {
					PlayerZoomer.toggleZoom(p);
					return;
				}
			}
		} 

		Session session = Session.getPlayerSession(p);
		if(session == null) return;
		if(session.tagging()) {
			Lasertag.isProtected.put(p, false);
			if(e.getAction() == Action.RIGHT_CLICK_AIR | e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if(e.getItem() == null) return;
				try {
					LaserShooter.fire(p, Weapon.getWeaponFromItem(e.getItem()));
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
	
	@EventHandler
	public void playerInteractAtEntity(PlayerInteractEntityEvent e) {
		Entity entity = e.getRightClicked();
		if(entity instanceof ItemFrame) {
			e.setCancelled(true);
		}
	}
}

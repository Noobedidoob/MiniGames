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
import me.noobedidoob.minigames.lasertag.methods.LaserShooter;
import me.noobedidoob.minigames.lasertag.methods.PlayerZoomer;
import me.noobedidoob.minigames.lasertag.methods.Weapons.Weapon;
import me.noobedidoob.minigames.lasertag.session.Session;
import me.noobedidoob.minigames.Minigames;

public class InteractListener implements Listener {
	
	public InteractListener(Minigames minigames) {
		PluginManager pluginManeger = Bukkit.getPluginManager();
		pluginManeger.registerEvents(this, minigames);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p  = e.getPlayer();
		if(Lasertag.isPlayerTesting(p)) {
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
			Lasertag.setPlayerTesting(p, false);
			if(e.getAction() == Action.RIGHT_CLICK_AIR | e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if(e.getItem() == null) return;
				try {
					LaserShooter.fire(p, Weapon.getWeaponFromItem(e.getItem()));
				} catch (Exception e2) {
					return;
				}
			} else if(e.getAction() == Action.LEFT_CLICK_AIR | e.getAction() == Action.LEFT_CLICK_BLOCK) {
				if(e.getItem() == null) return;
				if(e.getItem().getItemMeta().getDisplayName().toUpperCase().contains("SNIPER") | (session.withMultiweapons() && e.getItem().getItemMeta().getDisplayName().toUpperCase().contains("LASERGUN"))) {
					PlayerZoomer.toggleZoom(p);
				}
			}
		}
		
	}
}

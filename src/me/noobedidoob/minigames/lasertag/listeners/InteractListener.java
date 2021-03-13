package me.noobedidoob.minigames.lasertag.listeners;

import me.noobedidoob.minigames.Minigames;
import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.methods.LaserShooter;
import me.noobedidoob.minigames.lasertag.methods.PlayerZoomer;
import me.noobedidoob.minigames.lasertag.methods.Weapon;
import me.noobedidoob.minigames.lasertag.session.Session;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginManager;

public class InteractListener implements Listener {
	
	public InteractListener(Minigames minigames) {
		PluginManager pluginManeger = Bukkit.getPluginManager();
		pluginManeger.registerEvents(this, minigames);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p  = e.getPlayer();
		if(Lasertag.isPlayerTesting(p)) {
			if(e.getAction() == Action.RIGHT_CLICK_AIR | e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if(e.getItem() == null) return;
				if(Weapon.getWeaponFromItem(e.getItem())!= null)  LaserShooter.fireTest(p, Weapon.getWeaponFromItem(e.getItem()));
			} else if(e.getAction() == Action.LEFT_CLICK_AIR | e.getAction() == Action.LEFT_CLICK_BLOCK) {
				if(e.getItem() == null) return;
				if(e.getItem().getItemMeta().getDisplayName().toUpperCase().contains("SNIPER")) {
					PlayerZoomer.toggleZoom(p);
					return;
				} else if(Weapon.GRENADE.isWeapon(e.getItem())){
					int amount = p.getInventory().getItemInMainHand().getAmount();
					if(amount < 5) p.getInventory().getItemInMainHand().setAmount(amount+1);
					else p.getInventory().getItemInMainHand().setAmount(1);
				}
			}
		} 

		Session session = Session.getPlayerSession(p);
		if(session == null) return;
		if(session.tagging()) {
			Lasertag.setPlayerTesting(p, false);
			if(e.getAction() == Action.RIGHT_CLICK_AIR | e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if(e.getItem() == null) return;
				if(Weapon.getWeaponFromItem(e.getItem()) != null) LaserShooter.fire(p, Weapon.getWeaponFromItem(e.getItem()));
			} else if(e.getAction() == Action.LEFT_CLICK_AIR | e.getAction() == Action.LEFT_CLICK_BLOCK) {
				if(e.getItem() == null) return;
				if(e.getItem().getItemMeta().getDisplayName().toUpperCase().contains("SNIPER")) {
					PlayerZoomer.toggleZoom(p);
				} else if(Weapon.GRENADE.isWeapon(e.getItem())){
					int amount = p.getInventory().getItemInMainHand().getAmount();
					if(amount < 5) p.getInventory().getItemInMainHand().setAmount(amount+1);
					else p.getInventory().getItemInMainHand().setAmount(1);
				}
			}
		}

	}
}

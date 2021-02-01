package me.noobedidoob.minigames.lasertag.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.PluginManager;

import me.noobedidoob.minigames.lasertag.session.Session;
import me.noobedidoob.minigames.main.Minigames;

public class DamageListener implements Listener {
	
	public DamageListener(Minigames minigames) {
		PluginManager pluginManeger = Bukkit.getPluginManager();
		pluginManeger.registerEvents(this, minigames);
	}
	
	
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e) {
		if(e.getEntity() instanceof Player) {
			DamageCause dc = e.getCause();
			if(Session.getPlayerSession((Player) e.getEntity()) != null && Session.getPlayerSession((Player) e.getEntity()).tagging()) {
				if(dc == DamageCause.FALL) e.setCancelled(true);
			} else {
				e.setCancelled(true);
			}
		}
	}
}

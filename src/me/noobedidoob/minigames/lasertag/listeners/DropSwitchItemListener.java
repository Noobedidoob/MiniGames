package me.noobedidoob.minigames.lasertag.listeners;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.plugin.PluginManager;

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
		List<Player> players;
		if(Game.tagging() | Game.waiting()) {
			players = Arrays.asList(Game.players());
			if(players.contains(p)) {
				if(!Modifiers.multiWeapons) { 
					e.setCancelled(true);
					PlayerZoomer.toggleZoom(p);
				}
			}
		} 
	}
	
	@EventHandler
	public void onPlayerSwapItem(PlayerSwapHandItemsEvent e) {
		Player p = e.getPlayer();
		List<Player> players;
		if(Game.tagging() | Game.waiting()) {
			players = Arrays.asList(Game.players());
			if(players.contains(p)) {
				if(!Modifiers.multiWeapons) { 
					PlayerZoomer.toggleZoom(p);
					e.setCancelled(true);
				}
			}
		} 
	}
	@EventHandler
	public void onPlayerSwitchItemEvent(PlayerItemHeldEvent e) {
		Player p = e.getPlayer();
		if(Game.tagging() | Game.waiting()) {
			if(Game.isInGame(p)) {
				if(Modifiers.multiWeapons) {
					PlayerZoomer.zoomPlayerOut(p);
				}
			}
		}
	}
}

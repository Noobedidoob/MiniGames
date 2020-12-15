package me.noobedidoob.minigames.lasertag.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.PluginManager;

import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.methods.Game;
import me.noobedidoob.minigames.lasertag.methods.PlayerTeleporter;
import me.noobedidoob.minigames.main.Minigames;

public class RespawnListener implements Listener {
	
	public RespawnListener(Minigames minigames) {
		PluginManager pluginManeger = Bukkit.getPluginManager();
		pluginManeger.registerEvents(this, minigames);
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		if(Game.tagging()) {
			for(Player p : Game.players()) {
				if(p == e.getPlayer()) {
					e.setRespawnLocation(PlayerTeleporter.getPlayerSpawnLoc(p));
					Lasertag.isProtected.put(p, true);
				}
			}
		}
	}
}

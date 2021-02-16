package me.noobedidoob.minigames.lasertag.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.methods.PlayerTeleporter;
import me.noobedidoob.minigames.lasertag.session.Session;
import me.noobedidoob.minigames.Minigames;

public class RespawnListener implements Listener {
	
	public RespawnListener(Minigames minigames) {
		Bukkit.getPluginManager().registerEvents(this, minigames);
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		Session session = Session.getPlayerSession(e.getPlayer());
		if(session == null) return;
		if(session.tagging()) {
			for(Player p : session.getPlayers()) {
				if(p == e.getPlayer()) {
					Location respawnLoc = PlayerTeleporter.getPlayerSpawnLoc(p);
					p.setBedSpawnLocation(respawnLoc, true);
					e.setRespawnLocation(respawnLoc);
					Lasertag.setPlayerProtected(p, true);
				}
			}
		}
	}
}

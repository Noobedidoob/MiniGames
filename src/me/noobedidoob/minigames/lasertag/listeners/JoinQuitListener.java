package me.noobedidoob.minigames.lasertag.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;

import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.methods.PlayerZoomer;
import me.noobedidoob.minigames.lasertag.methods.Weapons;
import me.noobedidoob.minigames.lasertag.session.Session;
import me.noobedidoob.minigames.main.Minigames;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

@SuppressWarnings("unused")
public class JoinQuitListener implements Listener {
	
	public JoinQuitListener(Minigames minigames) {
		PluginManager pluginManeger = Bukkit.getPluginManager();
		pluginManeger.registerEvents(this, minigames);
	}
	
	
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		Session session = Session.getPlayerSession(p);
		if(session != null) {
			session.disconnectPlayer(p);
			e.setQuitMessage("");
			for(Player op : Bukkit.getOnlinePlayers()) {
				if(!session.isInSession(op)) {
					op.sendMessage("§e"+p.getName()+" left");
				}
			}
		}
		try {PlayerZoomer.zoomPlayerOut(p);} catch (Exception e2) {}
		
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		p.setGameMode(GameMode.ADVENTURE);
		p.setExp(1f);
		p.setLevel(0);
		p.setAllowFlight(true);
		Lasertag.setPlayersLobbyInv(p);
		
		
		for(Session session : Session.getAllSessions()) {
			if(session.disconnectedPlayers.get(p.getUniqueId()) != null) {
				session.reconnectPlayer(p);
				e.setJoinMessage("");
				for(Player op : Bukkit.getOnlinePlayers()) {
					if(!session.isInSession(op)) {
						op.sendMessage("§e"+p.getName()+" joined");
					}
				}
				return;
			}
		}

		p.teleport(Minigames.spawn);
		
	}
}
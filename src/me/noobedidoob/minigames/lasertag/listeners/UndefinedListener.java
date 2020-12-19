package me.noobedidoob.minigames.lasertag.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.PluginManager;

import me.noobedidoob.minigames.main.Minigames;

public class UndefinedListener implements Listener {
	
	public UndefinedListener(Minigames minigames) {
		PluginManager pluginManeger = Bukkit.getPluginManager();
		pluginManeger.registerEvents(this, minigames);
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		e.setMessage(ChatColor.translateAlternateColorCodes('&', e.getMessage()));
		e.setFormat("<§d%s§f>: %s");
	}
	
	
	@EventHandler
	public void onListPing(ServerListPingEvent e) {
		e.setMaxPlayers(Bukkit.getOnlinePlayers().size()+1);
	}
	
	@EventHandler
	public void onPlayerSwitchGameMode(PlayerGameModeChangeEvent e) {
		if(e.getNewGameMode().equals(GameMode.ADVENTURE)) e.getPlayer().setAllowFlight(true);
		else if(e.getNewGameMode().equals(GameMode.SURVIVAL)) e.getPlayer().setAllowFlight(false);
	}
	
	@EventHandler
	public void onPlayerChangeGameMode(PlayerGameModeChangeEvent e) {
		if(e.getNewGameMode() == GameMode.ADVENTURE) e.getPlayer().setAllowFlight(true);
	}
	
}

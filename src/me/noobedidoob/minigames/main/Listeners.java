package me.noobedidoob.minigames.main;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listeners implements Listener{
	
	@SuppressWarnings("unused")
	private Minigames m;
	public Listeners(Minigames minigames) {
		this.m = minigames;
		Bukkit.getPluginManager().registerEvents(this, minigames);
	}
	
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		p.getInventory().clear();
	}
	
	

}

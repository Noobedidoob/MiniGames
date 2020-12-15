package me.noobedidoob.minigames.lasertag.listeners;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.PluginManager;

import me.noobedidoob.minigames.main.Minigames;
import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.methods.Game;
import me.noobedidoob.minigames.lasertag.methods.Modifiers;

public class DeathListener implements Listener {
	
	public DeathListener(Minigames minigames) {
		PluginManager pluginManeger = Bukkit.getPluginManager();
		pluginManeger.registerEvents(this, minigames);
	}
	
	public enum KillType{
		SHOT,
		PVP,
	}
	public static HashMap<Player, String> deathMessage = new HashMap<Player, String>();
	private static HashMap<Player, Boolean> hideNextDM = new HashMap<Player, Boolean>();
	public static void hit(KillType type, Player killer, Player victim, double damage, boolean headshot, boolean snipe, boolean backstab) {
		
		if(damage < victim.getHealth()) {
			victim.damage(damage);
		} else {
			int points = 1;
			String addon = "";
			
			int headshotExtra = 0;
			String headshotAddon = "";
			if(headshot) {
				headshotExtra = Modifiers.headshotExtra;
				headshotAddon = " §7[§d§nHEADSHOT§r§7] ";
			}
			
			int backstabExtra = 0;
			String backstabAddon = "";
			if(backstab) {
				backstabExtra = Modifiers.backstabExtra;
				backstabAddon = " §7[§d§nBACKSTAB§r§7] ";
			}
			
			int snipeExra = 0;
			String shotOrSnipe = "shot";
			if(snipe) {
				snipeExra = Modifiers.snipeShotsExtra;
				shotOrSnipe = "snipe";
			}
			
			switch (type) {
			case SHOT:
				points = Modifiers.points+Modifiers.closeRangeExtra+headshotExtra+snipeExra;
				Game.addPoints(killer, points);
				if(points > 1) addon = "s";
				deathMessage.put(victim, Game.getPlayerColor(victim).getChatColor()+victim.getName()+" §7§owas "+shotOrSnipe+" by §r"+Game.getPlayerColor(killer).getChatColor()+killer.getName()+headshotAddon+" §7(§a+"+points+" Point"+addon+"§7)");
				break;
			case PVP:
				points = Modifiers.points+Modifiers.pvpExtra+backstabExtra;
				Game.addPoints(killer, points);
				if(points > 1) addon = "s";
				deathMessage.put(victim, Game.getPlayerColor(killer).getChatColor()+killer.getName()+" §7§okilled §r"+Game.getPlayerColor(victim).getChatColor()+victim.getName()+backstabAddon+" §7(§a+"+points+" Point"+addon+"§7)");
				hideNextDM.put(victim, true);
				break;
			default:
				break;
			}
			
			addStrike(killer);
			if(strikedPlayers.get(victim) == null) strikedPlayers.put(victim, 0);
			if(strikedPlayers.get(victim) >= Modifiers.minKillsForStrike) strikeShutdown(killer, victim);
			killer.playSound(killer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 0);
			Lasertag.isProtected.put(victim, true);
			
			victim.damage(100);
		}
	}
	
	@EventHandler
	public void onPlayerDies(PlayerDeathEvent e) {
		Player p = e.getEntity();
		if(Game.tagging()) {
			if(deathMessage.get(p) != null) {
				e.setDeathMessage(deathMessage.get(p));
				e.setKeepInventory(true);
				e.getKeepLevel();
				deathMessage.put(p, null);
			} else if(hideNextDM.get(p) != null && !hideNextDM.get(p)) {
				e.setDeathMessage(Game.getPlayerColor(p).getChatColor()+p.getName()+" §rdied");
			} else {
				e.setDeathMessage("");
				hideNextDM.put(p, false);
			}
		} else {
			System.out.println(e.getDeathMessage());
			e.setDeathMessage(p.getName()+" died");
		}
	}
	
	
	public static HashMap<Player, Integer> strikedPlayers = new HashMap<Player, Integer>();
	public static void addStrike(Player p) {
		if(strikedPlayers.get(p) == null) strikedPlayers.put(p, 0);
		strikedPlayers.put(p, strikedPlayers.get(p)+1);
		if(strikedPlayers.get(p) >= Modifiers.minKillsForStrike) {
			int strike = strikedPlayers.get(p);
			String pName = Game.getPlayerColor(p).getChatColor()+p.getName();
			String pAddon = "";
			if(Modifiers.strikeExtra > 1) pAddon = "s";
			for(Player ap : Game.players()) {
				ap.sendMessage("§e——————————————————");
				ap.sendMessage(pName+" §dHas a Strike of §a"+strike+"§d! §7(§a+"+Modifiers.strikeExtra+" extra Point"+pAddon+"§7)");
				ap.sendMessage("§e——————————————————");
			}
			Game.addPoints(p, Modifiers.strikeExtra);
		}
	}
	public static void strikeShutdown(Player killer, Player victim) {
		String pAddon = "";
		if(Modifiers.strikeShutdown > 1) pAddon = "s";
		for(Player ap : Game.players()) {
			ap.sendMessage("§e——————————————————");
			ap.sendMessage(Game.getPlayerColor(killer).getChatColor()+killer.getName()+" §dended the strike of"+Game.getPlayerColor(victim).getChatColor()+victim.getName()+"§d! §7(§a+"+Modifiers.strikeExtra+" extra Point"+pAddon+"§7)");
			ap.sendMessage("§e——————————————————");
		}
		Game.addPoints(killer, Modifiers.strikeShutdown);
	}
	
}

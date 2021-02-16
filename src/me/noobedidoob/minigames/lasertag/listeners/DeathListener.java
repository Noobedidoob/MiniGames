package me.noobedidoob.minigames.lasertag.listeners;

import java.util.HashMap;

import me.noobedidoob.minigames.utils.Flag;
import me.noobedidoob.minigames.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.PluginManager;

import me.noobedidoob.minigames.Minigames;
import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.session.SessionModifiers.Mod;
import me.noobedidoob.minigames.lasertag.session.Session;

public class DeathListener implements Listener {
	
	public DeathListener(Minigames minigames) {
		PluginManager pluginManeger = Bukkit.getPluginManager();
		pluginManeger.registerEvents(this, minigames);
	}
	
	public enum HitType {
		SHOT,
		PVP,
	}
	public static final HashMap<Player, String> PLAYER_DEATH_MESSAGE = new HashMap<>();
	private static final HashMap<Player, Boolean> HIDE_NEXT_DEATH_MESSAGE = new HashMap<>();
	public static void hit(HitType type, Player killer, Player victim, double damage, boolean headshot, boolean snipe, boolean backstab) {
		Session session = Session.getPlayerSession(killer);
		if(session == null) return;
		if(!session.isInSession(victim)) return;
		
		if (victim.getGameMode() == GameMode.ADVENTURE) {
			if (damage < victim.getHealth()) {
				if(headshot) damage *= session.getDoubleMod(Mod.HEADSHOT_MULTIPLIKATOR);
				if(snipe) damage *= session.getDoubleMod(Mod.SNIPER_SHOT_MULTIPLIKATOR);
				victim.damage(damage);
			} else {
				int points = session.getIntMod(Mod.POINTS);
				String addon = "";
				
				int headshotExtra = 0;
				String headshotAddon = "";
				if(headshot) {
					headshotExtra = session.getIntMod(Mod.HEADSHOT_EXTRA_POINTS);
					headshotAddon = " §7[§d§nHEADSHOT§r§7] ";
				}
				
				int backstabExtra = 0;
				String backstabAddon = "";
				if(backstab) {
					backstabExtra = session.getIntMod(Mod.BACKSTAB_EXTRA_POINTS);
					backstabAddon = " §7[§d§nBACKSTAB§r§7] ";
				}
				
				int snipeExra = 0;
				String shotOrSnipe = "shot";
				if(snipe) {
					snipeExra = session.getIntMod(Mod.SNIPER_KILL_EXTRA_POINTS);
					shotOrSnipe = "sniped";
				}
				
				switch (type) {
				case SHOT:
					points += session.getIntMod(Mod.NORMAL_KILL_EXTRA_POINTS)+headshotExtra+snipeExra;
					session.addPoints(killer, points);
					if(points > 1) addon = "s";
					PLAYER_DEATH_MESSAGE.put(victim, session.getPlayerColor(killer).getChatColor()+killer.getName()+" §7§o"+shotOrSnipe+" §r"+session.getPlayerColor(victim).getChatColor()+victim.getName()+headshotAddon+" §7(§a+"+points+" Point"+addon+"§7)");
					break;
				case PVP:
					points += session.getIntMod(Mod.PVP_KILL_EXTRA_POINTS)+backstabExtra;
					session.addPoints(killer, points);
					if(points > 1) addon = "s";
					PLAYER_DEATH_MESSAGE.put(victim, session.getPlayerColor(killer).getChatColor()+killer.getName()+" §7§okilled §r"+session.getPlayerColor(victim).getChatColor()+victim.getName()+backstabAddon+" §7(§a+"+points+" Point"+addon+"§7)");
					HIDE_NEXT_DEATH_MESSAGE.put(victim, true);
					break;
				default:
					break;
				}

				STREAKED_PLAYERS.putIfAbsent(victim, 0);
				victim.damage(100);
				if(session.withCaptureTheFlag() && Flag.getPlayerFlag(victim) != null) Flag.getPlayerFlag(victim).drop(victim.getLocation());
				Utils.runLater(()->{
					addStreak(killer);
					if (STREAKED_PLAYERS.get(victim) >= session.getIntMod(Mod.MINIMAL_KILLS_FOR_STREAK)) streakShutdown(killer, victim);
				},5);
				killer.playSound(killer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 0);
				Lasertag.setPlayerProtected(victim, true);
			} 
		}
	}
	
	@EventHandler
	public void onPlayerDies(PlayerDeathEvent e) {
		Player p = e.getEntity();
		Session session = Session.getPlayerSession(p);
		if(session == null) return;
		e.setDeathMessage("");
		if(session.tagging()) {
			Lasertag.setPlayerProtected(p, true);
			STREAKED_PLAYERS.put(p, 0);
			if(PLAYER_DEATH_MESSAGE.get(p) != null) {
				session.sendMessageAll(PLAYER_DEATH_MESSAGE.get(p));
				e.setKeepInventory(true);
				e.getKeepLevel();
				PLAYER_DEATH_MESSAGE.put(p, null);
			} else if(HIDE_NEXT_DEATH_MESSAGE.get(p) != null && !HIDE_NEXT_DEATH_MESSAGE.get(p)) {
				session.sendMessageAll(session.getPlayerColor(p).getChatColor()+p.getName()+" §rdied");
			} else {
				HIDE_NEXT_DEATH_MESSAGE.put(p, false);
			}
		}
	}
	
	
	private static final HashMap<Player, Integer> STREAKED_PLAYERS = new HashMap<>();
	private static void addStreak(Player p) {
		Session session = Session.getPlayerSession(p);
		if(session == null) return;
		STREAKED_PLAYERS.putIfAbsent(p, 0);
		STREAKED_PLAYERS.put(p, STREAKED_PLAYERS.get(p)+1);
		if(STREAKED_PLAYERS.get(p) >= session.getIntMod(Mod.MINIMAL_KILLS_FOR_STREAK)) {
			int streak = STREAKED_PLAYERS.get(p);
			String pName = session.getPlayerColor(p).getChatColor()+p.getName();
			String pAddon = "";
			if(session.getIntMod(Mod.STREAK_EXTRA_POINTS) > 1) pAddon = "s";
			for(Player ap : session.getPlayers()) {
				ap.sendMessage("§e——————————————————");
				ap.sendMessage(pName+" §dHas a streak of §a"+streak+"§d! §7(§a+"+session.getIntMod(Mod.STREAK_EXTRA_POINTS)+" extra Point"+pAddon+"§7)");
				ap.sendMessage("§e——————————————————"); 
			}
			session.addPoints(p, session.getIntMod(Mod.STREAK_EXTRA_POINTS));
		}
	}
	private static void streakShutdown(Player killer, Player victim) {
		Session session = Session.getPlayerSession(killer);
		if(session == null) return;
		STREAKED_PLAYERS.put(victim, 0);
		String pAddon = "";
		if(session.getIntMod(Mod.STREAK_SHUTDOWN_EXTRA_POINTS) > 1) pAddon = "s";
		for(Player ap : session.getPlayers()) {
			ap.sendMessage("§e——————————————————");
			ap.sendMessage(session.getPlayerColor(killer).getChatColor()+killer.getName()+" §dended the streak of "+session.getPlayerColor(victim).getChatColor()+victim.getName()+"§d! §7(§a+"+session.getIntMod(Mod.STREAK_EXTRA_POINTS)+" extra Point"+pAddon+"§7)");
			ap.sendMessage("§e——————————————————");
		}
		session.addPoints(killer, session.getIntMod(Mod.STREAK_SHUTDOWN_EXTRA_POINTS));
	}
	public static void resetPlayerStreak(Player p) {
		STREAKED_PLAYERS.put(p, 0);
	}
	
}

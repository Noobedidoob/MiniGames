package me.noobedidoob.minigames.lasertag.listeners;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import me.noobedidoob.minigames.main.Minigames;
import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.session.SessionModifiers.Mod;
import me.noobedidoob.minigames.lasertag.session.Session;

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
					deathMessage.put(victim, session.getPlayerColor(killer).getChatColor()+killer.getName()+" §7§o"+shotOrSnipe+" §r"+session.getPlayerColor(victim).getChatColor()+victim.getName()+headshotAddon+" §7(§a+"+points+" Point"+addon+"§7)");
					break;
				case PVP:
					points += session.getIntMod(Mod.PVP_KILL_EXTRA_POINTS)+backstabExtra;
					session.addPoints(killer, points);
					if(points > 1) addon = "s";
					deathMessage.put(victim, session.getPlayerColor(killer).getChatColor()+killer.getName()+" §7§okilled §r"+session.getPlayerColor(victim).getChatColor()+victim.getName()+backstabAddon+" §7(§a+"+points+" Point"+addon+"§7)");
					hideNextDM.put(victim, true);
					break;
				default:
					break;
				}

				if (streakedPlayers.get(victim) == null) streakedPlayers.put(victim, 0);
				if (streakedPlayers.get(victim) >= session.getIntMod(Mod.MINIMAL_KILLS_FOR_STREAK)) streakShutdown(killer, victim);
				victim.damage(100);
				new BukkitRunnable() {
					@Override
					public void run() {
						addStreak(killer, victim);
					}
				}.runTaskLater(Minigames.minigames, 1);
				killer.playSound(killer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 0);
				Lasertag.isProtected.put(victim, true);
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
			Lasertag.isProtected.put(p, true);
			streakedPlayers.put(p, 0);
			if(deathMessage.get(p) != null) {
				session.sendMessageAll(deathMessage.get(p));
				e.setKeepInventory(true);
				e.getKeepLevel();
				deathMessage.put(p, null);
			} else if(hideNextDM.get(p) != null && !hideNextDM.get(p)) {
				session.sendMessageAll(session.getPlayerColor(p).getChatColor()+p.getName()+" §rdied");
			} else {
				hideNextDM.put(p, false);
			}
		}
	}
	
	
	public static HashMap<Player, Integer> streakedPlayers = new HashMap<Player, Integer>();
	public static void addStreak(Player p, Player victim) {
		Session session = Session.getPlayerSession(p);
		if(session == null) return;
		if(streakedPlayers.get(p) == null) streakedPlayers.put(p, 0);
		streakedPlayers.put(p, streakedPlayers.get(p)+1);
		if(streakedPlayers.get(p) >= session.getIntMod(Mod.MINIMAL_KILLS_FOR_STREAK)) {
			int streak = streakedPlayers.get(p);
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
	public static void streakShutdown(Player killer, Player victim) {
		Session session = Session.getPlayerSession(killer);
		if(session == null) return;
		streakedPlayers.put(victim, 0);
		String pAddon = "";
		if(session.getIntMod(Mod.STREAK_SHUTDOWN_EXTRA_POINTS) > 1) pAddon = "s";
		for(Player ap : session.getPlayers()) {
			ap.sendMessage("§e——————————————————");
			ap.sendMessage(session.getPlayerColor(killer).getChatColor()+killer.getName()+" §dended the streak of "+session.getPlayerColor(victim).getChatColor()+victim.getName()+"§d! §7(§a+"+session.getIntMod(Mod.STREAK_EXTRA_POINTS)+" extra Point"+pAddon+"§7)");
			ap.sendMessage("§e——————————————————");
		}
		session.addPoints(killer, session.getIntMod(Mod.STREAK_SHUTDOWN_EXTRA_POINTS));
	}
	
}

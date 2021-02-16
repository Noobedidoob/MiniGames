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
				if(backstab) points += session.getIntMod(Mod.BACKSTAB_EXTRA_POINTS);
				else {
					if(headshot) points += session.getIntMod(Mod.HEADSHOT_EXTRA_POINTS);
					if(snipe) points += session.getIntMod(Mod.SNIPER_KILL_EXTRA_POINTS);
				}
				switch (type) {
				case SHOT:
					points += session.getIntMod(Mod.SHOT_KILL_EXTRA_POINTS);
					session.addPoints(killer, points, session.getPlayerColor(killer).getChatColor()+killer.getName()+" §7§o"+((snipe)?"snipe":"shot")+" §r"+session.getPlayerColor(victim).getChatColor()+victim.getName()+((headshot)?" §7[§d§nHEADSHOT§r§7] ": "")+" §7(§a+"+points+" point"+((points > 1)?"s":"")+"§7)");
					break;
				case PVP:
					points += session.getIntMod(Mod.PVP_KILL_EXTRA_POINTS);
					session.addPoints(killer, points, session.getPlayerColor(killer).getChatColor()+killer.getName()+" §7§okilled §r"+session.getPlayerColor(victim).getChatColor()+victim.getName()+((backstab)?" §7[§d§nBACKSTAB§r§7] ":"")+" §7(§a+"+points+" point"+((points > 1)?"s":"")+"§7)");
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
		e.setDeathMessage("");
		STREAKED_PLAYERS.put(p, 0);
	}
	
	
	private static final HashMap<Player, Integer> STREAKED_PLAYERS = new HashMap<>();
	private static void addStreak(Player p) {
		Session session = Session.getPlayerSession(p);
		if(session == null) return;
		STREAKED_PLAYERS.putIfAbsent(p, 0);
		int streak = STREAKED_PLAYERS.get(p);
		STREAKED_PLAYERS.put(p,streak++);
		if(streak >= session.getIntMod(Mod.MINIMAL_KILLS_FOR_STREAK)) {
			session.addPoints(p, session.getIntMod(Mod.STREAK_EXTRA_POINTS),"——§e"+session.getPlayerColor(p).getChatColor()+p.getName()+" §dHas a streak of §a"+streak+"§d! §7(§a+"+session.getIntMod(Mod.STREAK_EXTRA_POINTS)+" extra Point"+((session.getIntMod(Mod.STREAK_EXTRA_POINTS) > 1)?"s":"")+"§7)§e——");
		}
	}
	private static void streakShutdown(Player killer, Player victim) {
		Session session = Session.getPlayerSession(killer);
		if(session == null) return;
		STREAKED_PLAYERS.put(victim, 0);
		session.addPoints(killer, session.getIntMod(Mod.STREAK_SHUTDOWN_EXTRA_POINTS),"——§e"+session.getPlayerColor(killer).getChatColor()+killer.getName()+" §dended the streak of "+session.getPlayerColor(victim).getChatColor()+victim.getName()+"§d! §7(§a+"+session.getIntMod(Mod.STREAK_EXTRA_POINTS)+" extra Point"+((session.getIntMod(Mod.STREAK_EXTRA_POINTS) > 1)?"s":"")+"§7)§e——");
	}
	public static void resetPlayerStreak(Player p) {
		STREAKED_PLAYERS.put(p, 0);
	}
	
}

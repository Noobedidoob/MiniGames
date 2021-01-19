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

import me.noobedidoob.minigames.main.Minigames;
import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.session.Modifiers.Mod;
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
		
		if (victim.getGameMode() == GameMode.ADVENTURE | victim.getGameMode() == GameMode.SURVIVAL) {
			if (damage < victim.getHealth()) {
				victim.damage(damage);
			} else {
				int points = session.getIntMod(Mod.POINTS);
				String addon = "";
				
				int headshotExtra = 0;
				String headshotAddon = "";
				if(headshot) {
					headshotExtra = session.getIntMod(Mod.HEADSHOT_EXTRA);
					headshotAddon = " ｧ7[ｧdｧnHEADSHOTｧrｧ7] ";
				}
				
				int backstabExtra = 0;
				String backstabAddon = "";
				if(backstab) {
					backstabExtra = session.getIntMod(Mod.BACKSTAB_EXTRA);
					backstabAddon = " ｧ7[ｧdｧnBACKSTABｧrｧ7] ";
				}
				
				int snipeExra = 0;
				String shotOrSnipe = "shot";
				if(snipe) {
					snipeExra = session.getIntMod(Mod.SNIPER_SHOT_EXTRA);
					shotOrSnipe = "sniped";
				}
				
				switch (type) {
				case SHOT:
					points += session.getIntMod(Mod.NORMAL_SHOT_EXTRA)+headshotExtra+snipeExra;
					session.addPoints(killer, points);
					if(points > 1) addon = "s";
					deathMessage.put(victim, session.getPlayerColor(killer).getChatColor()+killer.getName()+" ｧ7ｧo"+shotOrSnipe+" ｧr"+session.getPlayerColor(victim).getChatColor()+victim.getName()+headshotAddon+" ｧ7(ｧa+"+points+" Point"+addon+"ｧ7)");
					break;
				case PVP:
					points += session.getIntMod(Mod.PVP_EXTRA)+backstabExtra;
					session.addPoints(killer, points);
					if(points > 1) addon = "s";
					deathMessage.put(victim, session.getPlayerColor(killer).getChatColor()+killer.getName()+" ｧ7ｧokilled ｧr"+session.getPlayerColor(victim).getChatColor()+victim.getName()+backstabAddon+" ｧ7(ｧa+"+points+" Point"+addon+"ｧ7)");
					hideNextDM.put(victim, true);
					break;
				default:
					break;
				}

				if (streakedPlayers.get(victim) == null) streakedPlayers.put(victim, 0);
				if (streakedPlayers.get(victim) >= session.getIntMod(Mod.MIN_KILLS_FOR_STREAK)) streakShutdown(killer, victim);
				victim.damage(100);
				addStreak(killer, victim);
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
		if(session.tagging()) {
			streakedPlayers.put(p, 0);
			if(deathMessage.get(p) != null) {
				e.setDeathMessage(deathMessage.get(p));
				e.setKeepInventory(true);
				e.getKeepLevel();
				deathMessage.put(p, null);
			} else if(hideNextDM.get(p) != null && !hideNextDM.get(p)) {
				e.setDeathMessage(session.getPlayerColor(p).getChatColor()+p.getName()+" ｧrdied");
			} else {
				e.setDeathMessage("");
				hideNextDM.put(p, false);
			}
		} else {
			System.out.println(e.getDeathMessage());
			e.setDeathMessage(p.getName()+" died");
		}
	}
	
	
	public static HashMap<Player, Integer> streakedPlayers = new HashMap<Player, Integer>();
	public static void addStreak(Player p, Player victim) {
		Session session = Session.getPlayerSession(p);
		if(session == null) return;
		if(streakedPlayers.get(p) == null) streakedPlayers.put(p, 0);
		streakedPlayers.put(p, streakedPlayers.get(p)+1);
		if(streakedPlayers.get(p) >= session.getIntMod(Mod.MIN_KILLS_FOR_STREAK)) {
			int streak = streakedPlayers.get(p);
			String pName = session.getPlayerColor(p).getChatColor()+p.getName();
			String pAddon = "";
			if(session.getIntMod(Mod.STREAK_EXTRA) > 1) pAddon = "s";
			for(Player ap : session.getPlayers()) {
				ap.sendMessage("ｧe覧覧覧覧覧覧覧覧覧");
				ap.sendMessage(pName+" ｧdHas a streak of ｧa"+streak+"ｧd! ｧ7(ｧa+"+session.getIntMod(Mod.STREAK_EXTRA)+" extra Point"+pAddon+"ｧ7)");
				ap.sendMessage("ｧe覧覧覧覧覧覧覧覧覧"); 
			}
			session.addPoints(p, session.getIntMod(Mod.STREAK_EXTRA));
		}
	}
	public static void streakShutdown(Player killer, Player victim) {
		Session session = Session.getPlayerSession(killer);
		if(session == null) return;
		streakedPlayers.put(victim, 0);
		String pAddon = "";
		if(session.getIntMod(Mod.STREAK_SHUTDOWN) > 1) pAddon = "s";
		for(Player ap : session.getPlayers()) {
			ap.sendMessage("ｧe覧覧覧覧覧覧覧覧覧");
			ap.sendMessage(session.getPlayerColor(killer).getChatColor()+killer.getName()+" ｧdended the streak of "+session.getPlayerColor(victim).getChatColor()+victim.getName()+"ｧd! ｧ7(ｧa+"+session.getIntMod(Mod.STREAK_EXTRA)+" extra Point"+pAddon+"ｧ7)");
			ap.sendMessage("ｧe覧覧覧覧覧覧覧覧覧");
		}
		session.addPoints(killer, session.getIntMod(Mod.STREAK_SHUTDOWN));
	}
	
}

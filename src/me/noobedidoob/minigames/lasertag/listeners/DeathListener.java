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
		
		if (victim.getGameMode() == GameMode.ADVENTURE | victim.getGameMode() == GameMode.SURVIVAL) {
			if (damage < victim.getHealth()) {
				victim.damage(damage);
			} else {
				int points = Modifiers.points;
				String addon = "";
				
				int headshotExtra = 0;
				String headshotAddon = "";
				if(headshot) {
					headshotExtra = Modifiers.headshotExtra;
					headshotAddon = " ｧ7[ｧdｧnHEADSHOTｧrｧ7] ";
				}
				
				int backstabExtra = 0;
				String backstabAddon = "";
				if(backstab) {
					backstabExtra = Modifiers.backstabExtra;
					backstabAddon = " ｧ7[ｧdｧnBACKSTABｧrｧ7] ";
				}
				
				int snipeExra = 0;
				String shotOrSnipe = "shot";
				if(snipe) {
					snipeExra = Modifiers.snipeShotsExtra;
					shotOrSnipe = "sniped";
				}
				
				switch (type) {
				case SHOT:
					points += Modifiers.closeRangeExtra+headshotExtra+snipeExra;
					Game.addPoints(killer, points);
					if(points > 1) addon = "s";
					deathMessage.put(victim, Game.getPlayerColor(killer).getChatColor()+killer.getName()+" ｧ7ｧo"+shotOrSnipe+" ｧr"+Game.getPlayerColor(victim).getChatColor()+victim.getName()+headshotAddon+" ｧ7(ｧa+"+points+" Point"+addon+"ｧ7)");
					break;
				case PVP:
					points += Modifiers.pvpExtra+backstabExtra;
					Game.addPoints(killer, points);
					if(points > 1) addon = "s";
					deathMessage.put(victim, Game.getPlayerColor(killer).getChatColor()+killer.getName()+" ｧ7ｧokilled ｧr"+Game.getPlayerColor(victim).getChatColor()+victim.getName()+backstabAddon+" ｧ7(ｧa+"+points+" Point"+addon+"ｧ7)");
					hideNextDM.put(victim, true);
					break;
				default:
					break;
				}

				if (streakedPlayers.get(victim) == null) streakedPlayers.put(victim, 0);
				if (streakedPlayers.get(victim) >= Modifiers.minKillsForStreak) streakShutdown(killer, victim);
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
		if(Game.tagging()) {
			streakedPlayers.put(p, 0);
			if(deathMessage.get(p) != null) {
				e.setDeathMessage(deathMessage.get(p));
				e.setKeepInventory(true);
				e.getKeepLevel();
				deathMessage.put(p, null);
			} else if(hideNextDM.get(p) != null && !hideNextDM.get(p)) {
				e.setDeathMessage(Game.getPlayerColor(p).getChatColor()+p.getName()+" ｧrdied");
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
		if(streakedPlayers.get(p) == null) streakedPlayers.put(p, 0);
		streakedPlayers.put(p, streakedPlayers.get(p)+1);
		if(streakedPlayers.get(p) >= Modifiers.minKillsForStreak) {
			int streak = streakedPlayers.get(p);
			String pName = Game.getPlayerColor(p).getChatColor()+p.getName();
			String pAddon = "";
			if(Modifiers.streakExtra > 1) pAddon = "s";
			for(Player ap : Game.players()) {
				ap.sendMessage("ｧe覧覧覧覧覧覧覧覧覧");
				ap.sendMessage(pName+" ｧdHas a streak of ｧa"+streak+"ｧd! ｧ7(ｧa+"+Modifiers.streakExtra+" extra Point"+pAddon+"ｧ7)");
				ap.sendMessage("ｧe覧覧覧覧覧覧覧覧覧"); 
			}
			Game.addPoints(p, Modifiers.streakExtra);
		}
	}
	public static void streakShutdown(Player killer, Player victim) {
		streakedPlayers.put(victim, 0);
		String pAddon = "";
		if(Modifiers.streakShutdown > 1) pAddon = "s";
		for(Player ap : Game.players()) {
			ap.sendMessage("ｧe覧覧覧覧覧覧覧覧覧");
			ap.sendMessage(Game.getPlayerColor(killer).getChatColor()+killer.getName()+" ｧdended the streak of "+Game.getPlayerColor(victim).getChatColor()+victim.getName()+"ｧd! ｧ7(ｧa+"+Modifiers.streakExtra+" extra Point"+pAddon+"ｧ7)");
			ap.sendMessage("ｧe覧覧覧覧覧覧覧覧覧");
		}
		Game.addPoints(killer, Modifiers.streakShutdown);
	}
	
}

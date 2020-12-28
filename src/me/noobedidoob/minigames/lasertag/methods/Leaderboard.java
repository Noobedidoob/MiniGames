package me.noobedidoob.minigames.lasertag.methods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.commands.ModifierCommands.Mod;
import me.noobedidoob.minigames.utils.MgUtils;

public class Leaderboard {
	
	public static Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
	@SuppressWarnings("deprecation")
	private static Objective obj = board.registerNewObjective("test", "dummy");
	public static long time;
	
	@SuppressWarnings("deprecation")
	public static void refreshScoreboard() {
		obj.unregister();
		obj = board.registerNewObjective("killsList", "lasertag");
		obj.setDisplayName("§b§lLasertag");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		if(time < 3600) obj.getScore("§c§l"+MgUtils.getTimeFormatFromLong(time, "m")).setScore(0);
		else obj.getScore("§c§l"+MgUtils.getTimeFormatFromLong(time, "h")).setScore(0);
		obj.getScore("").setScore(1);
		
		if(Game.solo()) {
			int i = 2;
			
			HashMap<Integer, List<Player>> playersInSorted = new HashMap<Integer, List<Player>>();
			
			int maxScore = 0;
			for(Player p : Game.players()) {
				List<Player> newList = new ArrayList<Player>();
				if(playersInSorted.get(Game.getPlayerPoints(p)) == null) {
					newList.add(p);
					playersInSorted.put(Game.getPlayerPoints(p), newList);
				} else {
					newList = playersInSorted.get(Game.getPlayerPoints(p));
					newList.add(p);
					playersInSorted.put(Game.getPlayerPoints(p), newList);
				}
				if(Game.getPlayerPoints(p) > maxScore) maxScore = Game.getPlayerPoints(p);
			}
			List<Player> sortedInRanks = new ArrayList<Player>();
			for(int c = 0; c <= maxScore; c++) {
				if(playersInSorted.get(c) != null) {
					List<Player> rankList = playersInSorted.get(c);
					for(Player p : rankList) {
						sortedInRanks.add(p);
					}
				}
			}
			
			
			for(Player p : sortedInRanks) {
				String underline = "";
				if(playersInSorted.get(maxScore).contains(p)) underline = "§n";
				obj.getScore(underline+Game.getPlayerColor(p).getChatColor()+p.getName()+" §7(§a"+Game.getPlayerPoints(p)+"§7)  ").setScore(i);
				i++;
			}
			
			for(Player p : Game.players()) {
				p.setScoreboard(board);
				if(Mod.HIGHLIGHT_PLAYERS.getBoolean()) {
					if(!p.hasPotionEffect(Lasertag.glowingEffect)) p.addPotionEffect(new PotionEffect(Lasertag.glowingEffect, 20*((int) time)+20, Mod.HIGHLIGHT_POWER.getInt(), false, false));
				}
				if(Lasertag.isProtected.get(p) == null) Lasertag.isProtected.put(p, false);
				else if(Lasertag.isProtected.get(p)) {
					p.spawnParticle(Particle.COMPOSTER, p.getLocation().subtract(0, 1, 0), 1, 2,2,2);
				}
			}
		} else {
			int i = 2;
			
			HashMap<Integer, List<Player[]>> teamsInSorted = new HashMap<Integer, List<Player[]>>();
			int maxTeamScore = 0;
			for(Player[] team : Game.getTeams()) {
				List<Player[]> newList = new ArrayList<Player[]>();
				if(teamsInSorted.get(Game.getTeamPoints(team)) == null) {
					newList.add(team);
					teamsInSorted.put(Game.getTeamPoints(team), newList);
				} else {
					newList = teamsInSorted.get(Game.getTeamPoints(team));
					newList.add(team);
					teamsInSorted.put(Game.getTeamPoints(team), newList);
				}
				if(Game.getTeamPoints(team) > maxTeamScore) maxTeamScore = Game.getTeamPoints(team);
			}
			List<Player[]> teamsSortedInRanks = new ArrayList<Player[]>();
			for(int c = 0; c <= maxTeamScore; c++) {
				if(teamsInSorted.get(c) != null) {
					List<Player[]> rankTeamsList = teamsInSorted.get(c);
					for(Player [] team : rankTeamsList) {
						teamsSortedInRanks.add(team);
					}
				}
			}
			
			for(Player[] team : teamsSortedInRanks) {
				obj.getScore(Game.getTeamColor(team).getChatColor()+Game.getTeamColor(team).getName()+" Team §7(§a"+Game.getTeamPoints(team)+"§7)  ").setScore(i);
				i++;
			}
			for(Player p : Game.players()) {
				p.setScoreboard(board);
				if(Mod.HIGHLIGHT_PLAYERS.getBoolean()) {
					if(!p.hasPotionEffect(Lasertag.glowingEffect)) p.addPotionEffect(new PotionEffect(Lasertag.glowingEffect, 20*((int) time)+20, Mod.HIGHLIGHT_POWER.getInt(), false, false));
				}
				//if(isProtected.get(p) == null) isProtected.put(p, false);
				try{
					if(Lasertag.isProtected.get(p)) p.spawnParticle(Particle.COMPOSTER, p.getLocation().subtract(0, 1, 0), 1, 2,2,2);
				} catch (NullPointerException e) {
					
				}
			}
		}
	}
	
	public static String randomName() {
		double d = (Math.random()*((Double.MAX_EXPONENT-Double.MIN_EXPONENT)+1))+Double.MIN_EXPONENT;
		String name = Double.toString(d);
		return name.substring(0, 15);
	}

}

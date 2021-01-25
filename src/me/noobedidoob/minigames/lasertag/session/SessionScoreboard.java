package me.noobedidoob.minigames.lasertag.session;

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
import me.noobedidoob.minigames.lasertag.session.SessionModifiers.Mod;
import me.noobedidoob.minigames.utils.MgUtils;
import me.noobedidoob.minigames.utils.MgUtils.TimeFormat;

public class SessionScoreboard {
	
	private Session session;
	public SessionScoreboard(Session session) {
		this.session = session;
	}
	
	public Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
	private Objective obj;
	
	public void refresh() {
		if(obj != null) obj.unregister();
		obj = board.registerNewObjective("Scoreboard", "lasertag", "�b�lScoreboard");
		obj.setDisplayName("�b�lScoreboard");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		long time = session.getTime(TimeFormat.SECONDS);
		if(time < 3600) obj.getScore("�eTime:  �c�l"+MgUtils.getTimeFormatFromLong(time, "m")).setScore(0);
		else obj.getScore("�eTime:  �c�l"+MgUtils.getTimeFormatFromLong(time, "h")).setScore(0);
		obj.getScore(" ").setScore(1);
		
		
		
		int i = 2;
		
		
		if(!session.isMapNull()){
			if(session.votingMap()) {
				obj.getScore("�eMap:  �o�7Voting...").setScore(2);
				obj.getScore("  ").setScore(3);
				i = 4;
			} else {
				
				obj.getScore("�eMap:  �r�b"+session.getMap().getName()).setScore(2);
				obj.getScore("  ").setScore(3);
				i = 4;
			}
		}
		
		if(session.isSolo()) {
			
			HashMap<Integer, List<Player>> playersInSorted = new HashMap<Integer, List<Player>>();
			
			int maxScore = 0;
			for(Player p : session.getPlayers()) {
				List<Player> newList = new ArrayList<Player>();
				if(playersInSorted.get(session.getPlayerPoints(p)) == null) {
					newList.add(p);
					playersInSorted.put(session.getPlayerPoints(p), newList);
				} else {
					newList = playersInSorted.get(session.getPlayerPoints(p));
					newList.add(p);
					playersInSorted.put(session.getPlayerPoints(p), newList);
				}
				if(session.getPlayerPoints(p) > maxScore) maxScore = session.getPlayerPoints(p);
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
				if(playersInSorted.get(maxScore).contains(p)) underline = "�n";
				obj.getScore(underline+session.getPlayerColor(p).getChatColor()+p.getName()+" �7(�a"+session.getPlayerPoints(p)+"�7)  ").setScore(i++);
			}
			obj.getScore("   ").setScore(i);
			
			for(Player p : session.getPlayers()) {
				p.setScoreboard(board);
				if(session.getBooleanMod(Mod.HIGHLIGHT_PLAYERS)) {
					if(!p.hasPotionEffect(Lasertag.glowingEffect)) p.addPotionEffect(new PotionEffect(Lasertag.glowingEffect, 20*((int) time)+20, session.getIntMod(Mod.HIGHLIGHT_POWER), false, false));
				}
				if(Lasertag.isProtected.get(p) == null) Lasertag.isProtected.put(p, false);
				else if(Lasertag.isProtected.get(p)) {
					p.spawnParticle(Particle.COMPOSTER, p.getLocation().subtract(0, 1, 0), 1, 2,2,2);
				}
			}
		} else {
			
			HashMap<Integer, List<SessionTeam>> teamsInSorted = new HashMap<Integer, List<SessionTeam>>();
			int maxTeamScore = 0;
			for(SessionTeam team : session.getTeams()) {
				List<SessionTeam> newList = new ArrayList<SessionTeam>();
				if(teamsInSorted.get(session.getTeamPoints(team)) == null) {
					newList.add(team);
					teamsInSorted.put(session.getTeamPoints(team), newList);
				} else {
					newList = teamsInSorted.get(session.getTeamPoints(team));
					newList.add(team);
					teamsInSorted.put(session.getTeamPoints(team), newList);
				}
				if(session.getTeamPoints(team) > maxTeamScore) maxTeamScore = session.getTeamPoints(team);
			}
			List<SessionTeam> teamsSortedInRanks = new ArrayList<SessionTeam>();
			for(int c = 0; c <= maxTeamScore; c++) {
				if(teamsInSorted.get(c) != null) {
					List<SessionTeam> rankTeamsList = teamsInSorted.get(c);
					for(SessionTeam team : rankTeamsList) {
						teamsSortedInRanks.add(team);
					}
				}
			}
			
			for(SessionTeam team : teamsSortedInRanks) {
				String spaces = "    ";
				for(int j = 0; j < i; j++) spaces += " ";
				obj.getScore(spaces).setScore(i++);
				
				HashMap<Integer, List<Player>> playersInSorted = new HashMap<Integer, List<Player>>();
				int maxScore = 0;
				for(Player p : team.getPlayers()) {
					List<Player> newList = new ArrayList<Player>();
					if(playersInSorted.get(session.getPlayerPoints(p)) == null) {
						newList.add(p);
						playersInSorted.put(session.getPlayerPoints(p), newList);
					} else {
						newList = playersInSorted.get(session.getPlayerPoints(p));
						newList.add(p);
						playersInSorted.put(session.getPlayerPoints(p), newList);
					}
					if(session.getPlayerPoints(p) > maxScore) maxScore = session.getPlayerPoints(p);
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
					obj.getScore("   "+session.getPlayerColor(p).getChatColor()+p.getName()+" �7(�a"+session.getPlayerPoints(p)+"�7)  ").setScore(i++);
				}
				obj.getScore(session.getTeamColor(team).getChatColor()+session.getTeamColor(team).getName()+" Team �7(�a"+session.getTeamPoints(team)+"�7)  ").setScore(i++);
			}
			obj.getScore("   ").setScore(i);
			for(Player p : session.getPlayers()) {
				p.setScoreboard(board);
				if(session.getBooleanMod(Mod.HIGHLIGHT_PLAYERS)) {
					if(!p.hasPotionEffect(Lasertag.glowingEffect)) p.addPotionEffect(new PotionEffect(Lasertag.glowingEffect, 20*((int) time)+20, session.getIntMod(Mod.HIGHLIGHT_POWER), false, false));
				}
				//if(isProtected.get(p) == null) isProtected.put(p, false);
				try{
					if(Lasertag.isProtected.get(p)) p.spawnParticle(Particle.COMPOSTER, p.getLocation().add(0, 3, 0), 1, 2,2,2);
				} catch (NullPointerException e) {
					
				}
			}
		}
	}
}
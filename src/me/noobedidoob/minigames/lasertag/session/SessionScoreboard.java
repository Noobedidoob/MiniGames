package me.noobedidoob.minigames.lasertag.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.noobedidoob.minigames.utils.BaseSphere;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.Lasertag.LasertagColor;
import me.noobedidoob.minigames.lasertag.session.SessionModifiers.Mod;
import me.noobedidoob.minigames.utils.Map;
import me.noobedidoob.minigames.utils.Utils;
import me.noobedidoob.minigames.utils.Utils.TimeFormat;

public class SessionScoreboard {
	
	private final Session session;
	public SessionScoreboard(Session session) {
		this.session = session;
	}
	
	public Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
	private Objective obj;
	
	public void refresh() {
		if(obj != null) obj.unregister();
		obj = board.registerNewObjective("Scoreboard", "lasertag", "§b§lScoreboard");
		obj.setDisplayName("§b§lScoreboard");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		long time = session.getTime(TimeFormat.SECONDS);
		if(session.tagging()) time++;
		if(time < 3600) obj.getScore("§eTime:  §c§l"+Utils.getTimeFormatFromLong(time, "m")).setScore(0);
		else obj.getScore("§eTime:  §c§l"+Utils.getTimeFormatFromLong(time, "h")).setScore(0);
		obj.getScore(" ").setScore(1);
		
		
		
		int i = 2;
		
		
		if(!session.isMapNull()){
			if(session.votingMap()) {
				for(int j = Map.MAPS.size(); j-- > 0;) {
					Map m = Map.MAPS.get(j);
					if(session.mapVotes.get(m) > 0 && j == Map.MAPS.size()-1) obj.getScore("  §n§6"+m.getName()+": §7(§a"+session.mapVotes.get(m)+"§7)").setScore(i++);
					else if(session.mapVotes.get(m) > 0) obj.getScore("  §6"+m.getName()+": §7(§a"+session.mapVotes.get(m)+"§7)").setScore(i++);
				}
				obj.getScore("§eMap:  §o§aVoting...").setScore(i++);
			} else {
				obj.getScore("§eMap:  §r§b"+session.getMap().getName()).setScore(i++);
			}
			obj.getScore("  ").setScore(i++);
		}
		
		if(session.isSolo()) {
			
			HashMap<Integer, List<Player>> playersInSorted = new HashMap<>();
			
			int maxScore = 0;
			for(Player p : session.getPlayers()) {
				List<Player> newList = new ArrayList<>();
				if (playersInSorted.get(session.getPlayerPoints(p)) != null) {
					newList = playersInSorted.get(session.getPlayerPoints(p));
				}
				newList.add(p);
				playersInSorted.put(session.getPlayerPoints(p), newList);
				if(session.getPlayerPoints(p) > maxScore) maxScore = session.getPlayerPoints(p);
			}
			List<Player> sortedInRanks = new ArrayList<>();
			for(int c = 0; c <= maxScore; c++) {
				if(playersInSorted.get(c) != null) {
					List<Player> rankList = playersInSorted.get(c);
					sortedInRanks.addAll(rankList);
				}
			}
			
			
			for(Player p : sortedInRanks) {
				String underline = "";
				if(playersInSorted.get(maxScore).contains(p)) underline = "§n";
				LasertagColor color = session.getPlayerColor(p);
				if(color == null) System.out.println("Color = null from player "+p.getName());
				int pp = session.getPlayerPoints(p);
				obj.getScore(underline+color.getChatColor()+p.getName()+" §7(§a"+pp+"§7)  ").setScore(i++);
			}
			obj.getScore("   ").setScore(i);
			
			for(Player p : session.getPlayers()) {
				p.setScoreboard(board);
				if(session.getBooleanMod(Mod.HIGHLIGHT_PLAYERS) && session.tagging()) {
					if(!p.hasPotionEffect(PotionEffectType.GLOWING)) p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20*((int) time)+20, session.getIntMod(Mod.HIGHLIGHT_POWER), false, false));
				}
				else if(Lasertag.isPlayerProtected(p)) {
					p.spawnParticle(Particle.COMPOSTER, p.getLocation().subtract(0, 1, 0), 1, 2,2,2);
				}
			}
		} else {
			
			HashMap<Integer, List<SessionTeam>> teamsInSorted = new HashMap<>();
			int maxTeamScore = 0;
			for(SessionTeam team : session.getTeams()) {
				List<SessionTeam> newList = new ArrayList<>();
				if (teamsInSorted.get(session.getTeamPoints(team)) != null) {
					newList = teamsInSorted.get(session.getTeamPoints(team));
				}
				newList.add(team);
				teamsInSorted.put(session.getTeamPoints(team), newList);
				if(session.getTeamPoints(team) > maxTeamScore) maxTeamScore = session.getTeamPoints(team);
			}
			List<SessionTeam> teamsSortedInRanks = new ArrayList<>();
			for(int c = 0; c <= maxTeamScore; c++) {
				if(teamsInSorted.get(c) != null) {
					List<SessionTeam> rankTeamsList = teamsInSorted.get(c);
					teamsSortedInRanks.addAll(rankTeamsList);
				}
			}
			
			for(SessionTeam team : teamsSortedInRanks) {
				StringBuilder spaces = new StringBuilder("    ");
				for(int j = 0; j < i; j++) spaces.append(" ");
				obj.getScore(spaces.toString()).setScore(i++);
				
				HashMap<Integer, List<Player>> playersInSorted = new HashMap<>();
				int maxScore = 0;
				for(Player p : team.getPlayers()) {
					List<Player> newList = new ArrayList<>();
					if (playersInSorted.get(session.getPlayerPoints(p)) != null) {
						newList = playersInSorted.get(session.getPlayerPoints(p));
					}
					newList.add(p);
					playersInSorted.put(session.getPlayerPoints(p), newList);
					if(session.getPlayerPoints(p) > maxScore) maxScore = session.getPlayerPoints(p);
				}
				List<Player> sortedInRanks = new ArrayList<>();
				for(int c = 0; c <= maxScore; c++) {
					if(playersInSorted.get(c) != null) {
						List<Player> rankList = playersInSorted.get(c);
						sortedInRanks.addAll(rankList);
					}
				}
				
				for(Player p : sortedInRanks) {
					obj.getScore("   "+session.getPlayerColor(p).getChatColor()+p.getName()+" §7(§a"+session.getPlayerPoints(p)+"§7)  ").setScore(i++);
				}
				obj.getScore(session.getTeamColor(team).getChatColor()+session.getTeamColor(team).name()+" Team §7(§a"+session.getTeamPoints(team)+"§7)  ").setScore(i++);
			}
			obj.getScore("   ").setScore(i);
			for(Player p : session.getPlayers()) {
				p.setScoreboard(board);
				if(session.getBooleanMod(Mod.HIGHLIGHT_PLAYERS) && session.tagging()) {
					if(!p.hasPotionEffect(PotionEffectType.GLOWING)) p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20*((int) time)+20, session.getIntMod(Mod.HIGHLIGHT_POWER), false, false));
				}
				if(Lasertag.isPlayerProtected(p)) BaseSphere.drawPlayerProtectionSphere(p);
			}
		}
	}
	
	public String[] getMapNamesSorted() {
		Map longest = Map.MAPS.get(0);
		
		for(Map m : Map.MAPS) {
			if(m.getName().length() > longest.getName().length()) longest = m;
		}
		
		List<String> list = new ArrayList<>();
		for(Map m : Map.MAPS) {
			int difference = longest.getName().length() - m.getName().length();
			
			StringBuilder name = new StringBuilder("§6" + m.getName() + ":");
			while(difference > 0) {
				name.append(" ");
				difference--;
			}
			list.add("  §6"+name+" §a"+session.mapVotes.get(m));
		}
		return list.toArray(new String[0]);
	}
}

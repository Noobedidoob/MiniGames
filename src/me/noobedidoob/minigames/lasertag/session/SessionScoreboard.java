package me.noobedidoob.minigames.lasertag.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.noobedidoob.minigames.utils.BaseSphere;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.methods.Mod;
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
		obj = board.registerNewObjective("Scoreboard", "lasertag", "�b�lLasertag");
		obj.setDisplayName("�b�lLasertag");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		long time = session.getTime(TimeFormat.SECONDS);
		if(session.tagging()) time++;
		if(time < 3600) obj.getScore("�eTime:  �c�l"+Utils.getTimeFormatFromLong(time, "m")).setScore(0);
		else obj.getScore("�eTime:  �c�l"+Utils.getTimeFormatFromLong(time, "h")).setScore(0);
		obj.getScore(" ").setScore(1);

		int i = 2;
		boolean limit = (session.getTeamsAmount() > 3 | (session.isSolo() && session.getPlayers().length < 7));

		int votes = 0;
		if(!session.isMapNull()){
			if(session.votingMap()) {
				if (session.getTeamsAmount() < 3 && session.getPlayers().length < 10) {
					for(int j = Map.MAPS.size(); j-- > 0;) {
						Map m = Map.MAPS.get(j);
						if(session.mapVotes.get(m) > 0 && j == Map.MAPS.size()-1) {
							obj.getScore("  �n�6"+m.getName()+": �7(�a"+session.mapVotes.get(m)+"�7)").setScore(i++);
							votes++;
						}
						else if(session.mapVotes.get(m) > 0) {
							obj.getScore("  �6"+m.getName()+": �7(�a"+session.mapVotes.get(m)+"�7)").setScore(i++);
							votes++;
						}
					}
				}
				obj.getScore("�eMap:  �o�aVoting...").setScore(i++);
			} else {
				obj.getScore("�eMap:  �r�b"+session.getMap().getName()).setScore(i++);
			}
			if(session.votingMap() && votes > 0 && session.getTeamsAmount() < 3 && session.getPlayers().length < 10){
				/*if(session.withCaptureTheFlag() | session.withMultiweapons()) */obj.getScore("  ").setScore(i++);
			} else {
				if(session.isSolo() && !session.withCaptureTheFlag() && !session.withMultiweapons()) obj.getScore("  ").setScore(i++);
			}
		}

		if(session.withCaptureTheFlag() | session.withMultiweapons() | session.withGrenades()){
			String text = "�eModes: ";
			if(session.withMultiweapons()) text += (session.withCaptureTheFlag() | session.withGrenades()? "�bMW":"�bMultiWeapons");
			if(session.withCaptureTheFlag()) text += (!text.equals("�eModes: ") ?"�7, ":"")+"�bCTF";
			if(session.withGrenades()) text += (!text.equals("�eModes: ") ?"�7, ":"")+"�bGrenades";
			obj.getScore(text).setScore(i++);
			if(session.isSolo()) obj.getScore("        ").setScore(i++);
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
				int pp = session.getPlayerPoints(p);
				obj.getScore(((playersInSorted.get(maxScore).contains(p))?"�n":"")+session.getPlayerColor(p).getChatColor()+p.getName()+" �7(�a"+pp+"�7)  ").setScore(i++);
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
				try {
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

					if (session.getTeamsAmount()< 4) {
						for(Player p : sortedInRanks) {
							Lasertag.LasertagColor color = session.getPlayerColor(p);
							ChatColor chatColor = color.getChatColor();
							int points = session.getPlayerPoints(p);
							obj.getScore("   "+chatColor+p.getName()+" �7(�a"+points+"�7)  ").setScore(i++);
//							obj.getScore("   "+session.getPlayerColor(p).getChatColor()+p.getName()+" �7(�a"+session.getPlayerPoints(p)+"�7)  ").setScore(i++);
						}
					}
					obj.getScore(session.getTeamColor(team).getChatColor()+session.getTeamColor(team).name()+" Team �7(�a"+session.getTeamPoints(team)+"�7)  ").setScore(i++);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		obj.getScore("   ").setScore(i);
		if(i > 14) {
			i++;
			if(time < 3600) obj.getScore("�eTime:  �c�l"+Utils.getTimeFormatFromLong(time, "m")).setScore(i++);
			else obj.getScore("�eTime:  �c�l"+Utils.getTimeFormatFromLong(time, "h")).setScore(i++);
			obj.getScore(" ").setScore(i);
		}
		for(Player p : session.getPlayers()) {
			p.setScoreboard(board);
			if(session.getBooleanMod(Mod.HIGHLIGHT_PLAYERS) && session.tagging()) p.setGlowing(true);
			if (session.tagging()) {
				if(Lasertag.isPlayerProtected(p)) BaseSphere.drawPlayerProtectionSphere(p);
				session.getMap().checkPlayerPosition(p);
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
			
			StringBuilder name = new StringBuilder("�6" + m.getName() + ":");
			while(difference > 0) {
				name.append(" ");
				difference--;
			}
			list.add("  �6"+name+" �a"+session.mapVotes.get(m));
		}
		return list.toArray(new String[0]);
	}
}

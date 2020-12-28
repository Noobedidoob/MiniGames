package me.noobedidoob.minigames.lasertag.methods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

import me.noobedidoob.minigames.lasertag.Lasertag.LtColorNames;
import me.noobedidoob.minigames.lasertag.listeners.DeathListener;
import me.noobedidoob.minigames.lasertag.commands.ModifierCommands.Mod;
import me.noobedidoob.minigames.utils.LasertagColor;
import me.noobedidoob.minigames.utils.Map;

public class Game {
	
	public Game() {
		map = null;
		solo = true;
		waiting = false;
		tagging = false;
//		testing = false;
		playerColor = new HashMap<Player, LasertagColor>();
		playerPoints = new HashMap<Player, Integer>();
		playerTeam = new HashMap<Player, Player[]>();
		teamColor = new HashMap<Player[], LasertagColor>();
		teamPoints = new HashMap<Player[], Integer>();
		teams = new ArrayList<Player[]>();
	}
	
	private static Map map = null;
	private static boolean solo = true;
	private static boolean capturing = false;
	private static boolean waiting = false;
	private static boolean tagging = false;
	private static Player[] players;
	
	private static HashMap<Player, LasertagColor> playerColor = new HashMap<Player, LasertagColor>();
	private static HashMap<Player, Integer> playerPoints = new HashMap<Player, Integer>();
	
	private static HashMap<Player, Player[]> playerTeam = new HashMap<Player, Player[]>();
	private static HashMap<Player[], LasertagColor> teamColor = new HashMap<Player[], LasertagColor>();
	private static HashMap<Player[], Integer> teamPoints = new HashMap<Player[], Integer>();
	private static List<Player[]> teams = new ArrayList<Player[]>();
	public  static boolean spawnAtBases;
	
	public static void register(boolean solo, Map map, Player[] players, boolean capturing) {
		Game.solo = solo;
		if(!solo && capturing) Game.capturing = true; 
		Game.map = map;
		Game.players = players;
		waiting = true;
		
		for(Player p : players) {
			playerPoints.put(p, 0);
		}
	}
	public static void setPlayerColor(Player p, LtColorNames colorName) {
		playerColor.put(p, new LasertagColor(colorName));
	}
	public static LasertagColor getPlayerColor(Player p) {
		return playerColor.get(p);
	}
	public static int getPlayerPoints(Player p) {
		return playerPoints.get(p);
	}
	public static void start() {
		tagging = true;
		waiting = false;
	}
	
	public static boolean solo() {
		return solo;
	}
	public static boolean teams() {
		return !solo;
	}
	public static boolean capturing() {
		return capturing;
	}
	public static boolean waiting() {
		return waiting;
	}
	public static boolean tagging() {
		return tagging;
	}
//	public static boolean testing() {
//		return testing;
//	}
//	public static void enableTesting() {
//		testing = true;
//	}
//	public static void disableTesting() {
//		testing = false;
//	}
	
	public static Player[] players() {
		return players;
	}
	public static void addDisconnectedPlayer() {
		List<Player> playerList = Arrays.asList(players);
		Player[] playerArray = new Player[playerList.size()];
		players = playerList.toArray(playerArray);
	}
	public static Map map() {
		return map;
	}
	
	public static void addTeam(Player[] team, LtColorNames colorName) {
		teams.add(team);
		teamPoints.put(team, 0);
		teamColor.put(team, new LasertagColor(colorName));
		for(Player p : team) {
			playerTeam.put(p, team);
			playerColor.put(p, new LasertagColor(colorName));
		}
	}
	public static List<Player[]> getTeams(){
		return teams;
	}
	public static LasertagColor getTeamColor(Player[] team) {
		return teamColor.get(team);
	}
	public static int getTeamPoints(Player[] team) {
		return teamPoints.get(team);
	}
	public static Player[] getPlayerTeam(Player p) {
		return playerTeam.get(p);
	}
	
	public static void addPoints(Player p, int points) {
		playerPoints.put(p, playerPoints.get(p)+points);
		if(!solo) teamPoints.put(getPlayerTeam(p), teamPoints.get(getPlayerTeam(p))+points);
	}
	
	public static void reset() {
		map = null;
		solo = true;
		waiting = false;
		tagging = false;
//		testing = false;
		for(Player p : players) DeathListener.streakedPlayers.put(p, 0);
		players = new Player[] {};
		
		playerColor = new HashMap<Player, LasertagColor>();
		playerPoints = new HashMap<Player, Integer>();
		
		teamColor = new HashMap<Player[], LasertagColor>();
		teamPoints = new HashMap<Player[], Integer>();
		teams = new ArrayList<Player[]>();
		Mod.resetMods();
	}
	
	public static boolean isInGame(Player p) {
		if(Game.tagging) {
			for(Player igp : players) if(igp == p) return true;
		}
		return false;
	}
	public static boolean isFromTeam(Player p1, Player p2) {
		if (teams()) {
			for (Player[] team : Game.getTeams()) {
				boolean pInTeam = false;
				for (Player tp : team) {
					if (tp == p1) pInTeam = true;
					if (pInTeam) {
						for (Player thp : team) {
							if (thp == p2) return true;
						}
					}
				}
			}
		}
		return false;
	}
}

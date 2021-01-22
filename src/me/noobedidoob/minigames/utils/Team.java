package me.noobedidoob.minigames.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

import me.noobedidoob.minigames.utils.LasertagColor.LtColorNames;

public class Team {
	
	private LtColorNames colorName;
	private List<Player> players = new ArrayList<Player>();
	private int points;
	
	
	public Team(LtColorNames colorName) {
		new Team(colorName, new Player[] {});
	}
	public Team(LtColorNames colorName, Player[] players) {
		this.colorName = colorName;
		for(Player p : players) {
			this.players.add(p);
		}
		points = 0;
		for(Player p : players) {
			playerTeam.put(p, this);
		}
	}
	
	public Player[] getPlayers() {
		return players.toArray(new Player[players.size()]);
	}
	
	public void addPlayer(Player p) {
		players.add(p);
		playerTeam.put(p, this);
		
	}
	public void removePlayer(Player p) {
		players.remove(p);
		playerTeam.put(p, null);
	}
	
	public LtColorNames getColorName() {
		return colorName;
	}
	public LasertagColor getLasertagColor() {
		return new LasertagColor(colorName);
	}
	public void setColor(LtColorNames colorName) {
		this.colorName = colorName;
	}
	
	public int getPoints() {
		return points;
	}
	public void addPoints(int amount) {
		points += amount;
	}
	public void setPoints(int amount) {
		points = amount;
	}
	public void resetPoints() {
		points = 0;
	}
	
	public boolean isInTeam(Player p) {
		return (players.contains(p));
	}
	
	
	
	private static HashMap<Player, Team> playerTeam = new HashMap<Player, Team>();
	public static Team getPlayerTeam(Player p) {
		return playerTeam.get(p);
	}
}

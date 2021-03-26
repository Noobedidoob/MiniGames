package me.noobedidoob.minigames.lasertag.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.noobedidoob.minigames.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.noobedidoob.minigames.lasertag.Lasertag.LasertagColor;

public class SessionTeam {
	
	private LasertagColor lasertagColor;
	private final List<Player> players = new ArrayList<>();
	private int points;
	private org.bukkit.scoreboard.Team scoreboardTeam;
	
	private Session session;
	public SessionTeam(Session session, LasertagColor colorName) {
		new SessionTeam(session, colorName, new Player[] {});
	}
	public SessionTeam(Session session, LasertagColor colorName, Player... players) {
		this.session = session;
		this.lasertagColor = colorName;
		points = 0;
		if(session.scoreboard.board.getTeam(colorName.name()) != null) session.scoreboard.board.getTeam(colorName.name()).unregister();
		scoreboardTeam = session.scoreboard.board.registerNewTeam(colorName.name());
		scoreboardTeam.setColor(colorName.getChatColor());
		for(Player p : players) {
			addPlayer(p);
		}
		for(Player p : players) {
			playerTeam.put(p, this);
		}
		session.refreshScoreboard();
	}
	
	public Player[] getPlayers() {
		return players.toArray(new Player[0]);
	}
	
	public void addPlayer(Player p) {
		if (!players.contains(p)) {
			players.add(p);
			playerTeam.put(p, this);
			scoreboardTeam.addEntry(p.getName());
			session.setPlayerColor(p,lasertagColor);
			p.getInventory().setChestplate(Utils.getLeatherArmorItem(Material.LEATHER_CHESTPLATE, lasertagColor.getChatColor()+lasertagColor.getName()+" team armor", lasertagColor.getColor()));
			p.getInventory().setLeggings(Utils.getLeatherArmorItem(Material.LEATHER_LEGGINGS, lasertagColor.getChatColor()+lasertagColor.getName()+" team armor", lasertagColor.getColor()));
			p.getInventory().setBoots(Utils.getLeatherArmorItem(Material.LEATHER_BOOTS, lasertagColor.getChatColor()+lasertagColor.getName()+" team armor", lasertagColor.getColor()));
		}
		
	}
	public void removePlayer(Player p) {
		if (players.contains(p)) {
			players.remove(p);
			playerTeam.put(p, null);
			try {
				scoreboardTeam.removeEntry(p.getName());
			} catch (Exception ignored){
			}
			p.getInventory().setChestplate(new ItemStack(Material.AIR));
			p.getInventory().setLeggings(new ItemStack(Material.AIR));
			p.getInventory().setBoots(new ItemStack(Material.AIR));
		}
	}
	
	public LasertagColor getLasertagColor() {
		return lasertagColor;
	}
	public Color getColor() {
		return lasertagColor.getColor();
	}
	public ChatColor getChatColor() {
		return lasertagColor.getChatColor();
	}
	public void setColor(LasertagColor colorName) {
		this.lasertagColor = colorName;
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
	
	public org.bukkit.scoreboard.Team getScoreboardTeam(){
		return scoreboardTeam;
	}
	
	
	public static SessionTeam getTeamByChooserSlot(int slot, Session session){
		for(SessionTeam team : session.getTeams()){
			if(team.getTeamChooserSlot() == slot) return team;
		}
		return null;
	}
	public int getTeamChooserSlot() {
		if(session.getTeamsAmount() > 4) return lasertagColor.ordinal();
		else {
			return (lasertagColor.ordinal()*2)+1;
		}
	}
	public ItemStack getTeamChooser() {
		List<String> lore = new ArrayList<>();
		for(Player tp : getPlayers()) {
			lore.add(getChatColor()+""+tp.getName());
		}
		return Utils.getLeatherArmorItem(Material.LEATHER_CHESTPLATE,getChatColor()+""+ getLasertagColor()+" Team", lasertagColor.getColor(), lore);
	}


	private static final HashMap<Player, SessionTeam> playerTeam = new HashMap<>();
	public static SessionTeam getPlayerTeam(Player p) {
		return playerTeam.get(p);
	}
}

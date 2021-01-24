package me.noobedidoob.minigames.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import me.noobedidoob.minigames.lasertag.session.Session;
import me.noobedidoob.minigames.utils.LasertagColor.LtColorNames;

public class Team {
	
	private LtColorNames colorName;
	private List<Player> players = new ArrayList<Player>();
	private int points;
	
	private Session session;
	public Team(Session session, LtColorNames colorName) {
		new Team(session, colorName, new Player[] {});
	}
	public Team(Session session, LtColorNames colorName, Player[] players) {
		this.session = session;
		this.colorName = colorName;
		for(Player p : players) {
			this.players.add(p);
		}
		points = 0;
		for(Player p : players) {
			playerTeam.put(p, this);
		}
		getTeamByCooserSlot.put(getTeamChooserSlot(), this);
	}
	
	public Player[] getPlayers() {
		return players.toArray(new Player[players.size()]);
	}
	
	public void addPlayer(Player p) {
		if (!players.contains(p)) {
			players.add(p);
			playerTeam.put(p, this);
		}
		
	}
	public void removePlayer(Player p) {
		if (players.contains(p)) {
			players.remove(p);
			playerTeam.put(p, null);
		}
	}
	
	public LtColorNames getColorName() {
		return colorName;
	}
	public LasertagColor getLasertagColor() {
		return new LasertagColor(colorName);
	}
	public Color getColor() {
		return colorName.getColor();
	}
	public ChatColor getChatColor() {
		return colorName.getChatColor();
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
	
	
	public static HashMap<Integer, Team> getTeamByCooserSlot = new HashMap<>();
	public int getTeamChooserSlot() {
		if(session.getTeamsAmount() > 4) return colorName.ordinal();
		else {
			return ((colorName.ordinal()*2)+1)+18;
		}
	}
	public ItemStack getTeamChooser() {
		ItemStack item = new ItemStack(Material.LEATHER_CHESTPLATE);
		LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.setColor(getColor());
		meta.setDisplayName(getChatColor()+""+getColorName()+" Team");
		List<String> lore = new ArrayList<String>();
		for(Player tp : getPlayers()) {
			lore.add(getChatColor()+""+tp.getName());
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	
	private static HashMap<Player, Team> playerTeam = new HashMap<Player, Team>();
	public static Team getPlayerTeam(Player p) {
		return playerTeam.get(p);
	}
}

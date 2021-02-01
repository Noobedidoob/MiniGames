package me.noobedidoob.minigames.lasertag.session;

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

import me.noobedidoob.minigames.lasertag.Lasertag.LasertagColor;

public class SessionTeam {
	
	private LasertagColor colorName;
	private List<Player> players = new ArrayList<Player>();
	private int points;
	private org.bukkit.scoreboard.Team scoreboardTeam;
	
	private Session session;
	public SessionTeam(Session session, LasertagColor colorName) {
		new SessionTeam(session, colorName, new Player[] {});
	}
	public SessionTeam(Session session, LasertagColor colorName, Player... players) {
		this.session = session;
		this.colorName = colorName;
		points = 0;
		getTeamByCooserSlot.put(getTeamChooserSlot(), this);
		scoreboardTeam = session.scoreboard.board.registerNewTeam(colorName.name());
		scoreboardTeam.setColor(colorName.getChatColor());

		for(Player p : players) {
			addPlayer(p);
		}
		for(Player p : players) {
			playerTeam.put(p, this);
		}
	}
	
	public Player[] getPlayers() {
		return players.toArray(new Player[players.size()]);
	}
	
	public void addPlayer(Player p) {
		if (!players.contains(p)) {
			System.out.println("Adding player! "+colorName.name());
			players.add(p);
			playerTeam.put(p, this);
			scoreboardTeam.addEntry(p.getName());
			
			ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
			ItemStack leggins = new ItemStack(Material.LEATHER_LEGGINGS);
			ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
			LeatherArmorMeta armourItemMeta = (LeatherArmorMeta) chestplate.getItemMeta();
			armourItemMeta.setUnbreakable(true);
			armourItemMeta.setColor(colorName.getColor());
			chestplate.setItemMeta(armourItemMeta);
			leggins.setItemMeta(armourItemMeta);
			boots.setItemMeta(armourItemMeta);
			p.getInventory().setChestplate(chestplate);
			p.getInventory().setLeggings(leggins);
			p.getInventory().setBoots(boots);
		}
		
	}
	public void removePlayer(Player p) {
		if (players.contains(p)) {
			players.remove(p);
			playerTeam.put(p, null);
			scoreboardTeam.removeEntry(p.getName());
			p.getInventory().setChestplate(new ItemStack(Material.AIR));
			p.getInventory().setLeggings(new ItemStack(Material.AIR));
			p.getInventory().setBoots(new ItemStack(Material.AIR));
		}
	}
	
	public LasertagColor getColorName() {
		return colorName;
	}
	public Color getColor() {
		return colorName.getColor();
	}
	public ChatColor getChatColor() {
		return colorName.getChatColor();
	}
	public void setColor(LasertagColor colorName) {
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
	
	public org.bukkit.scoreboard.Team getScoreboardTeam(){
		return scoreboardTeam;
	}
	
	
	public static HashMap<Integer, SessionTeam> getTeamByCooserSlot = new HashMap<>();
	public int getTeamChooserSlot() {
		if(session.getTeamsAmount() > 4) return colorName.ordinal();
		else {
			return (colorName.ordinal()*2)+1;
		}
	}
	public ItemStack getTeamChooser() {
		ItemStack item = new ItemStack(Material.LEATHER_CHESTPLATE);
		LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE);
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
	
	
	private static HashMap<Player, SessionTeam> playerTeam = new HashMap<Player, SessionTeam>();
	public static SessionTeam getPlayerTeam(Player p) {
		return playerTeam.get(p);
	}
}

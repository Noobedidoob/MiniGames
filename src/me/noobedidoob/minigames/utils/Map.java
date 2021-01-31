package me.noobedidoob.minigames.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class Map {
	
	public static List<Map> maps = new ArrayList<Map>(); 
	
	//覧覧覧覧覧覧覧擁n constructor覧覧覧覧覧覧覧覧�//
	private String name;
	
	private Coordinate centerCoord;
	private Area area;
	
	private World world;
	//覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧//
	
	//覧覧覧覧覧outside of constructor覧覧覧覧覧覧覧//
	private boolean withMiniguns = false;
	private boolean randomSpawn = false;
	private boolean baseSpawn = false;
	
	private int baseAmount = 0;
	
	private Coordinate[] minigunCoords = null;
	private HashMap<ChatColor, Coordinate> teamSpawCoords = new HashMap<ChatColor, Coordinate>();
	private HashMap<ChatColor, Boolean> hasColor = new HashMap<ChatColor, Boolean>();
	public List<Coordinate> baseCoords = new ArrayList<Coordinate>();
	public HashMap<Coordinate, ChatColor> baseColor = new HashMap<Coordinate, ChatColor>();
	
	private int protectionRaduis;
	public boolean approved; 
	//覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧//
	
	public Map(String name, Coordinate centerCoordinate, Area area, /*Area gatherArea, */ World world) {
		maps.add(this);
		this.name = name.substring(0, 1).toUpperCase()+name.substring(1);
		this.centerCoord = centerCoordinate;
		this.area = area;
		this.world = world;
		
		for(ChatColor color : ChatColor.values()) hasColor.put(color, false);
	}
	
	
	
	public Coordinate getCenterCoord() {
		return centerCoord;
	}
	
	public Area getArea() {
		return area;
	}

	
	

	public void withRandomSpawn(boolean value) {
		this.randomSpawn = value;
	}
	public boolean withRandomSpawn() {
		return this.randomSpawn;
	}


	public void withMiniguns(boolean value) {
		this.withMiniguns = value;
	}
	public boolean withMiniguns() {
		return this.withMiniguns;
	}
	public void setMinigunCoords(Coordinate...coordinates) {
		this.minigunCoords = coordinates;
	}
	public Coordinate[] getMinigunCoords() {
		return this.minigunCoords;
	}
	
	
	
	
	public void withBaseSpawn(boolean value) {
		this.baseSpawn = value;
	}
	public boolean withBaseSpawn() {
		return this.baseSpawn;
	}
	public void setTeamSpawnCoords(ChatColor color, Coordinate coordinate) {
		this.teamSpawCoords.put(color, coordinate);
		this.hasColor.put(color, true);
		int amount = 0;
		for(ChatColor c : ChatColor.values()) {
			if(this.hasColor(c)) amount++;
		}
		this.baseAmount = amount;
		
		baseCoords.add(coordinate);
		baseColor.put(coordinate, color);
	}
	public boolean hasColor(ChatColor color) {
		return this.hasColor.get(color);
	}
	public Coordinate getTeamSpawnCoord(ChatColor color) {
		if(hasColor(color)) return teamSpawCoords.get(color);
		else return centerCoord;
	}
	
	public int getBaseAmount() {
		return this.baseAmount;
	}
	
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return this.name;
	}
	
	
	
	public int getProtectionRaduis() {
		return protectionRaduis;
	}
	public void setProtectionRaduis(int protectionRaduis) {
		this.protectionRaduis = protectionRaduis;
	}



	public Location getRandomSpawnLocation() {
		int x = (int) (Math.random()*((area.getMaxX()-area.getMinX())+1))+area.getMinX();
		int z = (int) (Math.random()*((area.getMaxZ()-area.getMinZ())+1))+area.getMinZ();
		int y = world.getHighestBlockYAt(x, z)+1;
		if(world.getBlockAt(x, y, z).getType() != Material.AIR | world.getBlockAt(x, y+1, z).getType() != Material.AIR) return getRandomGatherLocation();
		if(y > area.getMaxY()-2) {
//			for(int i = y; i > area.getMinY(); i++) {
//				if(!world.getBlockAt(x, y, z).getType().isAir()) {
//					y = i+1;
//					i = area.getMinY()-1;
//				} else y = i;
//			}
			y = area.getMaxY()-2;
		}
		Location loc = new Location(world, x, y, z);
		return loc;
	}
	
	public Location getRandomGatherLocation() {
		int x = (int) (Math.random()*((area.getMaxX()-area.getMinX())+1))+area.getMinX();
		int z = (int) (Math.random()*((area.getMaxZ()-area.getMinZ())+1))+area.getMinZ();
		int y = world.getHighestBlockYAt(x, z);
		if(y > area.getMaxY()-2) {
			for(int i = y; i > area.getMinY(); i++) {
				if(!world.getBlockAt(x, y, z).getType().isAir()) {
					y = i+1;
					i = area.getMinY()-1;
				} else y = i;
			}
		}
		Location loc = new Location(world, x, y, z);
		return loc;
	}
	
	public Location getTeamSpawnLoc(ChatColor color) {
		if(hasColor(color)) return teamSpawCoords.get(color).getLocation(world);
		else return centerCoord.getLocation(world);
	}
}

package me.noobedidoob.minigames.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import me.noobedidoob.minigames.lasertag.Lasertag.LasertagColor;

public class Map {
	
	public static List<Map> maps = new ArrayList<Map>(); 
	
	private String name;
	
	private Coordinate centerCoord;
	private Area area;
	
	private World world;
	
	private boolean randomSpawn = false;
	private boolean baseSpawn = false;
	
	private int baseAmount = 0;
	
	private HashMap<LasertagColor, Coordinate> teamSpawCoords = new HashMap<LasertagColor, Coordinate>();
	private HashMap<LasertagColor, Boolean> hasColor = new HashMap<LasertagColor, Boolean>();
	public List<Coordinate> baseCoords = new ArrayList<Coordinate>();
	public HashMap<Coordinate, LasertagColor> baseColor = new HashMap<Coordinate, LasertagColor>();
	public HashMap<LasertagColor, BaseSphere> baseSphere = new HashMap<LasertagColor, BaseSphere>();
	
	private int protectionRaduis;
	
	public Map(String name, Coordinate centerCoordinate, Area area, /*Area gatherArea, */ World world) {
		maps.add(this);
		this.name = name.substring(0, 1).toUpperCase()+name.substring(1);
		this.centerCoord = centerCoordinate;
		this.area = area;
		this.world = world;
		
		for(LasertagColor color : LasertagColor.values()) hasColor.put(color, false);
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


	public void withBaseSpawn(boolean value) {
		this.baseSpawn = value;
	}
	public boolean withBaseSpawn() {
		return this.baseSpawn;
	}
	
	public void setTeamSpawnCoords(LasertagColor color, Coordinate coordinate) {
		this.teamSpawCoords.put(color, coordinate);
		this.hasColor.put(color, true);
		int amount = 0;
		for(LasertagColor c : LasertagColor.values()) {
			if(this.hasColor(c)) amount++;
		}
		this.baseAmount = amount;
		
		baseCoords.add(coordinate);
		baseColor.put(coordinate, color);
	}
	public Coordinate[] getBaseCoords() {
		return baseCoords.toArray(new Coordinate[baseCoords.size()]);
	}
	public boolean hasColor(LasertagColor color) {
		return this.hasColor.get(color);
	}
	public Coordinate getTeamSpawnCoord(LasertagColor color) {
		if(hasColor(color)) return teamSpawCoords.get(color);
		else return centerCoord;
	}
	public void drawBaseSphere(LasertagColor color, Player... players) {
		baseSphere.get(color).draw(players);
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
		
		for(Coordinate coord : baseCoords) {
			baseSphere.put(baseColor.get(coord), new BaseSphere(coord, protectionRaduis, baseColor.get(coord).getColor(), area));
		}
	}



	public Location getRandomSpawnLocation() {
		int x = (int) (Math.random()*(((area.getMaxX()-1)-(area.getMinX()+1))+1))+area.getMinX();
		int z = (int) (Math.random()*(((area.getMaxZ()-1)-(area.getMinZ()+1))+1))+area.getMinZ();
		for(int i = area.getMinY(); i < area.getMaxY(); i++) {
			if(world.getBlockAt(x, i, z).getType().isAir() && world.getBlockAt(x, i+1, z).getType().isAir()) return new Location(world, x, i, z);
		}
		return getRandomSpawnLocation();
	}
	
	
	public Location getTeamSpawnLoc(LasertagColor color) {
		if(hasColor(color)) return teamSpawCoords.get(color).getLocation(world);
		else return centerCoord.getLocation(world);
	}
	
	
	private boolean used = false;
	public void setUsed(boolean used) {
		this.used = used;
	}
	public boolean isUsed() {
		return used;
	}
	
	private boolean enabled = true;
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public boolean isEnabled() {
		return enabled;
	}
	
}

package me.noobedidoob.minigames.utils;

import org.bukkit.Location;

public class Area {
	
	private int maxX;
	private int minX;
	private int maxY;
	private int minY;
	private int maxZ;
	private int minZ;
	
	private Coordinate coord1;
	private Coordinate coord2;
	
	public Area(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		super();
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
		
		this.coord1 = new Coordinate(minX, minY, minZ);
		this.coord2 = new Coordinate(maxX, maxY, maxZ);
	}
	
	public Area(Coordinate coord1, Coordinate coord2) {
		super();
		this.maxX = coord2.getBlockX();
		this.minX = coord1.getBlockX();
		this.maxY = coord2.getBlockY();
		this.minY = coord1.getBlockY();
		this.maxZ = coord2.getBlockZ();
		this.minZ = coord1.getBlockZ();

		this.coord1 = coord1;
		this.coord2 = coord2;
	}
	
	
	public int getMaxX() {
		return maxX;
	}
	public void setMaxX(int maxX) {
		this.maxX = maxX;
	}
	public int getMinX() {
		return minX;
	}
	public void setMinX(int minX) {
		this.minX = minX;
	}
	public int getMaxY() {
		return maxY;
	}
	public void setMaxY(int maxY) {
		this.maxY = maxY;
	}
	public int getMinY() {
		return minY;
	}
	public void setMinY(int minY) {
		this.minY = minY;
	}
	public int getMaxZ() {
		return maxZ;
	}
	public void setMaxZ(int maxZ) {
		this.maxZ = maxZ;
	}
	public int getMinZ() {
		return minZ;
	}
	public void setMinZ(int minZ) {
		this.minZ = minZ;
	}
	
	
	public Coordinate getCoord1() {
		return coord1;
	}
	public void setCoord1(Coordinate coord1) {
		this.coord1 = coord1;
	}
	
	public Coordinate getCoord2() {
		return coord2;
	}
	public void setCoord2(Coordinate coord2) {
		this.coord2 = coord2;
	}


	public int getWidth() {
		int width = maxX - minX;
		if(width < 0) width = width*(-1);
		return width;
	}
	public int getHeight() {
		int height = maxY - minY;
		if(height < 0) height = height*(-1);
		return height;
	}
	public int getDepth() {
		int depth = maxZ - minZ;
		if(depth < 0) depth = depth*(-1);
		return depth;
	}
	
	public int getVolume() {
		return getWidth()*getHeight()*getDepth();
	}
	
	
	
	public Coordinate getRandomCoordinate() {
		int x = (int) (Math.random()*((maxX-minX)+1))+minX;
		int y = (int) (Math.random()*((maxY-minY)+1))+minY;
		int z = (int) (Math.random()*((maxZ-minZ)+1))+minZ;
		return new Coordinate(x, y, z);
	}
	
	public boolean isInside(Location loc) {
		if(loc.getX() >= minX && loc.getX() <= maxX) {
			if(loc.getY() >= minY && loc.getY() <= maxY) {
				return loc.getZ() >= minZ && loc.getZ() <= maxZ;
			}
		}
		return false;
	}
}

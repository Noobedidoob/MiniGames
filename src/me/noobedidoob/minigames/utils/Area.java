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
	
	public Area(int x1, int y1, int z1, int x2, int y2, int z2) {
		super();
		this.minX = Math.min(x1, x2);
		this.minY = Math.min(y1, y2);
		this.minZ = Math.min(z1, z2);
		this.maxX = Math.max(x1, x2);
		this.maxY = Math.max(y1, y2);
		this.maxZ = Math.max(z1, z2);
		
		this.coord1 = new Coordinate(x1, y1, z1);
		this.coord2 = new Coordinate(x2, y2, z2);
	}
	
	public Area(Coordinate coord1, Coordinate coord2) {
		super();
		this.minX = Math.min(coord1.getBlockX(), coord2.getBlockX());
		this.minY = Math.min(coord1.getBlockY(), coord2.getBlockY());
		this.minZ = Math.min(coord1.getBlockZ(), coord2.getBlockZ());
		this.maxX = Math.max(coord1.getBlockX(), coord2.getBlockX());
		this.maxY = Math.max(coord1.getBlockY(), coord2.getBlockY());
		this.maxZ = Math.max(coord1.getBlockZ(), coord2.getBlockZ());

		this.coord1 = new Coordinate(minX, minY, minZ);
		this.coord2 = new Coordinate(maxX, maxY, maxZ);
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


	public int getWidthX() {
		int width = maxX - minX;
		if(width < 0) width = width*(-1);
		return width;
	}
	public int getHeight() {
		int height = maxY - minY;
		if(height < 0) height = height*(-1);
		return height;
	}
	public int getWidthZ() {
		int depth = maxZ - minZ;
		if(depth < 0) depth = depth*(-1);
		return depth;
	}
	
	public int getVolume() {
		return getWidthX()*getHeight()* getWidthZ();
	}
	
	
	
	public Coordinate getRandomCoordinate() {
		int x = (int) (Math.random()*((maxX-minX)+1))+minX;
		int y = (int) (Math.random()*((maxY-minY)+1))+minY;
		int z = (int) (Math.random()*((maxZ-minZ)+1))+minZ;
		return new Coordinate(x, y, z);
	}
	
	public boolean isInside(Location loc) {
		if(minX <= loc.getX() && loc.getX() <= maxX) {
			if(minY <= loc.getY() && loc.getY() <= maxY) {
				return minZ <= loc.getZ() && loc.getZ() <= maxZ;
			}
		}
		return false;
	}
}

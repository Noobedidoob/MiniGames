package me.noobedidoob.minigames.utils;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class HitBox {
	
	private double minX;
	private double minY;
	private double minZ;
	private double maxX;
	private double maxY;
	private double maxZ;
	public HitBox(Entity p) {
		super();
		this.minX = p.getLocation().getX()-(p.getWidth()/2);
		this.minY = p.getLocation().getY();
		this.minZ = p.getLocation().getZ()-(p.getWidth()/2);
		this.maxX = (p.getLocation().getX()-(p.getWidth()/2))+p.getWidth();
		this.maxY = p.getLocation().getY()+p.getHeight()+0.1;
		this.maxZ = (p.getLocation().getZ()-(p.getWidth()/2))+p.getWidth();
	}
	
	
	public boolean isInside(double x, double y, double z) {
		if(minX <= x && x <= maxX) {
			if(minY <= y && y <= maxY) {
				return minZ <= z && z <= maxZ;
			}
		}
		return false;
	}
	public boolean isInside(Location loc) {
		return isInside(loc.getX(), loc.getY(), loc.getZ());
	}
	
	public static boolean isInside(Entity p, Location loc) {
		return new HitBox(p).isInside(loc);
	}
	
	public double getMinX() {
		return minX;
	}
	public void setMinX(double minX) {
		this.minX = minX;
	}
	public double getMinY() {
		return minY;
	}
	public void setMinY(double minY) {
		this.minY = minY;
	}
	public double getMinZ() {
		return minZ;
	}
	public void setMinZ(double minZ) {
		this.minZ = minZ;
	}
	public double getMaxX() {
		return maxX;
	}
	public void setMaxX(double maxX) {
		this.maxX = maxX;
	}
	public double getMaxY() {
		return maxY;
	}
	public void setMaxY(double maxY) {
		this.maxY = maxY;
	}
	public double getMaxZ() {
		return maxZ;
	}
	public void setMaxZ(double maxZ) {
		this.maxZ = maxZ;
	}
	
	
}

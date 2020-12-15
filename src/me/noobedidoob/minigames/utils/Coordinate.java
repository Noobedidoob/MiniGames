package me.noobedidoob.minigames.utils;

import org.bukkit.Location;
import org.bukkit.World;

import me.noobedidoob.minigames.main.Minigames;

public class Coordinate {
	
	private int x;
	private int y;
	private int z;
	
	public Coordinate(int x, int y, int z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public Coordinate(Location loc) {
		super();
		this.x = loc.getBlockX();
		this.y = loc.getBlockY();
		this.z = loc.getBlockZ();
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}
	
	
	public Location getLocation(World world) {
		return new Location(world, x, y, z);
	}
	public Location getLoc() {
		return new Location(Minigames.world, x, y, z);
	}
	
}

package me.noobedidoob.minigames.utils;

import me.noobedidoob.minigames.Minigames;
import org.bukkit.Location;

public class Coordinate {

    Location location;

    public Coordinate(double x, double y, double z) {
        location = new Location(Minigames.INSTANCE.world, x, y, z);
    }

    public Coordinate(Location loc) {
        location = loc;
    }


    public int getBlockX() {
        return location.getBlockX();
    }

    public int getBlockY() {
        return location.getBlockY();
    }

    public int getBlockZ() {
        return location.getBlockZ();
    }


    public double getX() {
        return location.getX();
    }

    public void setX(double x) {
        location.setX(x);
    }

    public double getY() {
        return location.getY();
    }

    public void setY(double y) {
        location.setY(y);
    }

    public double getZ() {
        return location.getZ();
    }

    public void setZ(double z) {
        location.setZ(z);
    }

    public Location getLocation() {
        return location.clone();
    }

}

package me.noobedidoob.minigames.utils;

import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.noobedidoob.minigames.main.Minigames;

public class BaseSphere {
	
	private Coordinate coord;
	private Color color;
	private Area limitedArea;
	private ArrayList<Vector> offsets;
	
	public BaseSphere(Coordinate coord, double radius, Color color, Area mapArea) {
		this.coord = coord;
		
		this.offsets = getSphereOffsets(radius, radius, radius, 0.5f, false);
		
		this.color = color;
		this.limitedArea = mapArea;
	}
	
	
	
	public void draw(Player... players) {
		if(players == null | players.length == 0) {
			for(Vector v : offsets) {
				Location newLoc = new Location(Minigames.world, coord.getX()+v.getX(), coord.getY()+v.getY(), coord.getZ()+v.getZ());
				if(limitedArea.isInside(newLoc)) {
					newLoc.getWorld().spawnParticle(Particle.REDSTONE, newLoc.getX(), newLoc.getY(), newLoc.getZ(), 0, 0, 0, 0, 1, new Particle.DustOptions(color, 1.5f));
				}
			}
		} else {
			for(Player p : players) {
				for(Vector v : offsets) {
					Location newLoc = new Location(Minigames.world, coord.getX()+v.getX(), coord.getY()+v.getY(), coord.getZ()+v.getZ());
					if(limitedArea.isInside(newLoc)) {
						p.spawnParticle(Particle.REDSTONE, newLoc.getX(), newLoc.getY(), newLoc.getZ(), 0, 0, 0, 0, 1, new Particle.DustOptions(color, 1.5f));
					}
				}
			}
		}
	}
	

	private static ArrayList<Vector> getSphereOffsets(double radiusX, double radiusY, double radiusZ, double dotsDistance, boolean filled) {
        ArrayList<Vector> pos = new ArrayList<Vector>();

        radiusX += 0.5;
        radiusY += 0.5;
        radiusZ += 0.5;

        final double invRadiusX = 1 / radiusX;
        final double invRadiusY = 1 / radiusY;
        final double invRadiusZ = 1 / radiusZ;

        final double ceilRadiusX = Math.ceil(radiusX);
        final double ceilRadiusY = Math.ceil(radiusY);
        final double ceilRadiusZ = Math.ceil(radiusZ);

        double nextXn = 0;
        forX: for (double x = 0; x <= ceilRadiusX; x += dotsDistance) {
            final double xn = nextXn;
            nextXn = (x + dotsDistance) * invRadiusX;
            double nextYn = 0;
            forY: for (double y = 0; y <= ceilRadiusY; y += dotsDistance) {
                final double yn = nextYn;
                nextYn = (y + dotsDistance) * invRadiusY;
                double nextZn = 0;
                forZ: for (double z = 0; z <= ceilRadiusZ; z += dotsDistance) {
                    final double zn = nextZn;
                    nextZn = (z + dotsDistance) * invRadiusZ;
                    double distanceSq = lengthSq(xn, yn, zn);
                    if (distanceSq > 1) {
                        if (z == 0) {
                            if (y == 0) {
                                break forX;
                            }
                            break forY;
                        }
                        break forZ;
                    }

                    if (!filled) {
                        if (lengthSq(nextXn, yn, zn) <= 1 && lengthSq(xn, nextYn, zn) <= 1 && lengthSq(xn, yn, nextZn) <= 1) {
                            continue;
                        }
                    }

                    pos.add(new Vector(x, y, z));
                    pos.add(new Vector(-x, y, z));
                    pos.add(new Vector(x, -y, z));
                    pos.add(new Vector(x, y, -z));
                    pos.add(new Vector(-x, -y, z));
                    pos.add(new Vector(x, -y, -z));
                    pos.add(new Vector(-x, y, -z));
                    pos.add(new Vector(-x, -y, -z));
                }
            }
        }
        return pos;
    }

	private static double lengthSq(double x, double y, double z) {
        return (x * x) + (y * y) + (z * z);
    }
	
	
	
	public static void drawProtSphere(Location loc, double radius, double dotsDistance, float dotsSize, Color color) {
		ArrayList<Vector> offsets = getSphereOffsets(radius, radius, radius, dotsDistance, false);
		for(Vector v : offsets) {
			loc.getWorld().spawnParticle(Particle.COMPOSTER, loc.getX()+v.getX(), loc.getY()+v.getY(), loc.getZ()+v.getZ(), 0, 0, 0, 0, 1);
		}
	}


}

package me.noobedidoob.minigames.utils;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.noobedidoob.minigames.lasertag.session.Session;
import me.noobedidoob.minigames.Minigames;

public class BaseSphere {
	
	private final Coordinate coord;
	private final Color color;
	private final Area limitedArea;
	private final ArrayList<Vector> offsets;
	private final Minigames minigames = Minigames.INSTANCE;
	
	public BaseSphere(Coordinate coord, double radius, Color color, Area mapArea) {
		this.coord = coord;
		
		this.offsets = getSphereOffsets(radius, 50);
		
		this.color = color;
		this.limitedArea = mapArea;
	}
	
	
	private boolean isCooledDown = false;
	public void draw(Player... players) {
	    if(isCooledDown) return;
		if((players != null && players.length == 0) | players == null) {
			for(Vector v : offsets) {
				Location newLoc = new Location(minigames.world, coord.getX()+v.getX(), coord.getY()+v.getY(), coord.getZ()+v.getZ());
				if(limitedArea.isInside(newLoc)) {
					newLoc.getWorld().spawnParticle(Particle.REDSTONE, newLoc.getX(), newLoc.getY(), newLoc.getZ(), 0, 0, 0, 0, 1, new Particle.DustOptions(color, 1.5f));
				}
			}
		} else {
			for(Player p : players) {
				for(Vector v : offsets) {
					Location newLoc = new Location(minigames.world, coord.getX()+v.getX(), coord.getY()+v.getY(), coord.getZ()+v.getZ());
					if(limitedArea.isInside(newLoc)) {
						p.spawnParticle(Particle.REDSTONE, newLoc.getX(), newLoc.getY(), newLoc.getZ(), 0, 0, 0, 0, 1, new Particle.DustOptions(color, 1.5f));
					}
				}
			}
		}
		isCooledDown = true;
		Utils.runLater(()->isCooledDown = false,15);
	}


    public static ArrayList<Vector> getSphereOffsets(double radius, int density){
        return getSphereOffsets(radius,density,density);
    }
    public static ArrayList<Vector> getSphereOffsets(double radius, int circleDensity, int dotsDensity){
        ArrayList<Vector> offsets = new ArrayList<>();
        for(double phi=0; phi<=Math.PI; phi+=Math.PI/circleDensity) {
            double y = radius*Math.cos(phi);
            for(double theta=0; theta<=2*Math.PI; theta+=Math.PI/dotsDensity) {
                double x = radius*Math.cos(theta)*Math.sin(phi);
                double z = radius*Math.sin(theta)*Math.sin(phi);
                offsets.add(new Vector(x,y,z));
            }
        }
        return offsets;
    }

	public static ArrayList<Vector> playerProtSphereOffsets = getSphereOffsets(1.3, 15);
	
	private static final HashMap<Player, Boolean> IS_PLAYER_COOLED_DOWN = new HashMap<>();
	public static void drawPlayerProtectionSphere(Player p) {
	    IS_PLAYER_COOLED_DOWN.putIfAbsent(p,false);
        if (!IS_PLAYER_COOLED_DOWN.get(p)) {
            Color c = Session.getPlayerSession(p) != null ? Session.getPlayerSession(p).getPlayerColor(p).getColor() : Color.RED;
            for(Vector v : playerProtSphereOffsets) {
                p.getLocation().getWorld().spawnParticle(Particle.REDSTONE, p.getLocation().getX()+v.getX(), p.getLocation().getY()+1+v.getY(), p.getLocation().getZ()+v.getZ(), 0, 0, 0, 0, 1, new Particle.DustOptions(c, 1.1f));
            }
            IS_PLAYER_COOLED_DOWN.put(p,true);
            Utils.runLater(()->IS_PLAYER_COOLED_DOWN.put(p,false), 15);
        }
    }


}

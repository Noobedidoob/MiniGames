package me.noobedidoob.minigames.lasertag.methods;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Rotatable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.noobedidoob.minigames.main.Minigames;
import me.noobedidoob.minigames.utils.Coordinate;
import me.noobedidoob.minigames.utils.LasertagColor;

public class Flag implements Listener{
	
	private Coordinate startCoord;
	private Block block;
	private Material flagType;
	private Location oldLoc;
	private Material oldMat;
	private World world;
	
	private Player player;
	private boolean attached = false;
	
	public Flag(Coordinate coord, LasertagColor color) {
		enable(coord, color);
	}
	public Flag(Location loc, LasertagColor color) {
		enable(new Coordinate(loc), color);
	}
	
	private void enable(Coordinate coord, LasertagColor color) {
		PluginManager pluginManeger = Bukkit.getPluginManager();
		pluginManeger.registerEvents(this, Minigames.minigames);
		
		startCoord = coord;
		world = Minigames.world;
		block = world.getBlockAt(coord.getLocation(world));
		oldLoc = coord.getLocation(world);
		oldMat = block.getType();
		flagType = Material.getMaterial(color.getChatColor().name().toUpperCase().replace("GOLD", "ORANGE")+"_BANNER");
		block.setType(flagType);
	}
	
	public void tp(Coordinate coord) {
		tp(coord, BlockFace.NORTH);
	}
	public void tp(Location loc) {
		tp(loc, BlockFace.NORTH);
	}
	
	public void tp(Coordinate coord, BlockFace facing) {
		world.getBlockAt(oldLoc).setType(oldMat);
		block = world.getBlockAt(coord.getLocation(world));
		oldLoc = block.getLocation();
		oldMat = block.getType();
		block.setType(flagType);
		BlockData bd = block.getBlockData();
		Rotatable rot = (Rotatable) bd;
		rot.setRotation(facing);
		block.setBlockData(bd);
	}
	public void tp(Location loc, BlockFace facing) {
		world.getBlockAt(oldLoc).setType(oldMat);
		block = world.getBlockAt(loc);
		oldLoc = block.getLocation();
		oldMat = block.getType();
		block.setType(flagType);
		BlockData bd = block.getBlockData();
		Rotatable rot = (Rotatable) bd;
		rot.setRotation(facing);
		block.setBlockData(bd);
	}
	
	public void tpToPlayer() {
		Location newLoc = player.getLocation().add(0, 0, 0);
		int y = -1;
		while(true) {
			if(newLoc.add(0, 1, 0).getBlock().getType().isAir() && newLoc.add(0, 2, 0).getBlock().getType().isAir() && y++ < 3) {
				newLoc = newLoc.add(0,1,0);
			} else {
				break;
			}
		}
		tp(newLoc, player.getFacing());
	}
	
	public void resetPosition() {
		tp(startCoord);
	}
	
	int repeater;
	public void attachPlayer(Player p) {
		player = p;
		tpToPlayer();
		attached = true;
		repeater = Bukkit.getScheduler().scheduleSyncRepeatingTask(Minigames.minigames, new Runnable() {
			@Override
			public void run() {
				player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 25, 255));
			}
		}, 0, 20);
	}
	public void unattach() {
		if(attached) {
			player = null;
			attached = false;
		}
		Bukkit.getScheduler().cancelTask(repeater);
		try {
			if (player.hasPotionEffect(PotionEffectType.GLOWING)) player.removePotionEffect(PotionEffectType.GLOWING);
		} catch (NullPointerException e) {}
	}
	public boolean isAtSpawn() {
		return (block.getLocation() == startCoord.getLocation(world));
	}
	
	public Location getLocation() {
		return block.getLocation();
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if(attached) {
			if(e.getPlayer().equals(player)) {
//				world.getBlockAt(oldLoc).setType(oldMat);
//				block = world.getBlockAt(player.getLocation().add(0, 3, 0));
//				oldLoc = block.getLocation();
//				oldMat = block.getType();
//				block.setType(flagType);
//				BlockData bd = block.getBlockData();
//				Rotatable rot = (Rotatable) bd;
//				rot.setRotation(player.getFacing());
//				block.setBlockData(bd);
				
				tpToPlayer();
			}
		}
	}

}

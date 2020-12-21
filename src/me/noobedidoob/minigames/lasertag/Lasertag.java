package me.noobedidoob.minigames.lasertag;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import me.noobedidoob.minigames.lasertag.listeners.ClickInventoryListener;
import me.noobedidoob.minigames.lasertag.listeners.DamageListener;
import me.noobedidoob.minigames.lasertag.listeners.DeathListener;
import me.noobedidoob.minigames.lasertag.listeners.DropSwitchItemListener;
import me.noobedidoob.minigames.lasertag.listeners.HitListener;
import me.noobedidoob.minigames.lasertag.listeners.InteractListener;
import me.noobedidoob.minigames.lasertag.listeners.JoinQuitListener;
import me.noobedidoob.minigames.lasertag.listeners.MoveListener;
import me.noobedidoob.minigames.lasertag.listeners.RespawnListener;
import me.noobedidoob.minigames.lasertag.listeners.UndefinedListener;
import me.noobedidoob.minigames.lasertag.methods.Game;
import me.noobedidoob.minigames.lasertag.methods.Modifiers;
import me.noobedidoob.minigames.lasertag.methods.SoloRound;
import me.noobedidoob.minigames.lasertag.methods.TeamsRound;
import me.noobedidoob.minigames.lasertag.methods.Weapons;
import me.noobedidoob.minigames.main.Minigames;
import me.noobedidoob.minigames.utils.Area;
import me.noobedidoob.minigames.utils.Coordinate;
import me.noobedidoob.minigames.utils.Map;
import me.noobedidoob.minigames.utils.MgUtils;

public class Lasertag {
	public static Minigames minigames;
	public Lasertag(Minigames minigames) {
		Lasertag.minigames = minigames;
		
		new Game();
		
		new InteractListener(minigames);
		new ClickInventoryListener(minigames);
		new HitListener(minigames);
		new DeathListener(minigames);
		new MoveListener(minigames);
		new JoinQuitListener(minigames);
		new DamageListener(minigames);
		new DropSwitchItemListener(minigames);
		new RespawnListener(minigames);
		new UndefinedListener(minigames);
	}
	
	
	public void enable() {
		
		for(Player p : Bukkit.getOnlinePlayers()) {
//			Weapons.playerCoolingdown.put(p, false);
//			Weapons.playerAmmo.put(p, Modifiers.bulletsInMagazine);
//			Weapons.playerMinigunAmmo.put(p, Modifiers.minigunAmmo);
//			Weapons.playerMinigunCooldown.put(p, false);
//			Weapons.playerReloading.put(p, false);
			Minigames.minigames.laserCommands.flagIsFollowing.put(p, false);
		}
		
		registerMaps();
		Modifiers.registerModifiers();
		Weapons.registerWeapons();
		Weapons.getTestSet();
	}
	public void disable() {
		try {
			if(Game.tagging()) {
				if(Game.teams()) TeamsRound.stop(true);
				else SoloRound.stop(true);
			}
		} catch (Exception e) {
		}
	}
	
	//TODO: wait for players to be ready
	//TODO: Capture the Flag Mode
	
	//-----------------misc-----------------//
	public static PotionEffectType glowingEffect = PotionEffectType.GLOWING;
	public static int timeCountdownTask;
	public static List<UUID> disconnectedPlayers = new ArrayList<UUID>();
	public static HashMap<Player, Boolean> isProtected = new HashMap<Player, Boolean>();
	public static boolean everybodyReady = false;
	public static Area testArea = new Area(194, 4, -98, 246, 22, -67);
	public static HashMap<Player, Boolean> playerTesting = new HashMap<Player, Boolean>();

	
	public enum LtColorNames {
		Red,
		Blue,
		Green,
		Yellow,
		Purple,
		Gray,
		Orange,
		White
	}
	
	public enum GameType {
		SOLO,
		TEAMS,
		CTF
	}
	
	//-------------------------------------//
	
	
	//---------------Maps------------------//
	
	public static HashMap<String, Map> maps = new HashMap<String, Map>();
	public static List<String> mapNames = new ArrayList<String>();
	
	//-------------------------------------//
	
	public void registerMaps() {
		World world = Minigames.world;
		ConfigurationSection cs = minigames.getConfig().getConfigurationSection("Lasertag.maps");
		int unenabledMaps = 0;
		for(String name : cs.getKeys(false)) {
			mapNames.add(name);
			Coordinate centerCoord = new Coordinate(cs.getInt(name+".center.x"), cs.getInt(name+".center.y"), cs.getInt(name+".center.z"));
			Coordinate coord1 = new Coordinate(cs.getInt(name+".area.x.min"), cs.getInt(name+".area.y.min"), cs.getInt(name+".area.z.min"));
			Coordinate coord2 = new Coordinate(cs.getInt(name+".area.x.max"), cs.getInt(name+".area.y.max"), cs.getInt(name+".area.z.max"));
//			int gatherYMin = coord1.getY();
//			int gatherYMax = coord2.getY();
//			if(cs.isConfigurationSection(name+".gather.y.min") && cs.isConfigurationSection(name+".gather.y.max")) {
//				gatherYMin = cs.getInt(name+".gather.y.min");
//				gatherYMax = cs.getInt(name+".gather.y.max");
//			}
//			Coordinate gatherCoord1 = new Coordinate(cs.getInt(name+".gather.x.min"), gatherYMin, cs.getInt(name+".gather.z.min"));
//			Coordinate gatherCoord2 = new Coordinate(cs.getInt(name+".gather.x.max"), gatherYMax, cs.getInt(name+".gather.z.max"));
			
			Map map = new Map(name, centerCoord, new Area(coord1, coord2), /*new Area(gatherCoord1, gatherCoord2), */world);
			maps.put(name, map);
			
			boolean withRandomSpawn = cs.getBoolean(name+".area.randomspawn");
			map.withRandomSpawn(withRandomSpawn);
			
			boolean withBaseSpawn = cs.getBoolean(name+".basespawn.enabled");
			map.withBaseSpawn(withBaseSpawn);
			
			if(withBaseSpawn) {
				ConfigurationSection subCs = cs.getConfigurationSection(name+".basespawn");
				for(String colorName : subCs.getKeys(false)) {
					if(!colorName.equalsIgnoreCase("enabled") && !colorName.equalsIgnoreCase("protectionradius")) {
						ChatColor baseColor = ChatColor.valueOf(colorName.toUpperCase().replace("ORANGE", "GOLD").replace("PURPLE", "LIGHT_PURPLE"));
						Coordinate baseCoord = new Coordinate(subCs.getInt(colorName+".x"), subCs.getInt(colorName+".y"), subCs.getInt(colorName+".z"));
						map.setTeamSpawnCoords(baseColor, baseCoord);
					}
				}
				map.setProtectionRaduis(cs.getInt(name+".basespawn.protectionradius"));
			}
			
//			boolean withMiniguns = cs.getBoolean(name+".miniguns.enabled");
//			map.withMiniguns(withMiniguns);
//			if(withMiniguns) {
//				List<Coordinate> coordList = new ArrayList<Coordinate>();
//				List<Integer> xList = cs.getIntegerList(name+".miniguns.locations.x");
//				List<Integer> yList = cs.getIntegerList(name+".miniguns.locations.y");
//				List<Integer> zList = cs.getIntegerList(name+".miniguns.locations.z");
//				if(xList.size() == yList.size() && xList.size() == zList.size()) {
//					for(int i = 0; i < xList.size(); i++) {
//						Coordinate coord = new Coordinate(xList.get(i), yList.get(i), zList.get(i));
//						coordList.add(coord);
//					}
//					Coordinate[] coordinates = new Coordinate[xList.size()];
//					coordinates = coordList.toArray(coordinates);
//					map.setMinigunCoords(coordinates);
//				}
//			}
			
			boolean enabled = true;
			if(world == null) System.out.println("WORLD IS NULL!!!");
			String blockName = cs.getString(name+".center.block").toUpperCase().replace(" ", "_");
			if(blockName.equals("!air")) {
				if(world.getBlockAt(centerCoord.getLocation(world).subtract(0, 1, 0)).getType().isAir()) enabled = false;
			} else if(blockName.equals("*")) {
				
			} else if(blockName.contains("*")){
				if(!world.getBlockAt(centerCoord.getLocation(world).subtract(0, 1, 0)).getType().name().contains(blockName)) enabled = false;
			} else {
				if(!world.getBlockAt(centerCoord.getLocation(world).subtract(0, 1, 0)).getType().name().equals(blockName)) enabled = false;
			}
			map.approved = enabled;
			if(!enabled) {
				unenabledMaps++;
				MgUtils.warn("The Map \""+name.substring(0, 1).toUpperCase()+name.substring(1) + "\" could not be approved: ");
				MgUtils.warn("The given block name or criteria '"+blockName+"' from config didn't match with the found block '"+ world.getBlockAt(centerCoord.getLocation(world).subtract(0, 1, 0)).getType().name()+"' at "+centerCoord.getX()+", "+(centerCoord.getY()-1)+", "+centerCoord.getZ());
				MgUtils.warn("The map will still be playable but won't be listed in the tab-complete list.");
			}
		}
		File reloadedBefore = new File(minigames.getDataFolder()+File.pathSeparator+"reloaded.before");
		if(unenabledMaps > cs.getKeys(false).size()/2) {
			if(!reloadedBefore.exists()) {
				MgUtils.informLs("Attempting to reload server due to error while enab");
				try {
					if(reloadedBefore.createNewFile()) Bukkit.reload();
					else {
						MgUtils.warnLs("An error eoccured while creating the reloaded.before file! Therefore it is not posible to reload the server!");
						MgUtils.informLs("Please reload the server manually in order to enable the maps!");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				MgUtils.informLs("Deleting reloaded.before file...");
				if(reloadedBefore.delete()) MgUtils.informLs("Success!");
			}
		} else if(reloadedBefore.exists()) reloadedBefore.delete();
	}
	
	
	
}
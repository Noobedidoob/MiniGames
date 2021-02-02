package me.noobedidoob.minigames.lasertag;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffectType;

import me.noobedidoob.minigames.lasertag.commands.ModifierCommands;
import me.noobedidoob.minigames.lasertag.commands.SessionCommands;
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
import me.noobedidoob.minigames.lasertag.methods.Weapons;
import me.noobedidoob.minigames.lasertag.session.Session;
import me.noobedidoob.minigames.lasertag.session.SessionInventorys;
import me.noobedidoob.minigames.main.Minigames;
import me.noobedidoob.minigames.utils.Area;
import me.noobedidoob.minigames.utils.Coordinate;
import me.noobedidoob.minigames.utils.Map;

public class Lasertag implements Listener{
	public static Minigames minigames;
	public static Lasertag lasertag;
	public static LaserCommands laserCommands;
	public static ModifierCommands modifierCommands;
	public static SessionCommands sessionCommands;
	
	
	public Lasertag(Minigames minigames) {
		Lasertag.minigames = minigames;
		lasertag = this;
		
//		new Game();
		
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
		laserCommands = new LaserCommands(minigames, new ModifierCommands(minigames), new SessionCommands(minigames));
		minigames.getCommand("lasertag").setExecutor(laserCommands);
		minigames.getCommand("lasertag").setTabCompleter(laserCommands);
		Bukkit.getPluginManager().registerEvents(this, minigames);
		
		for(Player p : Bukkit.getOnlinePlayers()) {
			laserCommands.flagIsFollowing.put(p, false);
			p.setExp(1f);
			p.setLevel(0);
			setPlayersLobbyInv(p);
		}
		
		registerMaps();
		Weapons.registerWeapons();
	}
	public void disable() {
		for(Player p : Bukkit.getOnlinePlayers()) {
			p.setWalkSpeed(0.2f);
		}
//		Session.closeAllSessions();
	}
	
	//TODO: wait for players to be ready
	//TODO: Capture the Flag Mode
	
	//-----------------misc-----------------//
	public static PotionEffectType glowingEffect = PotionEffectType.GLOWING;
	public static int timeCountdownTask;
	public static List<UUID> disconnectedPlayers = new ArrayList<UUID>();
	public static HashMap<Player, Boolean> isProtected = new HashMap<Player, Boolean>();
	public static Area testArea = new Area(194, 4, -98, 246, 22, -67);
	public static HashMap<Player, Boolean> playerTesting = new HashMap<Player, Boolean>();

	
	public enum GameType {
		SOLO,
		TEAMS,
		CTF
	}
	
	//-------------------------------------//
	
	
	//---------------Maps------------------//
	
	public static HashMap<String, Map> getMapByName = new HashMap<String, Map>();
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
			
			Map map = new Map(name, centerCoord, new Area(coord1, coord2), /*new Area(gatherCoord1, gatherCoord2), */world);
			getMapByName.put(name, map);
			
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
			if(!enabled) {
				map.setEnabled(false);
				unenabledMaps++;
				warn("The Map \""+name.substring(0, 1).toUpperCase()+name.substring(1) + "\" could not be approved because expected block  '"+blockName+"' didnt match with "
				+world.getBlockAt(centerCoord.getLocation(world).subtract(0, 1, 0)).getType().name()+"' at "+centerCoord.getX()+", "+(centerCoord.getY()-1)+", "+centerCoord.getZ()
				+". The map will still be playable but won't be listed in the tab-complete list.");
			}
		}
		File reloadedBefore = new File(minigames.getDataFolder()+File.pathSeparator+"reloaded.before");
		if(unenabledMaps > (mapNames.size()/2)) {
			if(!reloadedBefore.exists()) {
				inform("Attempting to reload server due to error while enab");
				try {
					if(reloadedBefore.createNewFile()) Bukkit.reload();
					else {
						warn("An error eoccured while creating the reloaded.before file! Therefore it is not posible to reload the server!");
						inform("Please reload the server manually in order to enable the maps!");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				inform("Deleting reloaded.before file...");
				if(reloadedBefore.delete()) inform("Success!");
			}
		} else if(reloadedBefore.exists()) reloadedBefore.delete();
	}
	
	private static int repeater;
	public static void animateExpBar(Player p, long ticks) {
		p.setExp(0);
		repeater = Bukkit.getScheduler().scheduleSyncRepeatingTask(minigames, new Runnable() {
			float funit = 1f/ticks;
			float f = 0f;
			float t = 0;
			@Override
			public void run() {
				if(t < ticks) {
					t++;
					f = f + funit;
					p.setExp(f);
				} else {
					Bukkit.getScheduler().cancelTask(repeater);
					p.setExp(1f);
				}
			}
		}, 0, 1);
	}
	
	public static void setPlayersLobbyInv(Player p) {
		ItemStack find = new ItemStack(Material.COMPASS);
		ItemMeta findMeta = find.getItemMeta();
		findMeta.setDisplayName("§aFind Sessions");
		find.setItemMeta(findMeta);
		p.getInventory().setItem(0, find);
		
		ItemStack create = new ItemStack(Material.NETHER_STAR);
		ItemMeta cMeta = create.getItemMeta();
		cMeta.setDisplayName("§eStart new session");
		create.setItemMeta(cMeta);
		p.getInventory().setItem(1, create);
	}
	public static void openPlayerFindSessionInv(Player p) {
		Inventory inv = Bukkit.createInventory(null, (((Session.getAllSessions().length-1)/9)+1)*9, "§0Join a session:");
		
		int i = 0;
		for(Session session : Session.getAllSessions()) {
			if(!session.tagging()) {
				ItemStack s = new ItemStack(Material.PLAYER_HEAD);
				SkullMeta meta = (SkullMeta) s.getItemMeta();
				meta.setDisplayName("§d"+session.getOwner().getName()+"§a's session");
				meta.setOwningPlayer(session.getOwner());
				List<String> lore = new ArrayList<String> ();
				for(Player a : session.getAdmins()) {
					if(a != session.getOwner()) lore.add("§b"+a.getName());
				}
				for(Player ap : session.getPlayers()) {
					if(!session.isAdmin(ap)) lore.add("§a"+ap.getName());
				}
				meta.setLore(lore);
				s.setItemMeta(meta);
				inv.setItem(i++, s);
			}
		}
		
		p.openInventory(inv);
	}
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if(e.getItem() == null) return;
		if(e.getItem().getType() == Material.COMPASS) {
			openPlayerFindSessionInv(e.getPlayer());
		} else if(e.getItem().getType() == Material.NETHER_STAR) {
			SessionInventorys.openNewSessionInv(e.getPlayer());
		}
	}
	@EventHandler
	public void onPlayerClickInventory(InventoryClickEvent e) {
		try {
			Player p = (Player) e.getWhoClicked();
			if(e.getSlot() < e.getInventory().getSize()+1 && e.getInventory().getItem(e.getSlot()).getType().equals(Material.PLAYER_HEAD) && e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName().contains("session")) {
				String code = e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName().replaceAll("§a", "").replaceAll("§d", "").replaceAll("'s session", "").replaceAll(" ", "");
				Session s = Session.getSessionFromCode(code);
				if(s != null) {
					if(!s.isPlayerBanned((Player) e.getWhoClicked())) {
						if(!s.tagging()) {
							p.closeInventory();
							s.addPlayer((Player) e.getWhoClicked());
						} else Session.sendMessage(p, "§cThe session is already running! Please wait!");
					} else Session.sendMessage(p, "§cYou've been banned from this session! Ask the owner to unban you!");
				} else Session.sendMessage(p, "§cError occured! Couldn't find session!");
			}
		} catch (NullPointerException | ArrayIndexOutOfBoundsException e1) {
		}
	}
	
	
	
	public enum LasertagColor {
		Red(ChatColor.RED, 255, 0, 0),
		Blue(ChatColor.BLUE, 0, 160, 255),
		Green(ChatColor.GREEN, 100, 255, 0),
		Yellow(ChatColor.YELLOW, 255, 255, 0),
		Purple(ChatColor.LIGHT_PURPLE, 150, 0, 255),
		Gray(ChatColor.GRAY, 150, 150, 150),
		Orange(ChatColor.GOLD, 255, 150, 0),
		White(ChatColor.WHITE, 255, 255, 255);
		
		private Color color;
		private ChatColor chatColor;
		
		LasertagColor(ChatColor chatColor, int r, int g, int b) {
			this.chatColor = chatColor;
			this.color = Color.fromRGB(r, g, b);
		}

		public ChatColor getChatColor() {
			return chatColor;
		}
		public Color getColor() {
			return color;
		}
		
		public String getName() {
			return this.name();
		}
		
		public static LasertagColor getFromString(String s) {
			for(LasertagColor name : LasertagColor.values()) {
				if(name.name().equalsIgnoreCase(s)) return name;
			}
			return null;
		}
	}
	
	
	
	public static Logger logger = Bukkit.getLogger();
	public static void inform(String msg) {
		logger.log(Level.INFO, "[LasetTag] "+msg);
	}
	public static void warn(String msg) {
		logger.warning("[LasetTag] "+msg);
	}
	public static void severe(String msg) {
		logger.severe("[LasetTag] "+msg);
	}
	
}
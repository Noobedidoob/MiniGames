package me.noobedidoob.minigames.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;

import me.noobedidoob.minigames.Minigames;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class Utils {
	
	public static Logger logger = Bukkit.getLogger();
	public enum Mode {
		MINIGAMES,
		LASERTAG,
		HIDEANDSEEK
	}

	public static void runLater(Runnable runnable, int delay){
		new BukkitRunnable() {
			@Override
			public void run() {
				runnable.run();
			}
		}.runTaskLater(Minigames.INSTANCE, delay);
	}
	public static void runDefinedRepeater(Runnable r, int delay, int interval, int repeatAmount){
		new BukkitRunnable(){
			int times = 0;
			@Override
			public void run(){
				r.run();
				if(times++ == repeatAmount-1) cancel();
			}
		}.runTaskTimer(Minigames.INSTANCE,delay,interval);
	}


	public static ItemStack getItemStack(Material material, String displayName, ItemFlag... flags) {
		return getItemStack(material, displayName, 1, flags);
	}
	public static ItemStack getItemStack(Material material, String displayName, List<String> lore) {
		return getItemStack(material, displayName, 1, lore);
	}
	public static ItemStack getItemStack(Material material, String displayName, int amount, ItemFlag... flags) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta meta = item.getItemMeta();
		assert meta != null;
		if(displayName != null) meta.setDisplayName(displayName);
		meta.setUnbreakable(true);
		if(flags.length > 0) meta.addItemFlags(flags);
		else meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE, ItemFlag.HIDE_UNBREAKABLE);
		item.setItemMeta(meta);
		return item;
	}
	public static ItemStack getItemStack(Material material, String displayName, Integer amount, List<String> lore, ItemFlag... flags) {
		ItemStack item = new ItemStack(material, amount != null ? amount : 1);
		ItemMeta meta = item.getItemMeta();
		assert meta != null;
		if(displayName != null) meta.setDisplayName(displayName);
		meta.setUnbreakable(true);
		if(lore != null) meta.setLore(lore);
		if(flags.length > 0) meta.addItemFlags(flags);
		else meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(meta);
		return item;
	}
	public static ItemStack getLeatherArmorItem(Material leatherArmorMaterial, String displayName, Color color) {
		return getLeatherArmorItem(leatherArmorMaterial, displayName, color, null, 1);
	}
	public static ItemStack getLeatherArmorItem(Material leatherArmorMaterial, String displayName, Color color, int amount) {
		return getLeatherArmorItem(leatherArmorMaterial, displayName, color, null, amount);
	}
	public static ItemStack getLeatherArmorItem(Material leatherArmorMaterial, String displayName, Color color, List<String> lore) {
		return getLeatherArmorItem(leatherArmorMaterial, displayName, color, lore, 1);
	}
	public static ItemStack getLeatherArmorItem(Material leatherArmorMaterial, String displayName, Color color, List<String> lore, int amount) {
		ItemStack item = new ItemStack(leatherArmorMaterial, amount);
		LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
		assert meta != null;
		if(displayName != null) meta.setDisplayName(displayName);
		meta.setColor(color);
		if(lore != null) meta.setLore(lore);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE, ItemFlag.HIDE_UNBREAKABLE);
		meta.setUnbreakable(true);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getPlayerSkullItem(Player p, String displayName){
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		assert skullMeta != null;
		skullMeta.setDisplayName((displayName != null)?displayName:p.getName());
		skullMeta.setOwningPlayer(p);
		skull.setItemMeta(skullMeta);
		return skull;
	}

	
	public static String getTimeFormatFromLong(long seconds, String type) {
		long millis = seconds*1000;
	    SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
	    if(type.equalsIgnoreCase("h")) dateFormat = new SimpleDateFormat("HH:mm:ss");
	    if(type.equalsIgnoreCase("s")) dateFormat = new SimpleDateFormat("ss");
	    TimeZone tz = TimeZone.getTimeZone("MESZ");
	    dateFormat.setTimeZone(tz);
		return dateFormat.format(new Date(millis));
	}
	
	public static void removeItemFromPlayer(Player p, ItemStack i) {
		ItemStack[] ogInv = p.getInventory().getContents();
		List<ItemStack> isList = new ArrayList<>();
		for(ItemStack is : ogInv) {
			if(is != i) isList.add(i);
		}
		ItemStack[] newInv = new ItemStack[isList.size()];
		newInv = isList.toArray(newInv);
		p.getInventory().clear();
		p.getInventory().setContents(newInv);
		
	}
	
//	public static int getTimeFromArgs(String timeString, String format) throws NumberFormatException, Exception{
//		int time = 0;
//		int dpamount = 0; for(int i = 0; i < timeString.length(); i++) {if(timeString.charAt(i) == ':') dpamount++;}
//
//		boolean b = format.equalsIgnoreCase("s") | format.toLowerCase().contains("second");
//		if(dpamount == 0) {
//			float f = Float.parseFloat(timeString.trim());
//			if(format != null) {
//				if(b) time = (int) f;
//				else if(format.equalsIgnoreCase("h") | format.toLowerCase().contains("hour")) time = (int) ((f*60)*60);
//				else time = (int) (f*60);
//			} else time = (int) (f*60);
//		} else if(dpamount == 1) {
//			String n1s = "";
//			String n2s = "";
//			boolean after = false;
//			for(int i = 0; i < timeString.length(); i++) {
//				if(timeString.charAt(i) == ':') after = true;
//				else {
//					if(!after) n1s+= timeString.charAt(i);
//					else n2s+= timeString.charAt(i);
//				}
//			}
//			if(format != null) {
//				if(b) time = Integer.parseInt(n1s);
//				else if(format.equalsIgnoreCase("h") | format.toLowerCase().contains("hour")) time = (Integer.parseInt(n1s)*60)*60+Integer.parseInt(n2s.trim())*60;
//				else time = Integer.parseInt(n1s)*60+Integer.parseInt(n2s.trim());
//			} else {
//				time = Integer.parseInt(n1s.trim())*60+Integer.parseInt(n2s.trim());
//			}
//		} else if(dpamount == 2){
//			String n1s = "";
//			String n2s = "";
//			String n3s = "";
//			int at = 1;
//			for(int i = 0; i < timeString.length(); i++) {
//				if(timeString.charAt(i) == ':') at++;
//				else {
//					if(at == 1) n1s+= timeString.charAt(i);
//					else if(at == 2) n2s+= timeString.charAt(i);
//					else n3s+= timeString.charAt(i);
//				}
//			}
//			if(format != null) {
//				if(b) time = Integer.parseInt(n1s);
//				else if(format.equalsIgnoreCase("h") | format.toLowerCase().contains("hour")) time = (Integer.parseInt(n1s)*60)*60+Integer.parseInt(n2s.trim())*60+Integer.parseInt(n3s);
//				else time = Integer.parseInt(n1s)*60+Integer.parseInt(n2s.trim());
//			} else {
//				time = (Integer.parseInt(n1s)*60)*60+Integer.parseInt(n2s.trim())*60+Integer.parseInt(n3s);
//			}
//		} else throw new Exception("TimeFormatException: Maximal allowed separators ar 2, given are "+dpamount);
//
//		return time;
//	}
	
	public enum TimeFormat{
		SECONDS,
		MINUTES,
		HOURS;
		
		public static TimeFormat getFromString(String s) {
			String format = s.substring(0, 1).toUpperCase();
			for(TimeFormat tf : TimeFormat.values()) {
				if(tf.name().substring(0, 1).equalsIgnoreCase(format)) {
					return tf;
				}
			}
			return TimeFormat.MINUTES;
		}
	}
	
	
	public enum ServerProperty {

        SPAWN_PROTECTION("spawn-protection"),
        SERVER_NAME("server-name"),
        FORCE_GAMEMODE("force-gamemode"),
        NETHER("allow-nether"),
        DEFAULT_GAMEMODE("gamemode"),
        QUERY("enable-query"),
        PLAYER_IDLE_TIMEOUT("player-idle-timeout"),
        DIFFICULTY("difficulty"),
        SPAWN_MONSTERS("spawn-monsters"),
        OP_PERMISSION_LEVEL("op-permission-level"),
        RESOURCE_PACK_HASH("resource-pack-hash"),
        RESOURCE_PACK("resource-pack"),
        ANNOUNCE_PLAYER_ACHIEVEMENTS("announce-player-achievements"),
        PVP("pvp"),
        SNOOPER("snooper-enabled"),
        LEVEL_NAME("level-name"),
        LEVEL_TYPE("level-type"),
        LEVEL_SEED("level-seed"),
        HARDCORE("hardcore"),
        COMMAND_BLOCKS("enable-command-blocks"),
        MAX_PLAYERS("max-players"),
        PACKET_COMPRESSION_LIMIT("network-compression-threshold"),
        MAX_WORLD_SIZE("max-world-size"),
        IP("server-ip"),
        PORT("server-port"),
        DEBUG_MODE("debug"),
        SPAWN_NPCS("spawn-npcs"),
        SPAWN_ANIMALS("spawn-animals"),
        FLIGHT("allow-flight"),
        VIEW_DISTANCE("view-distance"),
        WHITE_LIST("white-list"),
        GENERATE_STRUCTURES("generate-structures"),
        MAX_BUILD_HEIGHT("max-build-height"),
        MOTD("motd"),
        REMOTE_CONTROL("enable-rcon");

        private String propertyName;

        ServerProperty(String propertyName) {
            this.propertyName = propertyName;
        }

        public String getPropertyName() {
            return propertyName;
        }

    }
	
	public static boolean contains(String string, String... strings) {
		for(String s : strings) {
			if(string.toUpperCase().contains(s.toUpperCase())) return true;
		}
		return false;
	}
	
	
	public static boolean isBetween(double min, double value, double max) {
		return value < max && value > min;
	}
	
	public static int randomInt(int min, int max) {
		return (int) Math.round((Math.random() * ((max - min) + 1)) + min);
	}
	public static double randomDouble(double min, double max) {
		return (Math.random() * ((max - min) + 1)) + min;
	}
	
	public static boolean isNumericOnly(String s) {
		return s.matches("\\d+");
	}
	public static boolean isAlphabeticOnly(String s) {
		char[] chars = s.toCharArray();
	    for (char c : chars) {
	        if(!Character.isLetter(c)) {
	            return false;
	        }
	    }
	    return true;
	}

	public static ItemStack getNonNullItemStack(Inventory inv, int slot){
		ItemStack item = inv.getItem(slot);
		if(item == null) return new ItemStack(Material.AIR);
		return item;
	}
}

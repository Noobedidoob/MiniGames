package me.noobedidoob.minigames.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MgUtils {
	
	public static Logger logger = Bukkit.getLogger();
	public enum Mode {
		MINIGAMES,
		LASERTAG,
		HIDEANDSEEK
	}
	
	
	public static String getTimeFormatFromLong(long seconds, String type) {
		long millis = seconds*1000;
	    SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
	    if(type.equalsIgnoreCase("h")) dateFormat = new SimpleDateFormat("HH:mm:ss");
	    if(type.equalsIgnoreCase("s")) dateFormat = new SimpleDateFormat("ss");
	    TimeZone tz = TimeZone.getTimeZone("MESZ");
	    dateFormat.setTimeZone(tz);
	    String time = dateFormat.format(new Date(millis));
	    return time;
	}
	
	public static void removeItemFromPlayer(Player p, ItemStack i) {
		ItemStack[] ogInv = p.getInventory().getContents();
		List<ItemStack> isList = new ArrayList<ItemStack>();
		for(ItemStack is : ogInv) {
			if(is != i) isList.add(i);
		}
		ItemStack[] newInv = new ItemStack[isList.size()];
		newInv = isList.toArray(newInv);
		p.getInventory().clear();
		p.getInventory().setContents(newInv);
		
	}
	
	public static int getTimeFromArgs(String timeString, String format) throws NumberFormatException, Exception{
		int time = 0;
		int dpamount = 0; for(int i = 0; i < timeString.length(); i++) {if(timeString.charAt(i) == ':') dpamount++;}
		
		if(dpamount == 0) {
			float f = Float.parseFloat(timeString.trim());
			if(format != null) {
				if(format.equalsIgnoreCase("s") | format.toLowerCase().contains("second")) time = (int) f;
				else if(format.equalsIgnoreCase("h") | format.toLowerCase().contains("hour")) time = (int) ((f*60)*60);
				else time = (int) (f*60);
			} else time = (int) (f*60);
		} else if(dpamount == 1) {
			String n1s = "";
			String n2s = "";
			boolean after = false;
			for(int i = 0; i < timeString.length(); i++) {
				if(timeString.charAt(i) == ':') after = true;
				else {
					if(!after) n1s+= timeString.charAt(i);
					else n2s+= timeString.charAt(i);
				}
			}
			if(format != null) {
				if(format.equalsIgnoreCase("s") | format.toLowerCase().contains("second")) time = Integer.parseInt(n1s);
				else if(format.equalsIgnoreCase("h") | format.toLowerCase().contains("hour")) time = (Integer.parseInt(n1s)*60)*60+Integer.parseInt(n2s.trim())*60;
				else time = Integer.parseInt(n1s)*60+Integer.parseInt(n2s.trim());
			} else {
				time = Integer.parseInt(n1s.trim())*60+Integer.parseInt(n2s.trim());
			}
		} else if(dpamount == 2){
			String n1s = "";
			String n2s = "";
			String n3s = "";
			int at = 1;
			for(int i = 0; i < timeString.length(); i++) {
				if(timeString.charAt(i) == ':') at++;
				else {
					if(at == 1) n1s+= timeString.charAt(i);
					else if(at == 2) n2s+= timeString.charAt(i);
					else n3s+= timeString.charAt(i);
				}
			}
			if(format != null) {
				if(format.equalsIgnoreCase("s") | format.toLowerCase().contains("second")) time = Integer.parseInt(n1s);
				else if(format.equalsIgnoreCase("h") | format.toLowerCase().contains("hour")) time = (Integer.parseInt(n1s)*60)*60+Integer.parseInt(n2s.trim())*60+Integer.parseInt(n3s);
				else time = Integer.parseInt(n1s)*60+Integer.parseInt(n2s.trim());
			} else {
				time = (Integer.parseInt(n1s)*60)*60+Integer.parseInt(n2s.trim())*60+Integer.parseInt(n3s);
			}
		} else throw new Exception("TimeFormatException: Maximal allowed separators ar 2, given are "+dpamount);
		
		return time;
	}
	
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
		if(value < max && value > min) return true;
		else return false;
	}
	
	public static int randomInt(int min, int max) {
		return (int) Math.round((Math.random() * ((max - min) + 1)) + min);
	}
	public static double randomDouble(int min, int max) {
		return (Math.random() * ((max - min) + 1)) + min;
	}
	
	public static boolean isNumericOnly(String s) {
		if(s.matches("\\d+")) return true;
		return false;
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
}

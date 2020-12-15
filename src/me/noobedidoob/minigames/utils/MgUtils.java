package me.noobedidoob.minigames.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
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
	
	public static void inform(String msg) {
		logger.log(Level.INFO, "[MiniGamse] "+msg);
	}
	public static void warn(String msg) {
		logger.warning("[MiniGamse] "+msg);
	}
	public static void severe(String msg) {
		logger.severe("[MiniGamse] "+msg);
	}
	
	public static void informLs(String msg) {
		logger.log(Level.INFO, "[LasetTag] "+msg);
	}
	public static void warnLs(String msg) {
		logger.warning("[LasetTag] "+msg);
	}
	public static void severeLs(String msg) {
		logger.severe("[LasetTag] "+msg);
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
	
	public static long getTimeFromArgs(String timeString, String format) throws NumberFormatException, Exception{
		long time = 0;
		int dpamount = 0; for(int i = 0; i < timeString.length(); i++) {if(timeString.charAt(i) == ':') dpamount++;}
		
		if(dpamount == 0) {
			float f = Float.parseFloat(timeString.trim());
			if(format != null) {
				if(format.equalsIgnoreCase("s") | format.toLowerCase().contains("second")) time = (long) f;
				else if(format.equalsIgnoreCase("h") | format.toLowerCase().contains("hour")) time = (long) ((f*60)*60);
				else time = (long) (f*60);
			} else time = (long) (f*60);
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
				if(format.equalsIgnoreCase("s") | format.toLowerCase().contains("second")) time = Long.parseLong(n1s);
				else if(format.equalsIgnoreCase("h") | format.toLowerCase().contains("hour")) time = (Long.parseLong(n1s)*60)*60+Long.parseLong(n2s.trim())*60;
				else time = Long.parseLong(n1s)*60+Long.parseLong(n2s.trim());
			} else {
				time = Long.parseLong(n1s.trim())*60+Long.parseLong(n2s.trim());
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
				if(format.equalsIgnoreCase("s") | format.toLowerCase().contains("second")) time = Long.parseLong(n1s);
				else if(format.equalsIgnoreCase("h") | format.toLowerCase().contains("hour")) time = (Long.parseLong(n1s)*60)*60+Long.parseLong(n2s.trim())*60+Long.parseLong(n3s);
				else time = Long.parseLong(n1s)*60+Long.parseLong(n2s.trim());
			} else {
				time = (Long.parseLong(n1s)*60)*60+Long.parseLong(n2s.trim())*60+Long.parseLong(n3s);
			}
		} else throw new Exception("TimeFormatException: Maximal allowed separators ar 2, given are "+dpamount);
		
		return time;
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
}

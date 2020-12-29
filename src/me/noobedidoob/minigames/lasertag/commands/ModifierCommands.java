package me.noobedidoob.minigames.lasertag.commands;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.noobedidoob.minigames.lasertag.methods.Game;
import me.noobedidoob.minigames.lasertag.methods.Weapons;
import me.noobedidoob.minigames.main.Minigames;
import me.noobedidoob.minigames.utils.MgUtils;

public class ModifierCommands  {
	
	@SuppressWarnings("unused")
	private Minigames minigames;
	public ModifierCommands(Minigames minigames) {
		this.minigames = minigames;
	}
	
	
	public void perform(CommandSender sender, String[] args) {
		
		if(args[0].equalsIgnoreCase("getModifiers") | args[0].equalsIgnoreCase("modifiers")) {
			sender.sendMessage("\n§7—————————§d§lModifiers§r§7—————————");
			for(Mod m : Mod.values()) {
				sender.sendMessage("§7> "+m.getDescription()+": §a"+m.getOg().toString());
			}
			sender.sendMessage("§7————————————————————————\n");
			return;
		} else if(args[0].equalsIgnoreCase("getModifierTypes")) {
			for(Mod m : Mod.values()) {
				sender.sendMessage("§7"+m.name()+" <§e"+m.getValueTypeName()+"§7>");
			}
		} else if(args[0].equalsIgnoreCase("setmodifier")) {
			Mod m = Mod.valueOf(args[1].toUpperCase().replace("-", "_"));
			String valString = args[2];
			Object value = valString;
			
			if(MgUtils.isNumericOnly(valString)) {
				value = Integer.parseInt(valString);
			} else if(MgUtils.isAlphabeticOnly(valString)) {
				if(valString.equalsIgnoreCase("true") | valString.equalsIgnoreCase("false")) value = Boolean.parseBoolean(valString);
				else {
					sender.sendMessage("§cThe given value is invalid! Please use a §evalid number §cor §etrue§c/§efalse!");
					return;
				}
			} else {
				
			}
			
			try {
				value = Integer.parseInt(valString);
			} catch (NumberFormatException e1) {
				try {
					value = Double.parseDouble(valString+"d");
				} catch (NumberFormatException e2) {
					if(valString.equalsIgnoreCase("true") | valString.equalsIgnoreCase("false")) value = Boolean.parseBoolean(valString);
					else {
						sender.sendMessage("§cThe given value is invalid! Please use a §evalid number §cor §etrue§c/§efalse!");
						return;
					}
				}
			}
			if(m != null) {
				if(value.getClass() == m.getOg().getClass()) {
					m.set(value);
					if(m == Mod.WITH_MULTIWEAPONS && (boolean) value) {
						Bukkit.dispatchCommand(sender, "lt withmultiweapons");
					}
					sender.sendMessage("§aSuccessfully set the value of the modifier §d"+m.name().toLowerCase()+" §a to §e"+value.toString());
					return;
				} else {
					sender.sendMessage("§cThe given type of value doesnt match with the modifiers value type! Please use §e"+m.getValueTypeName());
					return;
				}
			} else {
				sender.sendMessage("§cThe modifier §d"+args[1]+" §cdoesn't exist! Use §e/lt setmodifier §cto get all available modifiers");
				return;
			}
		} else if(args[0].equalsIgnoreCase("withmultiweapons")) {
			if (Game.waiting()) {
				Mod.WITH_MULTIWEAPONS.set(true);
				ItemStack newLasergun = Weapons.lasergunItem;
				ItemStack newDagger = Weapons.daggerItem;
				newLasergun.removeEnchantment(Enchantment.DAMAGE_ALL);
				ItemMeta newLasergunMeta = newLasergun.getItemMeta();
				ItemMeta newDaggerMeta = newLasergun.getItemMeta();
				for (Player ap : Game.players()) {
					if (Game.teams()) {
						newLasergunMeta.setDisplayName(Game.getTeamColor(Game.getPlayerTeam(ap)).getChatColor()
								+ "§lLasergun #" + (Game.getTeamColor(Game.getPlayerTeam(ap)).getOrdinal()+1));
						newDaggerMeta.setDisplayName(Game.getTeamColor(Game.getPlayerTeam(ap)).getChatColor()
								+ "§lDagger #" + (Game.getTeamColor(Game.getPlayerTeam(ap)).getOrdinal()+1));
					} else {
						int ordinal = Game.getPlayerColor(ap).getOrdinal();
						newLasergunMeta.setDisplayName(Game.getPlayerColor(ap).getChatColor() + "§lLasergun #" + (ordinal + 1));
						newDaggerMeta.setDisplayName(Game.getPlayerColor(ap).getChatColor() + "§lDagger #" + (ordinal + 1));
					}
					newLasergun.setItemMeta(newLasergunMeta);
					newDagger.setItemMeta(newDaggerMeta);
					for(int slot = 0; slot < 9; slot++) {
						ap.getInventory().setItem(slot, new ItemStack(Material.AIR));
					}
					ap.getInventory().setItem(0, newLasergun);
					ap.getInventory().setItem(1, newDagger);
					
					
					ap.openInventory(Weapons.getPlayersWeaponsInv(ap));
				} 
			} else sender.sendMessage("§cPlease register a new round first!");
			return;
		}

		sender.sendMessage("§cSyntax ERROR! Please use §e/lt §cto see all commands and their arguments");
		return;
	}
	
	public List<String> getTabComplete(List<String> list, CommandSender sender, String[] args) {
		
		if(args.length == 1) {
			list.add("getModifiers");
			list.add("getModifierTypes");
			if(Game.waiting() && sender.isOp()) {
				list.add("withmultiweapons");
				list.add("setModifier");
			}
		} else if(args.length >= 2 && args[0].equalsIgnoreCase("setmodifier") && sender.isOp()) {
			if(args.length == 2) {
				for(Mod m : Mod.values()) list.add(m.name().toLowerCase());
			} else if(args.length == 3 && Mod.valueOf(args[1].toUpperCase().replace("-", "_")) != null) {
				if(Mod.valueOf(args[1].toUpperCase().replace("-", "_")).getValueTypeName() == "true/false") {
					list.add("true");
					list.add("false");
				}
			}
		}
		
		try {
			String prevArg = args[args.length-2];
			Mod m = Mod.valueOf(prevArg.toUpperCase().replaceAll("-", "_"));
			if (m != null) {
				if (m.getValueTypeName() == "true/false") {
					if (sender.isOp()) {
						list.add("true");
						list.add("false");
					}
				} 
			}
		} catch(IllegalArgumentException | ArrayIndexOutOfBoundsException e) {}
		
		return list;
	}
	
private static HashMap<Mod, Object> modifierValue = new HashMap<Mod, Object>();
	
	public enum Mod{
		POINTS(1, "Normal amount of points a player gets"),
		WITH_MULTIWEAPONS(false, "Playing with multiple weapons"),
		SNIPER_SHOT_EXTRA(1, "Extra points when killing with snipe-shot"),
		MINIMAL_SNIPE_DISTANCE(35, "Minimal distance of a shot to be a sniper shot"),
		NORMAL_SHOT_EXTRA(0, "Extra points when a player shot normal"),
		BACKSTAB_EXTRA(0, "Extra points when backstabbing"),
		PVP_EXTRA(0, "Extra ponts when killed at melee"),
		HEADSHOT_EXTRA(1, "Extra ponts when killing with headshot"),
		STREAK_EXTRA(2, "Extra points when having a streak"),
		STREAK_SHUTDOWN(2, "Extra points when shutting down a streak"),
		MIN_KILLS_FOR_STREAK(5, "Minimal kill amount required for a streak"),
		MULTIKILLS_EXTRA(2, "Extra points when killing multiple players at once"),
		SPAWNPROTECTION_SECONDS(10, "Seconds a player is protected after spawning"),
		WIDTH_ADDON(0d, "Addon to a players hitbox width"),
		HEIGHT_ADDON(0d, "Addon to a players hitbox height"),
//		WITHEVENTS
		SHOOT_THROUGH_BLOCKS(false, "Shoot through blocks"),
		HIGHLIGHT_PLAYERS(false, "Making players glow and more visible"),
		HIGHLIGHT_POWER(255, "Glowing power"),
		LASERGUN_COOLDOWN_TICKS(12, "Ticks a lasergun takes to cool down"),
		LASERGUN_MULTIWEAPONS_COOLDOWN_TICKS(2, "Ticks a lasergun takes to cool down when playing with multiple weapons"),
		SNIPER_COOLDOWN_TICKS(100, "Ticks a sniperrifle takes to cool down"),
		SHOTGUN_COOLDOWN_TICKS(40, "Ticks a shotgun takes to cool down"),
		SNIPER_AMMO_BEFORE_COOLDOWN(2, "Maximal sniper ammo"),
		LASERGUN_NORMAL_DAMAGE(100, "Normal lasergun shot damage"),
		LASERGUN_MULTIWEAPONS_DAMAGE(9, "lasergun shot damage when playing with multiple weapons"),
		LASERGUN_PVP_DAMAGE(10, "Lasergun melee damage (only without multiweapons"),
		SHOTGUN_DAMAGE(11, "Shotgun shot damage"),
		SNIPER_DAMAGE(100, "Sniper shot damage"),
		STABBER_DAMAGE(10, "Stabber melee damage");
		
		private Object ogValue;
		private Object currentValue;
		private String description;
		private String valueTypeName;
		Mod(Object value, String description) {
            this.ogValue = value;
            this.currentValue = value;
            this.description = description;
            modifierValue.put(this, value);
            if(value instanceof Integer) valueTypeName = "Full-Number";
            else if(value instanceof Double) valueTypeName = "Number";
            else valueTypeName = "true/false";
        }
		

        public Object get() {
        	return currentValue;
        }
        public int getInt() {
        	try {
				return (int) currentValue;
			} catch (Exception e) {
				return 0;
			}
        }
        public double getDouble() {
		    try {
		    	return (double) currentValue;
			} catch (Exception e) {
				return 0;
			}
        }
        public boolean getBoolean() {
	        try {
	        	return (boolean) currentValue;
			} catch (Exception e) {
				return false;
			}
        }
        
        public void set(Object value) {
        	if(value.getClass() == ogValue.getClass()) {
        		currentValue = value;
            	modifierValue.put(this, value);
        	}
        }
		
        public Object getOg() {
            return ogValue;
        }
        public int getOgInt() {
        	try {
				return (int) ogValue;
			} catch (Exception e) {
				return 0;
			}
        }
        public double getOgDouble() {
        	try {
				return (double) ogValue;
			} catch (Exception e) {
				return 0;
			}
        }
        public boolean getOgBoolean() {
        	try {
				return (boolean) ogValue;
			} catch (Exception e) {
				return false;
			}
        }
        
        
        public String getDescription() {
        	return description;
        }
        public String getValueTypeName() {
        	return valueTypeName;
        }
        
        public static boolean withMultiweapons() { return Mod.WITH_MULTIWEAPONS.getBoolean(); }
        public static boolean multiWeapons() { return Mod.WITH_MULTIWEAPONS.getBoolean(); }
        
        public static void resetMods() {
    		for(Mod m : Mod.values()) {
    			modifierValue.put(m, m.getOg());
    		}
    	}
	}
	
}

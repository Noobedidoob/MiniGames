package me.noobedidoob.minigames.main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor, TabCompleter{
	
	private Minigames m;
	public Commands(Minigames minigames) {
		this.m = minigames;
	}
	
	String opCommands = "§egetworlds §7— \n"
						+ "§esetworld <name> §7— \n"
						+ "§eresetworld <true|false> §7— \n";
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if(cmd.getName().equalsIgnoreCase("lobby")){
			((Player) sender).teleport(Minigames.spawn);
			return true;
		} else if(args.length == 0) {
			if(sender.isOp()) sender.sendMessage(opCommands);
		} else if(args.length == 1) {
			if(args[0].equalsIgnoreCase("getworlds")) {
				sender.sendMessage("\n§aThere are the following overworlds:");
				for(World w : Bukkit.getWorlds()) {
					if(w.getEnvironment() == Environment.NORMAL) {
						sender.sendMessage("§d"+w.getName());
					}
				}
			}
		} else if(args.length == 2) {
			if(args[0].equalsIgnoreCase("setworld")) {
				if(m.waitingForName) {
					String n = args[1];
					World newWorld;
					if(Bukkit.getWorld(n) != null) {
						newWorld = Bukkit.getWorld(n);
						if(newWorld.getEnvironment() == Environment.NORMAL) {
							m.getConfig().set("world", newWorld.getName());
							m.saveConfig();
							Minigames.world = newWorld;
							m.waitingForName = false;
							m.worldFound = true;
							Bukkit.broadcastMessage("§aSuccessfully changed the main world for the MiniGames to §b"+newWorld.getName()+"§a! Use §e/mg getworlds §ato get all worlds and §e/mg setworld <worldname> §ato change the main world for MiniGames");
							return true;
						} else {
							sender.sendMessage("§cThis world is not an overworld! Please select an overworld");
							return true;
						}
					} else {
						int number = 0;
						try {
							number = Integer.parseInt(n)-1;
							newWorld = Bukkit.getWorlds().get(number);
						} catch (Exception e2) {
							sender.sendMessage("§cCouldn't find world!");
							return true;
						}
						
						if(newWorld.getEnvironment() == Environment.NORMAL) {
							m.getConfig().set("world", newWorld.getName());
							m.saveConfig();
							Minigames.world = newWorld;
							m.waitingForName = false;
							m.worldFound = true;
							Bukkit.broadcastMessage("§aSuccessfully changed the main world for the MiniGames to §b"+newWorld.getName()+"§a! Use §e/mg getworlds §ato get all worlds and §e/mg setworld <worldname> §ato change the main world for MiniGames");
							return true;
						} else {
							sender.sendMessage("§cThis world is not an overworld! Please select an overworld");
							return true;
						}
					}
				} else {
					String n = args[1];
					World newWorld;
					if(Bukkit.getWorld(n) != null) {
						newWorld = Bukkit.getWorld(n);
						if(newWorld.getEnvironment() == Environment.NORMAL) {
							m.getConfig().set("world", newWorld.getName());
							m.saveConfig();
							Minigames.world = newWorld;
							Bukkit.broadcastMessage("§aSuccessfully changed the main world for the MiniGames to §b"+newWorld.getName()+"§a!");
							return true;
						} else {
							sender.sendMessage("§cThis world is not an overworld! Please select an overworld");
							return true;
						}
					} else {
						sender.sendMessage("§cThe world §d"+n+"§c doesn't exist!");
					}
				}
			} else if(args[0].equalsIgnoreCase("resetworld")) {
				if(sender.isOp()) {
					String vString = args[1];
					if(vString.toUpperCase().contains("TRUE") | vString.toUpperCase().contains("false")) {
						boolean value = Boolean.parseBoolean(vString);
						Minigames.world.setAutoSave(value);
						sender.sendMessage("§aSuccessfully set resetworld to §d"+value+"§a!");
						sender.sendMessage("§7Note: If you want to completely reset the world just delete the §d\"Minigames\" §7folder!");
					} else {
						sender.sendMessage("§c'§b"+vString+"§c' is not a valid statement! Please use §etrue §cor §efalse");
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> list = new ArrayList<String>();
		if(cmd.getName().equals("minigames")) {
			if(args.length == 0) {
				list.add("setworld");
				list.add("getworlds");
				list.add("resetworld");
			} else if(args.length == 1) {
				if(args[0].equalsIgnoreCase("setworld")) {
					for(World w : Bukkit.getWorlds()) {
						if(w.getEnvironment() == Environment.NORMAL) {
							list.add(w.getName());
						}
					}
				} else if(args[0].equalsIgnoreCase("resetworld")) {
					list.add("true");
					list.add("false");
				}
				
			}
		}
		
		return list;
	}

}

package me.noobedidoob.minigames;

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

	private Minigames minigames;
	public Commands(Minigames minigames) {
		this.minigames = minigames;
	}
	
	String opCommands = "§egetworlds §7— \n"
						+ "§esetworld <name> §7— \n";
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if(cmd.getName().equalsIgnoreCase("lobby")){
			Minigames.teleportPlayersToSpawn((Player) sender);
			return true;
		} else if(args.length == 0) {
			if(sender.isOp()) sender.sendMessage(opCommands);
			return true;
		} /*else if(args.length == 1) {
			if(args[0].equalsIgnoreCase("getworlds") && sender.isOp()) {
				sender.sendMessage("\n§aThere are the following overworlds:");
				for(World w : Bukkit.getWorlds()) {
					if(w.getEnvironment() == Environment.NORMAL) {
						sender.sendMessage("§d"+w.getName());
					}
				}
				return true;
			} else if(args[0].equalsIgnoreCase("setTexturepack") && sender instanceof Player) {
				TextComponent msg = new TextComponent("§eYou can either click ");
				TextComponent linkMsg = new TextComponent("here");
				linkMsg.setColor(net.md_5.bungee.api.ChatColor.GOLD);
				linkMsg.setUnderlined(true);
				linkMsg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, Minigames.texturepackURL));
				linkMsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Download the texturepack").create()));
				msg.addExtra(linkMsg);
				msg.addExtra(new TextComponent(" §eto download the txturepack or activate server resourcepacks in the server-settings before joining!"));
				((Player)sender).spigot().sendMessage(msg);
				return true;
			}
		} else if(args.length == 2) {
			if(args[0].equalsIgnoreCase("setworld") && sender.isOp()) {
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
							Bukkit.broadcastMessage("§aSuccessfully changed the main world for the Minigames to §b"+newWorld.getName()+"§a! Use §e/mg getworlds §ato get all worlds and §e/mg setworld <worldname> §ato change the main world for Minigames");
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
							Bukkit.broadcastMessage("§aSuccessfully changed the main world for the Minigames to §b"+newWorld.getName()+"§a! Use §e/mg getworlds §ato get all worlds and §e/mg setworld <worldname> §ato change the main world for Minigames");
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
							Bukkit.broadcastMessage("§aSuccessfully changed the main world for the Minigames to §b"+newWorld.getName()+"§a!");
							return true;
						} else {
							sender.sendMessage("§cThis world is not an overworld! Please select an overworld");
							return true;
						}
					} else {
						sender.sendMessage("§cThe world §d"+n+"§c doesn't exist!");
					}
				}
			}
		}*/
		
		sender.sendMessage("§cSyntax error! Use §e/minigames §cto see all commands!");
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> list = new ArrayList<>();
		if(args.length == 0) {
			if (sender.isOp()) {
				list.add("setworld");
				list.add("getworlds");
			}
			list.add("settexturepack");
		} else if(args.length == 1) {
			if(args[0].equalsIgnoreCase("setworld") && sender.isOp()) {
				for(World w : Bukkit.getWorlds()) {
					if(w.getEnvironment() == Environment.NORMAL) {
						list.add(w.getName());
					}
				}
			}
			
		}
		
		return list;
	}

}

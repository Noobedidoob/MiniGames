package me.noobedidoob.minigames.lasertag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import me.noobedidoob.minigames.lasertag.Lasertag.LasertagColor;
import me.noobedidoob.minigames.lasertag.commands.ModifierCommands;
import me.noobedidoob.minigames.lasertag.commands.SessionCommands;
import me.noobedidoob.minigames.lasertag.methods.Flag;
import me.noobedidoob.minigames.main.Minigames;

public class LaserCommands implements CommandExecutor, TabCompleter {

	private Minigames m;
	private ModifierCommands modifierCommands;
	private SessionCommands sessionCommands;
	public LaserCommands(Minigames minigames, ModifierCommands modifierCommands, SessionCommands sessionCommands) {
		this.m = minigames;
		this.modifierCommands = modifierCommands;
		this.sessionCommands = sessionCommands;
	}
	
	
	String opCommands = "\n§7————————All Lasertag commands————————\n"
					  + "§e/lt new teams <players...> vs <players...> ... <time> [format] <mapname>\n"
					  + "§e/lt new solo <player1> <player2> ... <time> [format] <mapname>\n"
					  + "§e/lt modifiers — §7See current stats of modifiers\n" 
					  + "§e/lt setmodifier — §7See all changeable modifiers\n"
					  + "§e/lt setmodifier <name> <value>\n"
					  + "§e/lt cancel — §7cancel the registrated session\n"
					  + "§e/lt start — §7start the registrated session\n"
					  + "§e/lt stop — §7stop the ongoing game\n"
					  + "§e/lt loadtexturepack — §7stop the ongoing game\n"
					  + "\n————————————————————————————————\n";
	
	
	String commands = "\n———————————————\n"
					+ "/lt modifiers — \n"
					+ "/lt loadtexturepack — \n"
					+ "\n———————————————\n";
	

	int counter = 1;
	List<String> lore = new ArrayList<String>();
	public HashMap<Player, Flag> playerFlag = new HashMap<Player, Flag>();
	public HashMap<Player, Boolean> flagIsFollowing = new HashMap<Player, Boolean>();
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if(m.worldFound) {
			if(cmd.getName().equalsIgnoreCase("lasertag")) {
				if(args.length == 1 && args[0].equalsIgnoreCase("test") && sender instanceof Player) {
					Player p = (Player) sender;
					p.sendMessage("§aTESTING...");
					p.getInventory().clear();
					Inventory inv = Bukkit.createInventory(null, 9, "Test inv");
					ItemStack item = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName("§a§lTest");
					lore.add("§aTest 0");
					meta.setLore(lore);
					item.setItemMeta(meta);
					inv.setItem(0, item);
					p.openInventory(inv);
					
					new BukkitRunnable(){
			            @Override
			            public void run(){
			                Inventory inv = ((HumanEntity) p).getOpenInventory().getTopInventory();
			               
			                ItemStack item = inv.getItem(0);
			                ItemMeta meta = item.getItemMeta();
			                lore.add("Test "+counter++);
		                    meta.setLore(lore);
			               
		                    item.setItemMeta(meta);
			                inv.setItem(0, item);
			                
			                if(counter == 10) cancel();
			            }  
			        }.runTaskTimer(Minigames.minigames, 20L, 20L);
					
					
//					counter = 1;
//					new Timer().schedule(new TimerTask() {
//						@Override
//						public void run() {
//							if(counter != 10) {
//
//								lore.add("Test "+counter);
//								p.getOpenInventory().getTopInventory().getItem(0).getItemMeta().setLore(lore);
//								p.updateInventory();
//								
//								p.sendMessage("Added lore nr. "+counter++);
//							} else cancel();
//						}
//					}, 1000, 1000);
					return true;
				}
				
				if(args.length >= 1) {
					if(args[0].toLowerCase().contains("modifier")/* | args[0].equalsIgnoreCase("withmultiweapons")*/) {
						modifierCommands.perform(sender, args);
						return true;
					}
					
					if(SessionCommands.commandArgs.contains(args[0]) | SessionCommands.adminCommandArgs.contains(args[0])) {
						sessionCommands.perform(sender, args);
						return true;
					}
				}
				
				if(args.length == 0) {
					if(sender.isOp()) sender.sendMessage("§e"+opCommands);
					else sender.sendMessage("§e"+commands);
					return true;
				} else if(args.length == 1) {
					
					if(args[0].equalsIgnoreCase("flagtest") && sender instanceof Player) {
						String c = args[1];
						Player p = (Player) sender;
						if(c.equalsIgnoreCase("unfollow") && flagIsFollowing.get(p)) {
							if(playerFlag.get(p) != null) {
								flagIsFollowing.put(p, false);
								playerFlag.get(p).unattach();
							}
						} else if(c.equalsIgnoreCase("follow") && !flagIsFollowing.get(p)) {
							if(playerFlag.get(p) != null) {
								flagIsFollowing.put(p, true);
								playerFlag.get(p).attachPlayer(p);
							}
						} else if(!flagIsFollowing.get(p)){
							try {
								Flag f = new Flag(p.getLocation(), LasertagColor.valueOf(c.substring(0,1).toUpperCase()+c.substring(1).toLowerCase()));
								f.attachPlayer(p);
								playerFlag.put(p, f);
								flagIsFollowing.put(p, true);
							} catch (Exception e) {
								p.sendMessage("Not a command or color: "+c);
								e.printStackTrace();
							}
						} else p.sendMessage("You already have an Flag");
						return true;
					} 
					
					else if(args[0].equalsIgnoreCase("loadtexturepack")) {
						if(sender instanceof Player) {
							sender.sendMessage("Loading texturepack...");
							((Player) sender).setResourcePack("[URL]"+Minigames.texturepackURL+"[/URL]");
						} else sender.sendMessage("You may only perform ths command as a player");
						return true;
					} 
				}
			}
		} else {
			sender.sendMessage("§cMain world for MiniGames still not found! Please define the main world first to continue!");
			return true;
		}
		sender.sendMessage("§cSyntax ERROR! Please use §e/lt §cto see all commands and their arguments");
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		List<String> list = new ArrayList<String>();
		if(m.worldFound) {
			if(cmd.getName().equalsIgnoreCase("lasertag")) {
				
				list = modifierCommands.getTabComplete(list, sender, args);
				list = sessionCommands.getTabComplete(list, sender, args);
				
				if(args.length == 1) {
					
//					if(!Game.waiting() && !Game.tagging() && sender.isOp()) list.add("new");
//					if(Game.waiting() && sender.isOp()) list.add("start");
////					if(Game.waiting() && sender.isOp()) list.add("withmultiweapons");
//					if(Game.waiting() && sender.isOp()) list.add("cancel");
//					if(Game.tagging() && sender.isOp()) list.add("stop");
//					list.add("modifiers");
					list.add("loadtexturepack");
//					if(Game.waiting() && sender.isOp()) list.add("setmodifier");
					refreshTabCommplete(list, args[0]);
				} else if(args.length == 2) {
					String prevArg = args[args.length-2];
					if(prevArg.equalsIgnoreCase("new")) {
						if(sender.isOp()) list.add("solo");
						if(sender.isOp()) list.add("teams");
					} /*else if(prevArg.equalsIgnoreCase("setmodifier")) {
						for(String s : modifiersList) {
							if(sender.isOp()) list.add(s);
						}
					}*/
					refreshTabCommplete(list, args[1]);
				} else {
					String prevArg = args[args.length-2];
					if(prevArg.equalsIgnoreCase("highlightplayers") | prevArg.equalsIgnoreCase("shootthroughblocks") | prevArg.equalsIgnoreCase("spawnatbase") | prevArg.equalsIgnoreCase("withevents")) {
						if(sender.isOp()) list.add("true");
						if(sender.isOp()) list.add("false");
					} /*else if(!modifiersList.contains(prevArg)){
						try {
							@SuppressWarnings("unused")
							int i = Integer.parseInt(prevArg);
							i++;
							if(sender.isOp()) {
								for(Map map : Lasertag.maps.values()) {
									if(map.approved) list.add(map.getName());
								}
							}
						} catch (NumberFormatException e) {
							try {
								@SuppressWarnings("unused")
								int i = Integer.parseInt(args[args.length-3]);
								i++;
								if(sender.isOp()) {
									for(Map map : Lasertag.maps.values()) {
										if(map.approved) list.add(map.getName());
									}
								}
							} catch (NumberFormatException e2) {
								if(args[1].equalsIgnoreCase("teams")) {
									if(args[args.length-3] != "vs") {
										if(sender.isOp()) list.add("vs");
									}
								}
								for(Player p : Bukkit.getOnlinePlayers()) {
									if(!String.join(" ", args).contains(p.getName())) 
									if(sender.isOp()) list.add(p.getName());
								}
							}
						}
					}*/
					refreshTabCommplete(list, args[args.length-1]);
				}
			} 
		}
		return list;
	}
	
	public void refreshTabCommplete(List<String> list, String arg) {
		List<String> toRemoveList = new ArrayList<String>();
		for(String s : list) {
			if(!(s.length() < arg.length())) {
				if(!s.substring(0, arg.length()).equalsIgnoreCase(arg)) toRemoveList.add(s);
			} else toRemoveList.add(s);
		}
		list.removeAll(toRemoveList);
	}
	
}

package me.noobedidoob.minigames.lasertag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import me.noobedidoob.minigames.lasertag.Lasertag.LtColorNames;
import me.noobedidoob.minigames.lasertag.commands.ModifierCommands;
import me.noobedidoob.minigames.lasertag.commands.RoundCommands;
import me.noobedidoob.minigames.lasertag.methods.Flag;
import me.noobedidoob.minigames.lasertag.methods.Game;
import me.noobedidoob.minigames.main.Minigames;
import me.noobedidoob.minigames.utils.LasertagColor;
import me.noobedidoob.minigames.utils.MgUtils;

public class LaserCommands implements CommandExecutor, TabCompleter {

	private Minigames m;
	private ModifierCommands modifierCommands;
	private RoundCommands roundCommands;
	public LaserCommands(Minigames minigames, ModifierCommands modifierCommands, RoundCommands roundCommands) {
		this.m = minigames;
		this.modifierCommands = modifierCommands;
		this.roundCommands = roundCommands;
	}
	
	
	String opCommands = "\nｧ7覧覧覧覧All Lasertag commands覧覧覧覧\n"
					  + "ｧe/lt new teams <players...> vs <players...> ... <time> [format] <mapname>\n"
					  + "ｧe/lt new solo <player1> <player2> ... <time> [format] <mapname>\n"
					  + "ｧe/lt modifiers � ｧ7See current stats of modifiers\n" 
					  + "ｧe/lt setmodifier � ｧ7See all changeable modifiers\n"
					  + "ｧe/lt setmodifier <name> <value>\n"
					  + "ｧe/lt cancel � ｧ7cancel the registrated round\n"
					  + "ｧe/lt start � ｧ7start the registrated round\n"
					  + "ｧe/lt stop � ｧ7stop the ongoing game\n"
					  + "ｧe/lt loadtexturepack � ｧ7stop the ongoing game\n"
					  + "\n覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧\n";
	
	
	String commands = "\n覧覧覧覧覧覧覧予n"
					+ "/lt modifiers � \n"
					+ "/lt loadtexturepack � \n"
					+ "\n覧覧覧覧覧覧覧予n";
	
	
	public HashMap<Player, Flag> playerFlag = new HashMap<Player, Flag>();
	public HashMap<Player, Boolean> flagIsFollowing = new HashMap<Player, Boolean>();
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if(m.worldFound) {
			if(cmd.getName().equalsIgnoreCase("lasertag")) {
				
				if(args.length >= 1) {
					if(args[0].toLowerCase().contains("modifier") | args[0].equalsIgnoreCase("withmultiweapons")) {
						modifierCommands.perform(sender, args);
						return true;
					}
					
					if(MgUtils.contains(args[0], "start", "stop", "cancel","end", "new", "settime")) {
						roundCommands.perform(sender, args);
						return true;
					}
				}
				
				if(args.length == 0) {
					if(sender.isOp()) sender.sendMessage("ｧe"+opCommands);
					else sender.sendMessage("ｧe"+commands);
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
								Flag f = new Flag(p.getLocation(), new LasertagColor(LtColorNames.valueOf(c.substring(0,1).toUpperCase()+c.substring(1).toLowerCase())));
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
						
						
//					if(args[0].equalsIgnoreCase("start") && sender.isOp()) {
//						return RoundCommands.start(args, sender);
//					} else if(args[0].equalsIgnoreCase("cancel") && sender.isOp()) {
//						return RoundCommands.cancel(args, sender);
//					} else if(args[0].equalsIgnoreCase("stop") && sender.isOp()) {
//						return RoundCommands.stop(args, sender);
//					} else if(args[0].equalsIgnoreCase("end") && sender.isOp()) {
//						return RoundCommands.end(args, sender);
//					} else if(args[0].equalsIgnoreCase("settime") && sender.isOp()) {
//						return RoundCommands.setTime(args, sender);
//					}
					
					
					
					/*else if(args[0].equalsIgnoreCase("modifiers")) {
						return ModifierCommands.getCurrentModifiers(args, sender);
					} else if(args[0].equalsIgnoreCase("setmodifier")) {
						return ModifierCommands.getAccessableModifiers(sender);
					} else if(args[0].equalsIgnoreCase("withmultiweapons")) {
						if (Game.waiting()) {
							Modifiers.setMod(Mod.WITH_MULTIWEAPONS, true);
							ItemStack newLasergun = Weapons.lasergunItem;
							ItemStack newDagger = Weapons.daggerItem;
							newLasergun.removeEnchantment(Enchantment.DAMAGE_ALL);
							ItemMeta newLasergunMeta = newLasergun.getItemMeta();
							ItemMeta newDaggerMeta = newLasergun.getItemMeta();
							for (Player ap : Game.players()) {
								if (Game.teams()) {
									newLasergunMeta.setDisplayName(Game.getTeamColor(Game.getPlayerTeam(ap)).getChatColor()
											+ "ｧlLasergun #" + (Game.getTeamColor(Game.getPlayerTeam(ap)).getOrdinal()+1));
									newDaggerMeta.setDisplayName(Game.getTeamColor(Game.getPlayerTeam(ap)).getChatColor()
											+ "ｧlDagger #" + (Game.getTeamColor(Game.getPlayerTeam(ap)).getOrdinal()+1));
								} else {
									int ordinal = Game.getPlayerColor(ap).getOrdinal();
									newLasergunMeta.setDisplayName(Game.getPlayerColor(ap).getChatColor() + "ｧlLasergun #" + (ordinal + 1));
									newDaggerMeta.setDisplayName(Game.getPlayerColor(ap).getChatColor() + "ｧlDagger #" + (ordinal + 1));
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
						} else sender.sendMessage("ｧcPlease register a new round first!");
						return true;
					} */
				} /*else if(args.length == 3) {
					if(!Game.tagging()) {
						if(Game.waiting()) {
							if(args[0].equalsIgnoreCase("setmodifier") && sender.isOp()) {
								return ModifierCommands.setModifier(args, sender);
							}
						}
					}
				}*/
				
				
//				if(args.length > 3) {
//					if(args[0].equalsIgnoreCase("new") && sender.isOp()) {
//						if(args[1].equalsIgnoreCase("solo")) {
//							return RoundCommands.newSolo(args, sender);
//						} else if(args[1].equalsIgnoreCase("teams")) {
//							return RoundCommands.newTeams(args, sender);
//						}
//					}
//				}
			}
		} else {
			sender.sendMessage("ｧcMain world for MiniGames still not found! Please define the main world first to continue!");
			return true;
		}
		sender.sendMessage("ｧcSyntax ERROR! Please use ｧe/lt ｧcto see all commands and their arguments");
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		List<String> list = new ArrayList<String>();
		if(m.worldFound) {
			if(cmd.getName().equalsIgnoreCase("lasertag")) {
				
				list = modifierCommands.getTabComplete(list, sender, args);
				list = roundCommands.getTabComplete(list, sender, args);
				
				if(args.length == 1) {
					
					if(!Game.waiting() && !Game.tagging() && sender.isOp()) list.add("new");
					if(Game.waiting() && sender.isOp()) list.add("start");
//					if(Game.waiting() && sender.isOp()) list.add("withmultiweapons");
					if(Game.waiting() && sender.isOp()) list.add("cancel");
					if(Game.tagging() && sender.isOp()) list.add("stop");
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

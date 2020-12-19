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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.noobedidoob.minigames.lasertag.Lasertag.LtColorNames;
import me.noobedidoob.minigames.lasertag.commands.ModifierCommands;
import me.noobedidoob.minigames.lasertag.commands.RoundCommands;
import me.noobedidoob.minigames.lasertag.methods.Flag;
import me.noobedidoob.minigames.lasertag.methods.Game;
import me.noobedidoob.minigames.lasertag.methods.LaserShooter;
import me.noobedidoob.minigames.lasertag.methods.Modifiers;
import me.noobedidoob.minigames.lasertag.methods.Weapons;
import me.noobedidoob.minigames.main.Minigames;
import me.noobedidoob.minigames.utils.LasertagColor;
import me.noobedidoob.minigames.utils.Map;

public class LaserCommands implements CommandExecutor, TabCompleter {

	private Minigames m;
	public LaserCommands(Minigames minigames) {
		this.m = minigames;
	}
	
	
	String opCommands = "\n§7————————All Lasertag commands————————\n"
					  + "§e/lt new teams <players...> vs <players...> ... <time> [format] <mapname>\n"
					  + "§e/lt new solo <player1> <player2> ... <time> [format] <mapname>\n"
					  + "§e/lt modifiers — §7See current stats of modifiers\n" 
					  + "§e/lt setmodifier — §7See all changeable modifiers\n"
					  + "§e/lt setmodifier <name> <value>\n"
					  + "§e/lt cancel — §7cancel the registrated round\n"
					  + "§e/lt start — §7start the registrated round\n"
					  + "§e/lt stop — §7stop the ongoing game\n"
					  + "§e/lt loadtexturepack — §7stop the ongoing game\n"
					  + "\n————————————————————————————————\n";
	
	
	String commands = "\n———————————————\n"
					+ "/lt modifiers — \n"
					+ "/lt loadtexturepack — \n"
					+ "\n———————————————\n";
	
	
	public HashMap<Player, Flag> playerFlag = new HashMap<Player, Flag>();
	public HashMap<Player, Boolean> flagIsFollowing = new HashMap<Player, Boolean>();
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if(m.worldFound) {
			if(cmd.getName().equalsIgnoreCase("lasertag")) {
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
					} else if(args[0].equalsIgnoreCase("start") && sender.isOp()) {
						return RoundCommands.start(args, sender);
					} else if(args[0].equalsIgnoreCase("cancel") && sender.isOp()) {
						return RoundCommands.cancel(args, sender);
					} else if(args[0].equalsIgnoreCase("stop") && sender.isOp()) {
						return RoundCommands.stop(args, sender);
					} else if(args[0].equalsIgnoreCase("end") && sender.isOp()) {
						return RoundCommands.end(args, sender);
					} else if(args[0].equalsIgnoreCase("modifiers")) {
						return ModifierCommands.getCurrentModifiers(args, sender);
					} else if(args[0].equalsIgnoreCase("setmodifier")) {
						return ModifierCommands.getAccessableModifiers(sender);
					} else if(args[0].equalsIgnoreCase("withmultiweapons")) {
						if (Game.waiting()) {
							Modifiers.setModifier("multiweapons", true);
							Modifiers.setModifier("laserguncooldown", 2);
							LaserShooter.withMultiWeapons = true;
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
						return true;
					} 
				} else if(args.length == 2) {
//					if(args[0].equalsIgnoreCase("test") && sender.isOp()) {
//						if(args[1].equalsIgnoreCase("off")) {
//							Game.disableTesting();
//							for(ArmorStand as : LaserShooter.invisibleStands) {
//								as.setVisible(true);
//							}
//							sender.sendMessage("§aStopped testing Lasertag");
//							return true;
//						} else {
//							try {
//								Lasertag.testWeapon = Weapon.valueOf(args[1].toUpperCase());
//								Game.enableTesting();
//								ItemStack weapon;
//								switch (Lasertag.testWeapon.name()) {
//								case "LASERGUN":
//									weapon = Weapons.lasergunItem.clone();
//									break;
//								case "SHOTGUN":
//									weapon = Weapons.shotgunItem.clone();
//									break;
//								case "SNIPER":
//									weapon = Weapons.sniperItem.clone();
//									break;
//								default:
//									sender.sendMessage("§cYou can't use this weapon!");
//									return true;
//								}
//								ItemMeta im = weapon.getItemMeta();
//								im.setDisplayName(im.getDisplayName()+" TEST");
//								weapon.setItemMeta(im);
//								sender.sendMessage("§aTesting Lasertag with "+Lasertag.testWeapon.name());
//								if(sender instanceof Player) ((Player) sender).getInventory().addItem(weapon);
//							} catch (Exception e) {
//								sender.sendMessage("§cThere is no Weapon called "+args[1]+"!");
//							}
//							return true;
//						}
//					} 
				} else if(args.length == 3) {
					if(!Game.tagging()) {
						if(Game.waiting()) {
							if(args[0].equalsIgnoreCase("setmodifier") && sender.isOp()) {
								return ModifierCommands.setModifier(args, sender);
							}
						}
					}
					if(args[0].equalsIgnoreCase("settime") && sender.isOp()) {
						return RoundCommands.setTime(args, sender);
					}
				}
				if(args.length > 3) {
					if(args[0].equalsIgnoreCase("new") && sender.isOp()) {
						if(args[1].equalsIgnoreCase("solo")) {
							return RoundCommands.newSolo(args, sender);
						} else if(args[1].equalsIgnoreCase("teams")) {
							return RoundCommands.newTeams(args, sender);
						}
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
	
	boolean loaded = false;
	List<String> modifiersList = new ArrayList<String>();
	public void loadMods() {
		modifiersList.add("laserguncooldown");
		modifiersList.add("snipercooldown");
		modifiersList.add("shotguncooldown");
		modifiersList.add("points");
		modifiersList.add("snipe");
		modifiersList.add("minsnipedistance");
		modifiersList.add("closerange");
		modifiersList.add("backstab");
		modifiersList.add("pvp");
		modifiersList.add("headshot");
		modifiersList.add("strike");
		modifiersList.add("minkillstrike");
		modifiersList.add("multikill");
		modifiersList.add("heightaddon");
		modifiersList.add("widthaddon");
		modifiersList.add("glowamplifier");
		modifiersList.add("highlightplayers");
		modifiersList.add("shootthroughblocks");
		modifiersList.add("spawnatbase");
		modifiersList.add("withevents");
		modifiersList.add("withminigun");
		modifiersList.add("withammo");
		modifiersList.add("magazinesize");
		modifiersList.add("magazinereloadtime");
		loaded = true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		List<String> tabCompleteList = new ArrayList<String>();
		if(m.worldFound) {
			if(!loaded) loadMods();
			if(cmd.getName().equalsIgnoreCase("lasertag")) {
				if(args.length == 1) {
					if(!Game.waiting() && !Game.tagging() && sender.isOp()) tabCompleteList.add("new");
					if(Game.waiting() && sender.isOp()) tabCompleteList.add("start");
					if(Game.waiting() && sender.isOp()) tabCompleteList.add("withmultiweapons");
					if(Game.waiting() && sender.isOp()) tabCompleteList.add("cancel");
					if(Game.tagging() && sender.isOp()) tabCompleteList.add("stop");
					tabCompleteList.add("modifiers");
					tabCompleteList.add("loadtexturepack");
					if(Game.waiting() && sender.isOp()) tabCompleteList.add("setmodifier");
					refreshTabCommplete(tabCompleteList, args[0]);
				} else if(args.length == 2) {
					String prevArg = args[args.length-2];
					if(prevArg.equalsIgnoreCase("new")) {
						if(sender.isOp()) tabCompleteList.add("solo");
						if(sender.isOp()) tabCompleteList.add("teams");
					} else if(prevArg.equalsIgnoreCase("setmodifier")) {
						for(String s : modifiersList) {
							if(sender.isOp()) tabCompleteList.add(s);
						}
					}
					refreshTabCommplete(tabCompleteList, args[1]);
				} else {
					String prevArg = args[args.length-2];
					if(prevArg.equalsIgnoreCase("highlightplayers") | prevArg.equalsIgnoreCase("shootthroughblocks") | prevArg.equalsIgnoreCase("spawnatbase") | prevArg.equalsIgnoreCase("withevents")) {
						if(sender.isOp()) tabCompleteList.add("true");
						if(sender.isOp()) tabCompleteList.add("false");
					} else if(!modifiersList.contains(prevArg)){
						try {
							@SuppressWarnings("unused")
							int i = Integer.parseInt(prevArg);
							i++;
							if(sender.isOp()) {
								for(Map map : Lasertag.maps.values()) {
									if(map.approved) tabCompleteList.add(map.getName());
								}
							}
						} catch (NumberFormatException e) {
							try {
								@SuppressWarnings("unused")
								int i = Integer.parseInt(args[args.length-3]);
								i++;
								if(sender.isOp()) {
									for(Map map : Lasertag.maps.values()) {
										if(map.approved) tabCompleteList.add(map.getName());
									}
								}
							} catch (NumberFormatException e2) {
								if(args[1].equalsIgnoreCase("teams")) {
									if(args[args.length-3] != "vs") {
										if(sender.isOp()) tabCompleteList.add("vs");
									}
								}
								for(Player p : Bukkit.getOnlinePlayers()) {
									if(!String.join(" ", args).contains(p.getName())) 
									if(sender.isOp()) tabCompleteList.add(p.getName());
								}
							}
						}
					}
					refreshTabCommplete(tabCompleteList, args[args.length-1]);
				}
			} 
		}
		return tabCompleteList;
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

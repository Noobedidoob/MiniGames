package me.noobedidoob.minigames.lasertag.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.Lasertag.LtColorNames;
import me.noobedidoob.minigames.lasertag.methods.Game;
import me.noobedidoob.minigames.lasertag.methods.Leaderboard;
import me.noobedidoob.minigames.lasertag.commands.ModifierCommands.Mod;
import me.noobedidoob.minigames.lasertag.methods.RoundManager;
import me.noobedidoob.minigames.lasertag.methods.Weapons;
import me.noobedidoob.minigames.main.Minigames;
import me.noobedidoob.minigames.utils.Map;
import me.noobedidoob.minigames.utils.MgUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class RoundCommands {
	

	@SuppressWarnings("unused")
	private Minigames minigames;
	public RoundCommands(Minigames minigames) {
		this.minigames = minigames;
	}
	
	

	public void perform(CommandSender sender, String[] args) {
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("start") && sender.isOp()) {
				if (start(args, sender)) return;
			} else if (args[0].equalsIgnoreCase("cancel") && sender.isOp()) {
				if (cancel(args, sender)) return;
			} else if (args[0].equalsIgnoreCase("stop") && sender.isOp()) {
				if (stop(args, sender)) return;
			} else if (args[0].equalsIgnoreCase("end") && sender.isOp()) {
				if (end(args, sender)) return;
			} 
		} else if(args.length > 3) {
			if(args[0].equalsIgnoreCase("new") && sender.isOp()) {
				if(args[1].equalsIgnoreCase("solo")) {
					if(newSolo(args, sender)) return;
				} else if(args[1].equalsIgnoreCase("teams")) {
					if(newTeams(args, sender)) return;
				}
			}
		}
		sender.sendMessage("§cSyntax ERROR! Please use §e/lt §cto see all commands and their arguments");
	}
	
	public List<String> getTabComplete(List<String> list, CommandSender sender, String[] args) {
		if(args.length == 1) {
			
			if(!Game.waiting() && !Game.tagging() && sender.isOp()) list.add("new");
			if(Game.waiting() && sender.isOp()) list.add("start");
			if(Game.waiting() && sender.isOp()) list.add("cancel");
			if(Game.tagging() && sender.isOp()) list.add("stop");
		} else if(args.length == 2) {
			String prevArg = args[args.length-2];
			if(prevArg.equalsIgnoreCase("new")) {
				if(sender.isOp()) list.add("solo");
				if(sender.isOp()) list.add("teams");
			} 
		} else {
			String prevArg = args[args.length-2];
			try {
				if(Mod.valueOf(prevArg.toUpperCase().replaceAll("-", "_")) != null) return list;
			} catch (IllegalArgumentException e) {
				
			}
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
			
		}
		
		return list;
	}
	
	public static boolean cancel(String[] args, CommandSender sender) {
		if(Game.waiting()) {
			sender.sendMessage("§aCancelled the round!");
			RoundManager.cancel();
		} else sender.sendMessage("§cThere is no registrated round to cancel!");
		return true;
	}
	
	public static boolean start(String[] args, CommandSender sender) {
		if(!Game.tagging()) {
			if(Game.waiting()) {
				if(Mod.withMultiweapons()) {
					boolean allReady = true;
					Player[] allPlayers = Game.players();
					for(Player ap : allPlayers) {
						if(!Weapons.hasChoosenWeapon.get(ap)) allReady = false;
					}
					if(allReady) {
						RoundManager.start();
					} else {
						List<Player> unreadyPlayersList = new ArrayList<Player>();
						for(Player ap : allPlayers) {
							if(!Weapons.hasChoosenWeapon.get(ap)) {
								unreadyPlayersList.add(ap);
							}
						}
						String unreadyPlayers = "";
						int i = 0;
						for(Player aup : unreadyPlayersList) {
							if(i == 0) unreadyPlayers = Game.getPlayerColor(aup)+aup.getName();
							else if(i == 1) {
								if(unreadyPlayersList.size() == 2) unreadyPlayers += " §rand "+Game.getPlayerColor(aup)+aup.getName();
								else unreadyPlayers += "§r, "+Game.getPlayerColor(aup)+aup.getName();
							} else if(i == unreadyPlayersList.size()-1) unreadyPlayers += " §rand "+Game.getPlayerColor(aup)+aup.getName();
							else unreadyPlayers += "§r, "+Game.getPlayerColor(aup)+aup.getName();
							i++;
						}
						String addon = "";
						String itsTheir = "its";
						if(unreadyPlayersList.size() > 1) {
							addon = "s";
							itsTheir = "their";
						}
						sender.sendMessage("§cNot everyone is ready! The player"+addon+" "+unreadyPlayers+" §cdidn't choose "+itsTheir+" weapon yet!");
					}
				} else {
					RoundManager.start();
				}
				return true;
			} else sender.sendMessage("§cThere is no registrated round! Please use §e/lt new <teams | solo> §cto registrate a new round"); return true;
		} else sender.sendMessage("§cThe game is already running! Type §e/lt stop §cto stop the game"); return true;
	
	}
	public static boolean stop(String[] args, CommandSender sender) {
		if(Game.tagging()) {
			for(Player igp : Game.players()) {
				igp.sendTitle("§c§lStopped the game!", "", 20, 20*5, 20);
			}
			Leaderboard.time = 0;
			RoundManager.stop(true);
		} else sender.sendMessage("§cThere is no round to stop!");
		return true;
	}
	public static boolean end(String[] args, CommandSender sender) {
		if(Game.tagging()) {
			Leaderboard.time = 0;
		} else sender.sendMessage("§cThere is no round to end!");
		return true;
	}
	//TODO: Players Choosing own Team
	@SuppressWarnings("deprecation")
	public static boolean newSolo(String[] args, CommandSender sender) {
		if(!Game.tagging()) {
			if(!Game.waiting()) {
				long time = 0;
				String timeName = args[args.length-3].trim();
				String format = args[args.length-2].trim();
//				sender.sendMessage(format);
				if(!format.toLowerCase().contains("m") && !format.toLowerCase().contains("s") && !format.toLowerCase().contains("h")) {
					format = null;
					timeName = args[args.length-2].trim();
					//TODO Accept time format
				}
				try {
					time = MgUtils.getTimeFromArgs(timeName, format);
				} catch (NumberFormatException e) {
					sender.sendMessage("§cThe given argument §e"+e.getMessage().replace("For input string: ","")+" §cis not a valid number for this type of argument!");
					return true;
				} catch (Exception e) {
					sender.sendMessage("§cSyntax error: "+e.getMessage());
					return true;
				}
				
				
				String allArgs = String.join(" ", args);
				allArgs = allArgs.replace("new solo ", "");
				allArgs = allArgs.replace(" "+args[args.length-2]+" "+args[args.length-1], "");
				
				
				String[] playersNames = allArgs.split(" ");
				List<Player> playerList = new ArrayList<Player>();
				for(String n : playersNames) {
					if(Bukkit.getPlayer(n) != null) {
						if(!playerList.contains(Bukkit.getPlayer(n))) playerList.add(Bukkit.getPlayer(n));
						else {
							sender.sendMessage("§cPlayer §e"+n+" §cis already in the list!"); 
							return true;
						}
					} else {
						sender.sendMessage("§cThe player §e"+n+"§c is not online!"); 
						return true;
					}
				}
				Player[] players = new Player[playerList.size()];
				players = playerList.toArray(players);
				
				if(players.length < 2) {
					sender.sendMessage("§cYou can't play lasertag with less than 2 players ;D!");
					return true;
				}
				
				String mapName = args[args.length-1];
				if(Lasertag.maps.get(mapName) != null) {
					Map map = Lasertag.maps.get(mapName);
					if(!map.withRandomSpawn()) {
						if(map.getBaseAmount() == 2) {
							if(players.length != 2) {
								sender.sendMessage("§cToo much players! Please choose another map, decrease the number of players to 2 or play in teams!");
								return true;
							}
						} else {
							if(players.length > map.getBaseAmount()) {
								sender.sendMessage("§cToo much players! Please choose another map, decrease the number of players to "+map.getBaseAmount()+" or play in teams!");
								return true;
							}
						}
						Game.spawnAtBases = true;
					} else Game.spawnAtBases = false;
					
					try {
						Minigames.world.setSpawnLocation(map.getCenterCoord().getLocation(Minigames.world));
					} catch (NullPointerException e) {
						e.printStackTrace();
					}
				} else {
					sender.sendMessage("§cThis map doesn't exist!");
					return true;
				}
				
				Game.register(true, Lasertag.maps.get(mapName), players, false);
				Leaderboard.time = time;
				RoundManager.registerSolo();
				
				sender.sendMessage("§aRegistrated the solo round with the players "+allArgs+"! Type §e/lt start §ato begin");
				
				TextComponent yMessage = new TextComponent("START");
				yMessage.setColor(net.md_5.bungee.api.ChatColor.GOLD);
				yMessage.setBold(true);
				yMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lt start"));
				yMessage.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Start the registrated round").create() ) );
				TextComponent nMessage = new TextComponent("CANCEL");
				nMessage.setColor(net.md_5.bungee.api.ChatColor.RED);
				nMessage.setBold(true);
				nMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lt cancel"));
				nMessage.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Cancel the registrated round").create() ) );
				TextComponent separator = new TextComponent(" | ");
				separator.setColor(net.md_5.bungee.api.ChatColor.WHITE);
				yMessage.addExtra(separator);
				yMessage.addExtra(nMessage);
				
				if(sender instanceof Player) {
					((Player) sender).spigot().sendMessage(yMessage);
				}
			} else sender.sendMessage("§bThere already is a registrated round! Use §e/lt cancel §cto cancel it");
		} else sender.sendMessage("§cThere already is a ongoing game! Please use §e/lt stop §cto stop it");
		return true;
	}
	@SuppressWarnings("deprecation")
	public static boolean newTeams(String[] args, CommandSender sender) {

		if(!Game.tagging()) {
			if(!Game.waiting()) {
				long time = 0;
				String timeName = args[args.length-3].trim();
				String format = args[args.length-2].trim();
				if(!format.equalsIgnoreCase("m") && !format.equalsIgnoreCase("s") && !format.equalsIgnoreCase("h") 
				&& !format.toLowerCase().contains("minute") && !format.toLowerCase().contains("second") && !format.toLowerCase().contains("hour")) {
					format = null;
					timeName = args[args.length-2].trim();
				}
				try {
					time = MgUtils.getTimeFromArgs(timeName, format);
				} catch (NumberFormatException e) {
					sender.sendMessage("§cThe given argument §e"+e.getMessage().replace("For input string: ","")+" §cis not a valid number for this type of argument!");
					return true;
				} catch (Exception e) {
					sender.sendMessage("§cSyntax error: "+e.getMessage());
					return true;
				}
			
				
				String allArgs = String.join(" ", args);
				allArgs = allArgs.replace("new teams ", "");
				allArgs = allArgs.replace(" "+args[args.length-2]+" "+args[args.length-1], "");
				
				String allNames = allArgs.replace("vs ", "");
				String[] names = allNames.split(" ");
				List<Player> allPlayersList = new ArrayList<Player>();
				for(String n : names) {
					System.out.println("Adding "+n);
					if(Bukkit.getPlayer(n) != null) {
						if(!allPlayersList.contains(Bukkit.getPlayer(n))) allPlayersList.add(Bukkit.getPlayer(n));
						else {
							sender.sendMessage("§cPlayer §e"+n+" §cis already in the list!"); 
							return true;
						}
					} else {
						sender.sendMessage("§cThe player §e"+n+"§c is not online!"); 
						return true;
					}
				}
				Player[] allPlayers = new Player[allPlayersList.size()];
				allPlayers = allPlayersList.toArray(allPlayers);
				
				
				String[] teamsInString = allArgs.split(" vs ");
				List<String[]> teamsInListString = new ArrayList<String[]>();
				for(String t : teamsInString) {
					teamsInListString.add(t.split(" "));
				}
				
				List<Player[]> teamsInList = new ArrayList<Player[]>();
				for(String[] team : teamsInListString) {
					List<Player> playerInTeamList = new ArrayList<Player>();
					for(String name : team) {
						if(Bukkit.getPlayer(name) != null) playerInTeamList.add(Bukkit.getPlayer(name));
					}
					Player[] playerHolder = new Player[playerInTeamList.size()];
					playerHolder = playerInTeamList.toArray(playerHolder);
					teamsInList.add(playerHolder);
				}
				
				if(teamsInList.size() < 2) {
					sender.sendMessage("§cYou can't play lasertag with less than 2 teams!");
					return true;
				}
				
				String mapName = args[args.length-1];
				if(Lasertag.maps.get(mapName) != null) {
					Map map = Lasertag.maps.get(mapName);
					if(map.withBaseSpawn()) {
						if(map.getBaseAmount() == 2) {
							if(teamsInList.size() > 2) {
								sender.sendMessage("§cToo many teams! Please choose another map or decrease the number of teams to 2!");
								return true;
							}
						} else if(teamsInList.size() > map.getBaseAmount()) {
							sender.sendMessage("§cToo many teams! Please choose another map or decrease the number of teams to "+map.getBaseAmount()+"!");
							return true;
						}
						Game.spawnAtBases = true;
					} else {
						Game.spawnAtBases = false;
					}
					
					Minigames.world.setSpawnLocation(map.getCenterCoord().getLocation(Minigames.world));
				} else {
					sender.sendMessage("§cThis map doesn't exist!");
					return true;
				}
				
				if(teamsInList.size() > LtColorNames.values().length) {
					sender.sendMessage("§cToo many teams! Limit is "+LtColorNames.values().length);
				}
				
				Game.register(false, Lasertag.maps.get(mapName), allPlayers, false);
				Leaderboard.time = time;
				RoundManager.registerTeams(teamsInList);
				
				
				sender.sendMessage("§aRegistrated the team round with the teams "+allArgs+"! Type §e/lt start §ato begin");
				
				TextComponent yMessage = new TextComponent("START");
				yMessage.setColor(net.md_5.bungee.api.ChatColor.GOLD);
				yMessage.setBold(true);
				yMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lt start"));
				yMessage.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Start the registrated round").create() ) );
				TextComponent nMessage = new TextComponent("CANCEL");
				nMessage.setColor(net.md_5.bungee.api.ChatColor.RED);
				nMessage.setBold(true);
				nMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lt cancel"));
				nMessage.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Cancel the registrated round").create() ) );
				TextComponent separator = new TextComponent(" | ");
				separator.setColor(net.md_5.bungee.api.ChatColor.WHITE);
				yMessage.addExtra(separator);
				yMessage.addExtra(nMessage);
				
				if(sender instanceof Player) {
					((Player) sender).spigot().sendMessage(yMessage);
				}
			} else sender.sendMessage("§cThere already is a registrated round! Use §e/lt cancel §cto cancel it");
		} else sender.sendMessage("§cThere already is a ongoing game! Please use §e/lt stop §cto stop it");
		return true;
	}
	public static boolean setTime(String[] args, CommandSender sender) {
		if(Game.tagging()) {
			try {
				String timeName = args[1];
				String format = "m";
				if(args.length > 2) format = args[2];
				if(!format.contains("m") && !format.contains("s") && !format.contains("h")) Leaderboard.time = MgUtils.getTimeFromArgs(timeName, "m");
				else Leaderboard.time = MgUtils.getTimeFromArgs(timeName, format);
				Bukkit.broadcastMessage("§a§lChanged the rounds time to "+MgUtils.getTimeFormatFromLong(MgUtils.getTimeFromArgs(timeName, format), format.substring(0, 1))+format);
			} catch (NumberFormatException e) {
				sender.sendMessage("§cThe given argument §e"+e.getMessage().replace("For input string: ","")+" §cis not a Number!");
				return true;
			} catch (Exception e) {
				sender.sendMessage("§cSyntax error: "+e.getMessage());
				return true;
			}
		} else sender.sendMessage("§cYou may only perform this command in a running game");
		return true;
	}
}

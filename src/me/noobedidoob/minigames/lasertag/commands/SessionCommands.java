package me.noobedidoob.minigames.lasertag.commands;

import java.util.ArrayList;
import java.util.List;

import me.noobedidoob.minigames.Commands;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import me.noobedidoob.minigames.lasertag.session.Session;
import me.noobedidoob.minigames.lasertag.methods.Inventories;
import me.noobedidoob.minigames.lasertag.session.SessionTeam;
import me.noobedidoob.minigames.Minigames;
import me.noobedidoob.minigames.utils.Utils.TimeFormat;

public class SessionCommands implements CommandExecutor, TabCompleter{
	
	
	private final Minigames minigames;
	public SessionCommands(Minigames minigames) {
		this.minigames = minigames;
	}

	String commands = "\nｧ7覧覧覧覧 ｧbSession Commandsｧ7 覧覧覧覧予n"
			+ "ｧ6 new ｧ7� Create new session\n"
			+ "ｧ6 new ｧ7<ｧ6soloｧ7 | ｧ6teamsｧ7> � Create new session\n"
			+ "ｧ6 join ｧ7<ｧ6ownerｧ7> � Join a players session\n"
			+ "ｧ6 start ｧ7� Start session\n"
			+ "ｧ6 invite ｧ7<ｧ6playerｧ7> ｧ7� Invite player to session\n"
			+ "ｧ6 addAdmin ｧ7� Promote player to admin\n"
			+ "ｧ6 removeAdmin ｧ7� Demote player from admin\n"
			+ "ｧ6 setSolo ｧ7� Change to solo\n"
			+ "ｧ6 setTeams ｧ7� Change to teams\n"
			+ "ｧ6 setTeamsAmount ｧ7� Set amount of teams\n"
			+ "ｧ6 setWithMultiWeapons ｧ7<ｧ6trueｧ7|ｧ6falseｧ7> � Set amount of teams\n"
			+ "ｧ6 setWithCaptureTheFlag ｧ7<ｧ6trueｧ7|ｧ6falseｧ7> � Set amount of teams\n"
			+ "ｧ6 stop ｧ7� Stop session\n"
			+ "ｧ6 close ｧ7� Close session\n"
			+ "ｧ6 setTime ｧ7<ｧ6timeｧ7> <ｧ6formatｧ7> ｧ7� Start session\n"
			+ "ｧ6 kick ｧ7<ｧ6playerｧ7> � Kick player\n"
			+ "ｧ6 leave ｧ7� Leave session\n  "
			+ "\nｧa Use ｧ6/session ｧ7<ｧ6commandｧ7> ｧato perform a command!\n"
			+ "ｧ7覧覧覧覧覧覧覧覧覧覧覧覧覧覧\n  ";
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if(args.length == 0){
			sender.sendMessage(commands);
			return true;
		}

		if(sender instanceof Player){
			Player p = (Player) sender;
			Session s = Session.getPlayerSession(p);

			if (args.length == 1) {
				if(s == null) {
					if (args[0].equalsIgnoreCase("new")) {
						Inventories.openNewSessionInv(p);
						return true;
					}
				} else {
					if (args[0].equalsIgnoreCase("start")) {
						if (s.isAdmin(p)) {
							if(s.getPlayers().length > 0) {
								boolean enoughTeams = true;
								if(s.isTeams()) {
									int teamsWithPlayers = 0;
									for(SessionTeam team : s.getTeams()) {
										if(team.getPlayers().length > 0) teamsWithPlayers++;
									}
									if(teamsWithPlayers < 2) enoughTeams = false;
								}
								if(enoughTeams) s.start(false);
								else Session.sendMessage(p, "ｧcThere must be at least 2 teams with at least 1 player in it!");
							} else Session.sendMessage(p, "ｧcNot enough players!");
						} else Session.sendMessage(p, "ｧaYou need to be an admin of this session to perform this command!");
						return true;
					} 
					
					else if (args[0].equalsIgnoreCase("stop")) {
						if (s.isAdmin(p)) {
							s.stop(true, false);
						} else Session.sendMessage(p, "ｧcYou have to be an admin of this session to perform this command!");
						return true;
					} 
					
					else if(args[0].equalsIgnoreCase("close")) {
						if(s.getOwner() == p) {
							if(s.tagging()) s.stop(true, true);
							else s.close();
						} else Session.sendMessage(p, "ｧcYou have to be the owner of this session to perform this command");
						return true;
					}
					
					else if(args[0].equalsIgnoreCase("leave")) {
						s.removePlayer(p);
						return true;
					} 
					
					else if(args[0].equalsIgnoreCase("setTime")) {
						if(s.isAdmin(p)) {
							Inventories.openTimeInv(p);
						} else Session.sendMessage(p, "ｧcYou have to be an admin of this session to perform this command!");
						return true;
					} 
					
					else if(args[0].equalsIgnoreCase("addAdmin") | args[0].equalsIgnoreCase("setAdmin")) {
						if(s.isAdmin(p)) {
							if (!s.tagging()) {
								Inventories.openAddAdminInv(p);
							} else Session.sendMessage(p, "ｧcYou can't promote players while the game is running!");
						} else Session.sendMessage(p, "ｧcYou have to be an admin of this session to perform this command!");
						return true;
					}
					
					else if(args[0].equalsIgnoreCase("setSolo")) {
						if (s.isAdmin(p)) {
							if (s.waiting()) {
								s.setTeamsAmount(0);
							} else Session.sendMessage(p, "ｧcYou can't perform this command in a running round!");
						} else Session.sendMessage(p, "ｧcYou have to be an admin of this session to perform this command!");
						return true;
					}
					else if(args[0].equalsIgnoreCase("setTeams")) {
						if (s.isAdmin(p)) {
							if (s.waiting()) {
								s.setTeamsAmount(2);
							} else Session.sendMessage(p, "ｧcYou can't perform this command in a running round!");
						} else Session.sendMessage(p, "ｧcYou have to be an admin of this session to perform this command!");
						return true;
					}
					else if(args[0].equalsIgnoreCase("setTeamAmount") | args[0].equalsIgnoreCase("setTeamsAmount")) {
						if (s.isAdmin(p)) {
							if (s.waiting()) {
								Inventories.openTeamsInv(p);
							} else Session.sendMessage(p, "ｧcYou can't perform this command in a running round!");
						} else Session.sendMessage(p, "ｧcYou have to be an admin of this session to perform this command!");
						return true;
					}
				}
				
			} else if(args.length == 2) {
				if(args[0].equalsIgnoreCase("new")) {
					if(args[1].equalsIgnoreCase("solo") | args[1].equalsIgnoreCase("teams")) {
						if(Session.isPlayerInSession(p)) {
							Session.sendMessage(p, "ｧcPlease leave this session first!");
							return true;
						}
						Session.sendMessage(p, "ｧaRegistered new Session!");
						new Session(minigames, p, (!args[1].equalsIgnoreCase("teams")));
						return true;
					}
				} 
				
				else if(args[0].equalsIgnoreCase("join")) {
					if(s != null) {
						Session.sendMessage(p, "ｧcPlease leave this session first!");
						return true;
					}
					Session invS = Session.getSessionFromName(args[1]);
					if(invS != null) {
						if(!invS.isPlayerBanned(p)) {
							if (!invS.tagging()) {
								invS.addPlayer(p);
							} else Session.sendMessage(p, "ｧcThe plasyers are currently in-game. Please wait!");
						} else Session.sendMessage(p, "ｧcYou are banned from this session!");
					} else Session.sendMessage(p, "ｧcThis invitation expired!");
					return true;
				} 
				
				else if(args[0].equalsIgnoreCase("kick") | args[0].equalsIgnoreCase("ban")) {
					if(s != null) {
						if (s.isAdmin(p)) {
							Player kp = Bukkit.getPlayer(args[1]);
							if (kp != null) {
								if(s.isInSession(kp)) {
									if(kp != s.getOwner()) s.banPlayer(kp, p);
									else Session.sendMessage(p, "ｧcYou can't kick the owner!");
								} else Session.sendMessage(p, "ｧb"+args[1]+" ｧcis not in this session!");
							} else Session.sendMessage(p, "ｧcPlayer ｧb"+args[1]+" ｧcnot found!");
						} else Session.sendMessage(p, "ｧcYou have to be an admin of this session to perform this command!");
					} else Session.sendMessage(p, "ｧcYou're not in a session!");
					return true;
				} 
				
				else if(args[0].equalsIgnoreCase("addAdmin") | args[0].equalsIgnoreCase("promoteAdmin") | args[0].equalsIgnoreCase("setAdmin")) {
					if(s != null) {
						if(s.isAdmin(p)) {
							Player ap = Bukkit.getPlayer(args[1]);
							if(ap != null) {
								if (s.isInSession(ap)) {
									if (!s.isAdmin(ap)) {
										if (!s.tagging()) {
											s.addAdmin(ap);
											Session.sendMessage(p, "ｧaPromoted ｧb"+ap.getName()+" ｧato Admin!");
										} else Session.sendMessage(p, "ｧcYou can't demote players while the game is running!");
									} else Session.sendMessage(p, "ｧb"+args[1]+" ｧcis already an admin of this session!");
								} else Session.sendMessage(p, "ｧb"+args[1]+" ｧcis not in this session!");
							} else Session.sendMessage(p, "ｧcPlayer ｧb"+args[1]+" ｧcnot found!");
						} else Session.sendMessage(p, "ｧcYou have to be an admin of this session to perform this command!");
					} else Session.sendMessage(p, "ｧcYou're not in a session!");
					return true;
				}
				
				else if(args[0].equalsIgnoreCase("removeAdmin") | args[0].equalsIgnoreCase("demoteAdmin")) {
					if(s != null) {
						if(s.isAdmin(p)) {
							Player dp = Bukkit.getPlayer(args[1]);
							if(dp != null) {
								if (s.isInSession(dp)) {
									if (s.isAdmin(dp)) {
										if(dp != s.getOwner()) {
											if (!s.tagging()) {
												s.removeAdmin(dp);
												Session.sendMessage(p, "ｧaDemoted ｧb"+dp.getName()+" ｧafrom Admin!");
											} else Session.sendMessage(p, "ｧcYou can't demote players while the game is running!");
										} else Session.sendMessage(p, "ｧcYou can't do this to the owner!");
									} else Session.sendMessage(p, "ｧb"+args[1]+" ｧcis not an admin of this session!");
								} else Session.sendMessage(p, "ｧb"+args[1]+" ｧcis not in this session!");
							} else Session.sendMessage(p, "ｧcPlayer ｧb"+args[1]+" ｧcnot found!");
						} else Session.sendMessage(p, "ｧcYou have to be an admin of this session to perform this command!");
					} else Session.sendMessage(p, "ｧcYou're not in a session!");
					return true;
				}
				
				else if(args[0].equalsIgnoreCase("invite")) {
					if(s != null) {
						if(s.isAdmin(p)) {
							Player ip = Bukkit.getPlayer(args[1]);
							if(ip != null) {
								if (Session.getPlayerSession(ip) == null) {
									s.sendInvitation(ip);
									Session.sendMessage(p, "ｧaInvitation sent!");
								} else Session.sendMessage(p, "ｧb"+args[1]+" ｧcis already in a session!");
							} else Session.sendMessage(p, "ｧcPlayer ｧb"+args[1]+" ｧcnot found!");
						} else Session.sendMessage(p, "ｧcYou have to be an admin of this session to perform this command!");
					} else Session.sendMessage(p, "ｧcYou're not in a session!");
					return true;
				}
				
				else if(args[0].equalsIgnoreCase("setTeams")) {
					if(s != null) {
						if(s.isAdmin(p)) {
							try {
								int amount = Integer.parseInt(args[1]);
								if(amount > 1) {
									s.setTeamsAmount(amount);
								} else {
									Session.sendMessage(p, "ｧcYou have to give a valid number (minimum of 2)!");
								}
							} catch (NumberFormatException e) {
								Session.sendMessage(p, "ｧcYou have to give a valid number!");
							}
						} else Session.sendMessage(p, "ｧcYou have to be an admin of this session to perform this command!");
					} else Session.sendMessage(p, "ｧcYou're not in a session!");
					return true;
				}
				
				else if(args[0].equalsIgnoreCase("setTeamAmount") | args[0].equalsIgnoreCase("setTeamsAmount")) {
					if(s != null) {
						if(s.isAdmin(p)) {
							if (s.waiting()) {
								try {
									int amount = Integer.parseInt(args[1]);
									s.setTeamsAmount(amount);
								} catch (NumberFormatException e) {
									Session.sendMessage(p, "ｧcYou have to give a valid number!");
								}
							} else Session.sendMessage(p, "ｧcYou can't perform this command in a running round!");
						} else Session.sendMessage(p, "ｧcYou have to be an admin of this session to perform this command!");
					} else Session.sendMessage(p, "ｧcYou're not in a session!");
					return true;
				}

				else if(args[0].equalsIgnoreCase("setWithMultiWeapons")) {
					if(s != null) {
						if (s.isAdmin(p)) {
							if (s.waiting()) {
								s.setWithMultiWeapons(Boolean.parseBoolean(args[1]));
							} else Session.sendMessage(p, "ｧcYou can't perform this command in a running round!");
						} else Session.sendMessage(p,"ｧcYou have to be an admin of this session to perform this command!");
					} else Session.sendMessage(p, "ｧcYou're not in a session!");
					return true;
				}

				else if(args[0].equalsIgnoreCase("setWithCaptureTheFlag")) {
					if(s != null) {
						if (s.isAdmin(p)) {
							if (s.waiting()) {
								s.setWithCaptureTheFlag(Boolean.parseBoolean(args[1]));
							} else Session.sendMessage(p, "ｧcYou can't perform this command in a running round!");
						} else Session.sendMessage(p,"ｧcYou have to be an admin of this session to perform this command!");
					} else Session.sendMessage(p, "ｧcYou're not in a session!");
					return true;
				}
			}
			if(args[0].equalsIgnoreCase("setTime")) {
				if(s != null) {
					if(s.isAdmin(p)) {
						try {
							int time = Integer.parseInt(args[1]);
							TimeFormat format = TimeFormat.MINUTES;
							if(args.length == 3) format = TimeFormat.getFromString(args[2]);
							s.setTime(time, format, true);
						} catch (NumberFormatException e) {
							sender.sendMessage("ｧcThe given argument ｧe"+StringUtils.replace(e.getMessage(), "For input string: ","")+" ｧcis not a Number!");
							return true;
						} catch (Exception e) {
							sender.sendMessage("ｧcSyntax error: "+e.getMessage());
							return true;
						}
					} else Session.sendMessage(p, "ｧcYou have to be an admin of this session to perform this command!");
				} else Session.sendMessage(p, "ｧcYou're not in a session!");
				return true;
			}
		} else sender.sendMessage("You may only perform this command as a player!");
		
		
		
		sender.sendMessage("ｧcSyntax error! Please use ｧe/sessions ｧcto see all commands and their arguments");
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		List<String> list = new ArrayList<>();
		if(!(sender instanceof Player)) return list;

		Player p = (Player) sender;
		Session s = Session.getPlayerSession(p);
		if(args.length == 1) {
			if(s == null) {
				list.add("new");
				list.add("join");
			} else {
				if(s.isAdmin(p)) {
					if(!s.tagging()) {
						list.add("start");
						list.add("invite");
						list.add("addAdmin");
						list.add("removeAdmin");
						list.add("setSolo");
						list.add("setTeams");
						list.add("setTeamsAmount");
						list.add("setWithMultiWeapons");
						list.add("setWithCaptureTheFlag");
					}
					list.add("stop");
					list.add("close");
					list.add("setTime");
					list.add("kick");
					list.add("leave");
				}
			}
		} else if(args.length == 2) {
			if (s == null) {
				if (args[0].equalsIgnoreCase("new")) {
					list.add("solo");
					list.add("teams");
				} else if(args[0].equalsIgnoreCase("join")) {
					for(Player op : Bukkit.getOnlinePlayers()) {
						Session ops = Session.getPlayerSession(op);
						if(ops != null) {
							if(!list.contains(ops.getOwner().getName()) && ops.getOwner().getName().toLowerCase().contains(args[1].toLowerCase())) list.add(ops.getOwner().getName());
						}
					}
				}
			} else if(s.isAdmin(p)){
				if(args[0].equalsIgnoreCase("addAdmin") | args[0].equalsIgnoreCase("setAdmin")) {
					for(Player ap : s.getPlayers()) {
						if(!s.isAdmin(ap) && ap != p && ap.getName().toLowerCase().contains(args[1].toLowerCase())) list.add(ap.getName());
					}
				} else if(args[0].equalsIgnoreCase("removeAdmin") | args[0].equalsIgnoreCase("demoteAdmin")) {
					for(Player ap : s.getPlayers()) {
						if(s.isAdmin(ap) && ap != p && ap.getName().toLowerCase().contains(args[1].toLowerCase())) list.add(ap.getName());
					}
				} else if(args[0].equalsIgnoreCase("kick")) {
					for(Player ap : s.getPlayers()) {
						if(ap != s.getOwner() && ap != p && ap.getName().toLowerCase().contains(args[1].toLowerCase())) list.add(ap.getName());
					}
				} else if(args[0].equalsIgnoreCase("invite")) {
					for(Player op : Bukkit.getOnlinePlayers()) {
						if(Session.getPlayerSession(op) == null && op.getName().toLowerCase().contains(args[1].toLowerCase())) list.add(op.getName());
					}
				}

				else if(args[0].toLowerCase().startsWith("setwith")){
					list.add("true");
					list.add("false");
				}
			}
		} else if(args.length == 3){
			if(args[0].equalsIgnoreCase("setTime")) {
				list.add("minutes");
				list.add("seconds");
				list.add("hours");
			}
		}

		return Commands.filterTabAutocompleteList(args,list);
	}

}

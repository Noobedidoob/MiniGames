package me.noobedidoob.minigames.lasertag.commands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.noobedidoob.minigames.lasertag.session.Session;
import me.noobedidoob.minigames.lasertag.session.SessionInventorys;
import me.noobedidoob.minigames.main.Minigames;
import me.noobedidoob.minigames.utils.MgUtils;
import me.noobedidoob.minigames.utils.MgUtils.TimeFormat;
import me.noobedidoob.minigames.utils.Team;

public class SessionCommands {
	

	@SuppressWarnings("unused")
	private Minigames minigames;
	public SessionCommands(Minigames minigames) {
		this.minigames = minigames;
	}
	
	public static List<String> commandArgs = Arrays.asList(new String[] {"leave","new","join"});
	public static List<String> adminCommandArgs = Arrays.asList(new String[] {"start","stop","setTime","addAdmin","setAdmin","removeAdmin","demoteAdmin","kick", "end"});

	public void perform(CommandSender sender, String[] args) {
		if(sender instanceof Player){
			Player p = (Player) sender;
			Session s = Session.getPlayerSession(p);
			
			
			if (args.length == 1) {
				if(s != null) {
					
					if (args[0].equalsIgnoreCase("start")) {
						if (s.isAdmin(p)) {
							if(s.getPlayers().length > 0) {
								boolean enoughTeams = true;
								if(s.isTeams()) {
									int teamsWithPlayers = 0;
									for(Team team : s.getTeams()) {
										if(team.getPlayers().length > 0) teamsWithPlayers++;
									}
									if(teamsWithPlayers < 2) enoughTeams = false;
								}
								if(enoughTeams) s.start(false);
								else Session.sendMessage(p, "�cThere must be at least 2 teams with at least 1 player in it!");
							} else Session.sendMessage(p, "�cNot enough players!");
						} else Session.sendMessage(p, "�aYou need to be an admin of this session to perform this command!");
						return;
					} 
					
					else if (args[0].equalsIgnoreCase("stop") | args[0].equalsIgnoreCase("cancel")) {
						if (s.isAdmin(p)) {
							s.stop(true, false);
						} else Session.sendMessage(p, "�aYou have to be an admin of this session to perform this command!");
						return;
					} 
					
					else if(args[0].equalsIgnoreCase("end")) {
						if(s.getOwner() == p) {
							if(s.tagging()) s.stop(true, true);
							else s.close();
						} else Session.sendMessage(p, "�cYou have to be the owner of this session to perform this command");
						return;
					}
					
					else if(args[0].equalsIgnoreCase("leave")) {
						s.removePlayer(p);
						return;
					} 
					
					else if(args[0].equalsIgnoreCase("setTime")) {
						if(s.isAdmin(p)) {
							SessionInventorys.openTimeInv(p);
						} else Session.sendMessage(p, "�aYou have to be an admin of this session to perform this command!");
						return;
					} 
					
					else if(args[0].equalsIgnoreCase("addAdmin") | args[0].equalsIgnoreCase("setAdmin")) {
						if(s.isAdmin(p)) {
							SessionInventorys.openAddAdminInv(p);
						} else Session.sendMessage(p, "�aYou have to be an admin of this session to perform this command!");
						return;
					}
					
					else if(args[0].equalsIgnoreCase("invite")) {
						if(s.isAdmin(p)) {
							SessionInventorys.openInvitationInv(p);
						} else Session.sendMessage(p, "�aYou have to be an admin of this session to perform this command!");
					}
					
				} else Session.sendMessage(p, "�cYou're not in a session!");
				
			} else if(args.length == 2) {
				if(args[0].equalsIgnoreCase("new")) {
					if(args[1].equalsIgnoreCase("solo") | args[1].equalsIgnoreCase("teams")) {
						if(Session.getPlayerSession(p) != null) {
							Session.sendMessage(p, "�cPlease leave this session first!");
							return;
						}
						Session.sendMessage(p, "�aRegistered new Session!");
						new Session(p, (!args[1].equalsIgnoreCase("teams")));
						return;
					}
				} 
				
				else if(args[0].equalsIgnoreCase("join")) {
					if(s != null) {
						Session.sendMessage(p, "�cPlease leave this session first!");
						return;
					}
					Session invS = Session.getSessionFromCode(args[1]);
					if(invS != null) {
						if(invS.isPlayerInvited(p)) {
							invS.addPlayer(p);
						} else Session.sendMessage(p, "�cYou were not invited to this session!");
					} else Session.sendMessage(p, "�cThis invitation expired!");
					return;
				} 
				
				else if(args[0].equalsIgnoreCase("kick")) {
					if(s != null) {
						if (s.isAdmin(p)) {
							Player kp = Bukkit.getPlayer(args[1]);
							if (kp != null) {
								if(s.isInSession(kp)) {
									if(kp != s.getOwner()) s.kickPlayer(kp, p);
									else Session.sendMessage(p, "�cYou can't kick the owner!");
								} else Session.sendMessage(p, "�b"+args[1]+" �cis not in this session!");
							} else Session.sendMessage(p, "�cPlayer �b"+args[1]+" �cnot found!");
						} else Session.sendMessage(p, "�aYou have to be an admin of this session to perform this command!");
					} else Session.sendMessage(p, "�cYou're not in a session!");
					return;
				} 
				
				else if(args[0].equalsIgnoreCase("addAdmin") | args[0].equalsIgnoreCase("promoteAdmin") | args[0].equalsIgnoreCase("setAdmin")) {
					if(s != null) {
						if(s.isAdmin(p)) {
							Player ap = Bukkit.getPlayer(args[1]);
							if(ap != null) {
								if (s.isInSession(ap)) {
									if (!s.isAdmin(ap)) {
										s.addAdmin(ap);
										Session.sendMessage(p, "�aPromoted �b"+ap.getName()+" �ato Admin!");
									} else Session.sendMessage(p, "�b"+args[1]+" �cis already an admin of this session!");
								} else Session.sendMessage(p, "�b"+args[1]+" �cis not in this session!");
							} else Session.sendMessage(p, "�cPlayer �b"+args[1]+" �cnot found!");
						} else Session.sendMessage(p, "�aYou have to be an admin of this session to perform this command!");
					} else Session.sendMessage(p, "�cYou're not in a session!");
					return;
				}
				
				else if(args[0].equalsIgnoreCase("removeAdmin") | args[0].equalsIgnoreCase("demoteAdmin")) {
					if(s != null) {
						if(s.isAdmin(p)) {
							Player dp = Bukkit.getPlayer(args[1]);
							if(dp != null) {
								if (s.isInSession(dp)) {
									if (s.isAdmin(dp)) {
										if(dp != s.getOwner()) {
											s.removeAdmin(dp);
											Session.sendMessage(p, "�aDemoted �b"+dp.getName()+" �afrom Admin!");
										} else Session.sendMessage(p, "�cYou can't do this to the owner!");
									} else Session.sendMessage(p, "�b"+args[1]+" �cis not an admin of this session!");
								} else Session.sendMessage(p, "�b"+args[1]+" �cis not in this session!");
							} else Session.sendMessage(p, "�cPlayer �b"+args[1]+" �cnot found!");
						} else Session.sendMessage(p, "�aYou have to be an admin of this session to perform this command!");
					} else Session.sendMessage(p, "�cYou're not in a session!");
					return;
				}
				
				else if(args[0].equalsIgnoreCase("invite")) {
					if(s != null) {
						if(s.isAdmin(p)) {
							Player ip = Bukkit.getPlayer(args[1]);
							if(ip != null) {
								if (Session.getPlayerSession(ip) == null) {
									s.sendInvitation(ip);
									Session.sendMessage(p, "�aInvitation sent!");
								} else Session.sendMessage(p, "�b"+args[1]+" �cis already in a session!");
							} else Session.sendMessage(p, "�cPlayer �b"+args[1]+" �cnot found!");
						} else Session.sendMessage(p, "�aYou have to be an admin of this session to perform this command!");
					} else Session.sendMessage(p, "�cYou're not in a session!");
					return;
				}
			} else {
				if(args[0].equalsIgnoreCase("setTime")) {
					if(s != null) {
						if(s.isAdmin(p)) {
							try {
								String timeName = args[1];
								String format = "m";
								if(args.length > 2) format = args[2];
								if(!format.contains("m") && !format.contains("s") && !format.contains("h")) s.setTime(MgUtils.getTimeFromArgs(timeName, "m"), TimeFormat.SECONDS, true);
								else s.setTime(MgUtils.getTimeFromArgs(timeName, format), TimeFormat.SECONDS, true);
							} catch (NumberFormatException e) {
								sender.sendMessage("�cThe given argument �e"+e.getMessage().replace("For input string: ","")+" �cis not a Number!");
								return;
							} catch (Exception e) {
								sender.sendMessage("�cSyntax error: "+e.getMessage());
								return;
							}
						} else Session.sendMessage(p, "�aYou have to be an admin of this session to perform this command!");
					} else Session.sendMessage(p, "�cYou're not in a session!");
					return;
				}
			}
		} else sender.sendMessage("You may only perform this command as a player!");
		
		
		
		
		sender.sendMessage("�cSyntax ERROR! Please use �e/lt �cto see all commands and their arguments");
	}
	
	public List<String> getTabComplete(List<String> list, CommandSender sender, String[] args) {
		if(!(sender instanceof Player)) return list;
		
		Player p = (Player) sender;
		Session s = Session.getPlayerSession(p);
		if(args.length == 1) {
			if(s == null) {
				list.add("new");
				list.add("join");
			} else {
				if(s.isAdmin(p)) {
					if(!s.tagging()) list.add("start");
					list.add("invite");
					list.add("stop");
					list.add("cancel");
					list.add("setTime");
					list.add("addAdmin");
					list.add("removeAdmin");
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
							if(!list.contains(ops.getOwner().getName())) list.add(ops.getOwner().getName());
						}
					}
				}
			} else if(s.isAdmin(p)){
				if(args[0].equalsIgnoreCase("addAdmin") | args[0].equalsIgnoreCase("setAdmin")) {
					for(Player ap : s.getPlayers()) {
						if(!s.isAdmin(ap) && ap != p) list.add(ap.getName());
					}
				} else if(args[0].equalsIgnoreCase("removeAdmin") | args[0].equalsIgnoreCase("demoteAdmin")) {
					for(Player ap : s.getPlayers()) {
						if(s.isAdmin(ap) && ap != p) list.add(ap.getName());
					}
				} else if(args[0].equalsIgnoreCase("kick")) {
					for(Player ap : s.getPlayers()) {
						if(ap != s.getOwner() && ap != p) list.add(ap.getName());
					}
				} else if(args[0].equalsIgnoreCase("invite")) {
					for(Player op : Bukkit.getOnlinePlayers()) {
						if(Session.getPlayerSession(op) == null && !s.isPlayerInvited(op)) list.add(op.getName());
					}
				}
			}
		} else if(args.length == 3){
			if(args[0].equalsIgnoreCase("setTime")) {
				list.add("minutes");
				list.add("seconds");
				list.add("hours");
			}
		}
		
		return list;
	}
}

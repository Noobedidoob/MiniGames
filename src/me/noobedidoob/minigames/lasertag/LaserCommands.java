package me.noobedidoob.minigames.lasertag;

import me.noobedidoob.minigames.Minigames;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class LaserCommands implements CommandExecutor, TabCompleter {

	public LaserCommands(Minigames minigames) {

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
					  + "\n————————————————————————————————\n";
	
	
	String commands = "\n———————————————\n"
					+ "/lt modifiers — \n"
					+ "/lt loadtexturepack — \n"
					+ "\n———————————————\n";
	

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if(cmd.getName().equalsIgnoreCase("lasertag")) {

			if(args.length == 0) {
				if(sender.isOp()) sender.sendMessage("§e"+opCommands);
				else sender.sendMessage("§e"+commands);
				return true;
			} /*else if(args.length == 1) {
				
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

			}*/
		}
		sender.sendMessage("§cSyntax ERROR! Please use §e/lt §cto see all commands and their arguments");
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		List<String> list = new ArrayList<>();
		if(cmd.getName().equalsIgnoreCase("lasertag")) {
			
//				list = modifierCommands.getTabComplete(list, sender, args);
//				list = sessionCommands.getTabComplete(list, sender, args);
			
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
		return list;
	}
	
	public void refreshTabCommplete(List<String> list, String arg) {
		List<String> toRemoveList = new ArrayList<>();
		for(String s : list) {
			if(!(s.length() < arg.length())) {
				if(!s.substring(0, arg.length()).equalsIgnoreCase(arg)) toRemoveList.add(s);
			} else toRemoveList.add(s);
		}
		list.removeAll(toRemoveList);
	}
	
}

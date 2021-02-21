package me.noobedidoob.minigames.lasertag;

import me.noobedidoob.minigames.Minigames;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class LaserCommands implements CommandExecutor, TabCompleter {

	@SuppressWarnings({"FieldCanBeLocal", "unused"})
	private final Minigames minigames;

	public LaserCommands(Minigames minigames) {
		this.minigames = minigames;
	}

	String commands = "\n———————————————\n"
					+ "/lt loadtexturepack — \n"
					+ "\n———————————————\n";
	

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if(cmd.getName().equalsIgnoreCase("lasertag")) {

			if(args.length == 0) {
				sender.sendMessage("§e"+commands);
				return true;
			}
		}
		sender.sendMessage("§cSyntax ERROR! Please use §e/lt §cto see all commands and their arguments");
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		List<String> list = new ArrayList<>();
		if(cmd.getName().equalsIgnoreCase("lasertag")) {
			if(args.length == 1) {
				list.add("loadtexturepack");
				refreshTabCommplete(list, args[0]);
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

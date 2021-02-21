package me.noobedidoob.minigames;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor, TabCompleter{

//	private Minigames minigames;
	public Commands(Minigames minigames) {
//		this.minigames = minigames;
	}
	
	String commands = /*"§esettexturepack §7— Activate texturepackw\n";*/ "";

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if(cmd.getName().equalsIgnoreCase("lobby")){
			Minigames.teleportPlayersToSpawn((Player) sender);
			return true;
		} else if(args.length == 0) {
			sender.sendMessage(commands);
			return true;
		}
		
		sender.sendMessage("§cSyntax error! Use §e/minigames §cto see all commands!");
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> list = new ArrayList<>();
		if(args.length == 0) {
//			list.add("settexturepack");
			list.add("lobby");
		}
		return list;
	}

	public static List<String> filterTabAutocompleteList(String[] args, List<String> list){
		List<String> toRemoveList = new ArrayList<>();
		for (String value : list) {
			if (!value.toLowerCase().contains(args[args.length - 1].toLowerCase())) {
				toRemoveList.add(value);
			}
		}
		for(String s : toRemoveList){
			list.remove(s);
		}
		return list;
	}

}

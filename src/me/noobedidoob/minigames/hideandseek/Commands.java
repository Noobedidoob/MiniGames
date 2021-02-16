package me.noobedidoob.minigames.hideandseek;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

@SuppressWarnings("unused")
public class Commands implements CommandExecutor, TabCompleter {

//	private Minigames m;
//	public Commands(Minigames minigames) {
//		this.m = minigames;
//	}

	String commands = "/has test";
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
//		if(!(sender instanceof Player) | !sender.isOp()) return true;
//		Player p = (Player) sender;
//		
//		if(cmd.getName().equalsIgnoreCase("hideandseek")) {
//			if(args.length == 0) {
//				p.sendMessage("§e"+commands);
//				return true;
//			} else if(args.length == 1) {
//				if(args[0].equalsIgnoreCase("test")) {
//					if(HideAndSeek.isPlayerTesting(p)) {
//						HideAndSeek.send(p,"§cTurned testing off");
//						HideAndSeek.setPlayerTesting(p, false);
//					} else {
//						HideAndSeek.setPlayerTesting(p, true);
//						HideAndSeek.send(p, "§aTurned on testing!");
//					}
//				}
//			}
//		}
		return false;
	}
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		List<String> list = new ArrayList<>();
		if(args.length == 0) {
			list.add("test");
		}
		return list;
	}
	
}

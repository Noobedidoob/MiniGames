package me.noobedidoob.minigames.hideandseek;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.noobedidoob.minigames.main.Minigames;

public class HideCommands implements CommandExecutor, TabCompleter {

	@SuppressWarnings("unused")
	private Minigames m;
	private HideAndSeek h;
	public HideCommands(Minigames minigames, HideAndSeek hideAndSeek) {
		this.m = minigames;
		this.h = hideAndSeek;
	}

	String commands = "/has teams";
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		Player p = (Player) sender;
		
		if(cmd.getName().equalsIgnoreCase("hideandseek")) {
			if(args.length == 0) {
				p.sendMessage("§e"+commands);
				return true;
			} else if(args.length == 1) {
				
			} else if(args.length == 2) {
				if(args[0].equalsIgnoreCase("testing")) {
					if(args[1].equalsIgnoreCase("on")) {
						h.testing = true;
						for(Player op : Bukkit.getOnlinePlayers()) {
							if(!op.getInventory().contains(h.undisguiseItem)) op.getInventory().addItem(h.undisguiseItem);
						}
						sender.sendMessage("§aTesting HideAndSeek now");
						return true;
					} else if(args[1].equalsIgnoreCase("off")) {
						h.testing = false;
						sender.sendMessage("§aStopped testing HideAndSeek");
						return true;
					}
				}
			}
		}
		
		return false;
	}
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		List<String> list = new ArrayList<String>();
		for(Player p : Bukkit.getOnlinePlayers()) {
			list.add(p.getName());
		}
		return list;
	}
	
}

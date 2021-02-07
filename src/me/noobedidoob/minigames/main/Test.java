package me.noobedidoob.minigames.main;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import me.noobedidoob.minigames.utils.BaseSphere;

public class Test implements CommandExecutor,Listener{
	
	public Test() {
		Bukkit.getPluginManager().registerEvents(this, Minigames.minigames);
	}
	
	
	
	
	public void test(Player p) {
		
	}
	
	HashMap<Player, BaseSphere> playerSphere = new HashMap<>();
	
	

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(sender instanceof Player && sender.isOp()) {
			Player p = (Player) sender;
			if(args.length == 0) {
				test(p);
				p.sendMessage("Testing...");
			} else if(args.length == 1) {
				if(args[0].equalsIgnoreCase("sphere")) {
					
				}
			}
		}
		return true;
	}
	
}

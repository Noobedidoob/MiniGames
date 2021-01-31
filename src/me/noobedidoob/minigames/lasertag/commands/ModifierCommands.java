package me.noobedidoob.minigames.lasertag.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.noobedidoob.minigames.lasertag.session.SessionModifiers.Mod;
import me.noobedidoob.minigames.lasertag.session.Session;
import me.noobedidoob.minigames.main.Minigames;
import me.noobedidoob.minigames.utils.MgUtils;

public class ModifierCommands  {
	
	@SuppressWarnings("unused")
	private Minigames minigames;
	public ModifierCommands(Minigames minigames) {
		this.minigames = minigames;
	}
	
	
	public void perform(CommandSender sender, String[] args) {
		
		if(args[0].equalsIgnoreCase("getModifiers") | args[0].equalsIgnoreCase("modifiers")) {
			if (!(sender instanceof Player) | Session.getPlayerSession((Player) sender) == null) {
				for (Mod m : Mod.values()) {
					sender.sendMessage("\n§7———————§b§lStanderd Modifiers§r§7———————");
					sender.sendMessage("§7> " + m.getDescription() + ": §a" + m.getOg().toString());
					sender.sendMessage("§7——————————————————\n");
				} 
				return;
			} else {
				sender.sendMessage("\n§7—————————§b§lModifiers§r§7—————————");
				Session s = Session.getPlayerSession((Player) sender);
				s.modifiers.modValues.forEach((m, v) ->{
					sender.sendMessage("§7> " + m.getDescription() + ": §a" + s.getModValue(m).toString());
				});
				sender.sendMessage("§7—————————————————————\n");
			}
			return;
		} else if(args[0].equalsIgnoreCase("getModifierTypes")) {
			sender.sendMessage("\n§7———————§b§lModifier Types§r§7———————");
			for(Mod m : Mod.values()) {
				sender.sendMessage("§7"+m.name()+" <§a"+m.getValueTypeName()+"§7>");
			}
			sender.sendMessage("§7———————————————————\n");
			return;
		}
		
		if((sender instanceof Player) && Session.getPlayerSession((Player) sender) != null) {
			Player p = (Player) sender;
			Session session = Session.getPlayerSession(p);
			
			if(args[0].equalsIgnoreCase("setmodifier") && args.length == 3) {
				Mod m = Mod.getMod(args[1].toUpperCase().replace("-", "_"));
				String valString = args[2];
				Object value = valString;
				
				if(MgUtils.isNumericOnly(valString)) {
					value = Integer.parseInt(valString);
				} else if(MgUtils.isAlphabeticOnly(valString)) {
					if(valString.equalsIgnoreCase("true") | valString.equalsIgnoreCase("false")) value = Boolean.parseBoolean(valString);
					else {
						Session.sendMessage(p, "§cThe given value is invalid! Please use a §evalid number §cor §etrue§c/§efalse!");
						return;
					}
				} else {
					
				}
				
				try {
					value = Integer.parseInt(valString);
				} catch (NumberFormatException e1) {
					try {
						value = Double.parseDouble(valString+"d");
					} catch (NumberFormatException e2) {
						if(valString.equalsIgnoreCase("true") | valString.equalsIgnoreCase("false")) value = Boolean.parseBoolean(valString);
						else {
							Session.sendMessage(p, "§cThe given value is invalid! Please use a §evalid number §cor §etrue§c/§efalse!");
							return;
						}
					}
				}
				if(m != null) {
					if(value.getClass() == m.getOg().getClass()) {
						session.setMod(m, value);
						Session.sendMessage(p, "§aSuccessfully set the value of the modifier §b"+m.name().toLowerCase()+" §a to §e"+value.toString());
						return;
					} else {
						Session.sendMessage(p, "§cThe given type of value doesnt match with the modifiers value type! Please use §e"+m.getValueTypeName());
						return;
					}
				} else {
					Session.sendMessage(p, "§cThe modifier §b"+args[1]+" §cdoesn't exist! Use §e/lt setmodifier §cto get all available modifiers");
					return;
				}
			}
		}
		
		

		sender.sendMessage("§cSyntax ERROR! Please use §e/lt §cto see all commands and their arguments");
		return;
	}
	
	public List<String> getTabComplete(List<String> list, CommandSender sender, String[] args) {
		if(args.length == 1) {
			list.add("getModifiers");
			list.add("getModifierTypes");
			if(sender instanceof Player && Session.getPlayerSession((Player) sender) != null && Session.getPlayerSession((Player) sender).waiting() && Session.getPlayerSession((Player) sender).isAdmin((Player) sender)) {
				list.add("withmultiweapons");
				list.add("setModifier");
			}
		} else if(sender instanceof Player && args.length >= 2 && args[0].equalsIgnoreCase("setmodifier") && Session.getPlayerSession((Player) sender).isAdmin((Player) sender)) {
			if(args.length == 2) {
				for(Mod m : Mod.values()) list.add(m.name().toLowerCase());
			} else if(args.length == 3 && Mod.getMod(args[1].toUpperCase().replace("-", "_")) != null) {
				if(Mod.getMod(args[1].toUpperCase().replace("-", "_")).getValueTypeName() == "true/false") {
					list.add("true");
					list.add("false");
				}
			}
		}
		
		if(args.length == 3) {
			Mod m = Mod.getMod(args[2]);
			if (m != null) {
				if (m.getValueTypeName() == "true/false") {
					if (sender.isOp()) {
						list.add("true");
						list.add("false");
					}
				} 
			}
		}
		return list;
	}
}

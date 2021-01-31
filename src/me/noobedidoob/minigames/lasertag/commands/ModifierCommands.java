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
					sender.sendMessage("\nｧ7覧覧覧立bｧlStanderd Modifiersｧrｧ7覧覧覧�");
					sender.sendMessage("ｧ7> " + m.getDescription() + ": ｧa" + m.getOg().toString());
					sender.sendMessage("ｧ7覧覧覧覧覧覧覧覧覧\n");
				} 
				return;
			} else {
				sender.sendMessage("\nｧ7覧覧覧覧立bｧlModifiersｧrｧ7覧覧覧覧�");
				Session s = Session.getPlayerSession((Player) sender);
				s.modifiers.modValues.forEach((m, v) ->{
					sender.sendMessage("ｧ7> " + m.getDescription() + ": ｧa" + s.getModValue(m).toString());
				});
				sender.sendMessage("ｧ7覧覧覧覧覧覧覧覧覧覧予n");
			}
			return;
		} else if(args[0].equalsIgnoreCase("getModifierTypes")) {
			sender.sendMessage("\nｧ7覧覧覧立bｧlModifier Typesｧrｧ7覧覧覧�");
			for(Mod m : Mod.values()) {
				sender.sendMessage("ｧ7"+m.name()+" <ｧa"+m.getValueTypeName()+"ｧ7>");
			}
			sender.sendMessage("ｧ7覧覧覧覧覧覧覧覧覧予n");
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
						Session.sendMessage(p, "ｧcThe given value is invalid! Please use a ｧevalid number ｧcor ｧetrueｧc/ｧefalse!");
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
							Session.sendMessage(p, "ｧcThe given value is invalid! Please use a ｧevalid number ｧcor ｧetrueｧc/ｧefalse!");
							return;
						}
					}
				}
				if(m != null) {
					if(value.getClass() == m.getOg().getClass()) {
						session.setMod(m, value);
						Session.sendMessage(p, "ｧaSuccessfully set the value of the modifier ｧb"+m.name().toLowerCase()+" ｧa to ｧe"+value.toString());
						return;
					} else {
						Session.sendMessage(p, "ｧcThe given type of value doesnt match with the modifiers value type! Please use ｧe"+m.getValueTypeName());
						return;
					}
				} else {
					Session.sendMessage(p, "ｧcThe modifier ｧb"+args[1]+" ｧcdoesn't exist! Use ｧe/lt setmodifier ｧcto get all available modifiers");
					return;
				}
			}
		}
		
		

		sender.sendMessage("ｧcSyntax ERROR! Please use ｧe/lt ｧcto see all commands and their arguments");
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

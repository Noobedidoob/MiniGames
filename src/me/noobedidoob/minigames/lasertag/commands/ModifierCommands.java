package me.noobedidoob.minigames.lasertag.commands;

import org.bukkit.command.CommandSender;

import me.noobedidoob.minigames.lasertag.methods.Modifiers;

public class ModifierCommands {
	public static boolean getCurrentModifiers(String[] args, CommandSender sender) {
		sender.sendMessage("ｧ7Lasergun cooldown time: ｧa"+Modifiers.lasergunCooldown);
		sender.sendMessage("ｧ7Sniper cooldown time: ｧa"+Modifiers.sniperCooldown);
		sender.sendMessage("ｧ7Shotguncooldown time: ｧa"+Modifiers.shotgunCooldown);
		sender.sendMessage("ｧ7Normal Points: ｧa"+Modifiers.points);
		sender.sendMessage("ｧ7Extrapoints for snipe shots: ｧa"+Modifiers.snipeShotsExtra);
		sender.sendMessage("ｧ7Minimal distance for snipe shots: ｧa"+Modifiers.minSnipeDistance);
		sender.sendMessage("ｧ7Extrapoints for close-range-shots: ｧa"+Modifiers.closeRangeExtra);
		sender.sendMessage("ｧ7Extrapoints when killed with PvP-Attack: ｧa"+Modifiers.pvpExtra);
		sender.sendMessage("ｧ7Extrapoints for headshot: ｧa"+Modifiers.headshotExtra);
		sender.sendMessage("ｧ7Added height to hitbox: ｧa"+Modifiers.heightAddon);
		sender.sendMessage("ｧ7Added width to hitbox: ｧa"+Modifiers.widthAddon);
		sender.sendMessage("ｧ7Highlight players: ｧa"+Modifiers.highLightPlayers);
		sender.sendMessage("ｧ7Amplifier of highlight effect: ｧa"+Modifiers.glowingAmplifier);
		sender.sendMessage("ｧ7Shoot trough blocks: ｧa"+Modifiers.shootThroughBlocks);
		sender.sendMessage("ｧ7Playing with events: ｧa"+Modifiers.withEvents);
//		sender.sendMessage("ｧ7Playing with Minigun: ｧa"+Modifiers.withMinigun);
//		sender.sendMessage("ｧ7Playing with ammo: ｧa"+Modifiers.withAmmo);
		sender.sendMessage("ｧ7Magazine size: ｧa"+Modifiers.bulletsInMagazine);
		sender.sendMessage("ｧ7Time to reload magazine: ｧa"+Modifiers.magazinReloadTime);
		return true;
	}
	
	public static boolean setModifier(String[] args, CommandSender sender) {
		String name = args[1].toLowerCase();
		String valueString = args[2];
		
		if(Modifiers.integerModifier.get(name) != null) {
			int value = 0;
			try {
				value = Integer.parseInt(valueString);
			} catch (NumberFormatException e) {
				sender.sendMessage("ｧd"+valueString+" ｧcCan't be converted to a Number! \nｧc=> You have to ennter a full and valid number between ｧd"+Integer.MIN_VALUE+" ｧcand ｧd"+Integer.MAX_VALUE);
				return true;
			}
			Modifiers.setModifier(name, value);
			sender.sendMessage("ｧaSuccessfully set the modifier ｧd"+name+" ｧato ｧ6"+value);
		} else if(Modifiers.doubleModifier.get(valueString) != null) {
			double value = 0;
			try {
				value = Double.parseDouble(name);
			} catch (NumberFormatException e) {
				sender.sendMessage("ｧd"+valueString+" ｧcCan't be converted to a Number! \nｧc=> You have to ennter a valid number between ｧd"+Double.MIN_VALUE+" ｧcand ｧd"+Double.MAX_VALUE);
				return true;
			}
			Modifiers.setModifier(name, value);
			sender.sendMessage("ｧaSuccessfully set the modifier ｧd"+name+" ｧato ｧ6"+value);
		} else if(Modifiers.statusModifier.get(name) != null) {
			boolean value = Boolean.parseBoolean(valueString);
//			if(name == "spawnatbase") {
//				if(value == true) {
//					if(Game.map().withBaseSpawn()) {
//						l.modifiers.setModifier(name, value);
//						sender.sendMessage("ｧaSuccessfully set the modifier ｧd"+name+" ｧato ｧ6"+value);
//					}
//				} else {
//					if(l.randomSpawnMaps.contains(l.lasertagLoc)) {
//						l.modifiers.setModifier(name, value);
//						sender.sendMessage("ｧaSuccessfully set the modifier ｧd"+name+" ｧato ｧ6"+value);
//					}
//				}
//			} else {
				Modifiers.setModifier(name, value);
				sender.sendMessage("ｧaSuccessfully set the modifier ｧd"+name+" ｧato ｧ6"+value);
//			}
		} else {
			sender.sendMessage("ｧcThe modifier ｧd"+name+" ｧcdoesn't exist! Use ｧe/lt setmodifier ｧcto get all available modifiers");
		}
		return true;
	
	}
	
	public static boolean getAccessableModifiers(CommandSender sender) {
		sender.sendMessage("\nｧ7覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧"
					 + "ｧe reloadTime <number> � ｧ7\n"
					 + "ｧe points <number> � ｧ7Normal points\n"
					 + "ｧe snipe <number> � ｧ7\n"
					 + "ｧe minsnipedistance <number> � ｧ7\n"
					 + "ｧe closerange <number> � ｧ7\n"
					 + "ｧe backstab <number> � ｧ7\n"
					 + "ｧe pvp <number> � ｧ7\n"
					 + "ｧe headshot <number> � ｧ7\n"
					 + "ｧe strike <number> � ｧ7\n"
					 + "ｧe minkillstrike <number> � ｧ7\n"
					 + "ｧe multikill <number> � ｧ7\n"
					 + "ｧe minkillstrike <number> � ｧ7\n"
					 + "ｧe heightaddon <number> � ｧ7\n"
					 + "ｧe widthaddon <number> � ｧ7\n"
					 + "ｧe glowamplifier <0-255> � ｧ7\n"
					 + "ｧe highlightplayers <true | false> � ｧ7\n"
					 + "ｧe shootthroughblocks <true | false> � ｧ7\n"
					 + "ｧe spawnatbase <true | false> � ｧ7\n"
					 + "ｧe withevents <true | false> � ｧ7\n"
					 + "ｧe withminigun <true | false> � ｧ7\n"
					 + "ｧe withevents <true | false> � ｧ7\n"
					 + "ｧe withammo <true | false> � ｧ7\n"
					 + "ｧe magazinesize <number> � ｧ7\n"
					 + "ｧe magazinereloadtime <number> � ｧ7\n"
					 + "ｧe multiweapons <true | false> � ｧ7\n"
					 + "\nｧ7覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧");
		return true;
	}
}

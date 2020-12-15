package me.noobedidoob.minigames.lasertag.commands;

import org.bukkit.command.CommandSender;

import me.noobedidoob.minigames.lasertag.methods.Modifiers;

public class ModifierCommands {
	public static boolean getCurrentModifiers(String[] args, CommandSender sender) {
		sender.sendMessage("§7Lasergun cooldown time: §a"+Modifiers.lasergunCooldown);
		sender.sendMessage("§7Sniper cooldown time: §a"+Modifiers.sniperCooldown);
		sender.sendMessage("§7Shotguncooldown time: §a"+Modifiers.shotgunCooldown);
		sender.sendMessage("§7Normal Points: §a"+Modifiers.points);
		sender.sendMessage("§7Extrapoints for snipe shots: §a"+Modifiers.snipeShotsExtra);
		sender.sendMessage("§7Minimal distance for snipe shots: §a"+Modifiers.minSnipeDistance);
		sender.sendMessage("§7Extrapoints for close-range-shots: §a"+Modifiers.closeRangeExtra);
		sender.sendMessage("§7Extrapoints when killed with PvP-Attack: §a"+Modifiers.pvpExtra);
		sender.sendMessage("§7Extrapoints for headshot: §a"+Modifiers.headshotExtra);
		sender.sendMessage("§7Added height to hitbox: §a"+Modifiers.heightAddon);
		sender.sendMessage("§7Added width to hitbox: §a"+Modifiers.widthAddon);
		sender.sendMessage("§7Highlight players: §a"+Modifiers.highLightPlayers);
		sender.sendMessage("§7Amplifier of highlight effect: §a"+Modifiers.glowingAmplifier);
		sender.sendMessage("§7Shoot trough blocks: §a"+Modifiers.shootThroughBlocks);
		sender.sendMessage("§7Playing with events: §a"+Modifiers.withEvents);
//		sender.sendMessage("§7Playing with Minigun: §a"+Modifiers.withMinigun);
//		sender.sendMessage("§7Playing with ammo: §a"+Modifiers.withAmmo);
		sender.sendMessage("§7Magazine size: §a"+Modifiers.bulletsInMagazine);
		sender.sendMessage("§7Time to reload magazine: §a"+Modifiers.magazinReloadTime);
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
				sender.sendMessage("§d"+valueString+" §cCan't be converted to a Number! \n§c=> You have to ennter a full and valid number between §d"+Integer.MIN_VALUE+" §cand §d"+Integer.MAX_VALUE);
				return true;
			}
			Modifiers.setModifier(name, value);
			sender.sendMessage("§aSuccessfully set the modifier §d"+name+" §ato §6"+value);
		} else if(Modifiers.doubleModifier.get(valueString) != null) {
			double value = 0;
			try {
				value = Double.parseDouble(name);
			} catch (NumberFormatException e) {
				sender.sendMessage("§d"+valueString+" §cCan't be converted to a Number! \n§c=> You have to ennter a valid number between §d"+Double.MIN_VALUE+" §cand §d"+Double.MAX_VALUE);
				return true;
			}
			Modifiers.setModifier(name, value);
			sender.sendMessage("§aSuccessfully set the modifier §d"+name+" §ato §6"+value);
		} else if(Modifiers.statusModifier.get(name) != null) {
			boolean value = Boolean.parseBoolean(valueString);
//			if(name == "spawnatbase") {
//				if(value == true) {
//					if(Game.map().withBaseSpawn()) {
//						l.modifiers.setModifier(name, value);
//						sender.sendMessage("§aSuccessfully set the modifier §d"+name+" §ato §6"+value);
//					}
//				} else {
//					if(l.randomSpawnMaps.contains(l.lasertagLoc)) {
//						l.modifiers.setModifier(name, value);
//						sender.sendMessage("§aSuccessfully set the modifier §d"+name+" §ato §6"+value);
//					}
//				}
//			} else {
				Modifiers.setModifier(name, value);
				sender.sendMessage("§aSuccessfully set the modifier §d"+name+" §ato §6"+value);
//			}
		} else {
			sender.sendMessage("§cThe modifier §d"+name+" §cdoesn't exist! Use §e/lt setmodifier §cto get all available modifiers");
		}
		return true;
	
	}
	
	public static boolean getAccessableModifiers(CommandSender sender) {
		sender.sendMessage("\n§7————————————————————————————————"
					 + "§e reloadTime <number> — §7\n"
					 + "§e points <number> — §7Normal points\n"
					 + "§e snipe <number> — §7\n"
					 + "§e minsnipedistance <number> — §7\n"
					 + "§e closerange <number> — §7\n"
					 + "§e backstab <number> — §7\n"
					 + "§e pvp <number> — §7\n"
					 + "§e headshot <number> — §7\n"
					 + "§e strike <number> — §7\n"
					 + "§e minkillstrike <number> — §7\n"
					 + "§e multikill <number> — §7\n"
					 + "§e minkillstrike <number> — §7\n"
					 + "§e heightaddon <number> — §7\n"
					 + "§e widthaddon <number> — §7\n"
					 + "§e glowamplifier <0-255> — §7\n"
					 + "§e highlightplayers <true | false> — §7\n"
					 + "§e shootthroughblocks <true | false> — §7\n"
					 + "§e spawnatbase <true | false> — §7\n"
					 + "§e withevents <true | false> — §7\n"
					 + "§e withminigun <true | false> — §7\n"
					 + "§e withevents <true | false> — §7\n"
					 + "§e withammo <true | false> — §7\n"
					 + "§e magazinesize <number> — §7\n"
					 + "§e magazinereloadtime <number> — §7\n"
					 + "§e multiweapons <true | false> — §7\n"
					 + "\n§7————————————————————————————————");
		return true;
	}
}

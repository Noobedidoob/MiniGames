package me.noobedidoob.minigames.lasertag.methods;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.noobedidoob.minigames.lasertag.methods.Weapon;

public class PlayerZoomer {
	
	private static final HashMap<Player, Boolean> IS_PLAYER_ZOOMED = new HashMap<>();
	public static void toggleZoom(Player p) {
		ItemStack visor = new ItemStack(Material.CARVED_PUMPKIN);
		IS_PLAYER_ZOOMED.putIfAbsent(p, false);
		boolean zoomed = IS_PLAYER_ZOOMED.get(p);
		if(zoomed) {
			p.setWalkSpeed(0.2f);
			p.getInventory().setHelmet(new ItemStack(Material.AIR));
		} else {
			p.setWalkSpeed(-0.5f);
			p.getInventory().setHelmet(visor);
		}
		IS_PLAYER_ZOOMED.put(p, !zoomed);
	}
	
	public static void zoomPlayerIn(Player p, Weapon w) {
		ItemStack visor = new ItemStack(Material.CARVED_PUMPKIN); 
		switch (w) {
		case SNIPER:
			p.setWalkSpeed(-0.5f);
			break;
		case LASERGUN:
			p.setWalkSpeed(-0.1f);
			break;
		default:
			break;
		}
		p.getInventory().setHelmet(visor);
		IS_PLAYER_ZOOMED.put(p, true);
	}
	public static void zoomPlayerOut(Player p) {
		p.setWalkSpeed(0.2f);
		if(p.getInventory().getHelmet() != null && p.getInventory().getHelmet().getType().equals(Material.CARVED_PUMPKIN)) p.getInventory().setHelmet(new ItemStack(Material.AIR));
		IS_PLAYER_ZOOMED.put(p, false);
	}


}

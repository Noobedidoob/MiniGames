package me.noobedidoob.minigames.lasertag.methods;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.noobedidoob.minigames.lasertag.methods.Weapons.Weapon;

public class PlayerZoomer {
	
	static HashMap<Player, Boolean> isPlayerZoomed = new HashMap<Player, Boolean>();
	public static void toggleZoom(Player p) {
		ItemStack visor = new ItemStack(Material.CARVED_PUMPKIN); 
		if(isPlayerZoomed.get(p) == null) isPlayerZoomed.put(p, false);
		boolean zoomed = isPlayerZoomed.get(p);
		if(zoomed) {
			p.setWalkSpeed(0.2f);
			p.getInventory().setHelmet(new ItemStack(Material.AIR));
		} else {
			p.setWalkSpeed(-0.5f);
			p.getInventory().setHelmet(visor);
		}
		isPlayerZoomed.put(p, !zoomed);
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
		isPlayerZoomed.put(p, true);
	}
	public static void zoomPlayerOut(Player p) {
		p.setWalkSpeed(0.2f);
		p.getInventory().setHelmet(new ItemStack(Material.AIR));
		isPlayerZoomed.put(p, false);
	}
}

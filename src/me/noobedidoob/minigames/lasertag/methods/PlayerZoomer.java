package me.noobedidoob.minigames.lasertag.methods;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
	
	public static void zoomPlayerIn(Player p) {
		ItemStack visor = new ItemStack(Material.CARVED_PUMPKIN); 
		p.setWalkSpeed(-0.5f);
		p.getInventory().setHelmet(visor);
		isPlayerZoomed.put(p, true);
	}
	public static void zoomPlayerOut(Player p) {
		p.setWalkSpeed(0.2f);
		p.getInventory().setHelmet(new ItemStack(Material.AIR));
		isPlayerZoomed.put(p, false);
	}

}

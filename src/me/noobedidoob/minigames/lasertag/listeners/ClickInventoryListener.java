package me.noobedidoob.minigames.lasertag.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;

import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.methods.Game;
import me.noobedidoob.minigames.lasertag.commands.ModifierCommands.Mod;
import me.noobedidoob.minigames.lasertag.methods.Weapons;
import me.noobedidoob.minigames.main.Minigames;

public class ClickInventoryListener implements Listener {
	
	public ClickInventoryListener(Minigames minigames) {
		PluginManager pluginManeger = Bukkit.getPluginManager();
		pluginManeger.registerEvents(this, minigames);
	}
	
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerClickInventory(InventoryClickEvent e) {
		HumanEntity he = e.getWhoClicked();
		if(Game.waiting() && he instanceof Player) {
			Player p = (Player) he;
			if(e.getInventory().contains(Material.WHITE_STAINED_GLASS_PANE) && e.getInventory().contains(Weapons.shotgunItem.getType()) && e.getInventory().contains(Weapons.sniperItem.getType())) {
				int slot = e.getSlot();
				ItemStack newShotgun = Weapons.shotgunItem;
				ItemMeta newShotgunMeta = newShotgun.getItemMeta();
				ItemStack newSniper = Weapons.sniperItem;
				ItemMeta newSnipernMeta = newSniper.getItemMeta();
				if (Game.teams()) {
					newShotgunMeta.setDisplayName(Game.getTeamColor(Game.getPlayerTeam(p)).getChatColor()
							+ "§lShotgun #" + (Game.getTeamColor(Game.getPlayerTeam(p)).getOrdinal()+1));
					newSnipernMeta.setDisplayName(Game.getTeamColor(Game.getPlayerTeam(p)).getChatColor()
							+ "§lSniper #" + (Game.getTeamColor(Game.getPlayerTeam(p)).getOrdinal()+1));
				} else {
					int ordinal = Game.getPlayerColor(p).getOrdinal();
					newShotgunMeta.setDisplayName(Game.getPlayerColor(p).getChatColor() + "§lShotgun #" + (ordinal + 1));
					newSnipernMeta.setDisplayName(Game.getPlayerColor(p).getChatColor() + "§lSniper #" + (ordinal + 1));
				}
				newShotgun.setItemMeta(newShotgunMeta);
				newSniper.setItemMeta(newSnipernMeta);
				
				boolean ready = false;
				if(slot == 1) {
					p.getInventory().setItem(2, newShotgun);
					ready = true;
				} else if(slot == 7){
					p.getInventory().setItem(2, newSniper);
					ready = true;
				}
				
				if(ready) {
					if(p.getInventory().getItem(2).getItemMeta().getDisplayName().toUpperCase().contains("SNIPER")) p.getInventory().getItem(2).setAmount(Mod.SNIPER_AMMO_BEFORE_COOLDOWN.getInt());
					p.closeInventory();
					Weapons.hasChoosenWeapon.put((Player) p, true);
					
					boolean allReady = true;
					for(Player ap : Game.players()) {
						if(!Weapons.hasChoosenWeapon.get(ap)) allReady = false;
					}
					
					if(allReady) {
						for(Player ap : Game.players()) {
							ap.sendMessage("§a§lEverybody is ready!!");
							Lasertag.everybodyReady = true;
						}
					}
				}
			}
		}
		if(he.getGameMode() != GameMode.CREATIVE) e.setCancelled(true);
	}
	
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerCloseInventory(InventoryCloseEvent e) {
		try {
			Player p = (Player) e.getPlayer();
			if(Game.waiting()) {
				if(e.getInventory().contains(Material.WHITE_STAINED_GLASS_PANE) && e.getInventory().contains(Weapons.shotgunItem.getType()) && e.getInventory().contains(Weapons.sniperItem.getType())) {
					if(!Weapons.hasChoosenWeapon.get(p)) {
						Bukkit.getScheduler().scheduleSyncDelayedTask(Lasertag.minigames, new Runnable() {
							@Override
							public void run() {
								if(!Weapons.hasChoosenWeapon.get(p)) p.openInventory(Weapons.getPlayersWeaponsInv(p));
							}
						}, 20);
					}
				}
			}
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}
}
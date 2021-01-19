package me.noobedidoob.minigames.lasertag.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
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
import me.noobedidoob.minigames.lasertag.methods.Weapons;
import me.noobedidoob.minigames.lasertag.session.Modifiers.Mod;
import me.noobedidoob.minigames.lasertag.session.Session;
import me.noobedidoob.minigames.main.Minigames;

public class ClickInventoryListener implements Listener {
	
	public ClickInventoryListener(Minigames minigames) {
		PluginManager pluginManeger = Bukkit.getPluginManager();
		pluginManeger.registerEvents(this, minigames);
	}
	
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerClickInventory(InventoryClickEvent e) {
		if(!(e.getWhoClicked() instanceof Player)) return;
		Player p = (Player) e.getWhoClicked();
		Session session = Session.getPlayerSession(p);
		if(session == null) return;
		if(session.waiting()) {
			if(e.getInventory().contains(Material.WHITE_STAINED_GLASS_PANE) && e.getInventory().contains(Weapons.shotgunItem.getType()) && e.getInventory().contains(Weapons.sniperItem.getType())) {
				int slot = e.getSlot();
				ItemStack newShotgun = Weapons.shotgunItem;
				ItemMeta newShotgunMeta = newShotgun.getItemMeta();
				ItemStack newSniper = Weapons.sniperItem;
				ItemMeta newSnipernMeta = newSniper.getItemMeta();
				if (session.isTeams()) {
					newShotgunMeta.setDisplayName(session.getTeamColor(session.getPlayerTeam(p)).getChatColor()
							+ "§lShotgun #" + (session.getTeamColor(session.getPlayerTeam(p)).getOrdinal()+1));
					newSnipernMeta.setDisplayName(session.getTeamColor(session.getPlayerTeam(p)).getChatColor()
							+ "§lSniper #" + (session.getTeamColor(session.getPlayerTeam(p)).getOrdinal()+1));
				} else {
					int ordinal = session.getPlayerColor(p).getOrdinal();
					newShotgunMeta.setDisplayName(session.getPlayerColor(p).getChatColor() + "§lShotgun #" + (ordinal + 1));
					newSnipernMeta.setDisplayName(session.getPlayerColor(p).getChatColor() + "§lSniper #" + (ordinal + 1));
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
					if(p.getInventory().getItem(2).getItemMeta().getDisplayName().toUpperCase().contains("SNIPER")) p.getInventory().getItem(2).setAmount(session.getIntMod(Mod.SNIPER_AMMO_BEFORE_COOLDOWN));
					p.closeInventory();
					Weapons.hasChoosenWeapon.put((Player) p, true);
					
					boolean allReady = true;
					for(Player ap : session.getPlayers()) {
						if(!Weapons.hasChoosenWeapon.get(ap)) allReady = false;
					}
					
					if(allReady) {
						for(Player ap : session.getPlayers()) {
							ap.sendMessage("§a§lEverybody is ready!!");
							Lasertag.everybodyReady = true;
						}
					}
				}
			}
		}
		if(p.getGameMode() != GameMode.CREATIVE) e.setCancelled(true);
	}
	
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerCloseInventory(InventoryCloseEvent e) {
		try {
			Player p = (Player) e.getPlayer();
			Session session = Session.getPlayerSession(p);
			if(session == null) return;
			if(session.waiting()) {
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
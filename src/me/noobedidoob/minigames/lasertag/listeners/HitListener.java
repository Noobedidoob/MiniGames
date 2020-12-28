package me.noobedidoob.minigames.lasertag.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.PluginManager;

import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.listeners.DeathListener.KillType;
import me.noobedidoob.minigames.lasertag.methods.Game;
import me.noobedidoob.minigames.lasertag.commands.ModifierCommands.Mod;
import me.noobedidoob.minigames.main.Minigames;

public class HitListener implements Listener {
	
	public HitListener(Minigames minigames) {
		PluginManager pluginManeger = Bukkit.getPluginManager();
		pluginManeger.registerEvents(this, minigames);
	}
	
	
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			if(Game.tagging()) {
				Player p = (Player) e.getEntity();
				Player damager = (Player) e.getDamager();
				
				if (Game.isInGame(p) && Game.isInGame(damager)) {
					if (!Game.isFromTeam(p, damager)) {
						if (Lasertag.isProtected.get(p) == null) Lasertag.isProtected.put(p, false);
						if (Lasertag.isProtected.get(p)) {
							damager.sendMessage("§cHe still has spawnprotection! You can't hit him!");
							e.setCancelled(true);
						} else {
							if (!Mod.multiWeapons()) {
								double damage = e.getDamage();
								if (p.getInventory().getItemInMainHand() != null) {
									if(p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().toUpperCase().contains("LASERGUN")) damage = Mod.LASERGUN_PVP_DAMAGE.getInt();
								}
								if(damage < p.getHealth()-1) {
									e.setDamage(1);
									damage--;
								} else e.setCancelled(true);
								DeathListener.hit(KillType.PVP, damager, p, damage, false, false, false);
							} else {
								if (damager.getItemInHand().getItemMeta().getDisplayName().toUpperCase().contains("DAGGER")) {
									double damage = Mod.STABBER_DAMAGE.getInt();
									if(damage < p.getHealth()-1) {
										e.setDamage(1);
										damage--;
									} else e.setCancelled(true);
									DeathListener.hit(KillType.PVP, damager, p, damage, false, false, false);
								}
							}
						}
					} else {
						e.setCancelled(true);
					} 
				} else {
					e.setCancelled(true);
				}
			}
		}
	}
}

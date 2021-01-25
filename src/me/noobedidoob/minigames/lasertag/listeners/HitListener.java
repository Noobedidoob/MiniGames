package me.noobedidoob.minigames.lasertag.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.PluginManager;

import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.listeners.DeathListener.KillType;
import me.noobedidoob.minigames.lasertag.session.SessionModifiers.Mod;
import me.noobedidoob.minigames.lasertag.session.Session;
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
			Player p = (Player) e.getEntity();
			Session session = Session.getPlayerSession(p);
			if(session == null) return;
			Player damager = (Player) e.getDamager();
			
			if(session.tagging()) {
				if (session.isInSession(p) && session.isInSession(damager)) {
					if (!session.inSameTeam(p, damager)) {
						if (Lasertag.isProtected.get(p) == null) Lasertag.isProtected.put(p, false);
						if (Lasertag.isProtected.get(p)) {
							damager.sendMessage("§cHe still has spawnprotection! You can't hit him!");
							e.setCancelled(true);
						} else {
							if (!session.multiWeapons()) {
								double damage = e.getDamage();
								if (p.getInventory().getItemInMainHand() != null) {
									if(p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().toUpperCase().contains("LASERGUN")) damage = session.getIntMod(Mod.LASERGUN_PVP_DAMAGE);
								}
								if(damage < p.getHealth()-1) {
									e.setDamage(1);
									damage--;
								} else e.setCancelled(true);
								DeathListener.hit(KillType.PVP, damager, p, damage, false, false, false);
							} else {
								if (damager.getItemInHand().getItemMeta().getDisplayName().toUpperCase().contains("DAGGER")) {
									double damage = session.getIntMod(Mod.STABBER_DAMAGE);
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

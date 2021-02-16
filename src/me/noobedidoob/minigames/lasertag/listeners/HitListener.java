package me.noobedidoob.minigames.lasertag.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.PluginManager;

import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.listeners.DeathListener.HitType;
import me.noobedidoob.minigames.lasertag.session.SessionModifiers.Mod;
import me.noobedidoob.minigames.lasertag.session.Session;
import me.noobedidoob.minigames.Minigames;
import me.noobedidoob.minigames.utils.BaseSphere;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class HitListener implements Listener {
	
	public HitListener(Minigames minigames) {
		PluginManager pluginManeger = Bukkit.getPluginManager();
		pluginManeger.registerEvents(this, minigames);
	}
	
	
	
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
						if (Lasertag.isPlayerProtected(p)) {
							damager.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED+""+ChatColor.BOLD+"Player has spawnprotection"));
							BaseSphere.drawPlayerProtectionSphere(p);
							e.setCancelled(true);
						} else {
							if (!session.withMultiweapons()) {
								double damage = e.getDamage();
								if (p.getInventory().getItemInMainHand().getType() != Material.AIR) {
									if(p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().toUpperCase().contains("LASERGUN")) damage = session.getIntMod(Mod.LASERGUN_PVP_DAMAGE);
								}
								if(damage < p.getHealth()-1) {
									e.setDamage(1);
									damage--;
								} else e.setCancelled(true);
								DeathListener.hit(HitType.PVP, damager, p, damage, false, false, false);
							} else {
								if (damager.getInventory().getItemInMainHand().getItemMeta().getDisplayName().toUpperCase().contains("DAGGER")) {
									double damage = session.getIntMod(Mod.STABBER_DAMAGE);
									if(damage < p.getHealth()-1) {
										e.setDamage(1);
										damage--;
									} else e.setCancelled(true);
									DeathListener.hit(HitType.PVP, damager, p, damage, false, false, false);
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

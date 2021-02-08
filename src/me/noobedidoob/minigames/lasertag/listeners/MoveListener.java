package me.noobedidoob.minigames.lasertag.listeners;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.PluginManager;

import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.session.Session;
import me.noobedidoob.minigames.main.Minigames;
import me.noobedidoob.minigames.utils.Coordinate;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class MoveListener implements Listener {
	
	public MoveListener(Minigames minigames) {
		PluginManager pluginManeger = Bukkit.getPluginManager();
		pluginManeger.registerEvents(this, minigames);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		
		Player p = e.getPlayer();
		Session session = Session.getPlayerSession(p);
		
		if(session != null) {
			if(session.tagging()) {
				if(session.getMap().withBaseSpawn() && ((session.isSolo() && !session.getMap().withRandomSpawn()) | session.isTeams())) {
					for(Coordinate coord : session.getMap().baseCoords) { 
						if(session.getMap().baseColor.get(coord) != session.getPlayerColor(p)) {
							if(coord.getLocation(Minigames.world).distance(p.getLocation()) < session.getMap().getProtectionRaduis()) {
								p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED+""+ChatColor.BOLD+"You aren't allowed to be here!"));
								session.getMap().drawBaseSphere(session.getMap().baseColor.get(coord), p);
								damagePlayer(p);
							}
						}
					}
				}
				if(Double.parseDouble(Double.toString(e.getFrom().getX()-e.getTo().getX()).replace("-", "")) > 0.13 | Double.parseDouble(Double.toString(e.getFrom().getZ()-e.getTo().getZ()).replace("-", "")) > 0.15) {
					Lasertag.isProtected.put(e.getPlayer(), false);
				}
				if(e.getFrom().getPitch() != e.getTo().getPitch() | e.getFrom().getYaw() != e.getTo().getYaw()) {
					Lasertag.isProtected.put(e.getPlayer(), false);
				}
				if(e.getTo().getY() < 0 | (session.isMapSet() && session.getMap().getName().equalsIgnoreCase("skyhigh") && e.getTo().getY() < 70)) {
					e.getPlayer().damage(100);
				}
				if(session.withMultiweapons()) {
				}
			}
		} else {
			
//			if(!p.isSprinting() && p.getInventory().getItemInHand().getType().equals(Material.DIAMOND_SWORD)) {
//				for(Player target : Bukkit.getOnlinePlayers()) {
//					if (p.getLocation().distance(target.getLocation()) < 3) {
//						Vector inverseDirectionVec = target.getEyeLocation().getDirection().normalize().multiply(-1);
//						Location locBehindTarget = target.getLocation().add(inverseDirectionVec);
//						if (p.getLocation().distance(locBehindTarget) < 1) {
//							p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "" + ChatColor.BOLD + "YOU WOULD BE ABLE TO BACKSTAB HIM :D"));
//						} 
//					}
//				}
//			}
		}
	}
	
	HashMap<Player , Boolean> openForDamage = new HashMap<Player, Boolean>();
	public void damagePlayer(Player p) {
		if(openForDamage.get(p) == null) openForDamage.put(p, true);
		if(openForDamage.get(p)) {
			p.damage(5);
			openForDamage.put(p, false);
			Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.minigames, new Runnable() {
				@Override
				public void run() {
					openForDamage.put(p, true);
				}
			}, 20);
		}
	}
	
}

//walking:  		 ->  a: 0.21581024677994573    max: 0.2159   min: 0.2158
// sprinting 		 ->  a: 0.2806167679136546     max: 0.2806   min: 0.2805
// sprinting+jumping ->  a: 0.42752746176886924    max: 0.7425   min: 0.3356

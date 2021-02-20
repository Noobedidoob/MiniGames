package me.noobedidoob.minigames.lasertag.listeners;

import java.util.HashMap;

import me.noobedidoob.minigames.lasertag.methods.PlayerTeleporter;
import me.noobedidoob.minigames.utils.Flag;
import me.noobedidoob.minigames.utils.Map;
import me.noobedidoob.minigames.utils.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.PluginManager;

import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.session.Session;
import me.noobedidoob.minigames.Minigames;

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
//				session.getMap().checkPlayerPosition(p);
//				if(session.getMap().withBaseSpawn() && ((session.isSolo() && !session.getMap().withRandomSpawn()) | session.isTeams())) {
//					for(Coordinate coord : session.getMap().baseCoords) {
//						if(session.getMap().baseColor.get(coord) != session.getPlayerColor(p)) {
//							if(coord.getLocation().distance(p.getLocation()) < session.getMap().getProtectionRaduis()) {
//								p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED+""+ChatColor.BOLD+"You aren't allowed to be here!"));
//								warnPlayer(p,session.getMap(),session.getMap().baseColor.get(coord));
//							}
//						}
//					}
//				}
				if (Lasertag.isPlayerProtected(p)) {
					if(Double.parseDouble(StringUtils.replace(Double.toString(e.getFrom().getX()-e.getTo().getX()), "-", "")) > 0.13 | Double.parseDouble(StringUtils.replace(Double.toString(e.getFrom().getZ()-e.getTo().getZ()), "-", "")) > 0.15) {
						Lasertag.setPlayerProtected(e.getPlayer(), false);
					}
					if(e.getFrom().getPitch() != e.getTo().getPitch() | e.getFrom().getYaw() != e.getTo().getYaw()) {
						Lasertag.setPlayerProtected(e.getPlayer(), false);
					}
				}
				if(e.getTo().getY() < 0 | (session.isMapSet() && session.getMap().getName().equalsIgnoreCase("skyhigh") && e.getTo().getY() < 70)) {
					if(Flag.getPlayerFlag(p) != null) Flag.getPlayerFlag(p).teleportToBase();
					e.getPlayer().teleport(PlayerTeleporter.getPlayerSpawnLoc(e.getPlayer()));
				}
			}
		} /*else {
			
			if(!p.isSprinting() && p.getInventory().getItemInHand().getType().equals(Material.DIAMOND_SWORD)) {
				for(Player target : Bukkit.getOnlinePlayers()) {
					if (p.getLocation().distance(target.getLocation()) < 3) {
						Vector inverseDirectionVec = target.getEyeLocation().getDirection().normalize().multiply(-1);
						Location locBehindTarget = target.getLocation().add(inverseDirectionVec);
						if (p.getLocation().distance(locBehindTarget) < 1) {
							p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "" + ChatColor.BOLD + "YOU WOULD BE ABLE TO BACKSTAB HIM :D"));
						}
					}
				}
			}
		}*/
	}
	
	private final HashMap<Player , Boolean> openForDamage = new HashMap<>();
	public void warnPlayer(Player p, Map map, Lasertag.LasertagColor color) {
		openForDamage.putIfAbsent(p, true);
		if(openForDamage.get(p)) {
			p.damage(5);
			map.drawBaseSphere(color, p);
			openForDamage.put(p, false);
			Utils.runLater(() -> openForDamage.put(p,true), 20);
		}
	}
	
}

//walking:  		 ->  a: 0.21581024677994573    max: 0.2159   min: 0.2158
// sprinting 		 ->  a: 0.2806167679136546     max: 0.2806   min: 0.2805
// sprinting+jumping ->  a: 0.42752746176886924    max: 0.7425   min: 0.3356

package me.noobedidoob.minigames.lasertag.listeners;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.methods.PlayerZoomer;
import me.noobedidoob.minigames.lasertag.methods.Weapons;
import me.noobedidoob.minigames.lasertag.session.Session;
import me.noobedidoob.minigames.main.Minigames;
import me.noobedidoob.minigames.utils.Coordinate;
import me.noobedidoob.minigames.utils.Pair;
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
			if(session.tagging() && session.isInSession(p)) {
				if(session.getMap().withBaseSpawn()) {
					for(Coordinate coord : session.getMap().baseCoords) {
						if(session.getMap().baseColor.get(coord) != session.getPlayerColor(p).getChatColor()) {
							if(coord.getLocation(Minigames.world).distance(p.getLocation()) < session.getMap().getProtectionRaduis()) {
								p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED+""+ChatColor.BOLD+"You aren't allowed to be here!"));
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
				if(e.getTo().getY() < 0) {
					e.getPlayer().damage(100);
				}
				if(session.withMultiweapons()) {
				}
			}
		} else {
			if(e.getTo().getY() < 0) {
				Location spawnLoc = Minigames.world.getSpawnLocation();
				spawnLoc.setPitch(p.getLocation().getPitch());
				spawnLoc.setYaw(p.getLocation().getYaw());
				p.teleport(spawnLoc);
			}
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
		playersLastLocation.put(p, new Pair(e.getFrom(), e.getTo()));
		
		if (session != null && !session.isInSession(p)) {
			if (Lasertag.playerTesting.get(p) == null)
				Lasertag.playerTesting.put(p, false);
			if (!Lasertag.playerTesting.get(p)) {
				if(Lasertag.testArea.isInside(e.getTo())) {
					Lasertag.playerTesting.put(p, true);
					p.getInventory().clear();
					for (int i = 0; i < Weapons.testWeapons.size(); i++) {
						p.getInventory().setItem(i, Weapons.testWeapons.get(i));
					}
					p.getInventory().getItem(3).setAmount(2);
				}
			} else if (Lasertag.playerTesting.get(p) && !Lasertag.testArea.isInside(e.getTo())) {
				Lasertag.playerTesting.put(p, false);
				p.getInventory().clear();
				PlayerZoomer.zoomPlayerOut(p);
			} 
		}
	}
	
	HashMap<Player, Pair> playersLastLocation = new HashMap<Player, Pair>();
	
	@EventHandler
	public void onPlayerFly(PlayerToggleFlightEvent e) {
		Player p = e.getPlayer();
		if(p.getGameMode() == GameMode.ADVENTURE && p.getAllowFlight()) {
			e.setCancelled(true);
			p.setAllowFlight(false);
			p.setFlying(false);
			
//			Vector direction = p.getLocation().getDirection();
			Vector direction = ((Location) playersLastLocation.get(p).get1()).subtract((Location) playersLastLocation.get(p).get1()).toVector();
	        direction.setY(0.8);
	        p.setVelocity(direction);
	        p.getLocation().getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1, 1);
	        Lasertag.animateExpBar(p, 20*3);
	        Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.minigames, new Runnable() {
				@Override
				public void run() {
					p.setAllowFlight(true);
				}
			}, 20*3);
		} else if(p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR) {
			e.setCancelled(true);
			p.setFlying(false);
		}
	}
	
	@EventHandler
	public void onPlayerToggleSneak(PlayerToggleSneakEvent e) {
		Player p = e.getPlayer();
		Session session = Session.getPlayerSession(p);
		if(session == null) return;
		if(session.tagging()) {
			Lasertag.isProtected.put(e.getPlayer(), false);
		} else if(p == Bukkit.getPlayer("Noobedidoob")) {
//			FallingBlock b = world.spawnFallingBlock(p.getLocation(), Material.BLACK_BANNER, (byte) 0x0);
//			FallingBlock d = world.spawnFallingBlock(p.getLocation(), Material.DIRT, (byte) 0x0);
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

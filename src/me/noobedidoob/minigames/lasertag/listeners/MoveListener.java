package me.noobedidoob.minigames.lasertag.listeners;

import java.util.HashMap;

import me.noobedidoob.minigames.lasertag.methods.PlayerTeleporter;
import me.noobedidoob.minigames.utils.Flag;
import me.noobedidoob.minigames.utils.Map;
import me.noobedidoob.minigames.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;

import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.session.Session;
import me.noobedidoob.minigames.Minigames;
import org.bukkit.potion.PotionEffectType;

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
				if (session.withMultiweapons()) {

					boolean found = false;
					for(Entity entity : p.getNearbyEntities(2,2,2)){
						if(entity instanceof Player && session.isInSession((Player) entity)){
							Player target = (Player) entity;
							if(Utils.isPlayerBehindOtherPlayer(p,target)){
								found = true;
							}
						}
					}
					if(found){
						ItemMeta meta = p.getInventory().getItem(1).getItemMeta();
						meta.addEnchant(Enchantment.DAMAGE_ALL, 100, true);
						p.getInventory().getItem(1).setItemMeta(meta);
						p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN+""+ChatColor.BOLD+"Ready for backstab!"));
					} else if(p.getInventory().getItem(1) != null && p.getInventory().getItem(1).getItemMeta().hasEnchant(Enchantment.DAMAGE_ALL)) {
						ItemMeta meta = p.getInventory().getItem(1).getItemMeta();
						meta.removeEnchant(Enchantment.DAMAGE_ALL);
						p.getInventory().getItem(1).setItemMeta(meta);
						p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.WHITE+" "));
					}
				}

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
					p.removePotionEffect(PotionEffectType.BLINDNESS);
					p.removePotionEffect(PotionEffectType.SLOW);
					session.broadcast(session.getPlayerColor(p)+p.getName()+" §7dropped out");
				}
			}
		}
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

package me.noobedidoob.minigames.lasertag.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;

import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.methods.Game;
import me.noobedidoob.minigames.lasertag.methods.PlayerZoomer;
import me.noobedidoob.minigames.lasertag.methods.Weapons;
import me.noobedidoob.minigames.main.Minigames;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class JoinQuitListener implements Listener {
	
	public JoinQuitListener(Minigames minigames) {
		PluginManager pluginManeger = Bukkit.getPluginManager();
		pluginManeger.registerEvents(this, minigames);
	}
	
	
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		try {PlayerZoomer.zoomPlayerOut(p);} catch (Exception e2) {}
		if(Game.tagging()) {
			for(Player ap : Game.players()) {
				if(ap == p) {
					Lasertag.disconnectedPlayers.add(p.getUniqueId());
				}
			}
		}
	}
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		p.getInventory().clear();
		Lasertag.laserCommands.flagIsFollowing.put(p, false);
//		Weapons.playerCoolingdown.put(p, false);
		Weapons.lasergunCoolingdown.put(p, false);
		Weapons.shotgunCoolingdown.put(p, false);
		Weapons.sniperCoolingdown.put(p, false);
		if(p.getGameMode().equals(GameMode.ADVENTURE)) p.setAllowFlight(true);
		p.setResourcePack("https://www.dropbox.com/s/e3m6grsz7q7e5wi/Lasertag%20texturepack.zip?dl=1");
		p.sendMessage("§bThis server uses a customized texturepack for lasertag minigame. \n§bClick on §6LOAD §bto load the texturepack or on §aDOWNLOAD §bto download and install the texturepack manually.");
		
		TextComponent loadMsg = new TextComponent("LOAD");
		loadMsg.setColor(ChatColor.GREEN);
		loadMsg.setBold(true);
		loadMsg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lt loadtexturepack"));
		loadMsg.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click here to load the texturepack").create() ) );
		TextComponent seperator = new TextComponent(" | ");
		seperator.setColor(ChatColor.WHITE);
		TextComponent urlMsg = new TextComponent("DOWNLOAD");
		urlMsg.setColor(ChatColor.GOLD);
		urlMsg.setBold(true);
		urlMsg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, Minigames.texturepackURL));
		urlMsg.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click here to download the texturepack").create() ) );
		loadMsg.addExtra(seperator);
		loadMsg.addExtra(urlMsg);
		p.spigot().sendMessage(loadMsg);
		/*if(l.personalRecord.get(p.getUniqueId()) == null) {
			l.personalRecord.put(p.getUniqueId(), 0);
			l.uuidName.put(p.getUniqueId(), p.getName());
		}*/
		if(Game.tagging()) {
			for(UUID id : Lasertag.disconnectedPlayers) {
				if(id.equals(p.getUniqueId())) {
					p.sendMessage("§aWelcome Back, "+p.getName()+"! The game is still in Progress!");
					Game.addDisconnectedPlayer();
				}
			}
		} else {
			p.teleport(Minigames.spawn);
		}
	}
}
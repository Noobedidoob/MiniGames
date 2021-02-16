package me.noobedidoob.minigames;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.methods.PlayerZoomer;
import me.noobedidoob.minigames.lasertag.methods.Weapons.Weapon;
import me.noobedidoob.minigames.lasertag.session.Session;
import me.noobedidoob.minigames.utils.Pair;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Listeners implements Listener{
	
	private final Minigames minigames;
	public Listeners(Minigames minigames) {
		this.minigames = minigames;
		Bukkit.getPluginManager().registerEvents(this, minigames);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		p.setGameMode(GameMode.ADVENTURE);
		p.setExp(1f);
		p.setLevel(0);
		p.setAllowFlight(true);
		Lasertag.setPlayersLobbyInv(p);
		e.setJoinMessage("");
		
		TextComponent msg1 = new TextComponent("Hello there!\n");
		msg1.setColor(net.md_5.bungee.api.ChatColor.GREEN);

		TextComponent msg2 = new TextComponent("This server uses a custom texturepack! You should either click ");
		msg2.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
		msg1.addExtra(msg2);

		TextComponent linkMsg = new TextComponent("here");
		linkMsg.setColor(net.md_5.bungee.api.ChatColor.GOLD);
		linkMsg.setUnderlined(true);
		linkMsg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, Minigames.TEXTUREPACK_URL));
		//noinspection deprecation
		linkMsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Download the texturepack").create()));
		msg1.addExtra(linkMsg);

		TextComponent msg3 = new TextComponent(" to download it or activate server resourcepacks in the serversettings before joining!");
		msg3.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
		msg1.addExtra(msg3);

		p.spigot().sendMessage(msg1);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if(Session.getPlayerSession(p) == null) {
					p.teleport(minigames.spawn);
					for(Player op : Bukkit.getOnlinePlayers()) {
						if(op != p) op.sendMessage("§e"+p.getName()+" joined");
					}
				} else {
					for(Player op : Bukkit.getOnlinePlayers()) {
						if(!Session.getPlayerSession(p).isInSession(op)) op.sendMessage("§e"+p.getName()+" joined");
					}
				}
			}
		}.runTaskLater(minigames, 20);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		p.getInventory().clear();
	}
	
	private final HashMap<Player, ItemStack[]> playerStoredInv = new HashMap<>();
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();

		assert e.getTo() != null;
		
		if(Session.getPlayerSession(p) == null && e.getTo().getY() < 0) {
			Location spawnLoc = minigames.world.getSpawnLocation();
			spawnLoc.setPitch(p.getLocation().getPitch());
			spawnLoc.setYaw(p.getLocation().getYaw());
			p.teleport(spawnLoc);
		}
		
		playersLastLocation.put(p, new Pair(e.getFrom(), e.getTo()));
		
		if (Session.getPlayerSession(p) != null && Session.getPlayerSession(p).tagging()) return;
		if (!Lasertag.isPlayerTesting(p)) {
			if(Lasertag.getTestAera().isInside(e.getTo())) {
				Lasertag.setPlayerTesting(p, true);
				playerStoredInv.put(p, p.getInventory().getContents());
				p.getInventory().setItem(0, Weapon.LASERGUN.getTestItem());
				p.getInventory().setItem(1, Weapon.SHOTGUN.getTestItem());
				p.getInventory().setItem(2, Weapon.SNIPER.getTestItem());
			}
		} else if (Lasertag.isPlayerTesting(p) && !Lasertag.getTestAera().isInside(e.getTo())) {
			Lasertag.setPlayerTesting(p, false);
			p.getInventory().setContents(playerStoredInv.get(p));
			PlayerZoomer.zoomPlayerOut(p);
		} 
	}
	
	private final HashMap<Player, Pair> playersLastLocation = new HashMap<>();

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
	        assert p.getLocation().getWorld() != null;
	        p.getLocation().getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1, 1);
	        Lasertag.animateExpBar(p, 20*3);
	        Bukkit.getScheduler().scheduleSyncDelayedTask(minigames, () -> p.setAllowFlight(true), 20*3);
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
			Lasertag.setPlayerProtected(e.getPlayer(), false);
		} /*else if(p == Bukkit.getPlayer("Noobedidoob")) {
			FallingBlock b = world.spawnFallingBlock(p.getLocation(), Material.BLACK_BANNER, (byte) 0x0);
			FallingBlock d = world.spawnFallingBlock(p.getLocation(), Material.DIRT, (byte) 0x0);
		}*/
	}
	
	@EventHandler
	public void playerInteractAtEntity(PlayerInteractEntityEvent e) {
		Entity entity = e.getRightClicked();
		if(entity instanceof ItemFrame) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		if(!e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) e.setCancelled(true);
	}
	@EventHandler
	public void onPlayerItemPickup(@SuppressWarnings("deprecation") PlayerPickupItemEvent e) {
		if(!e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) e.setCancelled(true);
	}
	
	
	@EventHandler
	public void onPlayerClickInventory(InventoryClickEvent e) {
		if(!(e.getWhoClicked() instanceof Player)) return;
		Player p = (Player) e.getWhoClicked();
		if(p.getGameMode() != GameMode.CREATIVE) e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		e.setMessage(ChatColor.translateAlternateColorCodes('&', e.getMessage()));
		e.setFormat("<§d%s§f>: %s");
	}
	
	
	@EventHandler
	public void onListPing(ServerListPingEvent e) {
		e.setMaxPlayers(Bukkit.getOnlinePlayers().size()+1);
	}
	
	@EventHandler
	public void onPlayerSwitchGameMode(PlayerGameModeChangeEvent e) {
		if(e.getNewGameMode().equals(GameMode.ADVENTURE)) e.getPlayer().setAllowFlight(true);
		else if(e.getNewGameMode().equals(GameMode.SURVIVAL)) e.getPlayer().setAllowFlight(false);
	}
	
	@EventHandler
	public void onPlayerChangeGameMode(PlayerGameModeChangeEvent e) {
		if(e.getNewGameMode() == GameMode.ADVENTURE) e.getPlayer().setAllowFlight(true);
	}
	
	@EventHandler
	public void onPlayerPlaceBlock(BlockPlaceEvent e) {
		if(e.getPlayer().getGameMode() != GameMode.CREATIVE) e.setCancelled(true);
	}
	
}

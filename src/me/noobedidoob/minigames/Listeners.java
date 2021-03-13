package me.noobedidoob.minigames;

import java.util.HashMap;

import me.noobedidoob.minigames.utils.InstantFirework;
import me.noobedidoob.minigames.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
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
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.methods.PlayerZoomer;
import me.noobedidoob.minigames.lasertag.methods.Weapon;
import me.noobedidoob.minigames.lasertag.session.Session;
import me.noobedidoob.minigames.utils.Pair;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

@SuppressWarnings("deprecation")
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

		p.sendMessage("§7——————————————————————");
		TextComponent msg1 = new TextComponent("Hello there, ");
		msg1.setColor(net.md_5.bungee.api.ChatColor.GREEN);

		TextComponent msg15 = new TextComponent(p.getName()+"!\n");
		msg15.setColor(net.md_5.bungee.api.ChatColor.AQUA);
		msg1.addExtra(msg15);

		TextComponent msg2 = new TextComponent(" This server uses a custom texturepack! You should either click ");
		msg2.setColor(net.md_5.bungee.api.ChatColor.GOLD);
		msg1.addExtra(msg2);

		TextComponent linkMsg = new TextComponent("here");
		linkMsg.setColor(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE);
		linkMsg.setUnderlined(true);
		linkMsg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, Minigames.TEXTUREPACK_URL));
		linkMsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Download the texturepack").create()));
		msg1.addExtra(linkMsg);

		TextComponent msg3 = new TextComponent(" to download it or activate server resourcepacks in the server-settings before joining!");
		msg3.setColor(net.md_5.bungee.api.ChatColor.GOLD);
		msg1.addExtra(msg3);
		p.spigot().sendMessage(msg1);
		p.sendMessage("§7——————————————————————\n ");

		new BukkitRunnable() {
			@Override
			public void run() {
				if(!Session.isPlayerInSession(p)) {
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
		
		if(!Session.isPlayerInSession(p) && e.getTo().getY() < 0) {
			Location spawnLoc = minigames.world.getSpawnLocation();
			spawnLoc.setPitch(p.getLocation().getPitch());
			spawnLoc.setYaw(p.getLocation().getYaw());
			p.teleport(spawnLoc);
		}
		
		playersLastLocation.put(p, new Pair(e.getFrom(), e.getTo()));
		if (Session.isPlayerInSession(p) && Session.getPlayerSession(p).tagging()) return;
		if (!Lasertag.isPlayerTesting(p)) {
			if(Lasertag.getTestAera().isInside(e.getTo())) {
				Lasertag.setPlayerTesting(p, true);
				playerStoredInv.put(p, p.getInventory().getContents());
				Weapon.setTestInventory(p);
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
			playersLastLocation.putIfAbsent(p,new Pair(p.getLocation(),p.getLocation()));
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
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			Player hitP = (Player) e.getEntity();
			Player p = (Player) e.getDamager();
			if(Session.isPlayerInSession(p)) return;
//			if (p.getLocation().add(0, 1, 0).distance(Utils.getPlayerBackLocation(hitP)) <= 0.5) {
//				hitP.teleport(minigames.spawn);
//			}
			if(Utils.isPlayerBehindOtherPlayer(p,hitP)) {
				new InstantFirework(FireworkEffect.builder().with(FireworkEffect.Type.BALL).flicker(false).trail(false).withColor(Color.RED).build(), hitP.getLocation().add(0,1,0));
				hitP.teleport(minigames.spawn);
			}
		}
	}

	@EventHandler
	public void playerInteractAtEntity(PlayerInteractEntityEvent e) {
		Entity entity = e.getRightClicked();
		if(entity instanceof ItemFrame) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e){
		if(e.getPlayer().getGameMode().equals(GameMode.CREATIVE) && !Lasertag.isPlayerTesting(e.getPlayer()) && Session.getPlayerSession(e.getPlayer()) == null) return;
		Player p = e.getPlayer();
		ItemStack item = e.getItemDrop().getItemStack();
		item.setAmount(p.getInventory().getItemInMainHand().getAmount()+1);
		e.getItemDrop().remove();
		int slot = p.getInventory().getHeldItemSlot();
		Utils.runLater(()->p.getInventory().setItem(slot,item), 1);
	}

	@EventHandler
	public void onPlayerItemPickup(PlayerPickupItemEvent e) {
		if(!e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) e.setCancelled(true);
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e){
		e.setCancelled(true);
	}
	
	
	@EventHandler
	public void onPlayerClickInventory(InventoryClickEvent e) {
		if(e.getWhoClicked().getGameMode().equals(GameMode.CREATIVE)) return;
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
	public void onPlayerChangeGameMode(PlayerGameModeChangeEvent e) {
		try {
			if(e.getPlayer() != null && e.getNewGameMode() == GameMode.ADVENTURE) {
				e.getPlayer().setExp(1f);
				e.getPlayer().setAllowFlight(true);
			}
		} catch (Exception ignore) {
		}
	}
	
	@EventHandler
	public void onPlayerPlaceBlock(BlockPlaceEvent e) {
		if(e.getPlayer().getGameMode() != GameMode.CREATIVE) e.setCancelled(true);
	}
	
}

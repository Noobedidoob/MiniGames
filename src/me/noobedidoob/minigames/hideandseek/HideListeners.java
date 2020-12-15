package me.noobedidoob.minigames.hideandseek;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MiscDisguise;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import me.noobedidoob.minigames.main.Minigames;

public class HideListeners implements Listener {

	private Minigames m;
	private HideAndSeek h;
	public HideListeners(Minigames minigames, HideAndSeek hideAndSeek) {
		this.m = minigames;
		this.h = hideAndSeek;
		Bukkit.getPluginManager().registerEvents(this, m);
	}
	
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if(h.testing) {
			e.setCancelled(true);
			Player p = e.getPlayer();
			Block b = e.getClickedBlock();
			ItemStack i = new ItemStack(Material.AIR);
			if(e.getItem() != null) i = e.getItem();
			if(e.getAction().name().contains("RIGHT")) {
				if(i.getType() == Material.BARRIER && i.getItemMeta().getDisplayName().contains("UNDISGUISE")) {
					e.setCancelled(true);
					if(DisguiseAPI.isDisguised(p)) {
						DisguiseAPI.undisguiseToAll(p);
						Bukkit.getScheduler().cancelTask(h.playerCountdown.get(p));
						p.setLevel(0);
						p.setExp(0);
					}
				} else {
					if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
						MiscDisguise md = new MiscDisguise(DisguiseType.FALLING_BLOCK, b.getType());
						md.setEntity(p);
						md.startDisguise();
						h.startedDisguising(p);
					}
				}
			} else if(e.getAction().name().contains("LEFT")) {
				if(DisguiseAPI.isDisguised(p)) {
					Location loc = new Location(p.getWorld(), p.getLocation().getBlockX()+0.5, p.getLocation().getBlockY(), p.getLocation().getBlockZ()+0.5);
					loc.setPitch(p.getLocation().getPitch());
					loc.setYaw(p.getLocation().getYaw());
					p.teleport(loc);
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e) {
		if(h.testing) {
			Player p = e.getPlayer();
			Entity en = e.getRightClicked();
			if(p.getInventory().getItemInMainHand() == h.undisguiseItem) {
				if(DisguiseAPI.isDisguised(p)) {
					DisguiseAPI.undisguiseToAll(p);
				}
			} else {
				e.setCancelled(true);
				if(!(en instanceof Player)) {
					MobDisguise md = new MobDisguise(DisguiseType.getType(en));
					md.setEntity(p);
					md.startDisguise();
					h.startedDisguising(p);
				} else {
					PlayerDisguise pd = new PlayerDisguise((Player)en);
					pd.setEntity(p);
					pd.startDisguise();
					h.startedDisguising(p);
				}
			}
		}
	}
	
	
}
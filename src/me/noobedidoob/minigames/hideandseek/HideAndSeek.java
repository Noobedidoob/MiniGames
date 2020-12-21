package me.noobedidoob.minigames.hideandseek;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.libraryaddict.disguise.DisguiseAPI;
import me.noobedidoob.minigames.main.Minigames;

public class HideAndSeek {
	
	private Minigames m;
	public HideAndSeek(Minigames minigames) {
		this.m = minigames;
	}
	
	
	public boolean testing = false;
	public int maxDisguiseTime = 20;
	public HashMap<Player, Integer> playerCountdownTime = new HashMap<Player, Integer>();
	public HashMap<Player, Integer> playerCountdown = new HashMap<Player, Integer>();
	public ItemStack undisguiseItem = new ItemStack(Material.BARRIER);
	
	
	public void enable() {
		for(Player p : Bukkit.getOnlinePlayers()) {
			playerCountdownTime.put(p, maxDisguiseTime);
		}
		
		new HideListeners(Minigames.minigames, this);
		
		ItemMeta undisguiseItemMeta = undisguiseItem.getItemMeta();
		undisguiseItemMeta.setDisplayName("§c§lUNDISGUISE");
		undisguiseItem.setItemMeta(undisguiseItemMeta);
	}
	public void disable() {
		if(testing) {
			for(Player p : Bukkit.getOnlinePlayers()) {
				if(DisguiseAPI.isDisguised(p)) {
					DisguiseAPI.undisguiseToAll(p);
				}
			}
		}
	}
	
	int time;
	int c;
	boolean countingDown = false;
	public void startedDisguising(Player p) {
		if(countingDown) {
			Bukkit.getScheduler().cancelTask(playerCountdown.get(p));
		}
		countingDown = true;
		
		int newMaxDisguiseTime = maxDisguiseTime*5;
		playerCountdownTime.put(p, newMaxDisguiseTime);
		p.setExp(1f);
		p.setLevel(0);
		playerCountdown.put(p, Bukkit.getScheduler().scheduleSyncRepeatingTask(m, new Runnable() {
			@Override
			public void run() {
				if(playerCountdownTime.get(p) >= 0) {
					float f = (float) playerCountdownTime.get(p) / newMaxDisguiseTime;
					System.out.println(playerCountdownTime.get(p));
					p.setExp(f);
					p.setLevel(playerCountdownTime.get(p)/5);
					playerCountdownTime.put(p, playerCountdownTime.get(p)-1);
				} else {
					Bukkit.getScheduler().cancelTask(playerCountdown.get(p));
					if(DisguiseAPI.isDisguised(p)) {
						DisguiseAPI.undisguiseToAll(p);
					}
					countingDown = false;
				}
			}
		}, 0, 4));
	}
}

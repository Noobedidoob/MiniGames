package me.noobedidoob.minigames.lasertag.methods;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.Lasertag.LasertagColor;
import me.noobedidoob.minigames.lasertag.session.SessionModifiers.Mod;
import me.noobedidoob.minigames.lasertag.session.Session;

public class Weapons {

//	public static ItemStack lasergunItem = new ItemStack(Material.DIAMOND_HOE);
////	public static ItemStack minigunItem = new ItemStack(Material.DIAMOND_AXE);
//	public static ItemStack daggerItem = new ItemStack(Material.DIAMOND_SWORD);
//	public static ItemStack shotgunItem = new ItemStack(Material.DIAMOND_SHOVEL);
//	public static ItemStack sniperItem = new ItemStack(Material.DIAMOND_PICKAXE);
//	public static HashMap<Player, Boolean> hasChoosenWeapon = new HashMap<Player, Boolean>();
	public static void registerWeapons() {
//		ItemMeta lasergunItemMeta = lasergunItem.getItemMeta();
//		lasergunItemMeta.setDisplayName("§b§lLasergun");
//		lasergunItemMeta.setUnbreakable(true);
//		lasergunItem.setItemMeta(lasergunItemMeta);
//		lasergunItem.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 20);
//		
//		ItemMeta daggerItemMeta = daggerItem.getItemMeta();
//		daggerItemMeta.setDisplayName("§a§lDagger");
//		daggerItemMeta.setUnbreakable(true);
//		daggerItem.setItemMeta(daggerItemMeta);
//		daggerItem.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 20);
//		
//		ItemMeta shotgunItemMeta = shotgunItem.getItemMeta();
//		shotgunItemMeta.setUnbreakable(true);
//		shotgunItemMeta.setDisplayName("§e§lShotgun");
//		shotgunItem.setItemMeta(shotgunItemMeta);
//		
//		ItemMeta sniperItemMeta = sniperItem.getItemMeta();
//		sniperItemMeta.setUnbreakable(true);
//		sniperItemMeta.setDisplayName("§d§lSniper rifle");
//		sniperItem.setItemMeta(sniperItemMeta);
		
		Bukkit.getOnlinePlayers().forEach(p -> {
//			playerCoolingdown.put(p, false);
			lasergunCoolingdown.put(p, false);
			shotgunCoolingdown.put(p, false);
			sniperCoolingdown.put(p, false);
		});
	}
	
	
	
	public enum Weapon {
		LASERGUN(Material.DIAMOND_HOE, "§bLasergun"),
		DAGGER(Material.DIAMOND_SWORD, "§aDagger"),
		SHOTGUN(Material.DIAMOND_SHOVEL, "§eShotgun"),
		SNIPER(Material.DIAMOND_PICKAXE, "§dSniper rifle");
		
		private Material material;
		private ItemStack item;
		
		Weapon(Material material, String displayName){
			this.material = material;
			ItemStack item = new ItemStack(material);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(displayName);
			meta.setUnbreakable(true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
			item.setItemMeta(meta);
			this.item = item;
		}
		
		public Material getType() {
			return material;
		}
		public ItemStack getItem() {
			return item;
		}
		public ItemStack getTestItem() {
			ItemStack newItem = item;
			ItemMeta meta = newItem.getItemMeta();
			meta.setDisplayName(meta.getDisplayName()+" Test");
			newItem.setItemMeta(meta);
			return newItem;
		}
		public ItemStack getColoredItem(LasertagColor color) {
			ItemStack item = this.item;
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(color.getChatColor()+meta.getDisplayName().substring(2)+" "+(color.ordinal()+1));
			item.setItemMeta(meta);
			return item;
		}
		
		public static Weapon getWeaponFromItem(ItemStack item) {
			Material type = item.getType();
			String name = item.getItemMeta().getDisplayName();
			if(type.equals(Material.DIAMOND_HOE) && name.toUpperCase().contains("LASERGUN")) return Weapon.LASERGUN;
			else if(type.equals(Material.DIAMOND_SWORD) && name.toUpperCase().contains("DAGGER")) return Weapon.DAGGER;
			else if(type.equals(Material.DIAMOND_SHOVEL) && name.toUpperCase().contains("SHOTGUN")) return Weapon.SHOTGUN;
			else if(type.equals(Material.DIAMOND_PICKAXE) && name.toUpperCase().contains("SNIPER")) return Weapon.SNIPER;
			else return null;
		}
	}
	
	public static HashMap<Player, Boolean> lasergunCoolingdown = new HashMap<Player, Boolean>();
	public static HashMap<Player, Integer> lasergunCountDownTime = new HashMap<Player, Integer>();
	public static HashMap<Player, Integer> lasergunCountdown = new HashMap<Player, Integer>();
	public static HashMap<Player, Boolean> shotgunCoolingdown = new HashMap<Player, Boolean>();
	public static HashMap<Player, Integer> shotgunCountdown = new HashMap<Player, Integer>();
	public static HashMap<Player, Integer> shotgunCountdownTime = new HashMap<Player, Integer>();
	public static HashMap<Player, Boolean> sniperCoolingdown = new HashMap<Player, Boolean>();
	public static HashMap<Player, Integer> sniperCountdown = new HashMap<Player, Integer>();
	public static HashMap<Player, Integer> sniperCountdownTime = new HashMap<Player, Integer>();
	public static void cooldownPlayer(Player p, Weapon weapon, boolean testing) {
		Session session = Session.getPlayerSession(p);
		if(session != null) {
			if(weapon == Weapon.LASERGUN) {
				lasergunCoolingdown.put(p, true);
				int cooldown = session.getIntMod(Mod.LASERGUN_COOLDOWN_TICKS);
				if(session.withMultiweapons()) cooldown = session.getIntMod(Mod.LASERGUN_MULTIWEAPONS_COOLDOWN_TICKS);
				p.setCooldown(Material.DIAMOND_HOE, cooldown);
				Bukkit.getScheduler().scheduleSyncDelayedTask(Lasertag.minigames, new Runnable() {
					@Override
					public void run() {
						lasergunCoolingdown.put(p, false);
					}
				}, cooldown);
			} else if(weapon == Weapon.SNIPER) {
				sniperCoolingdown.put(p, true);
				p.setCooldown(Material.DIAMOND_PICKAXE, session.getIntMod(Mod.SNIPER_COOLDOWN_TICKS));
				Bukkit.getScheduler().scheduleSyncDelayedTask(Lasertag.minigames, new Runnable() {
					@Override
					public void run() {
						sniperCoolingdown.put(p, false);
						if(!testing) p.getInventory().getItem(2).setAmount(session.getIntMod(Mod.SNIPER_AMMO_BEFORE_COOLDOWN));
						else {
							p.getInventory().getItem(3).setAmount(session.getIntMod(Mod.SNIPER_AMMO_BEFORE_COOLDOWN));
							LaserShooter.playersSnipershots.put(p, 0);
						}
					}
				}, session.getIntMod(Mod.SNIPER_COOLDOWN_TICKS));
			} else if(weapon == Weapon.SHOTGUN) {
				shotgunCoolingdown.put(p, true);
				p.setCooldown(Material.DIAMOND_SHOVEL, session.getIntMod(Mod.SHOTGUN_COOLDOWN_TICKS));
				Bukkit.getScheduler().scheduleSyncDelayedTask(Lasertag.minigames, new Runnable() {
					@Override
					public void run() {
						shotgunCoolingdown.put(p, false);
					}
				}, session.getIntMod(Mod.SHOTGUN_COOLDOWN_TICKS));
			}
		} 
	}
	
	

}

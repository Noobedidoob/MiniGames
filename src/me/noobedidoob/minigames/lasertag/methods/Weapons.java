package me.noobedidoob.minigames.lasertag.methods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.noobedidoob.minigames.lasertag.Lasertag;

public class Weapons {

	public static ItemStack lasergunItem = new ItemStack(Material.DIAMOND_HOE);
//	public static ItemStack minigunItem = new ItemStack(Material.DIAMOND_AXE);
	public static ItemStack daggerItem = new ItemStack(Material.DIAMOND_SWORD);
	public static ItemStack shotgunItem = new ItemStack(Material.DIAMOND_SHOVEL);
	public static ItemStack sniperItem = new ItemStack(Material.DIAMOND_PICKAXE);
	public static HashMap<Player, Boolean> hasChoosenWeapon = new HashMap<Player, Boolean>();
	public static void registerWeapons() {
		ItemMeta lasergunItemMeta = lasergunItem.getItemMeta();
		lasergunItemMeta.setDisplayName("§a§lLasergun");
		lasergunItemMeta.setUnbreakable(true);
		lasergunItem.setItemMeta(lasergunItemMeta);
		lasergunItem.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 20);
		
//		ItemMeta minigunItemMeta = minigunItem.getItemMeta();
//		minigunItemMeta.setDisplayName("§c§lMinigun");
//		minigunItemMeta.setUnbreakable(true);
//		minigunItem.setItemMeta(minigunItemMeta);
//		minigunItem.addUnsafeEnchantment(Enchantment.QUICK_CHARGE, 15);
		
		ItemMeta daggerItemMeta = daggerItem.getItemMeta();
		daggerItemMeta.setDisplayName("§a§lDagger");
		daggerItemMeta.setUnbreakable(true);
		daggerItem.setItemMeta(daggerItemMeta);
		daggerItem.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 20);
		
		ItemMeta shotgunItemMeta = shotgunItem.getItemMeta();
		shotgunItemMeta.setUnbreakable(true);
		shotgunItemMeta.setDisplayName("§e§lShotgun");
		shotgunItem.setItemMeta(shotgunItemMeta);
		
		ItemMeta sniperItemMeta = sniperItem.getItemMeta();
		sniperItemMeta.setUnbreakable(true);
		sniperItemMeta.setDisplayName("§d§lSniper rifle");
		sniperItem.setItemMeta(sniperItemMeta);
		
		Bukkit.getOnlinePlayers().forEach(p -> {
//			playerCoolingdown.put(p, false);
			lasergunCoolingdown.put(p, false);
			shotgunCoolingdown.put(p, false);
			sniperCoolingdown.put(p, false);
		});
	}
	
	
	
	public static enum Weapon {
		LASERGUN,
		DAGGER,
		SHOTGUN,
		SNIPER,
//		MINIGUN
	}
	
//	public static HashMap<Player, Boolean> playerCoolingdown = new HashMap<Player, Boolean>();
	public static HashMap<Player, Boolean> lasergunCoolingdown = new HashMap<Player, Boolean>();
	public static HashMap<Player, Integer> lasergunCountDownTime = new HashMap<Player, Integer>();
	public static HashMap<Player, Integer> lasergunCountdown = new HashMap<Player, Integer>();
	public static HashMap<Player, Boolean> shotgunCoolingdown = new HashMap<Player, Boolean>();
	public static HashMap<Player, Integer> shotgunCountdown = new HashMap<Player, Integer>();
	public static HashMap<Player, Integer> shotgunCountdownTime = new HashMap<Player, Integer>();
	public static HashMap<Player, Boolean> sniperCoolingdown = new HashMap<Player, Boolean>();
	public static HashMap<Player, Integer> sniperCountdown = new HashMap<Player, Integer>();
	public static HashMap<Player, Integer> sniperCountdownTime = new HashMap<Player, Integer>();
	public static void cooldownPlayer(Player p, Weapon weapon) {
		if(weapon == Weapon.LASERGUN) {
//			playerCoolingdown.put(p, true);
			lasergunCoolingdown.put(p, true);
			p.setCooldown(Material.DIAMOND_HOE, Modifiers.lasergunCooldown);
			Bukkit.getScheduler().scheduleSyncDelayedTask(Lasertag.minigames, new Runnable() {
				@Override
				public void run() {
					lasergunCoolingdown.put(p, false);
//					playerCoolingdown.put(p, false);
				}
			}, Modifiers.lasergunCooldown);
		} else if(weapon == Weapon.SNIPER) {
//			playerCoolingdown.put(p, true);
			sniperCoolingdown.put(p, true);
			p.setCooldown(Material.DIAMOND_PICKAXE, Modifiers.sniperCooldown);
			Bukkit.getScheduler().scheduleSyncDelayedTask(Lasertag.minigames, new Runnable() {
				@Override
				public void run() {
					sniperCoolingdown.put(p, false);
//					playerCoolingdown.put(p, false);
					p.getInventory().getItem(2).setAmount(Modifiers.sniperAmmoBeforeCooldown);
				}
			}, Modifiers.sniperCooldown);
		} else if(weapon == Weapon.SHOTGUN) {
//			playerCoolingdown.put(p, true);
			shotgunCoolingdown.put(p, true);
			p.setCooldown(Material.DIAMOND_SHOVEL, Modifiers.shotgunCooldown);
			Bukkit.getScheduler().scheduleSyncDelayedTask(Lasertag.minigames, new Runnable() {
				@Override
				public void run() {
					shotgunCoolingdown.put(p, false);
//					playerCoolingdown.put(p, false);
				}
			}, Modifiers.shotgunCooldown);
		}
	}
	
//	public static HashMap<Player, Integer> playerAmmo = new HashMap<Player, Integer>();
//	public static HashMap<Player, Boolean> playerReloading = new HashMap<Player, Boolean>();
//	public static HashMap<Player, Integer> playerMinigunAmmo = new HashMap<Player, Integer>();
//	public static HashMap<Player, Boolean> playerMinigunCooldown = new HashMap<Player, Boolean>();
//	
	
//	public static void decreaseAmmo(Player p, int amount) {
//		if(playerAmmo.get(p) != null) {
//			int playerA = playerAmmo.get(p);
//			p.setLevel(playerA);
//			playerAmmo.put(p, playerA-amount);
//			if(playerA-amount == 0) reloadMagazine(p);
//		} else {
//			playerAmmo.put(p, Modifiers.bulletsInMagazine);
//			p.setLevel(Modifiers.bulletsInMagazine);
//		}
//	}
//	public static void reloadMagazine(Player p) {
//		p.setCooldown(lasergunItem.getType(), 20*Modifiers.magazinReloadTime);
//		playerReloading.put(p, true);
//		Bukkit.getScheduler().scheduleSyncDelayedTask(Lasertag.minigames, new Runnable() {
//			@Override
//			public void run() {
//				playerReloading.put(p, true);
//			}
//		}, 20*Modifiers.magazinReloadTime);
//	}
	
//	public static void decreaseMinigunAmmo(Player p) {
//		if(playerMinigunAmmo.get(p) == null) playerMinigunAmmo.put(p, Modifiers.minigunAmmo);
//		int ammo = playerMinigunAmmo.get(p);
//		ammo--;
//		p.setLevel(ammo);
//		if(ammo < 1) {
//			removeMinigun(p);
//		}
//	}
//	public static void removeMinigun(Player p) {
//		MyUtils.removeItemFromPlayer(p, minigunItem);
//		playerMinigunAmmo.put(p, Modifiers.minigunAmmo);
//		playerMinigunCooldown.put(p, true);
//		Bukkit.getScheduler().scheduleSyncDelayedTask(Lasertag.minigames, new Runnable() {
//			@Override
//			public void run() {
//				playerMinigunCooldown.put(p, false);
//			}
//		}, 20*60);
//	}
	
	public static Inventory getPlayersWeaponsInv(Player p) {
		Inventory weaponsInv = Bukkit.createInventory(null, 9, "§aChoose your secondary weapon!");
		ItemStack glass = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
		
		ItemStack newShotgun = Weapons.shotgunItem;
		ItemMeta newShotgunMeta = newShotgun.getItemMeta();
		ItemStack newSniper = Weapons.sniperItem;
		ItemMeta newSnipernMeta = newSniper.getItemMeta();
		if (Game.teams()) {
			newShotgunMeta.setDisplayName(Game.getTeamColor(Game.getPlayerTeam(p)).getChatColor()
					+ "§lShotgun #" + (Game.getTeamColor(Game.getPlayerTeam(p)).getOrdinal()+1));
			newSnipernMeta.setDisplayName(Game.getTeamColor(Game.getPlayerTeam(p)).getChatColor()
					+ "§lSniper #" + (Game.getTeamColor(Game.getPlayerTeam(p)).getOrdinal()+1));
		} else {
			int ordinal = Game.getPlayerColor(p).getOrdinal();
			newShotgunMeta.setDisplayName(Game.getPlayerColor(p).getChatColor() + "§lShotgun #" + (ordinal + 1));
			newSnipernMeta.setDisplayName(Game.getPlayerColor(p).getChatColor() + "§lSniper #" + (ordinal + 1));
		}
		newShotgun.setItemMeta(newShotgunMeta);
		newSniper.setItemMeta(newSnipernMeta);
		
		weaponsInv.setContents(new ItemStack[] {glass, newShotgun, glass, glass, glass, glass, glass, newSniper, glass});
		return weaponsInv;
	}
	
	public static Weapon getFireWeapon(ItemStack item) {
		Material type = item.getType();
		String name = item.getItemMeta().getDisplayName();
		if(type.equals(lasergunItem.getType()) && name.toUpperCase().contains("LASERGUN")) return Weapon.LASERGUN;
		else if(type.equals(shotgunItem.getType()) && name.toUpperCase().contains("SHOTGUN")) return Weapon.SHOTGUN;
		else if(type.equals(sniperItem.getType()) && name.toUpperCase().contains("SNIPER")) return Weapon.SNIPER;
//		else if(type.equals(minigunItem.getType()) && name.toUpperCase().contains("MINIGUN")) return Weapon.MINIGUN;
		else return null;
	}
	

	public static List<ItemStack> testWeapons = new ArrayList<ItemStack>();
	public static void getTestSet(){
		testWeapons.add(lasergunItem);
		testWeapons.add(daggerItem);
		testWeapons.add(shotgunItem);
		testWeapons.add(sniperItem);
		
		for(int i = 0; i < testWeapons.size(); i++) {
			ItemStack item = testWeapons.get(i);
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setDisplayName(itemMeta.getDisplayName()+" TEST");
			item.setItemMeta(itemMeta);
			testWeapons.set(i, item);
		}
	}
}

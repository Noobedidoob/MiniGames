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
import me.noobedidoob.minigames.lasertag.session.SessionModifiers.Mod;
import me.noobedidoob.minigames.lasertag.session.Session;

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
		if(session == null) return;
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
	
	public static Inventory getPlayersWeaponsInv(Player p) {
		Session session = Session.getPlayerSession(p);
		Inventory weaponsInv = Bukkit.createInventory(null, 9, "§aChoose your secondary weapon!");
		if(session == null) return weaponsInv;
		ItemStack glass = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
		
		ItemStack newShotgun = Weapons.shotgunItem;
		ItemMeta newShotgunMeta = newShotgun.getItemMeta();
		ItemStack newSniper = Weapons.sniperItem;
		ItemMeta newSnipernMeta = newSniper.getItemMeta();
		if (session.isTeams()) {
			newShotgunMeta.setDisplayName(session.getTeamColor(session.getPlayerTeam(p)).getChatColor()
					+ "§lShotgun #" + (session.getTeamColor(session.getPlayerTeam(p)).ordinal()+1));
			newSnipernMeta.setDisplayName(session.getTeamColor(session.getPlayerTeam(p)).getChatColor()
					+ "§lSniper #" + (session.getTeamColor(session.getPlayerTeam(p)).ordinal()+1));
		} else {
			int ordinal = session.getPlayerColor(p).ordinal();
			newShotgunMeta.setDisplayName(session.getPlayerColor(p).getChatColor() + "§lShotgun #" + (ordinal + 1));
			newSnipernMeta.setDisplayName(session.getPlayerColor(p).getChatColor() + "§lSniper #" + (ordinal + 1));
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

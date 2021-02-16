package me.noobedidoob.minigames.lasertag.methods;

import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.noobedidoob.minigames.lasertag.Lasertag.LasertagColor;
import me.noobedidoob.minigames.lasertag.session.SessionModifiers.Mod;
import me.noobedidoob.minigames.lasertag.session.Session;

public class Weapons {

	
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
			if(this == SNIPER) newItem.setAmount(Mod.SNIPER_AMMO_BEFORE_COOLDOWN.getOgInt());
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
		
		public boolean hasCooldown(Player p) {
			return p.hasCooldown(material);
		}
		public void setCooldown(Player p) {
			Session s = Session.getPlayerSession(p);
			int cooldown = Mod.LASERGUN_COOLDOWN_TICKS.getOgInt();
			if(s != null){
				switch (this) {
				case LASERGUN:
					cooldown = (s.withMultiweapons())? s.getIntMod(Mod.LASERGUN_MULTIWEAPONS_DAMAGE) : s.getIntMod(Mod.LASERGUN_NORMAL_DAMAGE);
					break;
				case SNIPER:
					cooldown = s.getIntMod(Mod.SNIPER_COOLDOWN_TICKS);
					Utils.runLater(() -> {
						p.getInventory().getItem((Lasertag.isPlayerTesting(p))?3:2).setAmount(s.getIntMod(Mod.SNIPER_AMMO_BEFORE_COOLDOWN));
						p.getInventory().getItem((Lasertag.isPlayerTesting(p))?4:3).setType(Material.AIR);
						p.getInventory().getItem((Lasertag.isPlayerTesting(p))?5:4).setType(Material.AIR);
					}, s.getIntMod(Mod.SNIPER_COOLDOWN_TICKS));
					break;
				case SHOTGUN:
					cooldown = s.getIntMod(Mod.SHOTGUN_COOLDOWN_TICKS);
					break;
				default:
					break;
				}
			} else {
				switch (this) {
				case SHOTGUN:
					cooldown = Mod.SHOTGUN_COOLDOWN_TICKS.getOgInt();
					break;
				case SNIPER:
					cooldown = Mod.SNIPER_COOLDOWN_TICKS.getOgInt();
					Utils.runLater(() -> {
						p.getInventory().getItem(3).setAmount(Mod.SNIPER_AMMO_BEFORE_COOLDOWN.getOgInt());
						p.getInventory().getItem(4).setAmount(0);
						p.getInventory().getItem(5).setAmount(0);
					}, Mod.SNIPER_COOLDOWN_TICKS.getOgInt());
					break;
				default:
					break;
				}
			}
			p.setCooldown(material, cooldown);
		}
	}
	
}

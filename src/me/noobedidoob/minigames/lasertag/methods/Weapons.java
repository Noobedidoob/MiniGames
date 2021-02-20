package me.noobedidoob.minigames.lasertag.methods;

import me.noobedidoob.minigames.lasertag.Lasertag.LasertagColor;
import me.noobedidoob.minigames.lasertag.session.Session;
import me.noobedidoob.minigames.lasertag.session.SessionModifiers.Mod;
import me.noobedidoob.minigames.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Weapons {

	
	public enum Weapon {
		LASERGUN(Material.DIAMOND_HOE, LasertagColor.Blue,"§bLasergun"),
		DAGGER(Material.DIAMOND_SWORD, LasertagColor.Green, "§aDagger"),
		SHOTGUN(Material.DIAMOND_SHOVEL, LasertagColor.Yellow, "§eShotgun"),
		SNIPER(Material.DIAMOND_PICKAXE, LasertagColor.Purple, "§dSniper");
		
		private final Material material;
		private final ItemStack item;

		Weapon(Material material, LasertagColor defaultColor, String displayName){
			this.material = material;
			ItemStack item = new ItemStack(material);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(displayName);
			meta.setUnbreakable(true);
			meta.setCustomModelData(defaultColor.ordinal()+1);
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
		public ItemStack getItem(String displayName){
			ItemStack item = this.item.clone();
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(displayName);
			item.setItemMeta(meta);
			return item;
		}
		public ItemStack getTestItem() {
			ItemStack newItem = item.clone();
			if(this == SNIPER) newItem.setAmount(Mod.SNIPER_AMMO_BEFORE_COOLDOWN.getOgInt());
			ItemMeta meta = newItem.getItemMeta();
			meta.setDisplayName(meta.getDisplayName()+" Test");
			newItem.setItemMeta(meta);
			return newItem;
		}
		public ItemStack getColoredItem(LasertagColor color) {
			ItemStack item = this.item.clone();
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(color.getChatColor()+meta.getDisplayName().substring(2));
			meta.setCustomModelData(color.ordinal()+1);
			item.setItemMeta(meta);
			return item;
		}
		public ItemStack getColoredItem(LasertagColor color, String displayName){
			ItemStack item = this.item.clone();
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(displayName);
			meta.setCustomModelData(color.ordinal()+1);
			item.setItemMeta(meta);
			return item;
		}
		
		public static Weapon getWeaponFromItem(ItemStack item) {
			Material type = item.getType();
			String name = item.getItemMeta().getDisplayName();
			for(Weapon w : values()){
				if(w.getType() == type && name.toUpperCase().contains(w.name().substring(2))) return w;
			}
			return null;
		}

		public String getName(){
			return this.name().charAt(0)+this.name().toLowerCase().substring(1);
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
					cooldown = (s.withMultiweapons())? s.getIntMod(Mod.LASERGUN_MULTIWEAPONS_COOLDOWN_TICKS) : s.getIntMod(Mod.LASERGUN_COOLDOWN_TICKS);
					break;
				case SNIPER:
					cooldown = s.getIntMod(Mod.SNIPER_COOLDOWN_TICKS);
					Utils.runLater(() -> p.getInventory().getItem(2).setAmount(s.getIntMod(Mod.SNIPER_AMMO_BEFORE_COOLDOWN)), s.getIntMod(Mod.SNIPER_COOLDOWN_TICKS));
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

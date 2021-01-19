package me.noobedidoob.minigames.utils;

import org.bukkit.ChatColor;
import org.bukkit.Color;

public class LasertagColor {
	
	private LtColorNames colorName;
	private ChatColor chatColor;
	private Color color;
	private int ordinal = 0;
	
	public LasertagColor(LtColorNames colorName) {
		super();
		this.colorName = colorName;
		ordinal = colorName.ordinal();
		if(colorName == LtColorNames.Red) {
			color = Color.fromBGR(0, 0, 255);
			chatColor = ChatColor.RED;
		}
		else if(colorName == LtColorNames.Blue) {
			color = Color.fromBGR(255, 170, 0);
			chatColor = ChatColor.BLUE;
		}
		else if(colorName == LtColorNames.Green) {
			color = Color.fromBGR(0, 255, 0);
			chatColor = ChatColor.GREEN;
		}
		else if(colorName == LtColorNames.Yellow) {
			color = Color.fromBGR(0, 255, 255);
			chatColor = ChatColor.YELLOW;
		}
		else if(colorName == LtColorNames.Orange) {
			color = Color.fromBGR(0, 160, 255);
			chatColor = ChatColor.GOLD;
		}
		else if(colorName == LtColorNames.Purple) {
			color = Color.fromBGR(255, 0, 200);
			chatColor = ChatColor.LIGHT_PURPLE;
		}
		else if(colorName == LtColorNames.Gray) {
			color = Color.fromBGR(150, 150, 150);
			chatColor = ChatColor.GRAY;
		}
		else if(colorName == LtColorNames.White) {
			color = Color.WHITE;
			chatColor = ChatColor.WHITE;
		}
	}
	
	public String getName() {
		return colorName.name();
	}
	public Color getColor() {
		return color;
	}
	public ChatColor getChatColor() {
		return chatColor;
	}
	public int getOrdinal() {
		return ordinal;
	}
	
	
	public enum LtColorNames {
		Red,
		Blue,
		Green,
		Yellow,
		Purple,
		Gray,
		Orange,
		White;
		
		public ChatColor getChatColor() {
			return new LasertagColor(this).getChatColor();
		}
		public Color getColor() {
			return new LasertagColor(this).getColor();
		}
	}
}

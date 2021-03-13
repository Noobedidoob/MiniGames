package me.noobedidoob.minigames.lasertag;

import me.noobedidoob.minigames.Commands;
import me.noobedidoob.minigames.Minigames;
import me.noobedidoob.minigames.lasertag.session.Session;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LaserCommands implements CommandExecutor, TabCompleter {

	@SuppressWarnings({"FieldCanBeLocal", "unused"})
	private final Minigames minigames;

	public LaserCommands(Minigames minigames) {
		this.minigames = minigames;
	}

	String commands = "\n§7————————— §bLasertag Commands§7 ——————————\n"
					+ "§6 gettexturepack §7— Activate lasertag texturepack\n  "
					+ "§6 test §7— Teleport to the test area\n  "
					+ "\n§a Use §6/lt §7<§6command§7> §ato perform a command!\n"
					+ "§7——————————————————————————————\n  ";
	

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if(cmd.getName().equalsIgnoreCase("lasertag")) {

			if(args.length == 0) {
				sender.sendMessage("§e"+commands);
				return true;
			} else if( args.length == 1){
				if(args[0].equalsIgnoreCase("settexturepack") | args[0].equalsIgnoreCase("gettexturepack")){
					sender.sendMessage("\n§7—————————§bEnable Texturepack—————————————");
					TextComponent msg1 = new TextComponent(" To use the custom texturepack you can either activate \"server resoucepacks\" in the server-settings before joining or ");
					msg1.setColor(net.md_5.bungee.api.ChatColor.GOLD);

					TextComponent linkMsg = new TextComponent("download");
					linkMsg.setColor(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE);
					linkMsg.setUnderlined(true);
					linkMsg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, Minigames.TEXTUREPACK_URL));
					//noinspection deprecation
					linkMsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to download the texturepack").create()));
					msg1.addExtra(linkMsg);

					TextComponent msg2 = new TextComponent(" and install it manually.");
					msg2.setColor(net.md_5.bungee.api.ChatColor.GOLD);
					msg1.addExtra(msg2);
					sender.spigot().sendMessage(msg1);
					sender.sendMessage("§7———————————————————————————\n ");
					return true;
				} else if(args[0].equalsIgnoreCase("test")){
					if(sender instanceof Player){
						Player p = (Player) sender;
						if(!Session.isPlayerInSession(p) | (Session.getPlayerSession(p) != null && !Session.getPlayerSession(p).tagging())){
							p.teleport(minigames.testLoc);
						}
						return true;
					}
				}
			}
		}
		sender.sendMessage("§cSyntax ERROR! Please use §e/lt §cto see all commands and their arguments");
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		List<String> list = new ArrayList<>();
		if(cmd.getName().equalsIgnoreCase("lasertag")) {
			if(args.length == 1) {
				list.add("gettexturepack");
				if (sender instanceof Player) {
					if(!Session.isPlayerInSession((Player) sender)){
						list.add("test");
					} else if(Session.getPlayerSession((Player) sender) != null && !Session.getPlayerSession((Player) sender).tagging()){
						list.add("test");
					}
				}
			}
		} 
		return Commands.filterTabAutocompleteList(args,list);
	}
	

}

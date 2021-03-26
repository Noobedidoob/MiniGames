package me.noobedidoob.minigames;

import me.noobedidoob.minigames.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class Test implements CommandExecutor, Listener {


    public Test(Minigames minigames) {
        Bukkit.getPluginManager().registerEvents(this, minigames);
    }


    public void test(Player p) {
        p.sendMessage( "testing");


    }
    public void test2(Player p){
        int amount = p.getInventory().getItemInMainHand().getAmount();
        if(amount < 5) p.getInventory().getItemInMainHand().setAmount(amount+1);
        else p.getInventory().getItemInMainHand().setAmount(1);
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        try {
            if(e.getAction() == Action.RIGHT_CLICK_AIR | e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
                if(e.getItem().getItemMeta().getDisplayName().equalsIgnoreCase("Test")) test(e.getPlayer());
            } else if(e.getAction() == Action.LEFT_CLICK_AIR | e.getAction().equals(Action.LEFT_CLICK_BLOCK)){
                if(e.getItem().getItemMeta().getDisplayName().equalsIgnoreCase("Test")) test2(e.getPlayer());
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
        if (sender instanceof Player && sender.isOp()) {
            Player p = (Player) sender;
            if (args.length == 0) {
                p.getInventory().addItem(Utils.getItemStack(Material.STICK,"Test"));
            }
        }
        return true;
    }
}

package me.noobedidoob.minigames;

import me.noobedidoob.minigames.utils.Utils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Test implements CommandExecutor, Listener {


    public Test(Minigames minigames) {
        Bukkit.getPluginManager().registerEvents(this, minigames);
    }

    public static List<Vector> getSphereOffsets(double radius, int density){
        return getSphereOffsets(radius,density,density);
    }
    public static List<Vector> getSphereOffsets(double radius, int circleDensity, int dotsDensity){
        List<Vector> offsets = new ArrayList<>();
        for(double phi=0; phi<=Math.PI; phi+=Math.PI/circleDensity) {
            double y = radius*Math.cos(phi);
            for(double theta=0; theta<=2*Math.PI; theta+=Math.PI/dotsDensity) {
                double x = radius*Math.cos(theta)*Math.sin(phi);
                double z = radius*Math.sin(theta)*Math.sin(phi);
                offsets.add(new Vector(x,y,z));
            }
        }
        return offsets;
    }
    public void test(Player p) {

    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        try {
            if(e.getAction() == Action.RIGHT_CLICK_AIR | e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
                if(e.getItem().getItemMeta().getDisplayName().equalsIgnoreCase("Test")) test(e.getPlayer());
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

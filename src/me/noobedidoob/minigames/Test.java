package me.noobedidoob.minigames;

import me.noobedidoob.minigames.utils.BaseSphere;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.HashMap;

public class Test implements CommandExecutor, Listener {

    HashMap<Player, BaseSphere> playerSphere = new HashMap<>();


    public Test(Minigames minigames) {
        Bukkit.getPluginManager().registerEvents(this, minigames);
    }

    ArmorStand stand;
    Player p;
    public void test(Player p) {
//        this.p = p;
//        stand = (ArmorStand) p.getWorld().spawnEntity(p.getLocation(), EntityType.ARMOR_STAND);
//        stand.setVisible(false);
//        stand.setGravity(false);
//        stand.getEquipment().setHelmet(new ItemStack(Material.RED_BANNER));
        p.getEquipment().setHelmet(new ItemStack(Material.RED_BANNER));
//        p.addPassenger(stand);

//        new BukkitRunnable(){
//            @Override
//            public void run() {
//                stand.teleport(p);
//            }
//        }.runTaskTimer(Minigames.INSTANCE,0,1);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
//        if(stand != null && p != null && e.getPlayer() == p) stand.teleport(p);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
        if (sender instanceof Player && sender.isOp()) {
            Player p = (Player) sender;
            if (args.length == 0) {
                test(p);
                p.sendMessage("Testing...");
            }
        }
        return true;
    }

    public static void setField(Object instance, String fieldName, Object value) {
        try {
            Field f = Class.forName("net.minecraft.server.v1_16_R3.Entity").getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(instance, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

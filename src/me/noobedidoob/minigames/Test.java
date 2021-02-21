package me.noobedidoob.minigames;

import me.noobedidoob.minigames.utils.Utils;
import net.minecraft.server.v1_16_R3.EntitySlime;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntityLiving;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Test implements CommandExecutor, Listener {


    public Test(Minigames minigames) {
        Bukkit.getPluginManager().registerEvents(this, minigames);
    }

    int id = 0;
    public void test(Player p) {
        p.sendMessage( "testing");
        Shulker s = (Shulker) p.getWorld().spawnEntity(p.getLocation().add(0,2,0), EntityType.SHULKER);
        s.setInvisible(true);
        s.setGlowing(true);
        s.setGravity(false);
        s.setPersistent(true);
        s.setAI(false);
        s.setInvulnerable(true);
        s.setColor(DyeColor.BLUE);
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent e){

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

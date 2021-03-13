package me.noobedidoob.minigames;

import me.noobedidoob.minigames.utils.Grenade;
import me.noobedidoob.minigames.utils.InstantFirework;
import me.noobedidoob.minigames.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.data.type.Snow;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class Test implements CommandExecutor, Listener {


    public Test(Minigames minigames) {
        Bukkit.getPluginManager().registerEvents(this, minigames);
    }


    HashMap<Player, Snowball> map = new HashMap<>();

    public void test(Player p) {
        p.sendMessage( "testing");

//        Snowball sb = (Snowball) p.getWorld().spawnEntity(p.getEyeLocation(), EntityType.SNOWBALL);
//        sb.setGlowing(true);
//        sb.setVelocity(p.getEyeLocation().getDirection().multiply((p.isSneaking()?0.5:1)));
//        map.put(p,sb);
//
//        Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.INSTANCE, ()->{
//            Snowball sbc = map.get(p);
//            new InstantFirework(FireworkEffect.builder().with(FireworkEffect.Type.BALL).flicker(false).trail(false).withColor(Color.RED).build(), sbc.getLocation());
//            sbc.remove();
//        }, 20*p.getInventory().getItemInMainHand().getAmount());

        new Grenade(p,20*p.getInventory().getItemInMainHand().getAmount(),1,Minigames.INSTANCE);

    }
    public void test2(Player p){
        int amount = p.getInventory().getItemInMainHand().getAmount();
        if(amount < 5) p.getInventory().getItemInMainHand().setAmount(amount+1);
        else p.getInventory().getItemInMainHand().setAmount(1);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e){
//        Entity entity = e.getEntity();
//        assert entity instanceof Snowball;
//        Location loc = entity.getLocation();
//
//        Snowball sb = (Snowball) loc.getWorld().spawnEntity(loc, EntityType.SNOWBALL);
//        sb.setGravity(false);
//        map.put(Bukkit.getPlayer("Noobedidoob"),sb);
//        if(e.getHitEntity() != null && e.getHitEntity() instanceof Player){
//            e.getHitEntity().addPassenger(sb);
//        }
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

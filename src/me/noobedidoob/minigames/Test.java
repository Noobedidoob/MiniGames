package me.noobedidoob.minigames;

import me.noobedidoob.minigames.utils.InstantFirework;
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

public class Test implements CommandExecutor, Listener {


    public Test(Minigames minigames) {
        Bukkit.getPluginManager().registerEvents(this, minigames);
    }

    public void test(Player p) {
        p.sendMessage( "testing");

        new InstantFirework(FireworkEffect.builder().with(FireworkEffect.Type.BALL).flicker(false).trail(false).withColor(Color.RED).build(), p.getEyeLocation().add(p.getEyeLocation().getDirection().multiply(1.5)));
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

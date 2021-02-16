package me.noobedidoob.minigames;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class Test implements CommandExecutor, Listener {


    public Test(Minigames minigames) {
        Bukkit.getPluginManager().registerEvents(this, minigames);
    }

    public void test(Player p) {
        p.sendMessage("testing...");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
        if (sender instanceof Player && sender.isOp()) {
            Player p = (Player) sender;
            if (args.length == 0) {
                test(p);
            }
        }
        return true;
    }
}

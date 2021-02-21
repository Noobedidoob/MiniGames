package me.noobedidoob.minigames.lasertag.commands;

import me.noobedidoob.minigames.Commands;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import me.noobedidoob.minigames.lasertag.session.SessionModifiers.Mod;
import me.noobedidoob.minigames.lasertag.session.Session;

import java.util.ArrayList;
import java.util.List;

public class ModifierCommands implements CommandExecutor, TabCompleter {

    String commands = "\n§7————————— §bModifier Commands§7 ——————————\n"
            + "§6 getModifiers §7— Get current values\n"
            + "§6 getModifierTypes §7— Get mod value types\n"
            + "§6 setmodifier §7<§6mod§7> <§6value§7> — Set value\n  "
            + "\n§a Use §6/mods §7<§6command§7> §ato perform a command!\n"
            + "§7——————————————————————————————\n  ";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
        if(args.length == 0){
            sender.sendMessage(commands);
            return true;
        } else {
            if(args[0].equalsIgnoreCase("getModifiers") | args[0].equalsIgnoreCase("modifiers")) {
                sendModifiers(sender);
            } else if(args[0].equalsIgnoreCase("getModifierTypes") | args[0].equalsIgnoreCase("getModTypes") | args[0].equalsIgnoreCase("getTypes") | args[0].equalsIgnoreCase("types")) {
                if(sender instanceof Player){
                    sender.sendMessage("\n§7———————§b§lModifier Types§r§7———————");
                    for(Mod m : Mod.values()) {
                        sender.sendMessage("§7"+m.name()+" <§a"+m.getValueTypeName()+"§7>");
                    }
                    sender.sendMessage("§7———————————————————\n");
                } else {
                    sender.sendMessage("\n§7------------§b§lModifier Types§r§7------------");
                    for(Mod m : Mod.values()) {
                        sender.sendMessage("§7"+m.name()+" <§a"+m.getValueTypeName()+"§7>");
                    }
                    sender.sendMessage("§7--------------------------------------\n");
                }
                return true;
            }

            if((sender instanceof Player) && Session.getPlayerSession((Player) sender) != null) {
                Player p = (Player) sender;
                Session session = Session.getPlayerSession(p);

                if(args[0].equalsIgnoreCase("setmodifier") && args.length == 3) {
                    Mod m = Mod.getMod(StringUtils.replace(args[1].toUpperCase(), "-", "_"));
                    String valString = args[2];
                    //noinspection UnusedAssignment
                    Object value = valString;

                    try {
                        value = Integer.parseInt(valString);
                    } catch (NumberFormatException e1) {
                        try {
                            value = Double.parseDouble(valString+"d");
                        } catch (NumberFormatException e2) {
                            if(valString.equalsIgnoreCase("true") | valString.equalsIgnoreCase("false")) value = Boolean.parseBoolean(valString);
                            else {
                                Session.sendMessage(p, "§cThe given value is invalid! Please use a §evalid number §cor §etrue§c/§efalse!");
                                return true;
                            }
                        }
                    }
                    if(m != null) {
                        if(value.getClass() == m.getOg().getClass()) {
                            session.setMod(m, value);
    //						Session.sendMessage(p, "§aSuccessfully set the value of the modifier §b"+m.name().toLowerCase()+" §a to §e"+value.toString());
                            return true;
                        } else if(value.getClass() == Integer.class && m.getOg().getClass() == Double.class) {
                            value = Double.parseDouble(valString+"d");
                            session.setMod(m, value);
    //						Session.sendMessage(p, "§aSuccessfully set the value of the modifier §b"+m.name().toLowerCase()+" §a to §e"+value.toString());
                            return true;
                        } else {
                            Session.sendMessage(p, "§cThe given type of value doesnt match with the modifiers value type! Please use §e"+m.getValueTypeName());
                            return true;
                        }
                    } else {
                        Session.sendMessage(p, "§cThe modifier §b"+args[1]+" §cdoesn't exist! Use §e/lt setmodifier §cto get all available modifiers");
                        return true;
                    }
                }
            }
        }


        sender.sendMessage("§cSyntax ERROR! Please use §e/mods §cto see all commands and their arguments");
        return true;
    }

    public void sendModifiers(CommandSender sender){
        if(sender instanceof Player){
            if(Session.getPlayerSession((Player) sender) != null){
                sender.sendMessage("\n§7—————————§b§lModifiers§r§7—————————");
                Session s = Session.getPlayerSession((Player) sender);
                s.modifiers.modValues.forEach((m, v) -> sender.sendMessage("§7> " + m.name() + ": §a" + s.getModValue(m).toString()));
                sender.sendMessage("§7—————————————————————\n");
            } else {
                sender.sendMessage("\n§7———————§b§lStanderd Modifiers§r§7———————");
                for (Mod m : Mod.values()) {
                    sender.sendMessage("§7> " + m.name()+ ": §a" + m.getOg().toString());
                }
                sender.sendMessage("§7——————————————————\n");
            }
        } else {
            sender.sendMessage("\n§7-----------§b§lStanderd Modifiers§r§7-----------");
            for (Mod m : Mod.values()) {
                sender.sendMessage("§7> " + m.name()+ ": §a" + m.getOg().toString());
            }
            sender.sendMessage("§7---------------------------------------\n");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
        List<String> list = new ArrayList<>();

        if(args.length == 1) {
           list.add("getModifiers");
           list.add("getModifierTypes");
            if(sender instanceof Player && Session.getPlayerSession((Player) sender) != null && Session.getPlayerSession((Player) sender).waiting() && Session.getPlayerSession((Player) sender).isAdmin((Player) sender)) {
               list.add("setModifier");
            }
        } else if(sender instanceof Player && args.length >= 2 && args[0].equalsIgnoreCase("setmodifier") && Session.getPlayerSession((Player) sender).isAdmin((Player) sender)) {
            if(args.length == 2) {
                for(Mod m : Mod.values()) {
                    if(m.name().toLowerCase().contains(args[1].toLowerCase())) list.add(m.name().toLowerCase());
                }
            } else if(args.length == 3 && Mod.getMod(StringUtils.replace(args[1].toUpperCase(), "-", "_")) != null) {
                if(Mod.getMod(StringUtils.replace(args[1].toUpperCase(), "-", "_")).getValueTypeName().equals("true/false")) {
                   list.add("true");
                   list.add("false");
                }
            }
        }

        if(args.length == 3) {
            Mod m = Mod.getMod(args[2]);
            if (m != null) {
                if (m.getValueTypeName().equals("true/false")) {
                    if (sender.isOp()) {
                       list.add("true");
                       list.add("false");
                    }
                }
            }
        }
        return Commands.filterTabAutocompleteList(args,list);
    }
}
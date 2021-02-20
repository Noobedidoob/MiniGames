package me.noobedidoob.minigames.lasertag.commands;

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

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
        if(args.length == 0){
            sendModifiers(sender);
            return true;
        } else {
            if(args[0].equalsIgnoreCase("getModifiers") | args[0].equalsIgnoreCase("modifiers")) {
                sendModifiers(sender);
            } else if(args[0].equalsIgnoreCase("getModifierTypes") | args[0].equalsIgnoreCase("getModTypes") | args[0].equalsIgnoreCase("getTypes") | args[0].equalsIgnoreCase("types")) {
                if(sender instanceof Player){
                    sender.sendMessage("\nｧ7覧覧覧立bｧlModifier Typesｧrｧ7覧覧覧�");
                    for(Mod m : Mod.values()) {
                        sender.sendMessage("ｧ7"+m.name()+" <ｧa"+m.getValueTypeName()+"ｧ7>");
                    }
                    sender.sendMessage("ｧ7覧覧覧覧覧覧覧覧覧予n");
                } else {
                    sender.sendMessage("\nｧ7------------ｧbｧlModifier Typesｧrｧ7------------");
                    for(Mod m : Mod.values()) {
                        sender.sendMessage("ｧ7"+m.name()+" <ｧa"+m.getValueTypeName()+"ｧ7>");
                    }
                    sender.sendMessage("ｧ7--------------------------------------\n");
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
                                Session.sendMessage(p, "ｧcThe given value is invalid! Please use a ｧevalid number ｧcor ｧetrueｧc/ｧefalse!");
                                return true;
                            }
                        }
                    }
                    if(m != null) {
                        if(value.getClass() == m.getOg().getClass()) {
                            session.setMod(m, value);
    //						Session.sendMessage(p, "ｧaSuccessfully set the value of the modifier ｧb"+m.name().toLowerCase()+" ｧa to ｧe"+value.toString());
                            return true;
                        } else if(value.getClass() == Integer.class && m.getOg().getClass() == Double.class) {
                            value = Double.parseDouble(valString+"d");
                            session.setMod(m, value);
    //						Session.sendMessage(p, "ｧaSuccessfully set the value of the modifier ｧb"+m.name().toLowerCase()+" ｧa to ｧe"+value.toString());
                            return true;
                        } else {
                            Session.sendMessage(p, "ｧcThe given type of value doesnt match with the modifiers value type! Please use ｧe"+m.getValueTypeName());
                            return true;
                        }
                    } else {
                        Session.sendMessage(p, "ｧcThe modifier ｧb"+args[1]+" ｧcdoesn't exist! Use ｧe/lt setmodifier ｧcto get all available modifiers");
                        return true;
                    }
                }
            }
        }


        sender.sendMessage("ｧcSyntax ERROR! Please use ｧe/mods ｧcto see all commands and their arguments");
        return true;
    }

    public void sendModifiers(CommandSender sender){
        if(sender instanceof Player){
            if(Session.getPlayerSession((Player) sender) != null){
                sender.sendMessage("\nｧ7覧覧覧覧立bｧlModifiersｧrｧ7覧覧覧覧�");
                Session s = Session.getPlayerSession((Player) sender);
                s.modifiers.modValues.forEach((m, v) -> sender.sendMessage("ｧ7> " + m.name() + ": ｧa" + s.getModValue(m).toString()));
                sender.sendMessage("ｧ7覧覧覧覧覧覧覧覧覧覧予n");
            } else {
                sender.sendMessage("\nｧ7覧覧覧立bｧlStanderd Modifiersｧrｧ7覧覧覧�");
                for (Mod m : Mod.values()) {
                    sender.sendMessage("ｧ7> " + m.name()+ ": ｧa" + m.getOg().toString());
                }
                sender.sendMessage("ｧ7覧覧覧覧覧覧覧覧覧\n");
            }
        } else {
            sender.sendMessage("\nｧ7-----------ｧbｧlStanderd Modifiersｧrｧ7-----------");
            for (Mod m : Mod.values()) {
                sender.sendMessage("ｧ7> " + m.name()+ ": ｧa" + m.getOg().toString());
            }
            sender.sendMessage("ｧ7---------------------------------------\n");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
        List<String> list = new ArrayList<>();

        if(args.length == 1) {
            list.add("getModifiers");
            list.add("getModifierTypes");
            if(sender instanceof Player && Session.getPlayerSession((Player) sender) != null && Session.getPlayerSession((Player) sender).waiting() && Session.getPlayerSession((Player) sender).isAdmin((Player) sender)) {
                list.add("withmultiweapons");
                list.add("setModifier");
            }
        } else if(sender instanceof Player && args.length >= 2 && args[0].equalsIgnoreCase("setmodifier") && Session.getPlayerSession((Player) sender).isAdmin((Player) sender)) {
            if(args.length == 2) {
                for(Mod m : Mod.values()) list.add(m.name().toLowerCase());
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
        return list;
    }
}
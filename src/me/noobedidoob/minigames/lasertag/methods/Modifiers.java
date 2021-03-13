package me.noobedidoob.minigames.lasertag.methods;

import me.noobedidoob.minigames.Minigames;
import me.noobedidoob.minigames.lasertag.session.Session;
import me.noobedidoob.minigames.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

public class Modifiers implements Listener {

    public Modifiers(Minigames minigames){
        Bukkit.getPluginManager().registerEvents(this,minigames);
    }




    private ArrayList<Inventory> modInvs = get();

    private static ArrayList<Inventory> get(){
        ArrayList<Inventory> invs= new ArrayList<>();
        int i = 0;
        Inventory inv = Bukkit.createInventory(null,9*6, "§0Select modifier §7(page 1)");
        inv.setItem(53, Utils.getItemStack(Material.GREEN_STAINED_GLASS_PANE, "§aNext page"));
        for (Mod m : Mod.values()) {
            if(i > 44) {
                i = 0;
                inv = Bukkit.createInventory(null,9*6, "§0Select modifier §7(page "+(((m.ordinal()-1)/44) +1)+")");
                inv.setItem(45, Utils.getItemStack(Material.RED_STAINED_GLASS_PANE, "§cPrevious page"));
                if(m.ordinal()+44 < Mod.values().length) inv.setItem(53, Utils.getItemStack(Material.GREEN_STAINED_GLASS_PANE, "§aNext page"));
            }
            StringBuilder name = new StringBuilder();
            for(String s : m.name().replace("_","-").toLowerCase().split("-")){
                if(name.toString().equals("")) name.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1));
                else name.append("-").append(Character.toUpperCase(s.charAt(0))).append(s.substring(1));
            }
            name.append(" §7(").append(m.getDescription()).append(")");
            inv.setItem(i,Utils.getItemStack(Material.FILLED_MAP,name.toString()));
        }
        invs.add(inv);
        return invs;
    }
    public void openModInv(Player p){
        if(Session.isPlayerInSession(p)){
            p.openInventory(modInvs.get(0));
        }
    }
    public void openModInv(Player p, int page){
        if(Session.isPlayerInSession(p)){
            p.openInventory(modInvs.get((page > 0)?page-1:0));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        Player p = (Player) e.getWhoClicked();
        if(!Session.isPlayerInSession(p)) return;
        Session session = Session.getPlayerSession(p);
        Inventory inv = e.getClickedInventory();
        int slot = e.getSlot();
        if(inv.getItem(slot) == null) return;

        if(e.getView().getTitle().contains("Select modifier")){

        }

    }

}

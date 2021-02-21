package me.noobedidoob.minigames.utils;

import me.noobedidoob.minigames.Minigames;
import me.noobedidoob.minigames.lasertag.Lasertag.LasertagColor;
import me.noobedidoob.minigames.lasertag.session.Session;
import me.noobedidoob.minigames.lasertag.session.SessionModifiers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class Flag implements Listener {

    private Session session;
    private ArmorStand armorStand;
    private final ItemStack banner;
    private final Location baseLocation;
    private final LasertagColor color;

    private Player playerAttachedTo;

    // TODO: 21.02.2021 BLINK
    public Flag(Location baseLocation, LasertagColor color){
        this.color = color;
        this.baseLocation = baseLocation;
        this.banner = new ItemStack(Material.valueOf(color.getChatColor().name()+"_BANNER"));

        Bukkit.getPluginManager().registerEvents(this, Minigames.INSTANCE);
    }

    public void attach(Player p){
        this.playerAttachedTo = p;
        p.getEquipment().setHelmet(banner);
        p.getInventory().setItem(8,Utils.getItemStack(banner.getType(),"§eDrop flag"));
        PLAYER_FLAG.put(p,this);
        p.setGlowing(true);

        armorStand.getEquipment().clear();
        armorStand.setGlowing(false);
        armorStand.teleport(baseLocation.clone().add(0, session.getMap().getArea().getHeight(),0));
    }

    public void drop(Location dropLocation){
        removeFromPlayer();

        armorStand.setGlowing(true);
        Location loc = dropLocation.clone();
        armorStand.getEquipment().setHelmet(banner);
        armorStand.teleport(loc.subtract(0,1,0));
        armorStand.setGravity(true);
        Utils.runLater(()-> {
            armorStand.setGravity(false);
            while(loc.getBlock().getType().isAir()){
                loc.subtract(0,1,0);
                if(session.getMap().getArea().isInside(loc)){
                    armorStand.teleport(loc);
                } else {
                    teleportToBase();
                }
            }
        },20);
    }

    public void teleportToBase(){
        removeFromPlayer();
        armorStand.teleport(baseLocation.clone().subtract(0,1,0));
        armorStand.getEquipment().setHelmet(banner);
        armorStand.setGlowing(true);
    }

    private void removeFromPlayer(){
        if (playerAttachedTo != null) {
            PLAYER_FLAG.put(playerAttachedTo,null);
            playerAttachedTo.setGlowing(false);
            playerAttachedTo.getEquipment().setHelmet(new ItemStack(Material.AIR));
            playerAttachedTo.getInventory().setItem(8,new ItemStack(Material.AIR));
            Utils.runLater(()->{
                playerAttachedTo.getInventory().setItem(8,new ItemStack(Material.AIR));
                this.playerAttachedTo = null;
            }, 5);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        if (session != null && session.isInSession(e.getPlayer())) {
            Player p = e.getPlayer();
            LasertagColor playerColor = session.getPlayerColor(p);
            if(playerAttachedTo == null){
                if (e.getTo().distance(armorStand.getLocation()) < 1.5) {
                    if(isAtBase()){
                        if(!playerColor.equals(color)) {
                            attach(e.getPlayer());
                        }
                    } else {
                        if(playerColor.equals(color)) {
                            teleportToBase();
                        } else {
                            attach(e.getPlayer());
                        }
                    }
                }
            } else {
                if(p == playerAttachedTo && session.getMap().getBaseCoord(playerColor).getLocation().distance(p.getLocation()) < session.getMap().getProtectionRaduis()){
                    if (session.getMap().getBaseFlag(playerColor).isAtBase()) {
                        teleportToBase();
                        session.addPoints(p,session.getIntMod(SessionModifiers.Mod.CAPTURE_THE_FLAG_POINTS), playerColor.getChatColor()+p.getName()+" §7§ocaptured the flag from "+color.getChatColor()+((session.isTeams()?"team "+color:session.getPlayerFromColor(color).getName())));
                    } else {
                        // TODO: 21.02.2021 Send notification, that its not possible
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        if (playerAttachedTo != null && e.getPlayer() == playerAttachedTo) {
            if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) | e.getAction().equals(Action.RIGHT_CLICK_AIR)){
                if(e.getItem() != null && e.getItem().getItemMeta().getDisplayName().toLowerCase().contains("drop flag")){
                    Vector direction = playerAttachedTo.getLocation().getDirection().multiply(-1);
                    Location loc = playerAttachedTo.getLocation().clone().add(direction).add(direction);
                    drop(loc);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e){
        if (playerAttachedTo != null && e.getPlayer() == playerAttachedTo) {
            if(e.getItemDrop().getItemStack().getItemMeta().getDisplayName().toLowerCase().contains("drop flag")){
                Vector direction = playerAttachedTo.getLocation().getDirection().multiply(-1);
                Location loc = playerAttachedTo.getLocation().clone().add(direction).add(direction);
                drop(loc);
            }
        }
    }


    public Session getSession() {
        return session;
    }

    public Location getBaseLocation() {
        return baseLocation;
    }

    public LasertagColor getColor() {
        return color;
    }

    public Player getPlayerAttachedTo() {
        return playerAttachedTo;
    }

    public boolean isAttached() {
        return playerAttachedTo != null;
    }

    public boolean isAtBase(){
        return playerAttachedTo == null && armorStand.getLocation().distance(baseLocation.clone().subtract(0,1,0)) < 0.1;
    }

    public boolean isEnabled(){
        return session != null;
    }
    public void enable(Session session){
        for(Player p : session.getPlayers()){
            p.setGlowing(false);
        }

        this.session = session;
        Location loc = baseLocation.clone().subtract(0,1,0);
        while(loc.getBlock().getType().isAir()){
            loc.subtract(0,1,0);
        }
        armorStand = (ArmorStand) baseLocation.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setGlowing(true);
        armorStand.getEquipment().setHelmet(banner);
    }
    public void disable(){
        this.session = null;
        armorStand.getEquipment().setHelmet(new ItemStack(Material.AIR));
        armorStand.remove();

        if(playerAttachedTo != null){
            PLAYER_FLAG.put(playerAttachedTo,null);
            playerAttachedTo.getEquipment().setHelmet(new ItemStack(Material.AIR));
            playerAttachedTo.setGlowing(false);
            this.playerAttachedTo = null;
        }
    }

    private static final HashMap<Player, Flag> PLAYER_FLAG = new HashMap<>();
    public static Flag getPlayerFlag(Player p){
        return PLAYER_FLAG.get(p);
    }
    public static boolean hasPlayerFlag(Player p){
        return (PLAYER_FLAG.get(p) != null);
    }



}

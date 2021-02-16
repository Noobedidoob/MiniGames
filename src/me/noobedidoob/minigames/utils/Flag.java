package me.noobedidoob.minigames.utils;

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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class Flag implements Listener {

    private Session session;
    private ArmorStand armorStand;
    private ItemStack banner;
    private Location baseLocation;
    private LasertagColor color;

    private Player playerAttachedTo;

    public Flag(Location baseLocation, LasertagColor color){
        this.color = color;
        this.baseLocation = baseLocation;
        this.banner = new ItemStack(Material.valueOf(color.getChatColor().name()+"_BANNER"));

        Bukkit.getPluginManager().registerEvents(this,session.minigames);
    }
    public Flag(Coordinate baseCoordinate, LasertagColor color){
        new Flag(baseCoordinate.getLocation(), color);
    }

    private BukkitTask glowTask;
    public void attach(Player p){
        //TODO: give player drop item
        if(session == null) return;
        this.playerAttachedTo = p;
        p.getEquipment().setHelmet(banner);
        PLAYER_FLAG.put(p,this);

        glowTask = new BukkitRunnable(){
            @Override
            public void run() {
                p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,25,255));
            }
        }.runTaskTimer(session.minigames,0,20);
    }

    public void drop(Location dropLocation){
        if(playerAttachedTo != null){
            PLAYER_FLAG.put(playerAttachedTo,null);
            playerAttachedTo.getEquipment().setHelmet(new ItemStack(Material.AIR));
            this.playerAttachedTo = null;
        }
        if(glowTask != null) glowTask.cancel();

        armorStand.teleport(dropLocation);
        armorStand.getEquipment().setHelmet(banner);
    }

    public void teleportToBase(){
        if(playerAttachedTo != null){
            PLAYER_FLAG.put(playerAttachedTo,null);
            playerAttachedTo.getEquipment().setHelmet(new ItemStack(Material.AIR));
            this.playerAttachedTo = null;
        }
        if(glowTask != null) glowTask.cancel();

        armorStand.teleport(baseLocation);
        armorStand.getEquipment().setHelmet(banner);
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
                    }
                }
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
        return playerAttachedTo == null && armorStand.getLocation() == baseLocation;
    }

    public boolean isEnabled(){
        return session != null;
    }
    public void enable(Session session){
        this.session = session;
        armorStand = (ArmorStand) baseLocation.getWorld().spawnEntity(baseLocation, EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.getEquipment().setHelmet(banner);
    }
    public void disable(){
        this.session = null;
        armorStand.getEquipment().setHelmet(new ItemStack(Material.AIR));
        armorStand.remove();

        if(playerAttachedTo != null){
            PLAYER_FLAG.put(playerAttachedTo,null);
            playerAttachedTo.getEquipment().setHelmet(new ItemStack(Material.AIR));
            this.playerAttachedTo = null;
        }
        if(glowTask != null) glowTask.cancel();
    }


    private static final HashMap<Player, Flag> PLAYER_FLAG = new HashMap<>();
    public static Flag getPlayerFlag(Player p){
        return PLAYER_FLAG.get(p);
    }
    public static boolean hasPlayerFlag(Player p){
        return (PLAYER_FLAG.get(p) != null);
    }



}

package me.noobedidoob.minigames.utils;

import me.noobedidoob.minigames.Minigames;
import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.listeners.DeathListener;
import me.noobedidoob.minigames.lasertag.methods.Mod;
import me.noobedidoob.minigames.lasertag.session.Session;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Grenade implements Listener {

    public final Player thrower;
    private Snowball snowball;


    private final BukkitTask timer;

    public Grenade(Player p, int ticks, double speed, Area area, Minigames minigames){
        this.thrower = p;
        Bukkit.getPluginManager().registerEvents(this,minigames);

        snowball = (Snowball) p.getWorld().spawnEntity(p.getEyeLocation(), EntityType.SNOWBALL);
        snowball.setVelocity(p.getEyeLocation().getDirection().multiply(speed));

        timer = Utils.runLater(()->{
            if(!area.isInside(snowball.getLocation())) return;

            Session session = Session.getPlayerSession(thrower);
            int radius = session != null ? session.getIntMod(Mod.GRENADE_EFFECT_RADUIS):Mod.GRENADE_EFFECT_RADUIS.getOgInt();
            spawnParticles(snowball.getLocation(),radius);

            if(snowball.getVehicle() != null){
                Player hitPlayer = (Player) snowball.getVehicle();
                if(!Flag.hasPlayerFlag(hitPlayer)) hitPlayer.setGlowing(false);
                if(Session.isPlayerInSession(hitPlayer)){
                    DeathListener.hit(DeathListener.HitType.GRENADE,thrower,hitPlayer,100, false, false, false);
                } else {
                    hitPlayer.teleport(minigames.spawn);
                }
            }
            for(Entity entity : snowball.getNearbyEntities(radius,radius,radius)){
                if(entity instanceof Player) {
                    Player target = (Player) entity;
                    if(session != null && session.isInSession(target)){
                        int damage = (snowball.getLocation().distance(target.getLocation())<0.7)?100:session.getIntMod(Mod.GRENADE_DAMAGE);
                        DeathListener.hit(DeathListener.HitType.GRENADE,thrower,target,damage, false, false, false);
                        affect(target);
                    } else if(Lasertag.isPlayerTesting(target)){
                        affect(target);
                    }
                }
            }

            BukkitTask timer = Utils.runTimer(()->{
                for(Entity entity : snowball.getNearbyEntities(radius,radius,radius)){
                    if(entity instanceof Player) {
                        affect((Player) entity);
                    }
                }
            }, 0,5);

            Utils.runLater(timer::cancel, 20*4);

            for(Block b : getNearbyBlocks(snowball.getLocation(),radius)){
                Material bm = b.getType();
                if(bm.name().contains("STAINED")) {
                    Utils.runLater(()->{
                        b.breakNaturally();
                        b.getLocation().getWorld().playSound(b.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.1f, 1);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.INSTANCE, () -> b.setType(bm), 20*5);
                    }, 3);
                }
            }
            snowball.remove();
        }, ticks);
    }

    public static List<Block> getNearbyBlocks(Location location, int radius) {
        List<Block> blocks = new ArrayList<>();
        for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for(int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                    blocks.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }

    private void affect(Player p) {
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 85, 1));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));
    }


    public void spawnParticles(Location location, float radius){
        location.getWorld().createExplosion(location,radius-0.2f, false, false);

        if(!location.subtract(0,0.5,0).getBlock().getType().isAir()) location = location.add(0,1,0);
        for(Vector v : BaseSphere.getSphereOffsets(radius,5)){
            location.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE,
                    location.getX()+v.getX(), location.getY()+v.getY(), location.getZ()+v.getZ(), 20,0.5d,0.5d,0.5d,0.01d);
        }
        for(Vector v : BaseSphere.getSphereOffsets(((double)radius/3)*2,4)){
            location.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE,
                    location.getX()+v.getX(), location.getY()+v.getY(), location.getZ()+v.getZ(), 20,0.5d,0.5d,0.5d,0.01d);
        }
        for(Vector v : BaseSphere.getSphereOffsets(((double) radius)/3,3)){
            location.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE,
                    location.getX()+v.getX(), location.getY()+v.getY(), location.getZ()+v.getZ(), 20,0.5d,0.5d,0.5d,0.01d);
        }

    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e){
        if(!(e.getEntity() instanceof Snowball)) return;
        Snowball ball = (Snowball) e.getEntity();
        Location loc = ball.getLocation();
        if(ball == snowball){
            if(Session.getPlayerSession(thrower).getMap().checkPlayerLaserLoc(snowball.getLocation(),thrower)) {
                if(timer != null) timer.cancel();
                snowball.remove();
                return;
            }
            snowball = (Snowball) loc.getWorld().spawnEntity(loc, EntityType.SNOWBALL);
            snowball.setGravity(false);

            if(e.getHitEntity() != null && e.getHitEntity() instanceof Player && e.getHitEntity() != thrower && (Session.isPlayerInSession((Player) e.getHitEntity()) | Lasertag.isPlayerTesting((Player) e.getHitEntity()))){
                e.getHitEntity().addPassenger(snowball);
                e.getHitEntity().setGlowing(true);
            }
        }
    }


}

package me.noobedidoob.minigames.lasertag.methods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Slab.Type;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.listeners.DeathListener;
import me.noobedidoob.minigames.lasertag.listeners.DeathListener.KillType;
import me.noobedidoob.minigames.lasertag.methods.Weapons.Weapon;
import me.noobedidoob.minigames.main.Minigames;

public class LaserShooter{
	public static List<ArmorStand> invisibleStands = new ArrayList<ArmorStand>();
	
	public static HashMap<Player, Integer> playersSnipershots = new HashMap<Player, Integer>();
	
	public static void fire(Player p, Weapon w) {
		List<Player> alreadyKilledPlayers = new ArrayList<Player>();
		
		switch (w) {
		case LASERGUN:
//			if(Weapons.playerCoolingdown.get(p) == null) Weapons.playerCoolingdown.put(p, false);
//			if(Weapons.playerReloading.get(p) == null) Weapons.playerReloading.put(p, false);
//			if(!Weapons.playerCoolingdown.get(p) && !Weapons.playerReloading.get(p)) {
//				Weapons.playerCoolingdown.put(p, true);
			if(Weapons.lasergunCoolingdown.get(p) == null) Weapons.lasergunCoolingdown.put(p, false);
			if(!Weapons.lasergunCoolingdown.get(p)) {
				Weapons.lasergunCoolingdown.put(p, true);
//				if(Modifiers.withAmmo) Weapons.decreaseAmmo(p, 1);
				
				Location l1 = p.getLocation();
				l1.setY(l1.getY()+p.getHeight()-0.225);
				Vector direction = l1.getDirection();
				direction.multiply(0.1);
				if(Modifiers.multiWeapons) {
					Vector newDirection = direction;
					direction = direction.setX(newDirection.getX()+ThreadLocalRandom.current().nextDouble(-0.001,0.001));
					direction = direction.setZ(newDirection.getZ()+ThreadLocalRandom.current().nextDouble(-0.001,0.001));
					direction = direction.setY(newDirection.getY()+ThreadLocalRandom.current().nextDouble(-0.001,0.001));
				}
				Location loc = l1.clone().add(direction);
				p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_HURT, 1, 6);
				
				int range = 100;
				if(Modifiers.multiWeapons) range = 35;
				for(double d = 0; d<range; d += 0.1) {
					if(d==0) Weapons.cooldownPlayer(p, Weapon.LASERGUN);
					loc = l1.add(direction);
					
					
					spawnProjectile(p, loc);
					
					for(Player hitP : Game.players()) {
						if(hitP != p) {
							Location pLoc = hitP.getLocation();
							if(compareLaserLocs(loc, pLoc, hitP.getHeight(), hitP.getWidth())) {
								boolean fromTeam = false;
								if(Game.teams()) {
									for(Player[] team : Game.getTeams()) {
										boolean pInTeam = false;
										for(Player tp : team) if(tp == p) pInTeam = true;
										if(pInTeam) {
											for(Player thp : team) if(thp == hitP) fromTeam = true;
										}
									}
								}
								if(!fromTeam && !alreadyKilledPlayers.contains(hitP)) {
									if(loc.distance(Game.map().getTeamSpawnLoc(Game.getPlayerColor(hitP).getChatColor())) < Game.map().getProtectionRaduis()) {
										p.sendMessage("§cYou cant shoot inside the base!");
										return;
									}
									boolean headshot = false;
									headshot= (loc.getY() < hitP.getEyeLocation().getY()+0.25 && loc.getY() > hitP.getEyeLocation().getY()-0.25);
									if(alreadyKilledPlayers.size() > 1) {
										Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.minigames, new Runnable() {
											@Override
											public void run() {
												String killedPlayersNames = "";
												int i = 0;
												for(Player kp : alreadyKilledPlayers) {
													if(i == 0) killedPlayersNames += Game.getPlayerColor(kp).getChatColor()+kp.getName();
													else killedPlayersNames += ", "+Game.getPlayerColor(kp).getChatColor()+kp.getName();
													i++;
												}
												int points = Modifiers.multiKillsExtra*alreadyKilledPlayers.size();
												String pAddon = "";
												if(points > 1) pAddon = "s";
												for(Player ap : Game.players()) {
													ap.sendMessage("§e——————————————————");
													ap.sendMessage(Game.getPlayerColor(p).getChatColor()+p.getName()+" §dkilled "+killedPlayersNames+" §dwith one shot! §7(§a+"+points+" extra Point"+pAddon+"§7)");
													ap.sendMessage("§e——————————————————");
												}
												
												Game.addPoints(p, points);
											}
										}, 10);
									}
									int damage = Modifiers.lasergunMWDamage;
									if(!Modifiers.multiWeapons) damage = Modifiers.lasergunNormalDamage; 
									DeathListener.hit(KillType.SHOT, p, hitP, damage, headshot, (d > Modifiers.minSnipeDistance), false);
									alreadyKilledPlayers.add(hitP);
								} 
							}
						}
					}
					if(isInBlock(loc)) return;
				}
			}
			break;
			
		
		case SHOTGUN:
			if(Weapons.shotgunCoolingdown.get(p) == null) Weapons.shotgunCoolingdown.put(p, false);
			if(!Weapons.shotgunCoolingdown.get(p)) {
				Weapons.shotgunCoolingdown.put(p, true);
//				if(Modifiers.withAmmo) Weapons.decreaseAmmo(p, 1);
				p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_HURT, 1, 6);
				Location startLoc = p.getLocation();
				startLoc.setY(startLoc.getY()+p.getEyeHeight()-0.1);
				
				Location[] startLocs = new Location[9];
				float dis = 16; 
				int n = 0;
				for(float pitch = dis; pitch > ((dis)*2)*(-1); pitch -= dis) {
					for(float yaw = dis*(-1); yaw < dis*2; yaw += dis) {
						startLocs[n] = startLoc.clone();
						startLocs[n].setPitch(startLocs[n].getPitch()+pitch);
						startLocs[n].setYaw(startLocs[n].getYaw()+yaw);
						n++;
					}
				}
				
				Vector[] dirs = new Vector[9];
				Location[] locs = new Location[9];
				for(int i = 0; i < 9; i++) {
					dirs[i] = startLocs[i].getDirection().multiply(0.1);
					locs[i] = startLocs[i].add(dirs[i]);
				}
				
				
				
				for(double d = 0; d<6; d += 0.1) {
					if(d==0) {
						Weapons.cooldownPlayer(p, Weapon.SHOTGUN);
					}
					
					for(int i = 0; i < 9; i++) {
						locs[i] = startLocs[i].add(dirs[i]);
						spawnProjectile(p, locs[i]);
					}
					
					
					for(Player hitP : Game.players()) {
						if(hitP != p) {
							Location pLoc = hitP.getLocation();
							for(Location loc : locs) {
								if(compareLaserLocs(loc, pLoc, hitP.getHeight(), hitP.getWidth())) {
									boolean fromTeam = false;
									if(Game.teams()) {
										for(Player[] team : Game.getTeams()) {
											boolean pInTeam = false;
											for(Player tp : team) if(tp == p) pInTeam = true;
											if(pInTeam) {
												for(Player thp : team) if(thp == hitP) fromTeam = true;
											}
										}
									}
									if(!fromTeam && !alreadyKilledPlayers.contains(hitP)) {
										if(loc.distance(Game.map().getTeamSpawnLoc(Game.getPlayerColor(hitP).getChatColor())) < Game.map().getProtectionRaduis()) {
											p.sendMessage("§cYou cant shoot inside the base!");
											return;
										}
										if(compareLaserLocs(loc, pLoc, hitP.getHeight(), hitP.getWidth())) {
											if(alreadyKilledPlayers.size() > 1) {
												Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.minigames, new Runnable() {
													@Override
													public void run() {
														String killedPlayersNames = "";
														int i = 0;
														for(Player kp : alreadyKilledPlayers) {
															if(i == 0) killedPlayersNames += Game.getPlayerColor(kp).getChatColor()+kp.getName();
															else killedPlayersNames += ", "+Game.getPlayerColor(kp).getChatColor()+kp.getName();
															i++;
														}
														int points = Modifiers.multiKillsExtra*alreadyKilledPlayers.size();
														String pAddon = "";
														if(points > 1) pAddon = "s";
														for(Player ap : Game.players()) {
															ap.sendMessage("§e——————————————————");
															ap.sendMessage(Game.getPlayerColor(p).getChatColor()+p.getName()+" §dkilled "+killedPlayersNames+" §dwith one shot! §7(§a+"+points+" extra Point"+pAddon+"§7)");
															ap.sendMessage("§e——————————————————");
														}
														
														Game.addPoints(p, points);
													}
												}, 10);
											}
											DeathListener.hit(KillType.SHOT, p, hitP, Modifiers.shotgunDamage, false, false, false);
//												p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 0);
											alreadyKilledPlayers.add(hitP);
										}
									}
								}
							}
						}
					}
					for(Location loc : locs) {
						if(isInBlock(loc)) return;
					}
				}
			}
			break;
		case SNIPER:
			if(Weapons.sniperCoolingdown.get(p) == null) Weapons.sniperCoolingdown.put(p, false);
			if(!Weapons.sniperCoolingdown.get(p)) {
//				if(Modifiers.withAmmo) Weapons.decreaseAmmo(p, 1);
				
				Location l1 = p.getLocation();
				l1.setY(l1.getY()+p.getHeight()-0.225);
				Vector direction = l1.getDirection();
				direction.multiply(1);
				Location loc = l1.clone().add(direction);
				p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_HURT, 10, 0);
				
				for(double d = 0; d<100; d += 1) {
					if(d==0) {
						if(playersSnipershots.get(p) == null) playersSnipershots.put(p, 0);
						int shots = playersSnipershots.get(p);
						if(shots == Modifiers.sniperAmmoBeforeCooldown-1) {
							Weapons.cooldownPlayer(p, Weapon.SNIPER);
							playersSnipershots.put(p, 0);
							p.getInventory().getItem(2).setAmount(1);
						} else {
							playersSnipershots.put(p, shots+1);
							p.getInventory().getItem(2).setAmount(Modifiers.sniperAmmoBeforeCooldown-1-shots);
						}
						
					}
					loc = l1.add(direction);
					
					spawnProjectile(p, loc);
					
					
					Player[] inGamePlayers = Game.players();
					for(Player hitP : inGamePlayers) {
						if(hitP != p) {
							Location pLoc = hitP.getLocation();
							if(compareLaserLocs(loc, pLoc, hitP.getHeight(), hitP.getWidth())) {
								boolean fromTeam = false;
								if(Game.teams()) {
									for(Player[] team : Game.getTeams()) {
										boolean pInTeam = false;
										for(Player tp : team) if(tp == p) pInTeam = true;
										if(pInTeam) {
											for(Player thp : team) if(thp == hitP) fromTeam = true;
										}
									}
								}
								if(!fromTeam && !alreadyKilledPlayers.contains(hitP)) {
									if(loc.distance(Game.map().getTeamSpawnLoc(Game.getPlayerColor(hitP).getChatColor())) < Game.map().getProtectionRaduis()) {
										p.sendMessage("§cYou cant shoot inside the base!");
										return;
									}
									if(compareLaserLocs(loc, pLoc, hitP.getHeight(), hitP.getWidth())) {
										boolean headshot = false;
										headshot= (loc.getY() < hitP.getEyeLocation().getY()+0.25 && loc.getY() > hitP.getEyeLocation().getY()-0.25);
										if(alreadyKilledPlayers.size() > 1) {
											Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.minigames, new Runnable() {
												@Override
												public void run() {
													String killedPlayersNames = "";
													int i = 0;
													for(Player kp : alreadyKilledPlayers) {
														if(i == 0) killedPlayersNames += Game.getPlayerColor(kp).getChatColor()+kp.getName();
														else killedPlayersNames += ", "+Game.getPlayerColor(kp).getChatColor()+kp.getName();
														i++;
													}
													int points = Modifiers.multiKillsExtra*alreadyKilledPlayers.size();
													String pAddon = "";
													if(points > 1) pAddon = "s";
													for(Player ap : Game.players()) {
														ap.sendMessage("§e——————————————————");
														ap.sendMessage(Game.getPlayerColor(p).getChatColor()+p.getName()+" §dkilled "+killedPlayersNames+" §dwith one shot! §7(§a+"+points+" extra Point"+pAddon+"§7)");
														ap.sendMessage("§e——————————————————");
													}
													
													Game.addPoints(p, points);
												}
											}, 10);
										}
										DeathListener.hit(KillType.SHOT, p, hitP, Modifiers.sniperdamage, headshot, (d > Modifiers.minSnipeDistance), false);
//											p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 0);
										alreadyKilledPlayers.add(hitP);
									}
								} 
							}
						}
					}
					if(isInBlock(loc)) return;
				}
			}
			break;
		
		default:
			break;
		}
	}
	
	
	
	public static boolean compareLaserLocs(Location laserLoc, Location playerLoc, double height, double width) {
		double lx = laserLoc.getX(); double ly = laserLoc.getY(); double lz = laserLoc.getZ(); World lw = laserLoc.getWorld();
		double px = playerLoc.getX(); double py = playerLoc.getY(); double pz = playerLoc.getZ(); World pw = playerLoc.getWorld();
		
		if(lw == pw) {
			if(lx <= px+(width+Modifiers.widthAddon)/2 && lx >= px-(width+Modifiers.widthAddon)/2) {
				if(ly <= py+height+Modifiers.heightAddon && ly >= py) {
					if(lz <= pz+(width+Modifiers.widthAddon)/2 && lz >= pz-(width+Modifiers.widthAddon)/2) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static boolean isInBlock(Location loc) {
		Block b = loc.getBlock();
		Material fm = b.getType();
		if(fm.name().contains("STAINED")) {
			b.setType(Material.AIR);
			Bukkit.getScheduler().scheduleSyncDelayedTask(Lasertag.minigames, new Runnable() {
				@Override
				public void run() {
					b.setType(fm);
				}
			}, 20*5);
			return true;
		}
		if(!Modifiers.shootThroughBlocks) {
			Material m = Minigames.world.getBlockAt(loc).getType();
			if(!m.isAir() && !m.name().contains("Fence")) {
				if(m.isSolid()) {
					if(Tag.SLABS.isTagged(m)) {
						if(b.getBlockData() instanceof Slab) {
							Slab slab = (Slab) b.getBlockData();
							if(slab.getType() == Type.BOTTOM) {
								if(loc.getY() < b.getY()+0.5) {
									return true;
								}
							} else {
								if(loc.getY() > b.getY()+0.5) {
									return true;
								}
							}
						}
					} else if(Tag.TRAPDOORS.isTagged(m)) {
						if(!((TrapDoor) b.getBlockData()).isOpen()) return true;
					} else if(Tag.DOORS.isTagged(m)) {
						if(!((Door) b.getBlockData()).isOpen()) return true;
					} else return true;
				}
			}
		}
		return false;
	}
	
	static Particle coloredLaser = Particle.REDSTONE;
	
	public static void spawnProjectile(Player p, Location loc) {
		if(Game.getPlayerColor(p).getColor() != Color.BLUE) p.getWorld().spawnParticle(Particle.REDSTONE, loc.getX(), loc.getY(), loc.getZ(), 0, 0, 0, 0, 1, new Particle.DustOptions(Game.getPlayerColor(p).getColor(), 0.5f));
		else p.getWorld().spawnParticle(Particle.REDSTONE, loc.getX(), loc.getY(), loc.getZ(), 0, 0, 0, 0, 1, new Particle.DustOptions(Color.fromRGB(0, 183, 255), 0.5f));
	}
	
	
	
	
	public static void fireTest(Player p, Weapon w) throws NullPointerException{
		if(w == null) throw new NullPointerException("Weapon is null");
		p.getInventory().getItemInMainHand().addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 7);
			
			switch (w) {
			case LASERGUN:
				if(!Weapons.lasergunCoolingdown.get(p)) {
					Weapons.cooldownPlayer(p, w);
					Location startLoc = p.getLocation();
					startLoc.setY(startLoc.getY()+p.getEyeHeight());
					p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_HURT, 1, 5);
					
					Vector direction = startLoc.getDirection();
					direction.multiply(0.1);
					Location loc = startLoc.clone().add(direction);
					
					for(double d = 0; d<100; d += 0.1) {
						loc = startLoc.add(direction);
						spawnTestProjectile(p, loc, Color.GREEN);
						
						for(Entity entity : p.getNearbyEntities(100,100,100)) {
							if(entity != (Entity) p && entity instanceof LivingEntity && !(entity.getType() == EntityType.ARMOR_STAND)) {
								LivingEntity le = (LivingEntity) entity;
								Location leloc = le.getLocation();
								if(compareLaserLocs(loc, leloc, le.getHeight(), le.getWidth())) {
									le.damage(20);
									p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 0);
								}
							} else if(entity.getType() == EntityType.ARMOR_STAND) {
								ArmorStand as = (ArmorStand) entity;
								if(compareLaserLocs(loc, entity.getLocation(), entity.getHeight(), entity.getWidth())) {
									p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 0);
									as.setVisible(false);
									if(!invisibleStands.contains(as)) invisibleStands.add(as);
								}
							} 
						}
						if(isInBlock(loc)) {
							return;
						}
					}
				}
				break;
				
				
				
				
				
				
			case SHOTGUN:
				if(!Weapons.shotgunCoolingdown.get(p)) {
					Weapons.cooldownPlayer(p, w);
					Location startLoc = p.getLocation();
					startLoc.setY(startLoc.getY()+p.getEyeHeight());
					p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_HURT, 1, 5);
					startLoc.setY(p.getLocation().getY()+p.getEyeHeight()-0.15);
					Location[] startLocs = new Location[9];
					float dis = 15; 
					int n = 0;
					for(float pitch = dis; pitch > ((dis)*2)*(-1); pitch -= dis) {
						for(float yaw = dis*(-1); yaw < dis*2; yaw += dis) {
							startLocs[n] = startLoc.clone();
							startLocs[n].setPitch(startLocs[n].getPitch()+pitch);
							startLocs[n].setYaw(startLocs[n].getYaw()+yaw);
							n++;
						}
					}
					
					Vector[] dirs = new Vector[9];
					Location[] locs = new Location[9];
					for(int i = 0; i < 9; i++) {
						dirs[i] = startLocs[i].getDirection().multiply(0.1);
						locs[i] = startLocs[i].add(dirs[i]);
					}
					
					for(double d = 0; d<3.6; d += 0.1) {
						for(int i = 0; i < 9; i++) {
							locs[i] = startLocs[i].add(dirs[i]);
							spawnTestProjectile(p, locs[i], Color.PURPLE);
						}
						for(Location sloc : locs) {
							for(Entity entity : p.getNearbyEntities(100, 100, 100)) {
								if(compareLaserLocs(sloc, entity.getLocation(), entity.getHeight(), entity.getWidth())) {
									p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 0);
								}
							}
							if(isInBlock(sloc)) return;
						}
					}
				}
				break;
				
				
				
				
				
			
			case SNIPER:
				if(!Weapons.sniperCoolingdown.get(p)) {
					Weapons.cooldownPlayer(p, w);
					Location startLoc = p.getLocation();
					startLoc.setY(startLoc.getY()+p.getEyeHeight());
					p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_HURT, 1, 5);
					Vector direction1 = startLoc.getDirection();
					direction1.multiply(1);
					Location loc1 = startLoc.clone().add(direction1);
					
					for(double d = 0; d<100; d += 1) {
						loc1 = startLoc.add(direction1);
						spawnTestProjectile(p, loc1, Color.RED);
						
						for(Entity entity : p.getNearbyEntities(100,100,100)) {
							if(entity != (Entity) p && entity instanceof LivingEntity && !(entity.getType() == EntityType.ARMOR_STAND)) {
								LivingEntity le = (LivingEntity) entity;
								Location leloc = le.getLocation();
								if(compareLaserLocs(loc1, leloc, le.getHeight(), le.getWidth())) {
									le.damage(20);
									p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 0);
								}
							} else if(entity.getType() == EntityType.ARMOR_STAND) {
								ArmorStand as = (ArmorStand) entity;
								if(compareLaserLocs(loc1, entity.getLocation(), entity.getHeight(), entity.getWidth())) {
									p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 0);
									as.setVisible(false);
									if(!invisibleStands.contains(as)) invisibleStands.add(as);
									Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.minigames, new Runnable() {
										@Override
										public void run() {
											
										}
									}, 20*10);
								}
							} 
						}
						if(isInBlock(loc1)) {
							return;
						}
					}
				}
				break;
			default:
				break;
			}
	}
	
	public static void spawnTestProjectile(Player p, Location loc, Color c) {
		p.getWorld().spawnParticle(Particle.REDSTONE, loc.getX(), loc.getY(), loc.getZ(), 0, 0, 0, 0, 1, new Particle.DustOptions(c, 0.5f));
	}
}

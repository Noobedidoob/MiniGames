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
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Slab.Type;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.listeners.DeathListener;
import me.noobedidoob.minigames.lasertag.listeners.DeathListener.KillType;
import me.noobedidoob.minigames.lasertag.methods.Weapons.Weapon;
import me.noobedidoob.minigames.lasertag.session.SessionModifiers;
import me.noobedidoob.minigames.lasertag.session.SessionModifiers.Mod;
import me.noobedidoob.minigames.lasertag.session.Session;
import me.noobedidoob.minigames.lasertag.session.SessionTeam;
import me.noobedidoob.minigames.main.Minigames;

public class LaserShooter{
	public static List<ArmorStand> invisibleStands = new ArrayList<ArmorStand>();
	
	public static HashMap<Player, Integer> playersSnipershots = new HashMap<Player, Integer>();
	
	public static void fire(Player p, Weapon w) {
		Session session = Session.getPlayerSession(p);
		if(session == null) return;
		SessionModifiers modifiers = session.modifiers;
		
		List<Player> alreadyKilledPlayers = new ArrayList<Player>();
		
		switch (w) {
		case LASERGUN:
			if(Weapons.lasergunCoolingdown.get(p) == null) Weapons.lasergunCoolingdown.put(p, false);
			if(!Weapons.lasergunCoolingdown.get(p)) {
				Weapons.lasergunCoolingdown.put(p, true);
				
				Location l1 = p.getLocation();
				l1.setY(l1.getY()+p.getHeight()-0.225);
				Vector direction = l1.getDirection();
				direction.multiply(0.1);
				if(session.withMultiweapons()) {
					Vector newDirection = direction;
					direction = direction.setX(newDirection.getX()+ThreadLocalRandom.current().nextDouble(-0.001,0.001));
					direction = direction.setZ(newDirection.getZ()+ThreadLocalRandom.current().nextDouble(-0.001,0.001));
					direction = direction.setY(newDirection.getY()+ThreadLocalRandom.current().nextDouble(-0.001,0.001));
				}
				Location loc = l1.clone().add(direction);
				p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_HURT, 1, 6);
				
				int range = 100;
				if(session.withMultiweapons()) range = 35;
				for(double d = 0; d<range; d += 0.1) {
					if(d==0) Weapons.cooldownPlayer(p, Weapon.LASERGUN, false);
					loc = l1.add(direction);
					
					
					spawnProjectile(p, loc);
					
					for(Player hitP : session.getPlayers()) {
						if(hitP != p) {
							if(isLaserInsideEntity(hitP, loc)) {
								boolean fromTeam = false;
								if(session.isTeams()) {
									for(SessionTeam team : session.getTeams()) {
										boolean pInTeam = false;
										for(Player tp : team.getPlayers()) if(tp == p) pInTeam = true;
										if(pInTeam) {
											for(Player thp : team.getPlayers()) if(thp == hitP) fromTeam = true;
										}
									}
								}
								if(!fromTeam && !alreadyKilledPlayers.contains(hitP)) {
									if(loc.distance(session.getMap().getTeamSpawnLoc(session.getPlayerColor(hitP).getChatColor())) < session.getMap().getProtectionRaduis()) {
										Minigames.sendPlayerActionBarMessage(p, "§cYou cant shoot inside the base!");
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
													if(i == 0) killedPlayersNames += session.getPlayerColor(kp).getChatColor()+kp.getName();
													else killedPlayersNames += ", "+session.getPlayerColor(kp).getChatColor()+kp.getName();
													i++;
												}
												int points =modifiers.getInt(Mod.MULTIKILLS_EXTRA)*alreadyKilledPlayers.size();
												String pAddon = "";
												if(points > 1) pAddon = "s";
												for(Player ap : session.getPlayers()) {
													ap.sendMessage("§e——————————————————");
													ap.sendMessage(session.getPlayerColor(p).getChatColor()+p.getName()+" §dkilled "+killedPlayersNames+" §dwith one shot! §7(§a+"+points+" extra Point"+pAddon+"§7)");
													ap.sendMessage("§e——————————————————");
												}
												
												session.addPoints(p, points);
											}
										}, 10);
									}
									int damage = modifiers.getInt(Mod.LASERGUN_NORMAL_DAMAGE);
									if(session.withMultiweapons()) damage = modifiers.getInt(Mod.LASERGUN_MULTIWEAPONS_DAMAGE); 
									DeathListener.hit(KillType.SHOT, p, hitP, damage, headshot, (d > modifiers.getInt(Mod.MINIMAL_SNIPE_DISTANCE)), false);
									alreadyKilledPlayers.add(hitP);
								} 
							}
						}
					}
					if(isInBlock(session, loc)) return;
				}
			}
			break;
			
		
		case SHOTGUN:
			if(Weapons.shotgunCoolingdown.get(p) == null) Weapons.shotgunCoolingdown.put(p, false);
			if(!Weapons.shotgunCoolingdown.get(p)) {
				Weapons.shotgunCoolingdown.put(p, true);
//				if(Mod.withAmmo) Weapons.decreaseAmmo(p, 1);
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
						Weapons.cooldownPlayer(p, Weapon.SHOTGUN, false);
					}
					
					for(int i = 0; i < 9; i++) {
						locs[i] = startLocs[i].add(dirs[i]);
						spawnProjectile(p, locs[i]);
					}
					
					
					for(Player hitP : session.getPlayers()) {
						if(hitP != p) {
							for(Location loc : locs) {
								if(isLaserInsideEntity(hitP, loc)) {
									boolean fromTeam = false;
									if(session.isTeams()) {
										for(SessionTeam team : session.getTeams()) {
											boolean pInTeam = false;
											for(Player tp : team.getPlayers()) if(tp == p) pInTeam = true;
											if(pInTeam) {
												for(Player thp : team.getPlayers()) if(thp == hitP) fromTeam = true;
											}
										}
									}
									if(!fromTeam && !alreadyKilledPlayers.contains(hitP)) {
										if(loc.distance(session.getMap().getTeamSpawnLoc(session.getPlayerColor(hitP).getChatColor())) < session.getMap().getProtectionRaduis()) {
											Minigames.sendPlayerActionBarMessage(p, "§cYou cant shoot inside the base!");
											return;
										}
										if(isLaserInsideEntity(hitP, loc)) {
											if(alreadyKilledPlayers.size() > 1) {
												Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.minigames, new Runnable() {
													@Override
													public void run() {
														String killedPlayersNames = "";
														int i = 0;
														for(Player kp : alreadyKilledPlayers) {
															if(i == 0) killedPlayersNames += session.getPlayerColor(kp).getChatColor()+kp.getName();
															else killedPlayersNames += ", "+session.getPlayerColor(kp).getChatColor()+kp.getName();
															i++;
														}
														int points = modifiers.getInt(Mod.MULTIKILLS_EXTRA)*alreadyKilledPlayers.size();
														String pAddon = "";
														if(points > 1) pAddon = "s";
														for(Player ap : session.getPlayers()) {
															ap.sendMessage("§e——————————————————");
															ap.sendMessage(session.getPlayerColor(p).getChatColor()+p.getName()+" §dkilled "+killedPlayersNames+" §dwith one shot! §7(§a+"+points+" extra Point"+pAddon+"§7)");
															ap.sendMessage("§e——————————————————");
														}
														
														session.addPoints(p, points);
													}
												}, 10);
											}
											DeathListener.hit(KillType.SHOT, p, hitP, modifiers.getInt(Mod.SHOTGUN_DAMAGE), false, false, false);
//												p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 0);
											alreadyKilledPlayers.add(hitP);
										}
									}
								}
							}
						}
					}
					for(Location loc : locs) {
						if(isInBlock(session, loc)) return;
					}
				}
			}
			break;
		case SNIPER:
			if(Weapons.sniperCoolingdown.get(p) == null) Weapons.sniperCoolingdown.put(p, false);
			if(!Weapons.sniperCoolingdown.get(p)) {
//				if(Mod.withAmmo) Weapons.decreaseAmmo(p, 1);
				
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
						if(shots == modifiers.getInt(Mod.SNIPER_AMMO_BEFORE_COOLDOWN)-1) {
							Weapons.cooldownPlayer(p, Weapon.SNIPER, false);
							playersSnipershots.put(p, 0);
							p.getInventory().getItem(2).setAmount(1);
						} else {
							playersSnipershots.put(p, shots+1);
							p.getInventory().getItem(2).setAmount(modifiers.getInt(Mod.SNIPER_AMMO_BEFORE_COOLDOWN)-1-shots);
						}
						
					}
					loc = l1.add(direction);
					
					spawnProjectile(p, loc);
					
					
					Player[] inGamePlayers = session.getPlayers();
					for(Player hitP : inGamePlayers) {
						if(hitP != p) {
							if(isLaserInsideEntity(hitP, loc)) {
								boolean fromTeam = false;
								if(session.isTeams()) {
									for(SessionTeam team : session.getTeams()) {
										boolean pInTeam = false;
										for(Player tp : team.getPlayers()) if(tp == p) pInTeam = true;
										if(pInTeam) {
											for(Player thp : team.getPlayers()) if(thp == hitP) fromTeam = true;
										}
									}
								}
								if(!fromTeam && !alreadyKilledPlayers.contains(hitP)) {
									if(loc.distance(session.getMap().getTeamSpawnLoc(session.getPlayerColor(hitP).getChatColor())) < session.getMap().getProtectionRaduis()) {
										Minigames.sendPlayerActionBarMessage(p, "§cYou cant shoot inside the base!");
										return;
									}
									if(isLaserInsideEntity(hitP, loc)) {
										boolean headshot = false;
										headshot= (loc.getY() < hitP.getEyeLocation().getY()+0.25 && loc.getY() > hitP.getEyeLocation().getY()-0.25);
										if(alreadyKilledPlayers.size() > 1) {
											Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.minigames, new Runnable() {
												@Override
												public void run() {
													String killedPlayersNames = "";
													int i = 0;
													for(Player kp : alreadyKilledPlayers) {
														if(i == 0) killedPlayersNames += session.getPlayerColor(kp).getChatColor()+kp.getName();
														else killedPlayersNames += ", "+session.getPlayerColor(kp).getChatColor()+kp.getName();
														i++;
													}
													int points = modifiers.getInt(Mod.MULTIKILLS_EXTRA)*alreadyKilledPlayers.size();
													String pAddon = "";
													if(points > 1) pAddon = "s";
													for(Player ap : session.getPlayers()) {
														ap.sendMessage("§e——————————————————");
														ap.sendMessage(session.getPlayerColor(p).getChatColor()+p.getName()+" §dkilled "+killedPlayersNames+" §dwith one shot! §7(§a+"+points+" extra Point"+pAddon+"§7)");
														ap.sendMessage("§e——————————————————");
													}
													
													session.addPoints(p, points);
												}
											}, 10);
										}
										DeathListener.hit(KillType.SHOT, p, hitP, modifiers.getInt(Mod.SNIPER_DAMAGE), headshot, (d > modifiers.getInt(Mod.MINIMAL_SNIPE_DISTANCE)), false);
//											p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 0);
										alreadyKilledPlayers.add(hitP);
									}
								} 
							}
						}
					}
					if(isInBlock(session, loc)) return;
				}
			}
			break;
		
		default:
			break;
		}
	}
	
//	public static void colorPlayerHitBox(Player p) {
//		HitBox hb = new HitBox(p);
//		for(double x = hb.getMinX(); x <= hb.getMaxX(); x += 0.1) {
//			for(double y = hb.getMinY(); y <= hb.getMaxY(); y += 0.1) {
//				for(double z = hb.getMinZ(); z <= hb.getMaxZ(); z += 0.1) {
//					spawnTestProjectile(p, new Location(p.getWorld(), x, y, z), Color.RED);
//				}
//			}
//		}
//		
//	}
	
	
	public static boolean isLaserInsideEntity(Entity p, Location loc) {
//		if(new HitBox(p).isInside(loc)) return true;
		
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();
		
		double width = p.getWidth();
		double height = p.getHeight()+0.15;
		
		try {
			width += Session.getPlayerSession((Player) p).modifiers.getDouble(Mod.WIDTH_ADDON);
			height += Session.getPlayerSession((Player) p).modifiers.getDouble(Mod.HEIGHT_ADDON);
		} catch (Exception e) {
		}
		
//		System.out.println("HEIGHT_ADDON: "+Mod.HEIGHT_ADDON.getDouble());
//		System.out.println("WIDTH_ADDON: "+Mod.WIDTH_ADDON.getDouble());
		
		double minX = p.getLocation().getX()-(width/2);
		double minY = p.getLocation().getY();
		double minZ = p.getLocation().getZ()-(width/2);
		double maxX = (p.getLocation().getX()-(width/2))+width;
		double maxY = p.getLocation().getY()+height;
		double maxZ = (p.getLocation().getZ()-(width/2))+width;
		
		if(minX <= x && x <= maxX) {
			if(minY <= y && y <= maxY) {
				if(minZ <= z && z <= maxZ) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean isInBlock(Session s, Location loc) {
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
		boolean shooThroughEnabled = s.getBooleanMod(Mod.SHOOT_THROUGH_BLOCKS);
		if(!shooThroughEnabled) {
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
		Session session = Session.getPlayerSession(p);
		if(session.getPlayerColor(p).getColor() != Color.BLUE) p.getWorld().spawnParticle(Particle.REDSTONE, loc.getX(), loc.getY(), loc.getZ(), 0, 0, 0, 0, 1, new Particle.DustOptions(session.getPlayerColor(p).getColor(), 0.5f));
		else p.getWorld().spawnParticle(Particle.REDSTONE, loc.getX(), loc.getY(), loc.getZ(), 0, 0, 0, 0, 1, new Particle.DustOptions(Color.fromRGB(0, 183, 255), 0.5f));
	}
	
	
	
	
	public static void fireTest(Player p, Weapon w) throws NullPointerException{
		if(w == null) throw new NullPointerException("Weapon is null");
		switch (w) {
		case LASERGUN:
			if(!Weapons.lasergunCoolingdown.get(p)) {
				Weapons.cooldownPlayer(p, w, true);
				Location startLoc = p.getLocation();
				startLoc.setY(startLoc.getY()+p.getEyeHeight());
				p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_HURT, 1, 5);
				
				Vector direction = startLoc.getDirection();
				direction.multiply(0.1);
				Location loc = startLoc.clone().add(direction);
				
				for(double d = 0; d<100; d += 0.1) {
					loc = startLoc.add(direction);
					spawnTestProjectile(p, loc, Color.fromRGB(0, 170, 255));

					if(!checkloc(p, loc)) return;
				}
			}
			break;
			
			
			
			
			
			
		case SHOTGUN:
			if(!Weapons.shotgunCoolingdown.get(p)) {
				Weapons.cooldownPlayer(p, w, true);
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
						spawnTestProjectile(p, locs[i], Color.YELLOW);
					}
					for(Location loc : locs) {
						if(!checkloc(p, loc)) return;
					}
				}
			}
			break;
			
			
			
			
			
		
		case SNIPER:
			if(!Weapons.sniperCoolingdown.get(p)) {
				Location startLoc = p.getLocation();
				startLoc.setY(startLoc.getY()+p.getEyeHeight());
				p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_HURT, 1, 5);
				Vector direction1 = startLoc.getDirection();
				direction1.multiply(1);
				Location loc1 = startLoc.clone().add(direction1);
				
				for(double d = 0; d<100; d += 1) {
					
					if(d==0) {
						if(playersSnipershots.get(p) == null) playersSnipershots.put(p, 0);
						int shots = playersSnipershots.get(p);
						if(shots == Mod.SNIPER_AMMO_BEFORE_COOLDOWN.getOgInt()-1) {
							Weapons.cooldownPlayer(p, Weapon.SNIPER, true);
							playersSnipershots.put(p, 0);
							p.getInventory().getItem(3).setAmount(1);
						} else {
							playersSnipershots.put(p, shots+1);
							p.getInventory().getItem(3).setAmount(Mod.SNIPER_AMMO_BEFORE_COOLDOWN.getOgInt()-1-shots);
						}
						
					}
					
					loc1 = startLoc.add(direction1);
					spawnTestProjectile(p, loc1, Color.PURPLE);
					
					if(!checkloc(p, loc1)) return;
				}
			}
			break;
		default:
			
			break;
		}
			
	}
	
	private static boolean checkloc(Player p, Location loc) {
		if(!Lasertag.testArea.isInside(loc)) return false;
		if(isInBlock(null, loc)) return false;
		for(Entity entity : loc.getWorld().getNearbyEntities(loc, 2, 2, 2)) {
			if (entity != p) {
				if (entity instanceof ItemFrame) {
					if(isLaserInsideEntity(entity, loc)) {
						p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 0);
					}
				} else
				if(isLaserInsideEntity(entity, loc)) {
					p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 0);
				} 
			}
		}
		return true;
	}
	
	public static void spawnTestProjectile(Player p, Location loc, Color c) {
		p.getWorld().spawnParticle(Particle.REDSTONE, loc.getX(), loc.getY(), loc.getZ(), 0, 0, 0, 0, 1, new Particle.DustOptions(c, 0.5f));
	}
	
}

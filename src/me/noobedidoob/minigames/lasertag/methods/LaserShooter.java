package me.noobedidoob.minigames.lasertag.methods;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import me.noobedidoob.minigames.utils.HitBox;
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
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.listeners.DeathListener;
import me.noobedidoob.minigames.lasertag.listeners.DeathListener.HitType;
import me.noobedidoob.minigames.lasertag.methods.Weapons.Weapon;
import me.noobedidoob.minigames.lasertag.session.SessionModifiers;
import me.noobedidoob.minigames.lasertag.session.SessionModifiers.Mod;
import me.noobedidoob.minigames.lasertag.session.Session;
import me.noobedidoob.minigames.lasertag.session.SessionTeam;
import me.noobedidoob.minigames.Minigames;
import me.noobedidoob.minigames.utils.BaseSphere;
import me.noobedidoob.minigames.utils.Coordinate;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class LaserShooter{
	
	public static void fire(Player p, Weapon w) {
		Session session = Session.getPlayerSession(p);
		if(session == null) return;
		SessionModifiers modifiers = session.modifiers;
		
		List<Player> alreadyKilledPlayers = new ArrayList<>();
		
		switch (w) {
		case LASERGUN:
			if(!w.hasCooldown(p)) {
				if(session.getMap().checkLocPlayerShootingFrom(p)) return;
//				if ((session.isSolo() && !session.getMap().withRandomSpawn()) | (session.isTeams() && session.getMap().withBaseSpawn())) {
//					for (Coordinate coord : session.getMap().getBaseCoords()) {
//						if (p.getLocation().distance(coord.getLocation()) < session.getMap().getProtectionRaduis()) {
//							p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED+""+ChatColor.BOLD+"You can't shoot while in a base!"));
//							session.getMap().drawBaseSphere(session.getMap().baseColor.get(coord), p);
//							return;
//						}
//					}
//				}
				p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_HURT, 1, 6);

				Location l1 = p.getLocation();
				l1.setY(l1.getY()+p.getHeight()-0.225);
				Vector direction = l1.getDirection();
				direction.multiply(0.1);
				if(session.withMultiweapons()) {
					Vector newDirection = direction;
					direction = direction.setX(newDirection.getX()+ThreadLocalRandom.current().nextDouble(-0.0001,0.0001));
					direction = direction.setZ(newDirection.getZ()+ThreadLocalRandom.current().nextDouble(-0.0001,0.0001));
					direction = direction.setY(newDirection.getY()+ThreadLocalRandom.current().nextDouble(-0.0001,0.0001));
				}
				Location loc = l1.clone().add(direction);

				int range = 100;
//				if(session.withMultiweapons()) range = 35;
				for(double d = 0; d<range; d += 0.1) {
					if(d==0) w.setCooldown(p);
					loc = l1.add(direction);


					spawnProjectile(p, loc);

					if(session.getMap().checkPlayerLaserLoc(loc,p)) return;
//					if ((session.isSolo() && !session.getMap().withRandomSpawn()) | (session.isTeams() && session.getMap().withBaseSpawn())) {
//						for(Coordinate coord : session.getMap().getBaseCoords()) {
//							if(loc.distance(coord.getLocation()) < session.getMap().getProtectionRaduis()) {
//								p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED+""+ChatColor.BOLD+"You can't shoot into a base!"));
//								session.getMap().drawBaseSphere(session.getMap().baseColor.get(coord), p);
//								return;
//							}
//						}
//					}

					for(Player hitP : session.getPlayers()) {
						if(hitP != p && !alreadyKilledPlayers.contains(hitP)) {
							if(isLaserInsideEntity(hitP, loc)) {
								if(Lasertag.isPlayerProtected(hitP)) {
									p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED+""+ChatColor.BOLD+"Player has spawnprotection"));
									BaseSphere.drawPlayerProtectionSphere(hitP);
									return;
								}
								boolean fromTeam = false;
								if(session.isTeams()) {
									for(SessionTeam team : session.getTeams()) {
										boolean pInTeam = false;
										for(Player tp : team.getPlayers())
											if (tp == p) {
												pInTeam = true;
												break;
											}
										if(pInTeam) {
											for(Player thp : team.getPlayers())
												if (thp == hitP) {
													fromTeam = true;
													break;
												}
										}
									}
								}
								if(!fromTeam) {
//									if(alreadyKilledPlayers.size() > 1) {
//										Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.minigames, new Runnable() {
//											@Override
//											public void run() {
//												String killedPlayersNames = "";
//												int i = 0;
//												for(Player kp : alreadyKilledPlayers) {
//													if(i == 0) killedPlayersNames += session.getPlayerColor(kp).getChatColor()+kp.getName();
//													else killedPlayersNames += ", "+session.getPlayerColor(kp).getChatColor()+kp.getName();
//													i++;
//												}
//												int points =modifiers.getInt(Mod.MULTIKILLS_EXTRA_POINTS)*alreadyKilledPlayers.size();
//												String pAddon = "";
//												if(points > 1) pAddon = "s";
//												for(Player ap : session.getPlayers()) {
//													ap.sendMessage("§e——————————————————");
//													ap.sendMessage(session.getPlayerColor(p).getChatColor()+p.getName()+" §dkilled "+killedPlayersNames+" §dwith one shot! §7(§a+"+points+" extra Point"+pAddon+"§7)");
//													ap.sendMessage("§e——————————————————");
//												}
//
//												session.addPoints(p, points);
//											}
//										}, 10);
//									}
									DeathListener.hit(HitType.SHOT, p, hitP, (session.withMultiweapons())?session.getIntMod(Mod.LASERGUN_MULTIWEAPONS_DAMAGE): session.getIntMod(Mod.LASERGUN_NORMAL_DAMAGE), (loc.getY() < hitP.getEyeLocation().getY()+0.25 && loc.getY() > hitP.getEyeLocation().getY()-0.25), (d > modifiers.getInt(Mod.MINIMAL_SNIPE_DISTANCE)), false);
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
			if(!w.hasCooldown(p)) {
				if(session.getMap().checkLocPlayerShootingFrom(p)) return;
//				if ((session.isSolo() && !session.getMap().withRandomSpawn()) | (session.isTeams() && session.getMap().withBaseSpawn())) {
//					for(Coordinate coord : session.getMap().getBaseCoords()) {
//						if(p.getLocation().distance(coord.getLocation()) < session.getMap().getProtectionRaduis()) {
//							p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED+""+ChatColor.BOLD+"You can't shoot while in a base!"));
//							session.getMap().drawBaseSphere(session.getMap().baseColor.get(coord), p);
//							return;
//						}
//					}
//				}

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


				boolean warned = false;
				for(double d = 0; d<6; d += 0.1) {
					if(d==0) {
						w.setCooldown(p);
					}

					for(int i = 0; i < 9; i++) {
						boolean inside = false;
						for (Coordinate coord : session.getMap().getBaseCoords()) {
							if (locs[i].distance(coord.getLocation()) < session.getMap().getProtectionRaduis()) inside = true;
						}
						if (!inside) {
							locs[i] = startLocs[i].add(dirs[i]);
							spawnProjectile(p, locs[i]);
						}
					}
					for (Location loc : locs) {
						if(session.getMap().checkPlayerLaserLoc(loc,p)) return;
					}
//					if ((session.isSolo() && !session.getMap().withRandomSpawn()) | (session.isTeams() && session.getMap().withBaseSpawn())) {
//						if (!warned) {
//							for (Coordinate coord : session.getMap().getBaseCoords()) {
//								for (Location loc : locs) {
//									if (loc.distance(coord.getLocation()) < session.getMap().getProtectionRaduis()) {
//										p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED+""+ChatColor.BOLD+"You can't shoot into a base!"));
//										session.getMap().drawBaseSphere(session.getMap().baseColor.get(coord), p);
//										warned = true;
//									}
//								}
//							}
//						}
//					}
					for(Player hitP : session.getPlayers()) {
						if(hitP != p && !alreadyKilledPlayers.contains(hitP)) {
							for(Location loc : locs) {
								if(isLaserInsideEntity(hitP, loc)) {
									if(Lasertag.isPlayerProtected(hitP)) {
										p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED+""+ChatColor.BOLD+"Player has spawnprotection"));
										BaseSphere.drawPlayerProtectionSphere(hitP);
										return;
									}
									boolean fromTeam = false;
									if(session.isTeams()) {
										for(SessionTeam team : session.getTeams()) {
											boolean pInTeam = false;
											for(Player tp : team.getPlayers())
												if (tp == p) {
													pInTeam = true;
													break;
												}
											if(pInTeam) {
												for(Player thp : team.getPlayers())
													if (thp == hitP) {
														fromTeam = true;
														break;
													}
											}
										}
									}
									if(!fromTeam) {
//										if(alreadyKilledPlayers.size() > 1) {
//											Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.minigames, new Runnable() {
//												@Override
//												public void run() {
//													String killedPlayersNames = "";
//													int i = 0;
//													for(Player kp : alreadyKilledPlayers) {
//														if(i == 0) killedPlayersNames += session.getPlayerColor(kp).getChatColor()+kp.getName();
//														else killedPlayersNames += ", "+session.getPlayerColor(kp).getChatColor()+kp.getName();
//														i++;
//													}
//													int points = modifiers.getInt(Mod.MULTIKILLS_EXTRA_POINTS)*alreadyKilledPlayers.size();
//													String pAddon = "";
//													if(points > 1) pAddon = "s";
//													for(Player ap : session.getPlayers()) {
//														ap.sendMessage("§e——————————————————");
//														ap.sendMessage(session.getPlayerColor(p).getChatColor()+p.getName()+" §dkilled "+killedPlayersNames+" §dwith one shot! §7(§a+"+points+" extra Point"+pAddon+"§7)");
//														ap.sendMessage("§e——————————————————");
//													}
//
//													session.addPoints(p, points);
//												}
//											}, 10);
//										}
										DeathListener.hit(HitType.SHOT, p, hitP, modifiers.getInt(Mod.SHOTGUN_DAMAGE), false, false, false);
										alreadyKilledPlayers.add(hitP);
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
			if(!w.hasCooldown(p)) {
				if(session.getMap().checkLocPlayerShootingFrom(p)) return;
//				if ((session.isSolo() && !session.getMap().withRandomSpawn()) | (session.isTeams() && session.getMap().withBaseSpawn())) {
//					for(Coordinate coord : session.getMap().getBaseCoords()) {
//						if(p.getLocation().distance(coord.getLocation()) < session.getMap().getProtectionRaduis()) {
//							p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED+""+ChatColor.BOLD+"You can't shoot while in a base!"));
//							session.getMap().drawBaseSphere(session.getMap().baseColor.get(coord), p);
//							return;
//						}
//					}
//				}

				p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_HURT, 10, 0);

				Location l1 = p.getLocation();
				l1.setY(l1.getY()+p.getHeight()-0.225);
				Vector direction = l1.getDirection();
				direction.multiply(1);
				Location loc = l1.clone().add(direction);
				
				for(double d = 0; d<100; d += 1) {
					if(d==0) {
						if(p.getInventory().getItem(2).getAmount() > 1) {
							p.getInventory().getItem(2).setAmount(p.getInventory().getItem(2).getAmount()-1);
						} else {
							w.setCooldown(p);
						}
						
//						if(playersSnipershots.get(p) == null) playersSnipershots.put(p, 0);
//						int shots = playersSnipershots.get(p);
//						if(shots == modifiers.getInt(Mod.SNIPER_AMMO_BEFORE_COOLDOWN)-1) {
//							Weapons.cooldownPlayer(p, Weapon.SNIPER, false);
//							playersSnipershots.put(p, 0);
//							p.getInventory().getItem(2).setAmount(1);
//						} else {
//							playersSnipershots.put(p, shots+1);
//							p.getInventory().getItem(2).setAmount(modifiers.getInt(Mod.SNIPER_AMMO_BEFORE_COOLDOWN)-1-shots);
//						}
						
					}
					loc = l1.add(direction);
					
					spawnProjectile(p, loc);

					if(session.getMap().checkPlayerLaserLoc(loc,p)) return;
//					if ((session.isSolo() && !session.getMap().withRandomSpawn()) | (session.isTeams() && session.getMap().withBaseSpawn())) {
//						for(Coordinate coord : session.getMap().getBaseCoords()) {
//							if(loc.distance(coord.getLocation()) < session.getMap().getProtectionRaduis()) {
//								p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED+""+ChatColor.BOLD+"You can't shoot into a base!"));
//								session.getMap().drawBaseSphere(session.getMap().baseColor.get(coord), p);
//								return;
//							}
//						}
//					}
					
					Player[] inGamePlayers = session.getPlayers();
					for(Player hitP : inGamePlayers) {
						if(hitP != p && !alreadyKilledPlayers.contains(hitP)) {
							if(isLaserInsideEntity(hitP, loc)) {
								if(Lasertag.isPlayerProtected(hitP)) {
									p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED+""+ChatColor.BOLD+"Player has spawnprotection"));
									BaseSphere.drawPlayerProtectionSphere(hitP);
									return;
								}
								boolean fromTeam = false;
								if(session.isTeams()) {
									for(SessionTeam team : session.getTeams()) {
										boolean pInTeam = false;
										for(Player tp : team.getPlayers())
											if (tp == p) {
												pInTeam = true;
												break;
											}
										if(pInTeam) {
											for(Player thp : team.getPlayers())
												if (thp == hitP) {
													fromTeam = true;
													break;
												}
										}
									}
								}
								if(!fromTeam) {
									DeathListener.hit(HitType.SHOT, p, hitP, modifiers.getInt(Mod.SNIPER_DAMAGE), (loc.getY() < hitP.getEyeLocation().getY()+0.25 && loc.getY() > hitP.getEyeLocation().getY()-0.25), (d > modifiers.getInt(Mod.MINIMAL_SNIPE_DISTANCE)), false);
									alreadyKilledPlayers.add(hitP);
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
	
	public static void colorPlayerHitBox(Player p) {
		HitBox hb = new HitBox(p);
		for(double x = hb.getMinX(); x <= hb.getMaxX(); x += 0.1) {
			for(double y = hb.getMinY(); y <= hb.getMaxY(); y += 0.1) {
				for(double z = hb.getMinZ(); z <= hb.getMaxZ(); z += 0.1) {
					spawnTestProjectile(p, new Location(p.getWorld(), x, y, z), Color.RED);
				}
			}
		}
	}
	
	
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

		double minX = p.getLocation().getX()-(width/2);
		double minY = p.getLocation().getY();
		double minZ = p.getLocation().getZ()-(width/2);
		double maxX = (p.getLocation().getX()-(width/2))+width;
		double maxY = p.getLocation().getY()+height;
		double maxZ = (p.getLocation().getZ()-(width/2))+width;
		
		if(minX <= x && x <= maxX) {
			if(minY <= y && y <= maxY) {
				return minZ <= z && z <= maxZ;
			}
		}
		return false;
	}
	
	public static boolean isInBlock(Session s, Location loc) {
		Block b = loc.getBlock();
		Material fm = b.getType();
		if(fm.name().contains("STAINED")) {
			b.breakNaturally();
//			b.setType(Material.AIR);
			loc.getWorld().playSound(loc, Sound.BLOCK_GLASS_BREAK, 1.1f, 1);
			Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.INSTANCE, () -> b.setType(fm), 20*5);
			return true;
		}
		boolean shootThroughEnabled = false;
		if(s != null) shootThroughEnabled= s.getBooleanMod(Mod.SHOOT_THROUGH_BLOCKS);
		if(!shootThroughEnabled) {
			Material m = Minigames.INSTANCE.world.getBlockAt(loc).getType();
			if(!m.isAir() && !m.name().contains("Fence")) {
				if(m.isSolid()) {
					if(Tag.SLABS.isTagged(m)) {
						if(b.getBlockData() instanceof Slab) {
							Slab slab = (Slab) b.getBlockData();
							if(slab.getType() == Type.BOTTOM) {
								return loc.getY() < b.getY() + 0.5;
							} else {
								return loc.getY() > b.getY() + 0.5;
							}
						}
					} else if(Tag.TRAPDOORS.isTagged(m)) {
						return !((TrapDoor) b.getBlockData()).isOpen();
					} else if(Tag.DOORS.isTagged(m)) {
						return !((Door) b.getBlockData()).isOpen();
					} else return true;
				}
			}
		}
		return false;
	}
	
	static Particle coloredLaser = Particle.REDSTONE;
	
	public static void spawnProjectile(Player p, Location loc) {
		Session session = Session.getPlayerSession(p);
		if(session.getPlayerColor(p).getColor() != Color.BLUE) p.getWorld().spawnParticle(Particle.REDSTONE, loc.getX(), loc.getY(), loc.getZ(), 0, 0, 0, 0, 1, new Particle.DustOptions(session.getPlayerColor(p).getColor(), 0.8f));
		else p.getWorld().spawnParticle(Particle.REDSTONE, loc.getX(), loc.getY(), loc.getZ(), 0, 0, 0, 0, 1, new Particle.DustOptions(Color.fromRGB(0, 183, 255), 0.8f));
	}
	
	
	
	
	public static void fireTest(Player p, Weapon w) throws NullPointerException{
		if(w == null) throw new NullPointerException("Weapon is null");
		switch (w) {
		case LASERGUN:
			if(!p.hasCooldown(Weapon.LASERGUN.getType())) {
				p.setCooldown(Weapon.LASERGUN.getType(), Mod.LASERGUN_COOLDOWN_TICKS.getOgInt());
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
			if(!p.hasCooldown(Weapon.SHOTGUN.getType())) {
				p.setCooldown(Weapon.SHOTGUN.getType(), Mod.SHOTGUN_COOLDOWN_TICKS.getOgInt());
				Location startLoc = p.getLocation();
				startLoc.setY(startLoc.getY()+p.getEyeHeight());
				p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_HURT, 1, 5);
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
			if(!p.hasCooldown(Weapon.SNIPER.getType())) {
				Location startLoc = p.getLocation();
				startLoc.setY(startLoc.getY()+p.getEyeHeight());
				p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_HURT, 1, 5);
				Vector direction1 = startLoc.getDirection();
				direction1.multiply(1);
				Location loc1 = startLoc.clone().add(direction1);

				for(double d = 0; d<100; d += 1) {
					
					try {
						if(d==0) {
							int s = p.getInventory().getItem(2).getAmount();
							if(s > 1) {
								p.getInventory().getItem(2).setAmount(s-1);
							} else {
								p.setCooldown(Weapon.SNIPER.getType(), Mod.SNIPER_COOLDOWN_TICKS.getOgInt());
								p.getInventory().getItem(2).setAmount(1);
								new BukkitRunnable() {
									@Override
									public void run() {
										p.getInventory().getItem(2).setAmount(Mod.SNIPER_AMMO_BEFORE_COOLDOWN.getOgInt());
									}
								}.runTaskLater(Minigames.INSTANCE, Mod.SNIPER_COOLDOWN_TICKS.getOgInt());
							}
							
							
//							if(playersSnipershots.get(p) == null) playersSnipershots.put(p, 0);
//							int shots = playersSnipershots.get(p);
//							if(shots == Mod.SNIPER_AMMO_BEFORE_COOLDOWN.getOgInt()-1) {
//								Weapons.cooldownPlayer(p, Weapon.SNIPER, true);
//								playersSnipershots.put(p, 0);
//								p.getInventory().getItem(2).setAmount(1);
//							} else {
//								playersSnipershots.put(p, shots+1);
//								p.getInventory().getItem(2).setAmount((Mod.SNIPER_AMMO_BEFORE_COOLDOWN.getOgInt()-1)-shots);
//							}

						}
						
						loc1 = startLoc.add(direction1);
						spawnTestProjectile(p, loc1, Color.PURPLE);
						
						if(!checkloc(p, loc1)) return;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			break;
		default:
			
			break;
		}
			
	}
	
	private static boolean checkloc(Player p, Location loc) {
		if(!Lasertag.getTestAera().isInside(loc)) return false;
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

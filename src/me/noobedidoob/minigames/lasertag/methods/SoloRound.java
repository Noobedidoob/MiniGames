package me.noobedidoob.minigames.lasertag.methods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Difficulty;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.Lasertag.LtColorNames;
import me.noobedidoob.minigames.lasertag.listeners.DeathListener;
import me.noobedidoob.minigames.main.Minigames;

public class SoloRound {
	
	@SuppressWarnings("deprecation")
	public static void register() {
		Lasertag.everybodyReady = false;
		
//		if(Game.map().getName().equals("bridges")) {
//			int y = 15;
//			for(int x = Game.map().getGatherArea().getMinX(); x < Game.map().getGatherArea().getMaxX(); x++) {
//				for(int z = Game.map().getGatherArea().getMinZ(); z < Game.map().getGatherArea().getMaxZ(); z++) {
//					Minigames.world.getBlockAt(x, y, z).setType(Material.AIR);
//				}
//			}
//		}
		
		int c = 0;
		for(Player p : Game.players()) {
			LaserShooter.playersSnipershots.put(p, 0);
			p.setLevel(0);
//			Weapons.playerAmmo.put(p, Modifiers.bulletsInMagazine);
//			Weapons.playerMinigunAmmo.put(p, Modifiers.minigunAmmo);
//			Weapons.playerMinigunCooldown.put(p, false);
			Weapons.hasChoosenWeapon.put(p, false);
			
			Game.setPlayerColor(p, LtColorNames.values()[c]);
			org.bukkit.scoreboard.Team t = Leaderboard.board.registerNewTeam(Leaderboard.randomName());
			t.setColor(Game.getPlayerColor(p).getChatColor());
			t.addPlayer(p);
			p.getInventory().clear();
			
			ItemStack lasergun = Weapons.lasergunItem;
			ItemMeta lasergunMeta = lasergun.getItemMeta();
			lasergunMeta.setDisplayName(Game.getPlayerColor(p).getChatColor()+"§lLasergun");
			lasergun.setItemMeta(lasergunMeta);
			p.getInventory().addItem(lasergun);

			p.sendTitle("§aStarting Lasertag!", "Prepare yourself!", 20, 20*4, 20);
			if(c < 7) c++;
			else c = 0;
		}
		Leaderboard.refreshScoreboard();
	}
	
	public static void start() {
//		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "fill 357 20 -85 330 20 -58 minecraft:air");
		Game.start();
		for(Player p : Game.players()) {
			p.sendMessage("§aStarting Lasertag!");
			p.setGameMode(GameMode.ADVENTURE);
			p.getWorld().setDifficulty(Difficulty.PEACEFUL);
			p.teleport(PlayerTeleporter.getPlayerSpawnLoc(p));
			p.sendTitle("§l§aGo!", "", 5, 20, 5);
			Lasertag.isProtected.put(p, true);
			Lasertag.playerTesting.put(p, false);
			DeathListener.streakedPlayers.put(p, 0);
		}
		Lasertag.timeCountdownTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Lasertag.minigames, new Runnable() {
			@Override
			public void run() {
				if(Leaderboard.time >= 0) {
					Leaderboard.refreshScoreboard();
					if(Leaderboard.time <= 5) {
						if(Leaderboard.time > 0) {
							for(Player p : Game.players()) {
								p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 0.529732f);
								p.sendTitle("", "§c"+Leaderboard.time, 5, 20, 5);
							}
						}
					}
					Leaderboard.time--;
				} else {
					Bukkit.getScheduler().cancelTask(Lasertag.timeCountdownTask);
					stop(false);
				}
			}
		}, 0, 20);
	}
	public static void cancel() {
		Game.reset();
		for(Player p : Game.players()) {
			p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		}
	}
	public static void stop(boolean externalStop) {
		Modifiers.setModifier("multiweapons", false);
		Weapons.registerWeapons();
		if(externalStop) {
			Bukkit.getScheduler().cancelTask(Lasertag.timeCountdownTask);
			try {
				for(Player p : Game.players()) {
					p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
					p.teleport(Minigames.spawn);
					PlayerZoomer.zoomPlayerOut(p);
				}
			} catch (Exception e) {
			}
			Game.reset();
		} else {
			Player[] ingamePlayers = Game.players();
			Bukkit.getScheduler().scheduleSyncDelayedTask(Lasertag.minigames, new Runnable() {
				@Override
				public void run() {
					for(Player p : ingamePlayers) {
						p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
					}
				}
			}, 20*10);
			List<Player> winners = new ArrayList<Player>();
			int amount = 0;
			for(Player p : Game.players()) {
				if(Game.getPlayerPoints(p) > amount) {
					amount = Game.getPlayerPoints(p);
					winners = new ArrayList<Player>();
					winners.add(p);
				} else if(Game.getPlayerPoints(p) == amount) {
					winners.add(p);
				}
			}
			String winnerTeamsString = "";
			int i = 0;
			for(Player p : winners) {
				if(winners.size() > 2) {
					if(i == 0) winnerTeamsString = p.getName();
					else if(i < winners.size()) winnerTeamsString += ", "+p.getName();
					else winnerTeamsString += " and "+p.getName();
				} else if(winners.size() == 2) {
					if(i == 0) winnerTeamsString = p.getName();
					else if(i == 1) winnerTeamsString += " and "+p.getName();
				} else {
					winnerTeamsString = p.getName();
				}
				i++;
			}
			
			
			HashMap<Integer, List<Player>> playersInSorted = new HashMap<Integer, List<Player>>();
			
			int maxScore = 0;
			for(Player p : Game.players()) {
				List<Player> newList = new ArrayList<Player>();
				if(playersInSorted.get(Game.getPlayerPoints(p)) == null) {
					newList.add(p);
					playersInSorted.put(Game.getPlayerPoints(p), newList);
				} else {
					newList = playersInSorted.get(Game.getPlayerPoints(p));
					newList.add(p);
					playersInSorted.put(Game.getPlayerPoints(p), newList);
				}
				if(Game.getPlayerPoints(p) > maxScore) maxScore = Game.getPlayerPoints(p);
			}
			List<Player[]> sortedInRanks = new ArrayList<Player[]>();
			for(int c = maxScore; c >= 0; c--) {
				if(playersInSorted.get(c) != null) {
					List<Player> rankList = playersInSorted.get(c);
					Player[] rankArray = new Player[rankList.size()];
					rankArray = rankList.toArray(rankArray);
					sortedInRanks.add(rankArray);
				}
			}
			String leaderboardString = "";
			int r = 1;
			for(Player[] rank : sortedInRanks) {
				leaderboardString += "§r"+r+". ";
				
				if(rank.length > 1) {
					for(Player p : rank) {
						leaderboardString += Game.getPlayerColor(p).getChatColor()+p.getName()+"§7, ";
					}
					leaderboardString = leaderboardString.substring(0, leaderboardString.length()-2) + " §7(§d"+Game.getPlayerPoints(rank[0])+"§7)\n";
				} else {
					leaderboardString += Game.getPlayerColor(rank[0]).getChatColor()+rank[0].getName()+" §7(§d"+Game.getPlayerPoints(rank[0])+"§7)\n";
				}
				r++;
			}
			
			for(Player p : Game.players()) {
				PlayerZoomer.zoomPlayerOut(p);
				p.setWalkSpeed(0.2f);
				p.getInventory().setHelmet(new ItemStack(Material.AIR));
				p.sendMessage("\n§7—————§a§lPoints§r§7—————");
				p.sendMessage(leaderboardString);
				p.sendMessage("§7——————————————\n");
				PlayerTeleporter.gatherPlayers(winners);
				for(Player w : winners) {
					for(int t = 0; t < 5; t++) {
						if(p.hasPotionEffect(Lasertag.glowingEffect)) p.removePotionEffect(Lasertag.glowingEffect);
						Bukkit.getScheduler().scheduleSyncDelayedTask(Lasertag.minigames, new Runnable() {
							@Override
							public void run() {
								Firework fw = (Firework) Minigames.world.spawnEntity(w.getLocation(), EntityType.FIREWORK);
								FireworkMeta fwm = fw.getFireworkMeta();
								FireworkEffect fwe = FireworkEffect.builder().flicker(true).withColor(Color.GREEN).with(Type.BALL).trail(true).build();
								fwm.addEffect(fwe);
								fwm.setPower(1);
								fw.setFireworkMeta(fwm);
							}
						}, 60*t);
					}
				}
				
				p.sendTitle("§a"+winnerTeamsString+" §awon", "§aScore: "+amount, 20, 20*5, 20);
			}
		}
		
		
		for(org.bukkit.scoreboard.Team t : Leaderboard.board.getTeams()) {
			t.unregister();
		}
		
		Modifiers.withEvents = false;
		Modifiers.points = 1;
		Modifiers.snipeShotsExtra = 1;
		Modifiers.minSnipeDistance = 35;
		Modifiers.closeRangeExtra = 0;
		Modifiers.pvpExtra = 0;
		Modifiers.headshotExtra = 1;
		Modifiers.shootThroughBlocks = false;
		Modifiers.highLightPlayers = false;
		Modifiers.glowingAmplifier = 200;
		Modifiers.widthAddon = 0;
		Modifiers.heightAddon = 0;
		Modifiers.withAmmo = false;
		Modifiers.bulletsInMagazine = 50;
		Modifiers.magazinReloadTime = 20;
		Modifiers.lasergunCooldown = 12;
		Modifiers.sniperCooldown = 100;
		Modifiers.shotgunCooldown = 40;
		Modifiers.withMinigun = false;
		
		Game.reset();
	}
}

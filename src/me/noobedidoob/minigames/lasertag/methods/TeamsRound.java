package me.noobedidoob.minigames.lasertag.methods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import org.bukkit.inventory.meta.LeatherArmorMeta;

import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.Lasertag.LtColorNames;
import me.noobedidoob.minigames.main.Minigames;
import me.noobedidoob.minigames.utils.LasertagColor;

public class TeamsRound {
	@SuppressWarnings("deprecation")
	public static void register(List<Player[]> teamsList) {
		Lasertag.everybodyReady = false;
		
//		if(Game.map().getName().equals("bridges")) {
//			int y = 15;
//			for(int x = Game.map().getGatherArea().getMinX(); x < Game.map().getGatherArea().getMaxX(); x++) {
//				for(int z = Game.map().getGatherArea().getMinZ(); z < Game.map().getGatherArea().getMaxZ(); z++) {
//					Minigames.world.getBlockAt(x, y, z).setType(Material.AIR);
//				}
//			}
//		}
		
		
		for(org.bukkit.scoreboard.Team ut : Leaderboard.board.getTeams()) ut.unregister();
		int c = 0;
		for(Player[] team : teamsList) {
			LtColorNames teamColor = LtColorNames.values()[c];
			Game.addTeam(team, teamColor);
			ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
			ItemStack leggins = new ItemStack(Material.LEATHER_LEGGINGS);
			ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
			LeatherArmorMeta armourItemMeta = (LeatherArmorMeta) chestplate.getItemMeta();
			armourItemMeta.setUnbreakable(true);
			armourItemMeta.setColor(new LasertagColor(teamColor).getColor());
			chestplate.setItemMeta(armourItemMeta);
			leggins.setItemMeta(armourItemMeta);
			boots.setItemMeta(armourItemMeta);
			ItemStack teamLasergun = Weapons.lasergunItem;
			ItemMeta teamLasergunMeta = teamLasergun.getItemMeta();
			teamLasergunMeta.setDisplayName(Game.getTeamColor(team).getChatColor()+"§lLasergun");
			teamLasergun.setItemMeta(teamLasergunMeta);
			org.bukkit.scoreboard.Team t = Leaderboard.board.registerNewTeam(Leaderboard.randomName());
			t.setColor(Game.getTeamColor(team).getChatColor());
			for(Player p : team) {
				LaserShooter.playersSnipershots.put(p, 0);
//				Weapons.playerAmmo.put(p, Modifiers.bulletsInMagazine);
//				Weapons.playerMinigunAmmo.put(p, Modifiers.minigunAmmo);
//				Weapons.playerMinigunCooldown.put(p, false);
				t.addPlayer(p);
				p.sendTitle("§aStarting Lasertag!", "Prepare yourself!", 20, 20*4, 20);
				p.setLevel(0);
//				Weapons.playerCoolingdown.put(p, false);
				p.getInventory().clear();
				p.getInventory().setItem(0,teamLasergun);
				p.getInventory().setChestplate(chestplate);
				p.getInventory().setLeggings(leggins);
				p.getInventory().setBoots(boots);
				Weapons.hasChoosenWeapon.put(p, false);
			}
			if(c < 7) c++;
			else c = 0;
		}
		Leaderboard.refreshScoreboard();
	}
	public static void cancel() {
		Game.reset();
		for(Player p : Game.players()) {
			p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		}
	}
	public static void start() {
//		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "fill 357 20 -85 330 20 -58 minecraft:air");
		Game.start();
		for(Player p : Game.players()) {
			p.sendTitle("§a§lGo!", "Kill the players of the other teams", 20, 20*4, 20);
			p.setGameMode(GameMode.ADVENTURE);
			p.getWorld().setDifficulty(Difficulty.PEACEFUL);
			p.teleport(PlayerTeleporter.getPlayerSpawnLoc(p));
			Lasertag.isProtected.put(p, true);
			p.sendTitle("§l§aGo!", "", 5, 20, 5);
		}
		Lasertag.timeCountdownTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Lasertag.minigames, new Runnable() {
			@Override
			public void run() {
				if(Leaderboard.time >= 0) {
					Leaderboard.refreshScoreboard();
					if(Leaderboard.time <= 5) {
						for(Player p : Game.players()) {
							p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 0.529732f);
							p.sendTitle("§c"+Leaderboard.time, "", 5, 20, 5);
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
	public static void stop(boolean externalStop) {
		Modifiers.setModifier("multiweapons", false);
		Weapons.registerWeapons();
		try {
			for(Player p : Game.players()) {
				p.setDisplayName(p.getName());
				p.getInventory().clear();
				PlayerZoomer.zoomPlayerOut(p);
			}
		} catch (NullPointerException npe) {
			
		}
		if(externalStop) {
			Bukkit.getScheduler().cancelTask(Lasertag.timeCountdownTask);
			try {
				for(Player p : Game.players()) {
					p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
					p.sendMessage("§cStopped the game!");
					p.teleport(Minigames.spawn);
				}
				Game.reset();
			} catch (NullPointerException nExp) {Game.reset();}
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
			
			
			List<Player[]> winnerTeams = new ArrayList<Player[]>();
			int amount = 0;
			for(Player[] team : Game.getTeams()) {
				if(Game.getTeamPoints(team) > amount) {
					amount = Game.getTeamPoints(team);
					winnerTeams = new ArrayList<Player[]>();
					winnerTeams.add(team);
				} else if(Game.getTeamPoints(team) == amount) {
					winnerTeams.add(team);
				}
			}
			String winnerTeamsString = "";
			int i = 0;
			for(Player[] team : winnerTeams) {
				if(winnerTeams.size() > 2) {
					if(i == 0) winnerTeamsString = Game.getTeamColor(team).getChatColor()+"Team "+Game.getTeamColor(team).getName();
					else if(i < winnerTeams.size()) winnerTeamsString += "§r, "+Game.getTeamColor(team).getChatColor()+"Team "+Game.getTeamColor(team).getName();
					else winnerTeamsString += "§r and "+Game.getTeamColor(team).getChatColor()+"Team "+Game.getTeamColor(team).getName();
				} else if(winnerTeams.size() == 2) {
					if(i == 0) winnerTeamsString = Game.getTeamColor(team).getChatColor()+"Team "+Game.getTeamColor(team).getName();
					else if(i == 1) winnerTeamsString += " §rand "+Game.getTeamColor(team).getChatColor()+"Team "+Game.getTeamColor(team).getName();
				} else {
					winnerTeamsString = Game.getTeamColor(team).getChatColor()+"Team "+Game.getTeamColor(team).getName();
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
					leaderboardString += Game.getPlayerColor(rank[0]).getChatColor()+" §7(§d"+Game.getPlayerPoints(rank[0])+"§7)\n";
				}
				r++;
			}
			
			
			HashMap<Integer, List<Player[]>> teamsInSorted = new HashMap<Integer, List<Player[]>>();
			
			int maxTeamScore = 0;
			for(Player[] team : Game.getTeams()) {
				List<Player[]> newList = new ArrayList<Player[]>();
				if(teamsInSorted.get(Game.getTeamPoints(team)) == null) {
					newList.add(team);
					teamsInSorted.put(Game.getTeamPoints(team), newList);
				} else {
					newList = teamsInSorted.get(Game.getTeamPoints(team));
					newList.add(team);
					teamsInSorted.put(Game.getTeamPoints(team), newList);
				}
				if(Game.getTeamPoints(team) > maxTeamScore) maxTeamScore = Game.getTeamPoints(team);
			}
			List<List<Player[]>> teamsSortedInRanks = new ArrayList<List<Player[]>>();
			for(int c = maxTeamScore; c >= 0; c--) {
				if(teamsInSorted.get(c) != null) {
					List<Player[]> rankList = teamsInSorted.get(c);
					teamsSortedInRanks.add(rankList);
				}
			}
			
			String teamLeaderboardString = "";
			int tr = 1;
			for(List<Player[]> rankTeamList : teamsSortedInRanks) {
				teamLeaderboardString += "§7"+tr+". ";
				for(Player[] team : rankTeamList) {
					String teamName = Game.getTeamColor(team).getName();
					String teamNameColor = Game.getTeamColor(team).getName().toUpperCase().replace("ORANGE", "GOLD");
					if(tr < rankTeamList.size()-1) teamLeaderboardString += ChatColor.valueOf(teamNameColor)+teamName+" Team, ";
					else teamLeaderboardString += ChatColor.valueOf(teamNameColor)+teamName+" Team §7(§a"+Game.getTeamPoints(team)+"§7)\n";
				}
				tr++;
			}
			
			List<Player> winners = new ArrayList<Player>();
			for(Player[] winnerteam : winnerTeams) {
				for(Player winner : winnerteam) {
					winners.add(winner);
				}
			}
			
			for(Player p : Game.players()) {
				p.setWalkSpeed(0.2f);
				p.getInventory().setHelmet(new ItemStack(Material.AIR));
				PlayerTeleporter.gatherPlayers(winners);
				for(Player[] team : winnerTeams) {
					for(Player w : team) {
						if(p.hasPotionEffect(Lasertag.glowingEffect)) p.removePotionEffect(Lasertag.glowingEffect);
						for(int t = 0; t < 4; t++) {
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
				}
				p.sendTitle(winnerTeamsString+" §rwon", "Score: §d"+amount+"\n§rBest Player: ", 20, 20*5, 20);
				p.sendMessage("\n§7——————§a§lPoints§r§7——————");
				p.sendMessage("§l§aTeam Score:");
				p.sendMessage(teamLeaderboardString);
				p.sendMessage("\n§l§aPlayer Score:");
				p.sendMessage(leaderboardString);
				p.sendMessage("§7————————————————\n");
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
		LaserShooter.withMultiWeapons = false;
	}
	
}

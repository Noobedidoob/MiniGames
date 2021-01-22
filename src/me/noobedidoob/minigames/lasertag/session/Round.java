package me.noobedidoob.minigames.lasertag.session;

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
import me.noobedidoob.minigames.lasertag.listeners.DeathListener;
import me.noobedidoob.minigames.lasertag.methods.LaserShooter;
import me.noobedidoob.minigames.lasertag.methods.PlayerTeleporter;
import me.noobedidoob.minigames.lasertag.methods.PlayerZoomer;
import me.noobedidoob.minigames.lasertag.methods.Weapons;
import me.noobedidoob.minigames.main.Minigames;
import me.noobedidoob.minigames.utils.LasertagColor;
import me.noobedidoob.minigames.utils.LasertagColor.LtColorNames;
import me.noobedidoob.minigames.utils.MgUtils.TimeFormat;
import me.noobedidoob.minigames.utils.Team;

public class Round {
	
	private Session session;
	private Scoreboard scoreboard;
	public Round(Session session, Scoreboard scoreboard) {
		this.session = session;
		this.scoreboard = scoreboard;
	}
	
	

	private boolean tagging = false;
	public boolean tagging() {
		return tagging;
	}
	public void setTagging(boolean tagging) {
		this.tagging = tagging;
	}
	
	
	public void start() {
		tagging = true;
		refreshPlayerTeams();
		preparePlayers();
		for(Player p : session.getPlayers()) {
			p.sendTitle("ｧaｧlGo!", "Kill the players of the other teams", 20, 20*4, 20);
			p.setGameMode(GameMode.ADVENTURE);
			p.getWorld().setDifficulty(Difficulty.PEACEFUL);
			p.teleport(PlayerTeleporter.getPlayerSpawnLoc(p));
			Lasertag.isProtected.put(p, true);
			p.sendTitle("ｧlｧaGo!", "", 5, 20, 5);
			Lasertag.playerTesting.put(p, false);
			DeathListener.streakedPlayers.put(p, 0);
		}
		Lasertag.timeCountdownTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Lasertag.minigames, new Runnable() {
			@Override
			public void run() {
				if(session.getTime(TimeFormat.SECONDS) > 0) {
					if(session.getTime(TimeFormat.SECONDS) <= 5 && session.getTime(TimeFormat.SECONDS) > 0) {
						for(Player p : session.getPlayers()) {
							p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 0.529732f);
							p.sendTitle("ｧc"+session.getTime(TimeFormat.SECONDS), "", 5, 20, 5);
						}
					}
					session.setTime(session.getTime(TimeFormat.SECONDS)-1, TimeFormat.SECONDS, false);
					scoreboard.refresh();
				} else {
					Bukkit.getScheduler().cancelTask(Lasertag.timeCountdownTask);
					stop(false);
				}
			}
		}, 0, 20);
	}
	
	
	@SuppressWarnings("deprecation")
	public void refreshPlayerTeams() {
		if (session.isSolo()) {
			int c = 0;
			for (Player p : session.getPlayers()) {
				session.setPlayerColor(p, LtColorNames.values()[c]);
				org.bukkit.scoreboard.Team t = session.scoreboard.board.registerNewTeam(p.getName());
				t.setColor(session.getPlayerColor(p).getChatColor());
				t.addPlayer(p);
				if (c < 7) c++;
				else c = 0;
			} 
		} else {
			for(org.bukkit.scoreboard.Team ut : scoreboard.board.getTeams()) ut.unregister();
			int c = 0;
			for(Team team : session.getTeams()) {
				LtColorNames teamColor = LtColorNames.values()[c];
				org.bukkit.scoreboard.Team t = scoreboard.board.registerNewTeam(teamColor.name());
				t.setColor(session.getTeamColor(team).getChatColor());
				for(Player p : team.getPlayers()) {
					t.addPlayer(p);
					session.setPlayerColor(p, teamColor);
				}
				if(c < 7) c++;
				else c = 0;
			}
		}
	}
	public void preparePlayers() {
		if (session.isSolo()) {
			int c = 0;
			for(Player p : session.getPlayers()) {
				p.getInventory().clear();
				
				ItemStack lasergun = Weapons.lasergunItem;
				ItemMeta lasergunMeta = lasergun.getItemMeta();
				lasergunMeta.setDisplayName(session.getPlayerColor(p).getChatColor()+"ｧlLasergun #"+(session.getPlayerColor(p).getOrdinal()+1));
				lasergun.setItemMeta(lasergunMeta);
				p.getInventory().addItem(lasergun);
				if(c < 7) c++;
				else c = 0;
			}
		} else {
			for(org.bukkit.scoreboard.Team ut : scoreboard.board.getTeams()) ut.unregister();
			int c = 0;
			for(Team team : session.getTeams()) {
				LtColorNames teamColor = LtColorNames.values()[c];
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
				teamLasergunMeta.setDisplayName(session.getTeamColor(team).getChatColor()+"ｧlLasergun #"+(teamColor.ordinal()+1));
				teamLasergun.setItemMeta(teamLasergunMeta);
				for(Player p : team.getPlayers()) {
					p.getInventory().clear();
					p.getInventory().setItem(0,teamLasergun);
					p.getInventory().setChestplate(chestplate);
					p.getInventory().setLeggings(leggins);
					p.getInventory().setBoots(boots);
				}
				if(c < 7) c++;
				else c = 0;
			}
		}
		
		Lasertag.everybodyReady = false;
		for(Player p : session.getPlayers()) {
			Weapons.hasChoosenWeapon.put(p, false);
			LaserShooter.playersSnipershots.put(p, 0);
		}
	}
	
	
	
	public void stop(boolean externalStop) {
		tagging = false;
		Weapons.registerWeapons();
		if(externalStop) {
			Bukkit.getScheduler().cancelTask(Lasertag.timeCountdownTask);
			for(Player p : session.getPlayers()) { 
				p.sendTitle("ｧcStopped the game!","",20, 20*4, 20);
				p.teleport(Minigames.spawn);
				session.refreshPlayersLobbyInvs();
			}
		} else {
			if(session.isSolo()) evaluateSolo();
			else evaluateTeams();
		}
		

		try {
			for(Player p : session.getPlayers()) {
				p.setDisplayName(p.getName());
				p.getInventory().clear();
				PlayerZoomer.zoomPlayerOut(p);
				p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
				p.teleport(Minigames.spawn);
			}
		} catch (NullPointerException npe) { }
		for(org.bukkit.scoreboard.Team t : scoreboard.board.getTeams()) {
			t.unregister();
		}
		
	}
	
	
	
	
	
//	@SuppressWarnings("deprecation")
//	public static void registerSolo() {
//		int c = 0;
//		for(Player p : session.getPlayers()) {
//			sessionsetPlayerColor(p, LtColorNames.values()[c]);
//			org.bukkit.scoreboard.Team t = scoreboard.board.registerNewTeam(scoreboard.randomName());
//			t.setColor(session.getPlayerColor(p).getChatColor());
//			t.addPlayer(p);
//			p.getInventory().clear();
//			
//			ItemStack lasergun = Weapons.lasergunItem;
//			ItemMeta lasergunMeta = lasergun.getItemMeta();
//			lasergunMeta.setDisplayName(session.getPlayerColor(p).getChatColor()+"ｧlLasergun #"+(session.getPlayerColor(p).getOrdinal()+1));
//			lasergun.setItemMeta(lasergunMeta);
//			p.getInventory().addItem(lasergun);
//			if(c < 7) c++;
//			else c = 0;
//		}
//		
//		registerPlayers();
//	}
//	@SuppressWarnings("deprecation")
//	public static void registerTeams(List<Player[]> teamsList) {
//		for(Team ut : scoreboard.board.getTeams()) ut.unregister();
//		int c = 0;
//		for(Player[] team : teamsList) {
//			LtColorNames teamColor = LtColorNames.values()[c];
//			sessionaddTeam(team, teamColor);
//			ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
//			ItemStack leggins = new ItemStack(Material.LEATHER_LEGGINGS);
//			ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
//			LeatherArmorMeta armourItemMeta = (LeatherArmorMeta) chestplate.getItemMeta();
//			armourItemMeta.setUnbreakable(true);
//			armourItemMeta.setColor(new LasertagColor(teamColor).getColor());
//			chestplate.setItemMeta(armourItemMeta);
//			leggins.setItemMeta(armourItemMeta);
//			boots.setItemMeta(armourItemMeta);
//			ItemStack teamLasergun = Weapons.lasergunItem;
//			ItemMeta teamLasergunMeta = teamLasergun.getItemMeta();
//			teamLasergunMeta.setDisplayName(session.getTeamColor(team).getChatColor()+"ｧlLasergun #"+(teamColor.ordinal()+1));
//			teamLasergun.setItemMeta(teamLasergunMeta);
//			org.bukkit.scoreboard.Team t = scoreboard.board.registerNewTeam(scoreboard.randomName());
//			t.setColor(session.getTeamColor(team).getChatColor());
//			for(Player p : team) {
//				t.addPlayer(p);
//				p.getInventory().clear();
//				p.getInventory().setItem(0,teamLasergun);
//				p.getInventory().setChestplate(chestplate);
//				p.getInventory().setLeggings(leggins);
//				p.getInventory().setBoots(boots);
//			}
//			if(c < 7) c++;
//			else c = 0;
//		}
//		
//		registerPlayers();
//	}
	
//	private static void registerPlayers() {
//		Lasertag.everybodyReady = false;
//		for(Player p : session.getPlayers()) {
//			p.setLevel(0);
//			Weapons.hasChoosenWeapon.put(p, false);
//			LaserShooter.playersSnipershots.put(p, 0);
//			p.sendTitle("ｧaStarting Lasertag!", "Prepare yourself!", 20, 20*4, 20);
//		}
//		scoreboard.refreshScoreboard();
//	}
	
//	public static void cancel() {
//		sessionreset();
//		for(Player p : session.getPlayers()) {
//			p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
//		}
//	}
	
//	public static void start() {
////		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "fill 357 20 -85 330 20 -58 minecraft:air");
//		sessionstart();
//		for(Player p : session.getPlayers()) {
//			p.sendTitle("ｧaｧlGo!", "Kill the players of the other teams", 20, 20*4, 20);
//			p.setGameMode(GameMode.ADVENTURE);
//			p.getWorld().setDifficulty(Difficulty.PEACEFUL);
//			p.teleport(PlayerTeleporter.getPlayerSpawnLoc(p));
//			Lasertag.isProtected.put(p, true);
//			p.sendTitle("ｧlｧaGo!", "", 5, 20, 5);
//			Lasertag.playerTesting.put(p, false);
//			DeathListener.streakedPlayers.put(p, 0);
//		}
//		Lasertag.timeCountdownTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Lasertag.minigames, new Runnable() {
//			@Override
//			public void run() {
//				if(scoreboard.time >= 0) {
//					scoreboard.refreshScoreboard();
//					if(scoreboard.time <= 5 && scoreboard.time > 0) {
//						for(Player p : session.getPlayers()) {
//							p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 0.529732f);
//							p.sendTitle("ｧc"+scoreboard.time, "", 5, 20, 5);
//						}
//					}
//					scoreboard.time--;
//				} else {
//					Bukkit.getScheduler().cancelTask(Lasertag.timeCountdownTask);
//					stop(false);
//				}
//			}
//		}, 0, 20);
//	}
	
//	public static void stop(boolean externalStop) {
//		Weapons.registerWeapons();
//		if(externalStop) {
//			Bukkit.getScheduler().cancelTask(Lasertag.timeCountdownTask);
//			for(Player p : session.getPlayers()) { 
//				p.sendTitle("ｧcStopped the game!","",20, 20*4, 20);
//				p.teleport(Minigames.spawn);
//			}
//			sessionreset();
//		} else {
//			if(sessionsolo()) evaluateSolo();
//			else evaluateTeams();
//		}
//		
//
//		try {
//			for(Player p : session.getPlayers()) {
//				p.setDisplayName(p.getName());
//				p.getInventory().clear();
//				PlayerZoomer.zoomPlayerOut(p);
//				p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
//				p.teleport(Minigames.spawn);
//			}
//		} catch (NullPointerException npe) { }
//		for(org.bukkit.scoreboard.Team t : scoreboard.board.getTeams()) {
//			t.unregister();
//		}
//		
//		sessionreset();
//	}
	
	private void evaluateSolo() {

		Bukkit.getScheduler().scheduleSyncDelayedTask(Lasertag.minigames, new Runnable() {
			@Override
			public void run() {
				for(Player p : session.getPlayers()) {
					p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
				}
			}
		}, 20*10);
		List<Player> winners = new ArrayList<Player>();
		int amount = 0;
		for(Player p : session.getPlayers()) {
			if(session.getPlayerPoints(p) > amount) {
				amount = session.getPlayerPoints(p);
				winners = new ArrayList<Player>();
				winners.add(p);
			} else if(session.getPlayerPoints(p) == amount) {
				winners.add(p);
			}
		}
		String winnerTeamsString = "";
		int i = 0;
		for(Player p : winners) {
			if(winners.size() > 2) {
				if(i == 0) winnerTeamsString = p.getName();
				else if(i < winners.size()) winnerTeamsString += "ｧa, ｧd"+p.getName();
				else winnerTeamsString += " ｧaand ｧd"+p.getName();
			} else if(winners.size() == 2) {
				if(i == 0) winnerTeamsString = p.getName();
				else if(i == 1) winnerTeamsString += " ｧaand ｧd"+p.getName();
			} else {
				winnerTeamsString = p.getName();
			}
			i++;
		}
		
		
		HashMap<Integer, List<Player>> playersInSorted = new HashMap<Integer, List<Player>>();
		
		int maxScore = 0;
		for(Player p : session.getPlayers()) {
			List<Player> newList = new ArrayList<Player>();
			if(playersInSorted.get(session.getPlayerPoints(p)) == null) {
				newList.add(p);
				playersInSorted.put(session.getPlayerPoints(p), newList);
			} else {
				newList = playersInSorted.get(session.getPlayerPoints(p));
				newList.add(p);
				playersInSorted.put(session.getPlayerPoints(p), newList);
			}
			if(session.getPlayerPoints(p) > maxScore) maxScore = session.getPlayerPoints(p);
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
			leaderboardString += "ｧr"+r+". ";
			
			if(rank.length > 1) {
				for(Player p : rank) {
					leaderboardString += session.getPlayerColor(p).getChatColor()+p.getName()+"ｧ7, ";
				}
				leaderboardString = leaderboardString.substring(0, leaderboardString.length()-2) + " ｧ7(ｧd"+session.getPlayerPoints(rank[0])+"ｧ7)\n";
			} else {
				leaderboardString += session.getPlayerColor(rank[0]).getChatColor()+rank[0].getName()+" ｧ7(ｧd"+session.getPlayerPoints(rank[0])+"ｧ7)\n";
			}
			r++;
		}
		
		for(Player p : session.getPlayers()) {
			PlayerZoomer.zoomPlayerOut(p);
			p.sendMessage("\nｧ7覧覧立aｧlPointsｧrｧ7覧覧�");
			p.sendMessage(leaderboardString);
			p.sendMessage("ｧ7覧覧覧覧覧覧覧\n");
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
			
			p.sendTitle("ｧb"+winnerTeamsString+" ｧawon", "ｧaScore: ｧd"+amount, 20, 20*5, 20);
		}
	
	}
	
	private void evaluateTeams() {
		List<Team> winnerTeams = new ArrayList<Team>();
		int amount = 0;
		for(Team team : session.getTeams()) {
			if(team.getPoints() > amount) {
				amount = session.getTeamPoints(team);
				winnerTeams = new ArrayList<Team>();
				winnerTeams.add(team);
			} else if(team.getPoints() == amount) {
				winnerTeams.add(team);
			}
		}
		String winnerTeamsString = "";
		int i = 0;
		for(Team team : winnerTeams) {
			if(winnerTeams.size() > 2) {
				if(i == 0) winnerTeamsString = session.getTeamColor(team).getChatColor()+"Team "+session.getTeamColor(team).getName();
				else if(i < winnerTeams.size()) winnerTeamsString += "ｧr, "+session.getTeamColor(team).getChatColor()+"Team "+session.getTeamColor(team).getName();
				else winnerTeamsString += "ｧr and "+session.getTeamColor(team).getChatColor()+"Team "+session.getTeamColor(team).getName();
			} else if(winnerTeams.size() == 2) {
				if(i == 0) winnerTeamsString = session.getTeamColor(team).getChatColor()+"Team "+session.getTeamColor(team).getName();
				else if(i == 1) winnerTeamsString += " ｧrand "+session.getTeamColor(team).getChatColor()+"Team "+session.getTeamColor(team).getName();
			} else {
				winnerTeamsString = session.getTeamColor(team).getChatColor()+"Team "+session.getTeamColor(team).getName();
			}
			i++;
		}
		
		HashMap<Integer, List<Player>> playersInSorted = new HashMap<Integer, List<Player>>();
		int maxScore = 0;
		for(Player p : session.getPlayers()) {
			List<Player> newList = new ArrayList<Player>();
			if(playersInSorted.get(session.getPlayerPoints(p)) == null) {
				newList.add(p);
				playersInSorted.put(session.getPlayerPoints(p), newList);
			} else {
				newList = playersInSorted.get(session.getPlayerPoints(p));
				newList.add(p);
				playersInSorted.put(session.getPlayerPoints(p), newList);
			}
			if(session.getPlayerPoints(p) > maxScore) maxScore = session.getPlayerPoints(p);
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
			leaderboardString += "ｧr"+r+". ";
			
			if(rank.length > 1) {
				for(Player p : rank) {
					leaderboardString += session.getPlayerColor(p).getChatColor()+p.getName()+"ｧ7, ";
				}
				leaderboardString = leaderboardString.substring(0, leaderboardString.length()-2) + " ｧ7(ｧd"+session.getPlayerPoints(rank[0])+"ｧ7)\n";
			} else {
				leaderboardString += session.getPlayerColor(rank[0]).getChatColor()+rank[0].getName()+" ｧ7(ｧd"+session.getPlayerPoints(rank[0])+"ｧ7)\n";
			}
			r++;
		}
		
		
		HashMap<Integer, List<Team>> teamsInSorted = new HashMap<Integer, List<Team>>();
		
		int maxTeamScore = 0;
		for(Team team : session.getTeams()) {
			List<Team> newList = new ArrayList<Team>();
			if(teamsInSorted.get(team.getPoints()) == null) {
				newList.add(team);
				teamsInSorted.put(team.getPoints(), newList);
			} else {
				newList = teamsInSorted.get(team.getPoints());
				newList.add(team);
				teamsInSorted.put(team.getPoints(), newList);
			}
			if(session.getTeamPoints(team) > maxTeamScore) maxTeamScore = session.getTeamPoints(team);
		}
		List<List<Team>> teamsSortedInRanks = new ArrayList<List<Team>>();
		for(int c = maxTeamScore; c >= 0; c--) {
			if(teamsInSorted.get(c) != null) {
				List<Team> rankList = teamsInSorted.get(c);
				teamsSortedInRanks.add(rankList);
			}
		}
		
		String teamscoreboardString = "";
		int tr = 1;
		for(List<Team> rankTeamList : teamsSortedInRanks) {
			teamscoreboardString += "ｧ7"+tr+". ";
			for(Team team : rankTeamList) {
				String teamName = session.getTeamColor(team).getName();
				String teamNameColor = session.getTeamColor(team).getName().toUpperCase().replace("ORANGE", "GOLD");
				if(tr < rankTeamList.size()-1) teamscoreboardString += ChatColor.valueOf(teamNameColor)+teamName+" Team, ";
				else teamscoreboardString += ChatColor.valueOf(teamNameColor)+teamName+" Team ｧ7(ｧa"+session.getTeamPoints(team)+"ｧ7)\n";
			}
			tr++;
		}
		
		List<Player> winners = new ArrayList<Player>();
		for(Team winnerteam : winnerTeams) {
			for(Player winner : winnerteam.getPlayers()) {
				winners.add(winner);
			}
		}
		
		for(Player p : session.getPlayers()) {
			p.setWalkSpeed(0.2f);
			p.getInventory().setHelmet(new ItemStack(Material.AIR));
			PlayerTeleporter.gatherPlayers(winners);
			for(Team team : winnerTeams) {
				for(Player w : team.getPlayers()) {
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
			p.sendTitle(winnerTeamsString+" ｧrwon", "Score: ｧd"+amount+"\nｧrBest Player: ", 20, 20*5, 20);
			p.sendMessage("\nｧ7覧覧覧ｧaｧlPointsｧrｧ7覧覧覧");
			p.sendMessage("ｧlｧaTeam Score:");
			p.sendMessage(teamscoreboardString);
			p.sendMessage("\nｧlｧaPlayer Score:");
			p.sendMessage(leaderboardString);
			p.sendMessage("ｧ7覧覧覧覧覧覧覧覧\n");
		}
	}

}

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
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.Lasertag.LasertagColor;
import me.noobedidoob.minigames.lasertag.listeners.DeathListener;
import me.noobedidoob.minigames.lasertag.methods.LaserShooter;
import me.noobedidoob.minigames.lasertag.methods.PlayerTeleporter;
import me.noobedidoob.minigames.lasertag.methods.PlayerZoomer;
import me.noobedidoob.minigames.lasertag.methods.Weapons;
import me.noobedidoob.minigames.lasertag.methods.Weapons.Weapon;
import me.noobedidoob.minigames.main.Minigames;
import me.noobedidoob.minigames.utils.MgUtils.TimeFormat;

public class SessionRound {
	
	private Session session;
	private SessionScoreboard scoreboard;
	public SessionRound(Session session, SessionScoreboard scoreboard) {
		this.session = session;
		this.scoreboard = scoreboard;
	}
	
	

	public boolean tagging = false;
	public boolean tagging() {
		return tagging;
	}
	public void setTagging(boolean tagging) {
		this.tagging = tagging;
	}
	
	
	public void start() {
		tagging = true;
		for(Player p : session.getPlayers()) {
			p.sendTitle("§a§lGo!", "Kill the players of the other teams", 20, 20*4, 20);
			p.setGameMode(GameMode.ADVENTURE);
			p.getWorld().setDifficulty(Difficulty.PEACEFUL);
			p.teleport(PlayerTeleporter.getPlayerSpawnLoc(p));
			Lasertag.isProtected.put(p, true);
			p.sendTitle("§l§aGo!", "", 5, 20, 5);
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
							p.sendTitle("§c"+session.getTime(TimeFormat.SECONDS), "", 5, 20, 5);
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
	
	public void preparePlayers() {
		for(Player p : session.getPlayers()) {
			LaserShooter.playersSnipershots.put(p, 0);
			setPlayerGameInv(p);
		}
	}
	
	public void setPlayerGameInv(Player p) {
		
		if (session.isSolo()) {
			ItemStack lasergun = Weapon.LASERGUN.getItem();
			ItemMeta lasergunMeta = lasergun.getItemMeta();
			lasergunMeta.setDisplayName(session.getPlayerColor(p).getChatColor()+"§lLasergun #"+(session.getPlayerColor(p).ordinal()+1));
			lasergun.setItemMeta(lasergunMeta);
			
			p.getInventory().clear();
			p.getInventory().addItem(lasergun);
			
			if(session.withMultiweapons()){
				ItemStack dagger = Weapon.DAGGER.getItem();
				ItemMeta daggerMeta = dagger.getItemMeta();
				daggerMeta.setDisplayName(session.getPlayerColor(p).getChatColor()+"§lDagger #"+(session.getPlayerColor(p).ordinal()+1));
				daggerMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
				dagger.setItemMeta(daggerMeta);
				p.getInventory().setItem(1, dagger);
				
				ItemStack second = session.getPlayerSecondaryWeapon(p).getItem();
				ItemMeta sMeta = second.getItemMeta();
				String name = session.getPlayerSecondaryWeapon(p).name().substring(0, 1)+session.getPlayerSecondaryWeapon(p).name().toLowerCase().substring(1);
				sMeta.setDisplayName(session.getPlayerColor(p).getChatColor() + "§l"+name+" #" + (session.getPlayerColor(p).ordinal() + 1));
				second.setItemMeta(sMeta);
				p.getInventory().setItem(2, second);
			}
		} else {
			LasertagColor teamColor = session.getPlayerTeam(p).getColorName();
			ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
			ItemStack leggins = new ItemStack(Material.LEATHER_LEGGINGS);
			ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
			LeatherArmorMeta armourItemMeta = (LeatherArmorMeta) chestplate.getItemMeta();
			armourItemMeta.setUnbreakable(true);
			armourItemMeta.setColor(teamColor.getColor());
			chestplate.setItemMeta(armourItemMeta);
			leggins.setItemMeta(armourItemMeta);
			boots.setItemMeta(armourItemMeta);
			ItemStack teamLasergun = Weapon.LASERGUN.getItem();
			ItemMeta teamLasergunMeta = teamLasergun.getItemMeta();
			teamLasergunMeta.setDisplayName(teamColor.getChatColor()+"§lLasergun #"+(teamColor.ordinal()+1));
			teamLasergunMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
			teamLasergun.setItemMeta(teamLasergunMeta);
			
			p.getInventory().clear();
			p.getInventory().setItem(0,teamLasergun);
			p.getInventory().setChestplate(chestplate);
			p.getInventory().setLeggings(leggins);
			p.getInventory().setBoots(boots);
			
			if(session.withMultiweapons()) {
				ItemStack dagger = Weapon.DAGGER.getItem();
				ItemMeta daggerMeta = dagger.getItemMeta();
				daggerMeta.setDisplayName(session.getPlayerColor(p).getChatColor()+"§lDagger #"+(session.getPlayerColor(p).ordinal()+1));
				daggerMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
				dagger.setItemMeta(daggerMeta);
				p.getInventory().setItem(1, dagger);
				
				ItemStack second = session.getPlayerSecondaryWeapon(p).getItem();
				ItemMeta sMeta = second.getItemMeta();
				String name = session.getPlayerSecondaryWeapon(p).name().substring(0, 1)+session.getPlayerSecondaryWeapon(p).name().toLowerCase().substring(1);
				sMeta.setDisplayName(session.getPlayerColor(p).getChatColor() + "§l"+name+" #" + (session.getPlayerColor(p).ordinal() + 1));
				second.setItemMeta(sMeta);
				p.getInventory().setItem(2, second);
			}
		}
	}
	
	
	public void stop(boolean externalStop) {
		tagging = false;
		Weapons.registerWeapons();
		if(externalStop) {
			Bukkit.getScheduler().cancelTask(Lasertag.timeCountdownTask);
			for(Player p : session.getPlayers()) { 
				p.sendTitle("§cStopped the game!","",20, 20*4, 20);
				p.teleport(Minigames.spawn);
				session.setAllPlayersInv();
			}
		} else {
			if(session.isSolo()) evaluateSolo();
			else evaluateTeams();
		}
		

		for(Player p : session.getPlayers()) {
			PlayerZoomer.zoomPlayerOut(p);
		}
		
		session.stop(false, false);
		
	}
	
	
	
	
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
				else if(i < winners.size()) winnerTeamsString += "§a, §d"+p.getName();
				else winnerTeamsString += " §aand §d"+p.getName();
			} else if(winners.size() == 2) {
				if(i == 0) winnerTeamsString = p.getName();
				else if(i == 1) winnerTeamsString += " §aand §d"+p.getName();
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
			leaderboardString += "§r"+r+". ";
			
			if(rank.length > 1) {
				for(Player p : rank) {
					leaderboardString += session.getPlayerColor(p).getChatColor()+p.getName()+"§7, ";
				}
				leaderboardString = leaderboardString.substring(0, leaderboardString.length()-2) + " §7(§d"+session.getPlayerPoints(rank[0])+"§7)\n";
			} else {
				leaderboardString += session.getPlayerColor(rank[0]).getChatColor()+rank[0].getName()+" §7(§d"+session.getPlayerPoints(rank[0])+"§7)\n";
			}
			r++;
		}

		PlayerTeleporter.gatherPlayers(winners);
		for(Player p : session.getPlayers()) {
			p.sendMessage("\n§7—————§a§lPoints§r§7—————\n");
			p.sendMessage(leaderboardString);
			p.sendMessage("\n§7——————————————\n");
			for(Player w : winners) {
//				for(int t = 0; t < 5; t++) {
//					if(p.hasPotionEffect(Lasertag.glowingEffect)) p.removePotionEffect(Lasertag.glowingEffect);
//					Bukkit.getScheduler().scheduleSyncDelayedTask(Lasertag.minigames, new Runnable() {
//						@Override
//						public void run() {
							Firework fw = (Firework) Minigames.world.spawnEntity(w.getLocation(), EntityType.FIREWORK);
							FireworkMeta fwm = fw.getFireworkMeta();
							FireworkEffect fwe = FireworkEffect.builder().flicker(true).withColor(Color.GREEN).with(Type.BALL).trail(true).build();
							fwm.addEffect(fwe);
							fwm.setPower(1);
							fw.setFireworkMeta(fwm);
//						}
//					}, 60*t);
//				}
			}
			
			p.sendTitle("§b"+winnerTeamsString+" §awon", "§aScore: §d"+amount, 20, 20*5, 20);
		}
	
	}
	
	private void evaluateTeams() {
		List<SessionTeam> winnerTeams = new ArrayList<SessionTeam>();
		int amount = 0;
		for(SessionTeam team : session.getTeams()) {
			if(team.getPoints() > amount) {
				amount = session.getTeamPoints(team);
				winnerTeams = new ArrayList<SessionTeam>();
				winnerTeams.add(team);
			} else if(team.getPoints() == amount) {
				winnerTeams.add(team);
			}
		}
		String winnerTeamsString = "";
		int i = 0;
		for(SessionTeam team : winnerTeams) {
			if(winnerTeams.size() > 2) {
				if(i == 0) winnerTeamsString = session.getTeamColor(team).getChatColor()+"Team "+session.getTeamColor(team).getName();
				else if(i < winnerTeams.size()) winnerTeamsString += "§r, "+session.getTeamColor(team).getChatColor()+"Team "+session.getTeamColor(team).getName();
				else winnerTeamsString += "§r and "+session.getTeamColor(team).getChatColor()+"Team "+session.getTeamColor(team).getName();
			} else if(winnerTeams.size() == 2) {
				if(i == 0) winnerTeamsString = session.getTeamColor(team).getChatColor()+"Team "+session.getTeamColor(team).getName();
				else if(i == 1) winnerTeamsString += " §rand "+session.getTeamColor(team).getChatColor()+"Team "+session.getTeamColor(team).getName();
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
			leaderboardString += "§r§7"+r+". §r";
			
			if(rank.length > 1) {
				for(Player p : rank) {
					leaderboardString += session.getPlayerColor(p).getChatColor()+p.getName()+"§7, ";
				}
				leaderboardString = leaderboardString.substring(0, leaderboardString.length()-2) + " §7(§d"+session.getPlayerPoints(rank[0])+"§7)\n";
			} else {
				leaderboardString += session.getPlayerColor(rank[0]).getChatColor()+rank[0].getName()+" §7(§d"+session.getPlayerPoints(rank[0])+"§7)\n";
			}
			r++;
		}
		
		
		HashMap<Integer, List<SessionTeam>> teamsInSorted = new HashMap<Integer, List<SessionTeam>>();
		
		int maxTeamScore = 0;
		for(SessionTeam team : session.getTeams()) {
			List<SessionTeam> newList = new ArrayList<SessionTeam>();
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
		List<List<SessionTeam>> teamsSortedInRanks = new ArrayList<List<SessionTeam>>();
		for(int c = maxTeamScore; c >= 0; c--) {
			if(teamsInSorted.get(c) != null) {
				List<SessionTeam> rankList = teamsInSorted.get(c);
				teamsSortedInRanks.add(rankList);
			}
		}
		
		String teamscoreboardString = "";
		int tr = 1;
		for(List<SessionTeam> rankTeamList : teamsSortedInRanks) {
			teamscoreboardString += "§7"+tr+". ";
			for(SessionTeam team : rankTeamList) {
				String teamName = session.getTeamColor(team).getName();
				String teamNameColor = session.getTeamColor(team).getName().toUpperCase().replace("ORANGE", "GOLD");
				if(tr < rankTeamList.size()-1) teamscoreboardString += ChatColor.valueOf(teamNameColor)+teamName+" Team, ";
				else teamscoreboardString += ChatColor.valueOf(teamNameColor)+teamName+" Team §7(§a"+session.getTeamPoints(team)+"§7)\n";
			}
			tr++;
		}
		
		List<Player> winners = new ArrayList<Player>();
		for(SessionTeam winnerteam : winnerTeams) {
			for(Player winner : winnerteam.getPlayers()) {
				winners.add(winner);
			}
		}

		PlayerTeleporter.gatherPlayers(winners);
		for(Player p : session.getPlayers()) {
			for(SessionTeam team : winnerTeams) {
				for(Player w : team.getPlayers()) {
					if(p.hasPotionEffect(Lasertag.glowingEffect)) p.removePotionEffect(Lasertag.glowingEffect);
//					for(int t = 0; t < 4; t++) {
//						Bukkit.getScheduler().scheduleSyncDelayedTask(Lasertag.minigames, new Runnable() {
//							@Override
//							public void run() {
								Firework fw = (Firework) Minigames.world.spawnEntity(w.getLocation(), EntityType.FIREWORK);
								FireworkMeta fwm = fw.getFireworkMeta();
								FireworkEffect fwe = FireworkEffect.builder().flicker(true).withColor(Color.GREEN).with(Type.BALL).trail(true).build();
								fwm.addEffect(fwe);
								fwm.setPower(1);
								fw.setFireworkMeta(fwm);
//							}
//						}, 60*t);
//					}
				}
			}
			p.sendTitle(winnerTeamsString+" §rwon", "Score: §d"+amount+"\n§rBest Player: ", 20, 20*5, 20);
			p.sendMessage("\n§7——————§a§lPoints§r§7——————\n");
			p.sendMessage("§r§n§aTeam Score:§r");
			p.sendMessage(teamscoreboardString);
			p.sendMessage("\n§r§n§aPlayer Score:§r");
			p.sendMessage(leaderboardString);
			p.sendMessage("\n§7————————————————\n");
		}
	}

}

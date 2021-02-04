package me.noobedidoob.minigames.lasertag.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.lasertag.Lasertag.LasertagColor;
import me.noobedidoob.minigames.lasertag.listeners.DeathListener;
import me.noobedidoob.minigames.lasertag.methods.Weapons.Weapon;
import me.noobedidoob.minigames.lasertag.session.SessionModifiers.Mod;
import me.noobedidoob.minigames.main.Minigames;
import me.noobedidoob.minigames.utils.Map;
import me.noobedidoob.minigames.utils.MgUtils;
import me.noobedidoob.minigames.utils.MgUtils.TimeFormat;
import me.noobedidoob.minigames.utils.Pair;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Session implements Listener{
	
	public static SessionInventorys sessionInventorys = new SessionInventorys();
	public SessionScoreboard scoreboard;
	public SessionRound round;
	public SessionModifiers modifiers;
	
	private Player owner;
	private String code;
	

	public Session(Player owner, boolean solo) {
		scoreboard = new SessionScoreboard(this);
		round = new SessionRound(this, scoreboard);
		modifiers = new SessionModifiers();
		
		this.solo = solo;
		
		this.owner = owner;
		addPlayer(owner);
		addAdmin(owner);
		this.code = owner.getName();
		codeSession.put(code, this);
		
		SessionInventorys.openTimeInv(owner);
		sessions.add(this);
		
		for(Map m : Map.maps) mapVotes.put(m, 0);
		
	}
	public Session(Player owner, int teamsAmount) {
		scoreboard = new SessionScoreboard(this);
		round = new SessionRound(this, scoreboard);
		modifiers = new SessionModifiers();
		
		this.owner = owner;
		
		if(teamsAmount < 2) {
			solo = true;
		} else {
			solo = false;
			setTeamsAmount(teamsAmount);
			teams.get(0).addPlayer(owner);
		}
		
		addPlayer(owner);
		addAdmin(owner);
		this.code = owner.getName();
		codeSession.put(code, this);
		
		SessionInventorys.openTimeInv(owner);
		sessions.add(this);
		
		for(Map m : Map.maps) mapVotes.put(m, 0);
	}
	
	
	public void start(boolean countdown) {
		if(!round.tagging()) {
			if(setSessionMap()){
				map.setUsed(true);
				round.preparePlayers();
				if(!countdown) round.start();
				else {
					for(Player p : players) p.sendTitle("§aStarting Lasetag in §d5","§eMap: §b"+map.getName(),5,30,5);
					Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.minigames, new Runnable() {
						@Override
						public void run() {
							for(Player p : players) p.sendTitle("§aStarting Lasetag in §d4","§eMap: §b"+map.getName(), 0,30,5);
							Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.minigames, new Runnable() {
								@Override
								public void run() {
									for(Player p : players) p.sendTitle("§aStarting Lasetag in §d3","§eMap: §b"+map.getName(),0,30,5);
									Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.minigames, new Runnable() {
										@Override
										public void run() {
											for(Player p : players) p.sendTitle("§aStarting Lasetag in §d2","§eMap: §b"+map.getName(),0,30,5);
											Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.minigames, new Runnable() {
												@Override
												public void run() {
													for(Player p : players) p.sendTitle("§aStarting Lasetag in §d1","§eMap: §b"+map.getName(),0,20,5);
													Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.minigames, new Runnable() {
														@Override
														public void run() {
															round.start();
														}
													}, 20);
												}
											}, 20);
										}
									}, 20);
								}
							}, 20);
						}
					}, 20);
				}
			}
		}
	}
	
	
	public void stop(boolean external, boolean closeSession) {
		if (external) {
			if (round.tagging()) {
				round.stop(external);
			} 
		}
		map.setUsed(false);
		setAllPlayersWaitingInv();
		
		if(votedBefore) mapState = MapState.VOTING;
		else mapState = MapState.SET;
		votedBefore = false;
		
		for(Player p : players) {
			hasPlayerVoted.put(p, false);
		}
		for(Map m : Map.maps) {
			mapVotes.put(m, 0);
		}
		
		this.time = 5*60;
		setTime(ogTime, TimeFormat.SECONDS, false);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.minigames, new Runnable() {
			@Override
			public void run() {
				try {
					for (Player p : players) {
						playerPoints.put(p, 0);
					} 
					for (SessionTeam t : teams) {
						t.setPoints(0);
					} 
					refreshScoreboard();
				} catch (Exception e) {
				}
			}
		}, 20*10);
		if(closeSession) close();
	}
	public void close() {
		if(tagging()) stop(true, false);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.minigames, new Runnable() {
			@Override
			public void run() {
				for(Player p : players) {
					DeathListener.streakedPlayers.put(p, 0);
					setPlayerSession(p, null);
					playerSession.put(p, null);
					p.getInventory().clear();
					removePlayer(p);
				}
				removePlayerSessinos();
				modifiers.reset();
				codeSession.put(code, null);
				players = null;
				owner = null;
				admins = null;
				removeThisSession();
				System.out.println("Closed the session from "+code);
			}
		}, 20);
	}
	
	
	
	private boolean solo;
	public boolean isSolo() {
		return solo;
	}
	public boolean isTeams() {
		return !solo;
	}
	public boolean waiting() {
		return !round.tagging();
	}
	public boolean tagging() {
		return round.tagging();
	}

	public enum MapState{
		SET,
		VOTING,
		NULL;
	}
	
	private Map map;
	private MapState mapState = MapState.NULL;
	public void setMap(Map m) {
		if(m == null) {
			mapState = MapState.VOTING;
			broadcast("§aMap vote enabled! You can vote the map for this round!");
			for(Map am : Map.maps) {
				mapVotes.put(am, 0);
			}
			for(Player p : players) {
				hasPlayerVoted.put(p, false);
			}
		} else {
			mapState = MapState.SET;
			this.map = m;
			broadcast("§aPlaying on the map §b"+m.getName());
		}
		refreshScoreboard();
	}
	public Map getMap() {
		return map;
	}
	public boolean isMapSet() {
		return mapState == MapState.SET;
	}
	public boolean votingMap() {
		return mapState == MapState.VOTING;
	}
	public boolean isMapNull() {
		return mapState == MapState.NULL;
	}

	public HashMap<Map, Integer> mapVotes = new HashMap<Map, Integer>();
	public HashMap<Player, Boolean> hasPlayerVoted = new HashMap<Player, Boolean>();
	public void playerVoteMap(Player p, Map m) {
		if(!hasPlayerVoted.get(p)) {
			hasPlayerVoted.put(p, true);
			mapVotes.put(m, mapVotes.get(m)+1);
			broadcast("§d"+p.getName()+" §avoted for the map §b"+m.getName()+" §7(§d"+mapVotes.get(m)+"§7)");
			refreshScoreboard();
		}
	}
	
	boolean votedBefore = false;
	private boolean setSessionMap() {
		if(mapState == MapState.VOTING) {
			votedBefore = true;
			Map m = Map.maps.get(0);
			if(map != null) m = map;
			int maxVote = mapVotes.get(Map.maps.get(0));
			for(Map am : Map.maps) {
				if(maxVote < mapVotes.get(am)) {
					maxVote = mapVotes.get(am);
					m = am;
				}
			}
			this.map = m;
			
			if(!isMapPlayable(map)) {
				broadcast("§cThe map §6"+map.getName()+" §cis not playable. Please set the map again!");
				for(Map am : Map.maps) {
					mapVotes.put(am, 0);
					refreshScoreboard();
				}
				return false;
			}
			if(m.isUsed()) {
				broadcast("§cThis map is currently in use! Please try again later or choose another map!");
				return false;
			}
			
			broadcast("§ePlaying with in the map §b"+map.getName());
			mapState = MapState.SET;
			refreshScoreboard();
		}
		if(map == null) {
			if(mapState == MapState.VOTING) broadcast("§cAn error occured while setting the map! Please try again or choose another map!");
			else {
				for(Player a : admins) {
					sendMessage(a, "§cAn error occured while setting the map! Please try again or choose another map!");
				}
			}
			for(Map am : Map.maps) {
				mapVotes.put(am, 0);
				refreshScoreboard();
			}
			mapState = MapState.NULL;
			refreshScoreboard();
			SessionInventorys.openMapInv(owner);
			return false;
		} else if(map.isUsed()) {
			for(Player a : admins) {
				sendMessage(a, "§cThis map is currently in use! Please try again later or choose another map!");
			}
			return false;
		}
		return true;
	}
	public boolean isMapPlayable(Map m) {
		if(solo && !m.withRandomSpawn() && players.size() > m.getBaseAmount()) return false;
		if(!solo && !m.withRandomSpawn() && teamsAmount > m.getBaseAmount()) return false;
		return true;
	}
	
	
	
	private int time = 300;
	private int ogTime = time;
	private boolean timeSet = false;
	public void setTime(int time, TimeFormat format, boolean announce) {
		switch (format) {
		case MINUTES:
			this.time = time*60;
			break;
		case HOURS:
			this.time = (time*60)*60;
			break;
		default:
			this.time = time;
			break;
		}
		if(this.time > 3600) this.time = 36000;
		if(waiting()) this.ogTime = this.time;
		timeSet = true;
		scoreboard.refresh();
		if(announce) {
			if(format == TimeFormat.HOURS) broadcast("§aSession time was set to §b"+MgUtils.getTimeFormatFromLong(this.time, "h")+" §ehours");
			else if(format == TimeFormat.MINUTES) broadcast("§aRound time was set to §b"+MgUtils.getTimeFormatFromLong(this.time, "m")+" §eminutes");
			else broadcast("§aRound time was set to §b"+this.time+" §eseconds");
		}
	}
	public int getTime(TimeFormat format) {
		switch (format) {
		case MINUTES:
			return time/60;
		case HOURS:
			return (time/60)/60;
		default:
			return time;
		}
	}
	public boolean isTimeSet() {
		return timeSet;
	}
	
	
	
	

	public List<Player> bannedPlayers = new ArrayList<Player>();
	@SuppressWarnings("deprecation")
	public void sendInvitation(Player p) {
		sendMessage(p, "§eYou've been invited to the session of §b"+owner.getName());
		TextComponent linkMsg = new TextComponent("JOIN");
		linkMsg.setColor(net.md_5.bungee.api.ChatColor.GOLD);
		linkMsg.setBold(true);
		linkMsg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lt join "+owner.getName()));
		linkMsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Join the session of "+owner.getName()).create()));
		
		p.spigot().sendMessage(linkMsg);
		if(bannedPlayers.contains(p)) bannedPlayers.remove(p);
	}
	public boolean isPlayerBanned(Player p) {
		return bannedPlayers.contains(p);
	}
	

	

	private ArrayList<Player> admins = new ArrayList<Player>();
	public boolean isAdmin(Player p) {
		if(admins.contains(p)) return true;
		return false;
	}
	public Player getOwner() {
		return owner;
	}
	public void addAdmin(Player p) {
		admins.add(p);
		if(p != owner) sendMessage(p, "§aYou've been promoted to §eAdmin§e!");
		setPlayerInv(p);
	}
	public void removeAdmin(Player p) {
		if(admins.contains(p)) {
			admins.remove(p);
			sendMessage(p, "§cYou're not longer an §eAdmin§e!");
			setPlayerInv(p);
		}
	}
	public Player[] getAdmins() {
		return admins.toArray(new Player[admins.size()]);
	}
	
	
	
	
	
	
	
	
	
	

	private HashMap<Player, Integer> playerPoints = new HashMap<Player, Integer>();
	private HashMap<Player, LasertagColor> playerColor = new HashMap<Player, LasertagColor>();
	public void setPlayerColor(Player p, LasertagColor color) {
		playerColor.put(p, color);
		try { scoreboard.board.getTeam(color.name()).unregister(); } catch (NullPointerException e) { 	}
		Team t = scoreboard.board.registerNewTeam(color.name());
		t.setColor(color.getChatColor());
		t.addEntry(p.getName());
		refreshScoreboard();
	}
	public LasertagColor getPlayerColor(Player p) {
		return playerColor.get(p);
	}
	public void refreshSoloPlayerColors() {
		if (isSolo()) {
			for (Player p : players) {
				int ordinal = players.indexOf(p);
				if (ordinal > LasertagColor.values().length - 1) ordinal -= LasertagColor.values().length;
				playerColor.put(p, LasertagColor.values()[ordinal]);
				try { scoreboard.board.getTeam(LasertagColor.values()[ordinal].name()).unregister(); } catch (NullPointerException e) { 	}
				Team t = scoreboard.board.registerNewTeam(LasertagColor.values()[ordinal].name());
				t.setColor(LasertagColor.values()[ordinal].getChatColor());
				t.addEntry(p.getName());
			}
		} 
		refreshScoreboard();
	}
	public int getPlayerPoints(Player p) {
		return playerPoints.get(p);
	}
	public void addPoints(Player p, int points) {
		playerPoints.put(p, playerPoints.get(p)+points);
		if(!solo) getPlayerTeam(p).addPoints(points);
	}
	private List<Player> players = new ArrayList<Player>();
	public Player[] getPlayers() {
		return this.players.toArray(new Player[this.players.size()]);
	}
	public boolean isInSession(Player p) {
		return players.contains(p);
	}
	public void addPlayer(Player p) {
		broadcast("§b"+p.getName()+" §ajoined the session!");
		setPlayerSession(p, this);
		players.add(p);
		playerPoints.put(p, 0);
		hasPlayerVoted.put(p, false);
		sendMessage(p, "§aWelcome to the Game, §b"+p.getName()+"§r§a!");
		
		if (isTeams()) {
			if (p != owner) {
				generatePlayerTeam(p);
			}
		} else {
			refreshSoloPlayerColors();
		}
		setPlayerInv(p);
		
		refreshScoreboard();
		
		if(withMultiWeapons) SessionInventorys.openSecondaryWeaponChooserInv(p);
	}
	public void banPlayer(Player p, Player admin) {
		bannedPlayers.add(p);
		removePlayer(p);
		broadcast("§b"+p.getName()+" §cwas kicked from §e"+admin.getName());
		sendMessage(p, "§b" + admin.getName() + " §ckicked you out of the session!");
	}
	public void leavePlayer(Player p) {
		removePlayer(p);
		broadcast("§b"+p.getName()+" §cleft the session");
		sendMessage(p, "§cYou left the session!");
	}
	public void removePlayer(Player p) {
		try {if(isSolo()) {scoreboard.board.getTeam(getPlayerColor(p).name()).unregister();}} catch (NullPointerException e) {}
		p.getInventory().clear();
		Lasertag.setPlayersLobbyInv(p);
		try {
			if(isTeams()) getPlayerTeam(p).removePlayer(p);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		players.remove(p);
		if(admins.contains(p)) admins.remove(p);
		if(p == owner) {
			if(players.size() == 0) {
				close();
			} else if(admins.size() == 0) {
				for(Player ap : players) {
					admins.add(ap);
				}
				codeSession.put(code, null);
				owner = admins.get(0);
				sendMessage(owner, "§l§bYou were made the owner of this session!");
				code = owner.getName();
				codeSession.put(code, this);
				setPlayerInv(owner);
			} else {
				codeSession.put(code, null);
				owner = admins.get(0);
				sendMessage(owner, "§l§bYou were made the owner of this session!");
				code = owner.getName();
				codeSession.put(code, this);
				setPlayerInv(owner);
			}
		}
		if(isSolo() && waiting()) {
			try {
				for(Player ap : players) {
					int pColorIndex = players.indexOf(ap);
					if(pColorIndex > LasertagColor.values().length-1) pColorIndex -= LasertagColor.values().length-1;
					setPlayerColor(ap, LasertagColor.values()[pColorIndex]);
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		} else {
			for(SessionTeam t : teams) {
				if(t.getPlayers().length == 1 && teams.indexOf(t) > 0) {
					for(int i = teams.indexOf(t)-1; i > 0; i--) {
						if(teams.get(i).getPlayers().length == 0) {
							Player tp = t.getPlayers()[0];
							t.removePlayer(tp);
							addPlayerToTeam(tp, teams.get(i));
						}
					}
				}
			}
		}
		try { p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard()); } catch (NullPointerException e) { }
		scoreboard.refresh();
		setPlayerSession(p, null);
		Lasertag.setPlayersLobbyInv(p);
//		round.refreshPlayerTeams();
		
//		if(tagging()) {
//			if(players.size() < 2) stop(true, false);
//		}
	}
	
	public HashMap<UUID, Pair> disconnectedPlayers = new HashMap<UUID, Pair>();
	public void disconnectPlayer(Player p) {
		broadcast("§e"+p.getName()+" §cdisconnected!", p);
		int points = 0;
		if(tagging()) points = getPlayerPoints(p);
		disconnectedPlayers.put(p.getUniqueId(), new Pair(getPlayerColor(p), points));
		removePlayer(p);
	}
	
	public void reconnectPlayer(Player p) {
		if(disconnectedPlayers.get(p.getUniqueId()) != null) {
			LasertagColor color = (LasertagColor) disconnectedPlayers.get(p.getUniqueId()).get1();
			int points = (int) disconnectedPlayers.get(p.getUniqueId()).get2();
			disconnectedPlayers.put(p.getUniqueId(), null);
			playerSession.put(p, this);
			
			players.add(p);
			playerPoints.put(p, points);
			hasPlayerVoted.put(p, false);
			
			if(isTeams()) {
				addPlayerToTeam(p, color);
			} else {
				setPlayerColor(p, color);
			}
			
			setPlayerInv(p);
			
			new BukkitRunnable() {
				@Override
				public void run() {
					broadcast("§b"+p.getName()+" §aReturned to the session!", p);
					sendMessage(p, "§aWelcome back, §b"+p.getName()+"§r§a!");
				}
			}.runTaskLater(Minigames.minigames, 10);
			
		}
	}

	
	
	private List<SessionTeam> teams = new ArrayList<SessionTeam>();
	public List<Player> hasTeamChooseInvOpen = new ArrayList<Player>();
	private int teamsAmount;
	
	public void addTeam(Player[] players, LasertagColor color) {
		SessionTeam team = new SessionTeam(this, color, players);
		teams.add(team);
		for(Player p : players) {
			playerColor.put(p, color);
		}
	}
	public void addPlayerToTeam(Player p, LasertagColor name) {
		addPlayerToTeam(p, teams.get(name.ordinal()));
	}
	public void addPlayerToTeam(Player p, SessionTeam team) {
		if (getPlayerTeam(p) != team) {
			if(getPlayerTeam(p) != null) getPlayerTeam(p).removePlayer(p);
			team.addPlayer(p);
			playerColor.put(p, team.getColorName());
			refreshScoreboard();
//			round.refreshPlayerTeams();
		}
	}
	public void generatePlayerTeam(Player p) {
		SessionTeam sTeam = teams.get(0);
		int lowestAmount = teams.get(0).getPlayers().length;
		for (SessionTeam team : teams) {
			if (team.getPlayers().length < lowestAmount) {
				lowestAmount = team.getPlayers().length;
				sTeam = team;
			}
		}
		addPlayerToTeam(p, sTeam);
	}
	public SessionTeam[] getTeams(){
		return teams.toArray(new SessionTeam[teams.size()]);
	}
	public LasertagColor getTeamColor(SessionTeam team) {
		return team.getColorName();
	}
	public int getTeamPoints(SessionTeam team) {
		return team.getPoints();
	}
	public SessionTeam getPlayerTeam(Player p) {
		return SessionTeam.getPlayerTeam(p);
	}
	public int getTeamsAmount() {
		return teamsAmount;
	}
	private boolean teamAmountSet = false;
	public void setTeamsAmount(int amount) {
		if(round.tagging()) return;
		teamsAmount = amount;
		teamAmountSet = true;
		
		for(int i = 0; i < amount; i++) {
			if(i == 0) addTeam(new Player[] {owner}, LasertagColor.Red);
			else addTeam(new Player[] {}, LasertagColor.values()[i]);
		}
	}
	public boolean isTeamsAmountSet() {
		return teamAmountSet;
	}
	
	public boolean inSameTeam(Player p1, Player p2) {
		if (isTeams()) {
			for (SessionTeam team : getTeams()) {
				return (team.isInTeam(p1) && team.isInTeam(p2));
			}
		}
		return false;
	}
	
	
	
	public void refreshScoreboard() {
		scoreboard.refresh();
	}
	
	
	
	
	public Inventory teamChooseInv = Bukkit.createInventory(null,  9, "§1Choose Team:");
	
	
	public void broadcast(String s) {
		for(Player p : players) {
			sendMessage(p, s);
		}
	}
	public void sendMessageAll(String s) {
		for(Player p : players) {
			p.sendMessage(s);
		}
	}
	public void broadcast(String s, Player... excludedPlayers) {
		for(Player p : players) {
			boolean notExcluded = true;
			for(Player ep : excludedPlayers) if(ep == p) notExcluded = false;
			if(notExcluded) sendMessage(p, s);
		}
	}
	
	
	public void setAllPlayersInv() {
		for(Player p : players) setPlayerInv(p);
	}
	public void setPlayerInv(Player p) {
		if(tagging()) round.setPlayerGameInv(p);
		else setPlayerWaitingInv(p);
	}
	

	public void setAllPlayersWaitingInv() {
		for(Player p : players) {
			setPlayerWaitingInv(p);
		}
	}
	public void setPlayerWaitingInv(Player p) {
		SessionInventorys.setPlayerSessionWaitingInv(p);
	}
	
	
	
	public Object getModValue(Mod m) {
		return modifiers.get(m);
	}
	
	public int getIntMod(Mod m) {
	 	return modifiers.getInt(m);
	}
    public double getDoubleMod(Mod m) {
    	return modifiers.getDouble(m);
    }
    public boolean getBooleanMod(Mod m) {
    	return modifiers.getBoolean(m);
    }
    
    public void setMod(Mod m, Object value) {
    	modifiers.set(m, value);
    	broadcast("§aThe modifier §b"+m.name().toLowerCase()+" §a was set to §e"+value.toString());
    }
    
    
    
    private boolean withMultiWeapons = false;
    public boolean withMultiweapons() {
    	return withMultiWeapons;
    }
    
    private HashMap<Player, Boolean> isPlayerReady = new HashMap<Player, Boolean>();
    public boolean isPlayerReady(Player p) {
    	if(withMultiWeapons) {
    		if(isPlayerReady.get(p) == null) isPlayerReady.put(p, false);
    		return isPlayerReady.get(p);
    	} else return true;
    }
    public void setPlayerReady(Player p, boolean ready) {
    	isPlayerReady.put(p, ready);
    }
    public List<Player> getNotReadyPlayers(){
    	List<Player> list = new ArrayList<Player>();
    	for(Player p : players) {
    		if(!isPlayerReady(p)) list.add(p);
    	}
    	return list;
    }
    public boolean isEveryBodyReady() {
		for (Player p : players) {
			if (!isPlayerReady(p))
				return false;
		} 
		return true;
    }
    
    
    private HashMap<UUID, Weapon> playersSecondaryWeapon = new HashMap<UUID, Weapon>();
    public Weapon getPlayerSecondaryWeapon(Player p) {
    	return playersSecondaryWeapon.get(p.getUniqueId());
    }
    public void setPlayerSecondaryWeapon(Player p, Weapon w) {
    	playersSecondaryWeapon.put(p.getUniqueId(), w);
    	setPlayerReady(p, true);
    	
    	boolean allReady = true;
		for(Player ap : getPlayers()) {
			if(!isPlayerReady(ap)) allReady = false;
		}
		
		if(allReady) {
			for(Player a : getAdmins()) {
				Session.sendMessage(a, "§aEverybody has chosen their secondary weapon!");
			}
		}
    }
    public void setWithMultiWeapons(boolean withMultiWeapons) {
    	if (waiting()) {
    		if(withMultiWeapons) {
        		if(!this.withMultiWeapons) {
        			this.withMultiWeapons = true;
        			for(Player p : players) {
        				setPlayerReady(p, false);
        				SessionInventorys.openSecondaryWeaponChooserInv(p);
        				setPlayerInv(p);
        			}
				}
        			
        	} else {
        		this.withMultiWeapons = false;
        	}
    	}
    }
	
	
	
	
	
	
	public void removePlayerSessinos() {
		playerSession.forEach((p, s) ->{
			if(s == this) playerSession.put(p, null);
		});
	}
	public void removeThisSession() {
		sessions.remove(this);
	}

	private static HashMap<OfflinePlayer, Session> playerSession = new HashMap<OfflinePlayer, Session>();
	public static Session getPlayerSession(OfflinePlayer p) {
		return playerSession.get(p);
	}
	public static void setPlayerSession(OfflinePlayer p, Session s) {
		playerSession.put(p, s);
	}
	
	
	private static HashMap<String, Session> codeSession = new HashMap<String, Session>();
	public static Session getSessionFromCode(String code) {
		return codeSession.get(code);
	}
	
	public static void sendMessage(Player p, String s) {
		p.sendMessage( "§o§7[§6Session§7] §r§a"+s);
	}
	
	private static List<Session> sessions = new ArrayList<Session>();
	public static void closeAllSessions() {
		sessions.forEach(s ->{
			s.close();
		});
	}
	public static Session[] getAllSessions() {
		return sessions.toArray(new Session[sessions.size()]);
	}
}
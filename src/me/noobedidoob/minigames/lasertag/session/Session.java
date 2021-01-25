package me.noobedidoob.minigames.lasertag.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

import me.noobedidoob.minigames.lasertag.listeners.DeathListener;
import me.noobedidoob.minigames.lasertag.session.SessionModifiers.Mod;
import me.noobedidoob.minigames.main.Minigames;
import me.noobedidoob.minigames.utils.LasertagColor;
import me.noobedidoob.minigames.utils.LasertagColor.LtColorNames;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import me.noobedidoob.minigames.utils.Map;
import me.noobedidoob.minigames.utils.MgUtils;
import me.noobedidoob.minigames.utils.MgUtils.TimeFormat;

public class Session implements Listener{
	
	public static SessionInventorys sessionInventorys = new SessionInventorys();
	public SessionScoreboard scoreboard;
	public SessionRound round;
	public SessionModifiers modifiers;
	
	private Player owner;
	private String code;
	

	public Session(Player owner, boolean solo) {
		if(solo) new Session(owner, 0);
		else new Session(owner, 2);
	}
	public Session(Player owner, int teamsAmount) {
		scoreboard = new SessionScoreboard(this);
		round = new SessionRound(this, scoreboard);
		modifiers = new SessionModifiers();
		
		this.owner = owner;
		addPlayer(owner);
		addAdmin(owner);
		this.code = owner.getName();
		codeSession.put(code, this);
		
		if(teamsAmount < 2) {
			solo = true;
		} else {
			solo = false;
			setTeamsAmount(teamsAmount);
		}
		
		SessionInventorys.openInvitationInv(owner);
		sessions.add(this);
		
		for(Map m : Map.maps) mapVotes.put(m, 0);
	}
	
	
	int counter;
	int timer;
	public void start(boolean countdown) {
		if(!round.tagging()) {
			setSessionMap();
			round.preparePlayers();
			if(!countdown) round.start();
			else {
				counter = 5;
				timer = Bukkit.getScheduler().scheduleSyncRepeatingTask(Minigames.minigames, new Runnable() {
					@Override
					public void run() {
						if(counter > 0) {
							for(Player p : players) {
								p.sendTitle("§aStarting Lasetag in §d"+counter,"Prepare yourself!",5,20,5);
							}
							counter--;
						} else {
							Bukkit.getScheduler().cancelTask(timer);
							round.start();
						}
					}
				}, 0, 20);
			}
		}
	}
	public void stop(boolean external, boolean closeSession) {
		if (external) {
			if (round.tagging()) {
				round.stop(external);
			} 
		}
		
		refreshPlayersLobbyInvs();
		mapState = MapState.NULL;
		SessionInventorys.openMapInv(owner);
		for(Player p : players) {
			hasPlayerVoted.put(p, false);
		}
		for(Map m : Map.maps) {
			mapVotes.put(m, 0);
		}
		
		setTime(5, TimeFormat.MINUTES, false);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.minigames, new Runnable() {
			@Override
			public void run() {
				for(Player p : players) {
					playerPoints.put(p, 0);
				}
				for(SessionTeam t : teams) {
					t.setPoints(0);
				}
				refreshScoreboard();
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
	
	
	
	public void refreshScoreboard() {
		scoreboard.refresh();
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

	private HashMap<Map, Integer> mapVotes = new HashMap<Map, Integer>();
	public HashMap<Player, Boolean> hasPlayerVoted = new HashMap<Player, Boolean>();
	public void playerVoteMap(Player p, Map m) {
		if(!hasPlayerVoted.get(p)) {
			hasPlayerVoted.put(p, true);
			mapVotes.put(m, mapVotes.get(m)+1);
			broadcast("§d"+p.getName()+" §avoted for the map §b"+m.getName()+" §7(§d"+mapVotes.get(m)+"§7)", p);
			sendMessage(p, "§aVoted for §b"+m.getName());
		}
	}
	private void setSessionMap() {
		if(mapState == MapState.VOTING) {
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
			broadcast("§ePlaying with in the map §b"+map.getName());
			mapState = MapState.SET;
			refreshScoreboard();
		}
		if(map == null) setMap(Map.maps.get(0));
	}
	
	
	
	
	private int time = 300;
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
		timeSet = true;
		scoreboard.refresh();
		if(announce) {
			if(format == TimeFormat.HOURS) broadcast("§aSession time was set to §b"+MgUtils.getTimeFormatFromLong(this.time, "h")+" §ehours");
			else broadcast("§aRound time was set to §b"+MgUtils.getTimeFormatFromLong(this.time, "m")+" §eminutes");
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
	
	
	
	

	private boolean invited = false;
	public List<Player> invitedPlayers = new ArrayList<Player>();
	@SuppressWarnings("deprecation")
	public void sendInvitation(Player p) {
		sendMessage(p, "§eYou've been invited to the session of §b"+owner.getName());
		TextComponent linkMsg = new TextComponent("JOIN");
		linkMsg.setColor(net.md_5.bungee.api.ChatColor.GOLD);
		linkMsg.setBold(true);
		linkMsg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lt join "+owner.getName()));
		linkMsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Join the session of "+owner.getName()).create()));
		
		p.spigot().sendMessage(linkMsg);
		if(!invitedPlayers.contains(p)) invitedPlayers.add(p);
		invited = true;
	}
	public boolean isPlayerInvited(Player p) {
		return invitedPlayers.contains(p);
	}
	public boolean invitationSent() {
		return invited;
	}
	public void setInvitationSent(boolean sent) {
		invited = sent;
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
		refreshPlayerLobbyInv(p);
	}
	public void removeAdmin(Player p) {
		if(admins.contains(p)) {
			admins.remove(p);
			sendMessage(p, "§cYou're not longer an §eAdmin§e!");
			refreshPlayerLobbyInv(p);
		}
	}
	public Player[] getAdmins() {
		return admins.toArray(new Player[admins.size()]);
	}
	
	
	
	
	
	
	
	
	
	

	private HashMap<Player, Integer> playerPoints = new HashMap<Player, Integer>();
	private HashMap<Player, LasertagColor> playerColor = new HashMap<Player, LasertagColor>();
	public void setPlayerColor(Player p, LtColorNames colorName) {
		playerColor.put(p, new LasertagColor(colorName));
	}
	public LasertagColor getPlayerColor(Player p) {
		return playerColor.get(p);
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
		broadcast("§b"+p.getName()+" §aJoined the session!");
		setPlayerSession(p, this);
		players.add(p);
		playerPoints.put(p, 0);
		hasPlayerVoted.put(p, false);
		int pColorIndex = players.indexOf(p);
		if(pColorIndex > LtColorNames.values().length-1) pColorIndex -= LtColorNames.values().length-1;
		setPlayerColor(p, LtColorNames.values()[pColorIndex]);
		sendMessage(p, "§aWelcome to the Game, §b"+p.getName()+"§r§a!");
		refreshPlayerLobbyInv(p);
		
		if (isTeams() && p != owner) {
			SessionTeam sTeam = teams.get(0);
			int lowestAmount = teams.get(0).getPlayers().length;
			for (SessionTeam team : teams) {
				if(team.getPlayers().length < lowestAmount) {
					lowestAmount = team.getPlayers().length;
					sTeam = team;
				}
			} 
			sTeam.addPlayer(p);
		}
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.minigames, new Runnable() {
			@Override
			public void run() {
				scoreboard.refresh();
			}
		}, 20);
//		round.refreshPlayerTeams();
	}
	public void kickPlayer(Player p, Player admin) {
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
		p.getInventory().clear();
		players.remove(p);
		invitedPlayers.remove(p);
		try {
			getPlayerTeam(p).removePlayer(p);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
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
			} else {
				codeSession.put(code, null);
				owner = admins.get(0);
				sendMessage(owner, "§l§bYou were made the owner of this session!");
				code = owner.getName();
				codeSession.put(code, this);
			}
			refreshPlayerLobbyInv(owner);
		}
		if(isSolo() && waiting()) {
			try {
				for(Player ap : players) {
					int pColorIndex = players.indexOf(ap);
					if(pColorIndex > LtColorNames.values().length-1) pColorIndex -= LtColorNames.values().length-1;
					setPlayerColor(ap, LtColorNames.values()[pColorIndex]);
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
		p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		scoreboard.refresh();
		getPlayerTeam(p).removePlayer(p);
		setPlayerSession(p, null);
//		round.refreshPlayerTeams();
	}
	
	
	public void disconnectPlayer(Player p) {
		removePlayer(p);
	}

	
	
	private List<SessionTeam> teams = new ArrayList<SessionTeam>();
	public List<Player> hasTeamChooseInvOpen = new ArrayList<Player>();
	private int teamsAmount;
	
	public void addTeam(Player[] players, LtColorNames colorName) {
		SessionTeam team = new SessionTeam(this, colorName, players);
		teams.add(team);
		for(Player p : players) {
			playerColor.put(p, new LasertagColor(colorName));
		}
	}
	public void addPlayerToTeam(Player p, LtColorNames name) {
		addPlayerToTeam(p, teams.get(name.ordinal()));
	}
	public void addPlayerToTeam(Player p, SessionTeam team) {
		if (getPlayerTeam(p) != team) {
			if(getPlayerTeam(p) != null) getPlayerTeam(p).removePlayer(p);
			team.addPlayer(p);
			playerColor.put(p, team.getLasertagColor());
			refreshScoreboard();
//			round.refreshPlayerTeams();
		}
	}
	public SessionTeam[] getTeams(){
		return teams.toArray(new SessionTeam[teams.size()]);
	}
	public LasertagColor getTeamColor(SessionTeam team) {
		return team.getLasertagColor();
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
			if(i == 0) addTeam(new Player[] {owner}, LtColorNames.Red);
			else addTeam(new Player[] {}, LtColorNames.values()[i]);
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
	
	
	
	
	
	
	
	public Inventory teamChooseInv = Bukkit.createInventory(null,  9, "§1Choose Team:");
	
	
	public void broadcast(String s) {
		for(Player p : players) {
			sendMessage(p, s);
		}
	}
	public void broadcast(String s, Player... excludedPlayers) {
		for(Player p : players) {
			boolean notExcluded = true;
			for(Player ep : excludedPlayers) if(ep == p) notExcluded = false;
			if(notExcluded) sendMessage(p, s);
		}
	}
	public void refreshPlayerLobbyInv(Player p) {
		SessionInventorys.setPlayerLobbyInv(p);
	}
	public void refreshPlayersLobbyInvs() {
		for(Player p : players) {
			refreshPlayerLobbyInv(p);
		}
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
    }

    public boolean withMultiweapons() { return modifiers.withMultiweapons(); }
    public boolean multiWeapons() { return modifiers.multiWeapons(); }
	
	
	
	
	
	
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
}

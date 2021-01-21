package me.noobedidoob.minigames.lasertag.session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

import me.noobedidoob.minigames.lasertag.listeners.DeathListener;
import me.noobedidoob.minigames.lasertag.session.Modifiers.Mod;
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
	public Scoreboard scoreboard;
	public Round round;
	public Modifiers modifiers;
	
	private Player owner;
	private String code;
	
	
	public Session(Player owner, boolean solo) {
		this.owner = owner;
		addPlayer(owner);
		addAdmin(owner);
		this.code = owner.getName();
		codeSession.put(code, this);
		this.solo = solo;
		scoreboard = new Scoreboard(this);
		round = new Round(this, scoreboard);
		modifiers = new Modifiers();
		SessionInventorys.openInvitationInv(owner);
		SessionInventorys.setPlayerInv(owner);
		sessions.add(this);
	}
	
	

	public void start() {
		if(!round.tagging()) {
			round.start();
		}
	}
	public void stop(boolean external, boolean closeSession) {
		if(round.tagging()) {
			round.stop(external);
		}
		
		
		if(closeSession) close();
	}
	public void close() {
		if(tagging()) stop(true, false);
		for(Player p : players) {
			DeathListener.streakedPlayers.put(p, 0);
			setPlayerSession(p, null);
			p.getInventory().clear();
		}
		modifiers.reset();
		codeSession.put(code, null);
		players = null;
		owner = null;
		admins = null;
		System.out.println("Closed the session from "+code);
		sessions.remove(this);
	}
	
	
	
	public void refresh() {
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


	
	private Map map;
	public boolean voteMap = true;
	@SuppressWarnings("unused")
	private HashMap<Map, Integer> mapVotes = new HashMap<Map, Integer>();
	private boolean mapSet = false;
	public void setMap(Map m) {
		if(m == null) {
			voteMap = true;
			System.out.println("Map = null");
		}
		else {
			this.map = m;
			System.out.println("set map to "+this.map.getName());
			mapSet = true;
		}
	}
	public Map getMap() {
		return map;
	}
	public boolean isMapSet() {
		return mapSet;
	}
	public boolean votingMaps() {
		return voteMap;
	}
	
	
	
	
	
	private long time = 300;
	private boolean timeSet = false;
	public void setTime(int time, TimeFormat format) {
		setTime((long) time, format);
	}
	public void setTime(long time, TimeFormat format) {
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
		timeSet = true;
		scoreboard.refresh();
		broadcast("§aSession time was set to §d"+MgUtils.getTimeFormatFromLong(this.time, "m")+" §e"+format.name().toLowerCase());
	}
	public long getTime(TimeFormat format) {
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
		sendMessage(p, "§eYou've been invited to the session of §d"+owner.getName());
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
		if(p != owner) sendMessage(p, "§aYou've been promoted to §bAdmin§e!");
		SessionInventorys.setPlayerInv(p);
	}
	public void removeAdmin(Player p) {
		if(admins.contains(p)) {
			admins.remove(p);
			sendMessage(p, "§cYou're not longer an §bAdmin§e!");
			SessionInventorys.setPlayerInv(p);
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
		if(!solo) teamPoints.put(getPlayerTeam(p), teamPoints.get(getPlayerTeam(p))+points);
	}
	private List<Player> players = new ArrayList<Player>();
	public Player[] getPlayers() {
		return this.players.toArray(new Player[this.players.size()]);
	}
	public boolean isInSession(Player p) {
		return players.contains(p);
	}
	public void addPlayer(Player p) {
		broadcast("§d"+p.getName()+" §aJoined the session!");
		setPlayerSession(p, this);
		players.add(p);
		playerPoints.put(p, 0);
		int pColorIndex = players.indexOf(p);
		if(pColorIndex > LtColorNames.values().length-1) pColorIndex -= LtColorNames.values().length-1;
		setPlayerColor(p, LtColorNames.values()[pColorIndex]);
		sendMessage(p, "§aWelcome to the Game, §d"+p.getName()+"§r§a!");
		SessionInventorys.setPlayerInv(p);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.minigames, new Runnable() {
			@Override
			public void run() {
				scoreboard.refresh();
			}
		}, 20);
	}
	public void kickPlayer(Player p, Player admin) {
		removePlayer(p);
		broadcast("§d"+p.getName()+" §cwas kicked from §b"+admin.getName());
		sendMessage(p, "§d" + admin.getName() + " §ckicked you out of the session!");
	}
	public void leavePlayer(Player p) {
		removePlayer(p);
		broadcast("§d"+p.getName()+" §cleft the session");
		sendMessage(p, "§cYou left the session!");
	}
	public void removePlayer(Player p) {
		p.getInventory().clear();
		players.remove(p);
		invitedPlayers.remove(p);
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
				sendMessage(owner, "§l§dYou were made the owner of this session!");
				code = owner.getName();
				codeSession.put(code, this);
			} else {
				codeSession.put(code, null);
				owner = admins.get(0);
				sendMessage(owner, "§l§dYou were made the owner of this session!");
				code = owner.getName();
				codeSession.put(code, this);
			}
			
			SessionInventorys.setPlayerInv(owner);
		}
		if(isSolo() && waiting()) {
			players.forEach(ap ->{
				int pColorIndex = players.indexOf(ap);
				if(pColorIndex > LtColorNames.values().length-1) pColorIndex -= LtColorNames.values().length-1;
				setPlayerColor(ap, LtColorNames.values()[pColorIndex]);
			});
		}
		p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		scoreboard.refresh();
		
		setPlayerSession(p, null);
	}
	

	
	
	
	
	
	
	
	
	
	
	private List<List<Player>> teams = new ArrayList<List<Player>>();
	private HashMap<Player, Player[]> playerTeam = new HashMap<Player, Player[]>();
	private HashMap<Player[], LasertagColor> teamColor = new HashMap<Player[], LasertagColor>();
	private HashMap<Player[], Integer> teamPoints = new HashMap<Player[], Integer>();
	private int teamsAmount;
	
	public void addTeam(Player[] team, LtColorNames colorName) {
		teams.add(Arrays.asList(team));
		teamPoints.put(team, 0);
		teamColor.put(team, new LasertagColor(colorName));
		for(Player p : team) {
			playerTeam.put(p, team);
			playerColor.put(p, new LasertagColor(colorName));
		}
	}
	public List<Player[]> getTeams(){
		List<Player[]> teams = new ArrayList<Player[]>();
		for(List<Player> teamList : this.teams) {
			Player[] team = new Player[teamList.size()];
			team = teamList.toArray(team);
			teams.add(team);
		}
		return teams;
	}
	public LasertagColor getTeamColor(Player[] team) {
		return teamColor.get(team);
	}
	public int getTeamPoints(Player[] team) {
		return teamPoints.get(team);
	}
	public Player[] getPlayerTeam(Player p) {
		return playerTeam.get(p);
	}
	public int getTeamsAmount() {
		return teamsAmount;
	}
	private boolean teamAmountSet = false;
	public void setTeamsAmount(int amount) {
		if(!round.tagging()) {
			teamsAmount = amount;
			teamAmountSet = true;
		}
	}
	public boolean isTeamsAmountSet() {
		return teamAmountSet;
	}
	
	public boolean inSameTeam(Player p1, Player p2) {
		if (isTeams()) {
			for (Player[] team : getTeams()) {
				boolean pInTeam = false;
				for (Player tp : team) {
					if (tp == p1) pInTeam = true;
					if (pInTeam) {
						for (Player thp : team) {
							if (thp == p2) return true;
						}
					}
				}
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
	
	
	
	
	
	
	

	private static HashMap<Player, Session> playerSession = new HashMap<Player, Session>();
	public static Session getPlayerSession(Player p) {
		return playerSession.get(p);
	}
	public static void setPlayerSession(Player p, Session s) {
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

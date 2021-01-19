package me.noobedidoob.minigames.lasertag.session;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.PluginManager;

import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.main.Minigames;
import me.noobedidoob.minigames.utils.LasertagColor;
import me.noobedidoob.minigames.utils.LasertagColor.LtColorNames;
import me.noobedidoob.minigames.utils.MgUtils.TimeFormat;

public class SessionInventorys implements Listener{
	
	public SessionInventorys() {
		PluginManager pluginManeger = Bukkit.getPluginManager();
		pluginManeger.registerEvents(this, Minigames.minigames);
	}
	
	int mapInvCounter = 0;
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerClickInventory(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		Session session = Session.getPlayerSession(p);
		if(session == null) return;
		if(session.getOwner() == p) {
			Inventory inv = e.getInventory();
			int slot = e.getSlot();
			try {
				if(inv.getItem(4).getType() == Material.LIME_STAINED_GLASS_PANE) {
					try {
						if(slot == 4) {
							for(Player op : Bukkit.getOnlinePlayers()) {
								if(op != p) {
									if(!session.invitedPlayers.contains(op)) {
										session.sendInvitation(op);
									}
									else Session.sendMessage(p, "§cThis player was already invited!");
								}
							}
							session.setInvitationSent(true);
							openTimeInv(p);
						} else if(slot > 8 && slot < inv.getSize()-1) {
							Player ip = Bukkit.getPlayer(inv.getItem(slot).getItemMeta().getDisplayName().substring(2));
							session.sendInvitation(ip);
							Session.sendMessage(p, "§eInvited §d"+ip.getName());
							inv.setItem(slot, new ItemStack(Material.AIR));
						} else if(slot == inv.getSize()-1) {
							session.setInvitationSent(true);
							openTimeInv(p);
						}
					} catch (Exception e1) {}
				} else if(inv.getItem(4).getType() == Material.CLOCK){
					if(slot == 2) {
						if(inv.getItem(4).getAmount() > 1) inv.getItem(4).setAmount(inv.getItem(4).getAmount()-1);
					} else if(slot == 6) {
						inv.getItem(4).setAmount(inv.getItem(4).getAmount()+1);
					} else if(slot == 8) {
						session.setTime(inv.getItem(4).getAmount(), TimeFormat.MINUTES);
//						Session.sendMessage(p, "Time set to §d"+inv.getItem(4).getAmount()+" §eminutes!");
						if(!session.isMapSet()) openMapInv(p);
						else p.closeInventory();
					}
					ItemMeta timeMeta = inv.getItem(4).getItemMeta();
					timeMeta.setDisplayName("§dTime: §r"+inv.getItem(4).getAmount()+" Minutes");
					inv.getItem(4).setItemMeta(timeMeta);
				} else if(inv.getItem(4).getType() == Material.PURPLE_STAINED_GLASS_PANE) {
					if(slot == 4) {
						session.setMap(null);
						Session.sendMessage(p, "§eMap vote enabled!");
						p.closeInventory();
						if(session.isTeams()) openTeamsInv(p);
						for(Player ap : session.getPlayers()) {
							setPlayerInv(ap);
						}
					} else if(slot > 8 && slot-8 < Lasertag.maps.size()-1) {
						mapInvCounter = 0;
						Lasertag.maps.forEach((n,m) ->{
							if(slot == mapInvCounter+9) {
								session.setMap(m);
								Session.sendMessage(p, "Map set to §d"+m.getName()+"§e!");
								p.closeInventory();
								for(Player ap : session.getPlayers()) {
									setPlayerInv(ap);
								}
								if(session.isTeams()) openTeamsInv(p);
							}
							mapInvCounter++;
						});
						for(Player ap : session.getPlayers()) {
							setPlayerInv(ap);
						}
					}
				} else if(inv.getItem(4).getType() == Material.LEATHER_CHESTPLATE) {
					if(slot == 2) {
						if(inv.getItem(4).getAmount() > 1) inv.getItem(4).setAmount(inv.getItem(4).getAmount()-1);
					} else if(slot == 6) {
						inv.getItem(4).setAmount(inv.getItem(4).getAmount()+1);
					} else if(slot == 8) {
						session.setTeamsAmount(inv.getItem(4).getAmount());
						Session.sendMessage(p, "Playing with §d"+inv.getItem(4).getAmount()+" §eteams!");
						p.closeInventory();
					}
					ItemMeta timeMeta = inv.getItem(4).getItemMeta();
					timeMeta.setDisplayName("§a"+inv.getItem(4).getAmount()+" §dTeams");
					inv.getItem(4).setItemMeta(timeMeta);
				} else if(inv.getItem(0) != null && inv.getItem(0).getType() == Material.PLAYER_HEAD) {
					Player ap = Bukkit.getPlayer(inv.getItem(slot).getItemMeta().getDisplayName().substring(2));
					session.addAdmin(ap);
					Session.sendMessage(p, "Promoted §d"+ap.getName()+" §eto Admin!");
					inv.setItem(slot, new ItemStack(Material.AIR));
				}
				e.setCancelled(true);
			} catch (NullPointerException e1) {
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerCloseInventory(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		Session session = Session.getPlayerSession(p);
		if(session == null) return;
		if(session.getOwner() == p) {
			Inventory inv = e.getInventory();
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.minigames, new Runnable() {
				@Override
				public void run() {
					if (p.getOpenInventory() != null) {
						try {
							if (inv.getItem(4).getType() == Material.LIME_STAINED_GLASS_PANE) {
								if (!session.invitationSent())
									openInvitationInv(p);
							} else if (inv.getItem(4).getType() == Material.CLOCK) {
								if (!session.isTimeSet())
									openTimeInv(p);
							} else if (inv.getItem(4).getType() == Material.PURPLE_STAINED_GLASS_PANE) {
								if (!session.isMapSet() && !session.voteMap)
									openMapInv(p);
							} else if (inv.getItem(4).getType() == Material.LEATHER_CHESTPLATE) {
								if (session.isTeams() && !session.isTeamsAmountSet()) openTeamsInv(p);
							}
						} catch (NullPointerException e) {
						} 
					}
				}
			}, 5);
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Session session = Session.getPlayerSession(p);
		if(session == null) return;
		if(session.waiting()) {
			if((e.getAction().equals(Action.RIGHT_CLICK_BLOCK) | e.getAction().equals(Action.RIGHT_CLICK_AIR)) && e.getItem() != null) {
				ItemStack item = e.getItem();
				if(item.getType() == Material.DIAMOND_HOE) {
					session.start();
				} else if(item.getType() == Material.PAPER) {
					openMapVoteInv(p);
				} else if(item.getType() == Material.LEATHER_CHESTPLATE) {
					openTeamChooseInv(p);
				} else if(item.getType() == Material.PLAYER_HEAD) {
					openInvitationInv(p);
				} else if(item.getType() == Material.DIAMOND_HELMET) {
					openAddAdminInv(p);
				} else if(item.getType() == Material.CLOCK) {
					openTimeInv(p);
				} else if(item.getType() == Material.BARRIER) {
					session.leavePlayer(p);
				} 
			}
			e.setCancelled(true);
		}
	}
	
	
	private static ItemStack getNextItem() {
		ItemStack next = new ItemStack(Material.DIAMOND_HOE);
		ItemMeta nextMeta = next.getItemMeta();
		nextMeta.setDisplayName("§aNext");
		nextMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        nextMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		next.setItemMeta(nextMeta);
		return next;
	}
	private static ItemStack getAdditionItem(String displayName) {
		ItemStack more = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
		ItemMeta moreMeta = more.getItemMeta();
		moreMeta.setDisplayName(displayName);
		more.setItemMeta(moreMeta);
		return more;
	}
	private static ItemStack getSubstractionItem(String displayName) {
		ItemStack less = new ItemStack(Material.RED_STAINED_GLASS_PANE);
		ItemMeta lessMeta = less.getItemMeta();
		lessMeta.setDisplayName("§c§l-1 §r§dTeam");
		less.setItemMeta(lessMeta);
		return less;
	}
	public static void openTeamsInv(Player p) {
		Inventory inv = Bukkit.createInventory(null, 9*1, "§5Set amount of teams");
		
		ItemStack amount = new ItemStack(Material.LEATHER_CHESTPLATE, 2);
		LeatherArmorMeta aMeta = (LeatherArmorMeta) amount.getItemMeta();
		aMeta.setColor(new LasertagColor(LtColorNames.Red).getColor());
		aMeta.setDisplayName("§aTeams: §d2");
		aMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		amount.setItemMeta(aMeta);
		
		inv.setItem(2, getSubstractionItem("§c§l-1 §r§dTeam"));
		inv.setItem(4, amount);
		inv.setItem(6, getAdditionItem("§a§l+1 §r§dTeam"));
		inv.setItem(8, getNextItem());
		
		p.closeInventory();
		p.openInventory(inv);
	}
	
	public static void openInvitationInv(Player p) {
		Session session = Session.getPlayerSession(p);
		if(session == null) return;
		
		int playerRows = 0;
		if(Bukkit.getOnlinePlayers().size() > 1) playerRows = 1;
		if(Bukkit.getOnlinePlayers().size() > 9) playerRows = 2;
		if(Bukkit.getOnlinePlayers().size() > 18) playerRows = 3;
		if(Bukkit.getOnlinePlayers().size() > 27) playerRows = 4;
		if(Bukkit.getOnlinePlayers().size() > 36) playerRows = 5;
		Inventory inv = Bukkit.createInventory(null, 9*(1+playerRows), "§1Choose players to invite");
		
		ItemStack all = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
		ItemMeta allMeta = all.getItemMeta();
		allMeta.setDisplayName("§aInvite everyone");
		all.setItemMeta(allMeta);
		inv.setItem(4, all);
		
		int ii = 0;
		for(Player op : Bukkit.getOnlinePlayers()) {
			if (p != op && !session.invitedPlayers.contains(op)) {
				ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
				SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
				skullMeta.setDisplayName("§d"+op.getName());
				skullMeta.setOwningPlayer(op);
				skull.setItemMeta(skullMeta);
				inv.setItem(9 + ii, skull);
				ii++;
			}
		}
		

		inv.setItem((9*(1+playerRows))-1, getNextItem());
		
		p.closeInventory();
		p.openInventory(inv);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.minigames, new Runnable() {
			@Override
			public void run() {
				p.updateInventory();
			}
		}, 1);
	}
	
	public static void openTimeInv(Player p) {
		Session session = Session.getPlayerSession(p);
		if(session == null) return;
		Inventory inv = Bukkit.createInventory(null, 9*1, "§1Set session time (in minutes)");
		
		ItemStack timeItem = new ItemStack(Material.CLOCK, 5);
		ItemMeta timeMeta = timeItem.getItemMeta();
		long time = 5;
		if(session.isTimeSet()) time = session.getTime(TimeFormat.MINUTES);
		if(session.getTime(TimeFormat.SECONDS) < 60) time = 1;
		timeMeta.setDisplayName("§dTime: §r"+time+" Minutes");
		timeItem.setItemMeta(timeMeta);

		inv.setItem(2, getSubstractionItem("§c§l-1 §r§cminute"));
		inv.setItem(4, timeItem);
		inv.setItem(6, getAdditionItem("§a§l+1 §r§aminute"));
		inv.setItem(8, getNextItem());
		
		p.closeInventory();
		p.openInventory(inv);
	}

	private static int i = 9;
	public static void openMapInv(Player p) {
		int rows = 2;
		if(Lasertag.maps.size() > 9) rows = 3;
		if(Lasertag.maps.size() > 18) rows = 4;
		if(Lasertag.maps.size() > 27) rows = 5;
		if(Lasertag.maps.size() > 36) rows = 6; 
		Inventory inv = Bukkit.createInventory(null, 9*rows, "§1Choose Map");
		
		ItemStack vote = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
		ItemMeta meta = vote.getItemMeta();
		meta.setDisplayName("§aLet players vote");
		vote.setItemMeta(meta);
		inv.setItem(4, vote);
		
		i = 9;
		Lasertag.maps.forEach((n,m) ->{
			ItemStack item = new ItemStack(Material.FILLED_MAP);
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setDisplayName("§r§d"+m.getName());
			itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			item.setItemMeta(itemMeta);
			inv.setItem(i, item);
			i++;
		});
		
		p.closeInventory();
		p.openInventory(inv);
	}
	
	public static void setPlayerInv(Player p) {
		Session session = Session.getPlayerSession(p);
		if(session == null) return;
		p.getInventory().clear();
		
		
		ItemStack map = new ItemStack(Material.PAPER);
		ItemMeta mapMeta = map.getItemMeta();
		String mapTitle = "§eVote map";
		if(!session.voteMap) mapTitle = "§eMap: "+session.getMap().getName();
		mapMeta.setDisplayName(mapTitle);
		map.setItemMeta(mapMeta);
		
		if(session.isTeams()) {
			ItemStack team = new ItemStack(Material.LEATHER_CHESTPLATE);
			LeatherArmorMeta teamMeta = (LeatherArmorMeta) team.getItemMeta();
			teamMeta.setColor(session.getPlayerColor(p).getColor());
			teamMeta.setDisplayName(session.getPlayerColor(p).getChatColor()+"Choose team");
			teamMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			team.setItemMeta(teamMeta);

			if (session.isAdmin(p)) {
				p.getInventory().setItem(1, team);
				p.getInventory().setItem(2, map);
			} else {
				p.getInventory().setItem(0, team);
				p.getInventory().setItem(1, map);
			}
		} else {
			if (session.isAdmin(p)) {
				p.getInventory().setItem(1, map);
			} else {
				p.getInventory().setItem(0, map);
			}
		}
		
		if(session.isAdmin(p)) {
			ItemStack go = new ItemStack(Material.DIAMOND_HOE);
			ItemMeta goMeta = go.getItemMeta();
			goMeta.setDisplayName("§a§lSTART");
			go.setItemMeta(goMeta);
			p.getInventory().setItem(0, go);
			
			ItemStack addPlayerItem = new ItemStack(Material.PLAYER_HEAD);
			SkullMeta addPlayerMeta = (SkullMeta) addPlayerItem.getItemMeta();
			addPlayerMeta.setDisplayName("§aInvite Player");
			addPlayerMeta.setOwningPlayer(p);
			addPlayerItem.setItemMeta(addPlayerMeta);
			p.getInventory().setItem(4, addPlayerItem);
			
			ItemStack addAdminItem = new ItemStack(Material.DIAMOND_HELMET);
			ItemMeta addAdminMeta = addAdminItem.getItemMeta();
			addAdminMeta.setDisplayName("§bPromote player to admin");
			addAdminItem.setItemMeta(addAdminMeta);
			p.getInventory().setItem(5, addAdminItem);
			
			ItemStack setTimeItem = new ItemStack(Material.CLOCK);
			ItemMeta setTimeMeta = setTimeItem.getItemMeta();
			setTimeMeta.setDisplayName("§dChange time");
			setTimeItem.setItemMeta(setTimeMeta);
			p.getInventory().setItem(6, setTimeItem);
		}
		
		ItemStack exit = new ItemStack(Material.BARRIER);
		ItemMeta exitMeta = exit.getItemMeta();
		exitMeta.setDisplayName("§cLeave");
		exit.setItemMeta(exitMeta);
		p.getInventory().setItem(8, exit);
		
		if(session.getOwner() == p) p.getInventory().getItem(8).getItemMeta().setDisplayName("§c§lClose Session");
	}
	
	public static void openMapVoteInv(Player p) {
		int rows = 2;
		if(Lasertag.maps.size() > 9) rows = 3;
		if(Lasertag.maps.size() > 18) rows = 4;
		if(Lasertag.maps.size() > 27) rows = 5;
		if(Lasertag.maps.size() > 36) rows = 6; 
		Inventory inv = Bukkit.createInventory(null, 9*rows, "§1Vote for a Map");
		
		i = 0;
		Lasertag.maps.forEach((n,m) ->{
			ItemStack item = new ItemStack(Material.FILLED_MAP);
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setDisplayName("§r"+m.getName());
			itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			item.setItemMeta(itemMeta);
			inv.setItem(i, item);
			i++;
		});
		
		p.openInventory(inv);
	}
	public static void openTeamChooseInv(Player p) {
		Inventory inv = Bukkit.createInventory(null, 9, "§1Choose your team!");
		
		p.openInventory(inv);
	}
	
	public static void openAddAdminInv(Player p) {
		Session session = Session.getPlayerSession(p);
		if(session == null) return;
		
		int rows = 1;
		if((session.getPlayers().length-session.getAdmins().length) > 9) rows = 2;
		if((session.getPlayers().length-session.getAdmins().length) > 9*2) rows = 3;
		if((session.getPlayers().length-session.getAdmins().length) > 9*3) rows = 4;
		if((session.getPlayers().length-session.getAdmins().length) > 9*4) rows = 5;
		if((session.getPlayers().length-session.getAdmins().length) > 9*5) rows = 6;
		Inventory inv = Bukkit.createInventory(null, 9*rows, "§1Choose a new admin:");
		
		int ii = 0;
		for(Player op : session.getPlayers()) {
			if (p != op && !session.isAdmin(op)) {
				ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
				SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
				skullMeta.setDisplayName("§d"+op.getName());
				skullMeta.setOwningPlayer(op);
				skull.setItemMeta(skullMeta);
				inv.setItem(ii, skull);
				ii++;
			}
		}
		
		p.openInventory(inv);
	}
}

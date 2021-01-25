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
import org.bukkit.event.inventory.InventoryType;
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
import me.noobedidoob.minigames.utils.Map;
import me.noobedidoob.minigames.utils.MgUtils.TimeFormat;

public class SessionInventorys implements Listener{
	
	public SessionInventorys() {
		PluginManager pluginManeger = Bukkit.getPluginManager();
		pluginManeger.registerEvents(this, Minigames.minigames);
	}
	
	int mapInvCounter = 0;
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerClickInventory(InventoryClickEvent e) {
//		System.out.println(e.getInventory().getType()+": "+e.getSlot());
		
		Player p = (Player) e.getWhoClicked();
		Inventory inv = e.getClickedInventory();
		Integer slot = e.getSlot();
		try {
			if(slot == null | inv.getItem(slot) == null | inv.getType() != InventoryType.CHEST) return;
		} catch (NullPointerException e2) {
			return;
		}

		Session session = Session.getPlayerSession(p);
		if(session == null) {
			if(inv.getItem(4).getType() == Material.LEATHER_CHESTPLATE | inv.getItem(4).getType() == Material.BARRIER) {
				if(slot == 2 && inv.getItem(2).getType() == Material.RED_STAINED_GLASS_PANE) {
					if(inv.getItem(4).getAmount() > 1) {
						inv.getItem(4).setAmount(inv.getItem(4).getAmount()-1);
						if(inv.getItem(4).getAmount() == 1) {
							inv.getItem(4).setType(Material.BARRIER);
							ItemMeta timeMeta = inv.getItem(4).getItemMeta();
							timeMeta.setDisplayName("§cNo Teams -> §7(§bSOLO§7)");
							inv.getItem(4).setItemMeta(timeMeta);
						} else {
							ItemMeta timeMeta = inv.getItem(4).getItemMeta();
							timeMeta.setDisplayName("§a"+inv.getItem(4).getAmount()+" §bTeams");
							inv.getItem(4).setItemMeta(timeMeta);
						}
					}
				} else if(slot == 6 && inv.getItem(6).getType() == Material.LIME_STAINED_GLASS_PANE) {
					if(inv.getItem(4).getAmount() < 8) {
						inv.getItem(4).setAmount(inv.getItem(4).getAmount()+1);
						if(inv.getItem(4).getType() == Material.BARRIER) {
							ItemStack amount = new ItemStack(Material.LEATHER_CHESTPLATE, 2);
							LeatherArmorMeta aMeta = (LeatherArmorMeta) amount.getItemMeta();
							aMeta.setColor(new LasertagColor(LtColorNames.Red).getColor());
							aMeta.setDisplayName("§aTeams: §b2");
							aMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE);
							amount.setItemMeta(aMeta);
							inv.setItem(4, amount);
						} else {
							ItemMeta timeMeta = inv.getItem(4).getItemMeta();
							timeMeta.setDisplayName("§a"+inv.getItem(4).getAmount()+" §bTeams");
							inv.getItem(4).setItemMeta(timeMeta);
						}
					}
				} else if(slot == 8 && inv.getItem(8).getType() == Material.DIAMOND_HOE) {
					session = new Session(p, inv.getItem(4).getAmount());
					if(inv.getItem(4).getAmount() < 2) Session.sendMessage(p, "§aCreated a new solo session!");
					else Session.sendMessage(p, "§aCreated a new teams session with §d"+inv.getItem(4).getAmount()+" §ateams!");
					p.closeInventory();
				}
			}
		} else {
			if(session.tagging()) return;
			if(session.getOwner() == p) {
				try {
					if(inv.getItem(4).getType() == Material.LIME_STAINED_GLASS_PANE) {
						try {
							if(slot == 4 && inv.getItem(4).getType() == Material.LIME_STAINED_GLASS_PANE) {
								for(Player op : Bukkit.getOnlinePlayers()) {
									if(op != p) {
										if(!session.invitedPlayers.contains(op)) {
											session.sendInvitation(op);
										}
									}
								}
								Session.sendMessage(p, "§aInvited everyone!");
								session.setInvitationSent(true);
								if(!session.isTimeSet()) openTimeInv(p);
							} else if(slot > 8 && slot < inv.getSize()-1) {
								Player ip = Bukkit.getPlayer(inv.getItem(slot).getItemMeta().getDisplayName().substring(2));
								if(!session.invitedPlayers.contains(ip)) {
									session.sendInvitation(ip);
								}
								else Session.sendMessage(p, "§cThis player was already invited!");
								Session.sendMessage(p, "§aInvited §b"+ip.getName());
								inv.setItem(slot, new ItemStack(Material.AIR));
							} else if(slot == inv.getSize()-1 && inv.getItem(slot).getType() == Material.DIAMOND_HOE) {
								session.setInvitationSent(true);
								if(!session.isTimeSet()) openTimeInv(p);
							}
						} catch (Exception e1) {}
					} else if(inv.getItem(4).getType() == Material.CLOCK){
						if(slot == 2 && inv.getItem(2).getType() == Material.RED_STAINED_GLASS_PANE) {
							if(inv.getItem(4).getAmount() > 1) inv.getItem(4).setAmount(inv.getItem(4).getAmount()-1);
						} else if(slot == 6 && inv.getItem(6).getType() == Material.LIME_STAINED_GLASS_PANE) {
							if(inv.getItem(4).getAmount() < 60) inv.getItem(4).setAmount(inv.getItem(4).getAmount()+1);
							else Session.sendMessage(p, "§cMaxi time is 1 hour!");
						} else if(slot == 8 && inv.getItem(8).getType() == Material.DIAMOND_HOE) {
							session.setTime(inv.getItem(4).getAmount(), TimeFormat.MINUTES, true);
	//						Session.sendMessage(p, "Time set to §b"+inv.getItem(4).getAmount()+" §eminutes!");
							if(session.isMapNull()) openMapInv(p);
							else p.closeInventory();
						}
						ItemMeta timeMeta = inv.getItem(4).getItemMeta();
						timeMeta.setDisplayName("§bTime: §r"+inv.getItem(4).getAmount()+" Minutes");
						inv.getItem(4).setItemMeta(timeMeta);
					} else if(inv.getItem(4).getType() == Material.PURPLE_STAINED_GLASS_PANE) {
						if(slot == 4 && inv.getItem(4).getType() == Material.PURPLE_STAINED_GLASS_PANE) {
							session.setMap(null);
							Session.sendMessage(p, "§aMap vote enabled!");
							p.closeInventory();
							if(session.isTeams() && !session.isTeamsAmountSet()) openTeamsInv(p);
							session.refreshPlayersLobbyInvs();
						} else if(slot > 8 && slot-8 < Lasertag.maps.size()-1 && inv.getItem(slot).getType() == Material.FILLED_MAP) {
							mapInvCounter = 0;
							for(Map m : Map.maps) {
								if(slot == mapInvCounter+9) {
									session.setMap(m);
									p.closeInventory();
									for(Player ap : session.getPlayers()) {
										setPlayerLobbyInv(ap);
									}
									if(session.isTeams() && !session.isTeamsAmountSet()) openTeamsInv(p);
								}
								mapInvCounter++;
							}
							session.refreshPlayersLobbyInvs();
						}
					} else if(inv.getItem(4).getType() == Material.LEATHER_CHESTPLATE) {
						if(slot == 2 && inv.getItem(2).getType() == Material.RED_STAINED_GLASS_PANE) {
							if(inv.getItem(4).getAmount() > 1) {
								inv.getItem(4).setAmount(inv.getItem(4).getAmount()-1);
								if(inv.getItem(4).getAmount() == 1) {
									inv.getItem(4).setType(Material.BARRIER);
									inv.getItem(4).getItemMeta().setDisplayName("§cNo Teams -> §7(§bSOLO§7)");
								}
							}
						} else if(slot == 6 && inv.getItem(6).getType() == Material.LIME_STAINED_GLASS_PANE) {
							if(inv.getItem(4).getAmount() < 8) inv.getItem(4).setAmount(inv.getItem(4).getAmount()+1);
						} else if(slot == 8 && inv.getItem(8).getType() == Material.DIAMOND_HOE) {
							session.setTeamsAmount(inv.getItem(4).getAmount());
							p.closeInventory();
							session.refreshScoreboard();
							session.refreshPlayersLobbyInvs();
						}
						ItemMeta timeMeta = inv.getItem(4).getItemMeta();
						timeMeta.setDisplayName("§a"+inv.getItem(4).getAmount()+" §bTeams");
						inv.getItem(4).setItemMeta(timeMeta);
					} else if(inv.getItem(4).getType() == Material.BLUE_STAINED_GLASS_PANE) {
						if(slot == 4) {
							boolean doNonAdminsExist = true;
							for(Player ap : session.getPlayers()) {
								if(!session.isAdmin(ap)) {
									doNonAdminsExist = false;
									session.addAdmin(ap);
								}
							}
							if(doNonAdminsExist) {
								p.closeInventory();
								Session.sendMessage(p, "§aEverybody is an §badmin §anow!");
								session.broadcast("§d" + p.getName() + " §amade everybody an §badmin§a!", p);
							}
						} else if(slot > 8) {
							Player ap = Bukkit.getPlayer(inv.getItem(slot).getItemMeta().getDisplayName().substring(2));
							if(session.isInSession(ap) && !session.isAdmin(ap)) {
								session.addAdmin(ap);
								Session.sendMessage(p, "§aMade §b"+ap.getName()+" §aan §eadmin");
							}
							inv.setItem(slot, new ItemStack(Material.AIR));
						}
						
					}
					e.setCancelled(true);
				} catch (NullPointerException e1) {
					System.out.println("NPE CATCHED!");
				}
			}
			
			if(session.hasTeamChooseInvOpen.contains(p)) {
				if(inv.getItem(inv.first(Material.LEATHER_CHESTPLATE)).getItemMeta().getDisplayName().toUpperCase().contains("RED")) {
					SessionTeam chosenTeam = SessionTeam.getTeamByCooserSlot.get(slot);
					SessionTeam currentTeam = session.getPlayerTeam(p);
					
					if(chosenTeam == currentTeam) {
						Session.sendMessage(p, "§cYou're already in this team!");
					} else {
						session.addPlayerToTeam(p, chosenTeam);
						for(Player ip : session.hasTeamChooseInvOpen) {
							ip.getOpenInventory().getTopInventory().setItem(currentTeam.getTeamChooserSlot(), currentTeam.getTeamChooser());
							ip.getOpenInventory().getTopInventory().setItem(chosenTeam.getTeamChooserSlot(), chosenTeam.getTeamChooser());
						}
						Session.sendMessage(p, "§aYou're now in "+chosenTeam.getChatColor()+"team "+chosenTeam.getColorName());
						LeatherArmorMeta meta = (LeatherArmorMeta) p.getInventory().getItem(p.getInventory().first(Material.LEATHER_CHESTPLATE)).getItemMeta(); 
						meta.setColor(chosenTeam.getColor());
						p.getInventory().getItem(p.getInventory().first(Material.LEATHER_CHESTPLATE)).setItemMeta(meta);
					}
				}
			}
			
			if(session.votingMap() && inv.getItem(0) != null && inv.getItem(0).getType() == Material.FILLED_MAP) {
				session.playerVoteMap(p, Map.maps.get(slot));
				p.closeInventory();
				p.getInventory().getItem(p.getInventory().first(Material.PAPER)).getItemMeta().setDisplayName("§eVoted for: §d"+Map.maps.get(slot).getName());
			}
		}
		e.setCancelled(true);
		
	}
	
	
	
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerCloseInventory(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		Inventory inv = e.getInventory();
		Session session = Session.getPlayerSession(p);
		if(session == null) return;
		if(session.tagging()) return;
		if(session.getOwner() == p) {
			
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
								if (session.isMapNull())
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
		
		if(inv.contains(Material.LEATHER_CHESTPLATE) && inv.getItem(inv.first(Material.LEATHER_CHESTPLATE)).getItemMeta().getDisplayName().toUpperCase().contains("RED")) {
			session.hasTeamChooseInvOpen.remove(p);
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Session session = Session.getPlayerSession(p);
		if(session == null) return;
		if(session.tagging()) return;
		if(session.waiting()) {
			if((e.getAction().equals(Action.RIGHT_CLICK_BLOCK) | e.getAction().equals(Action.RIGHT_CLICK_AIR)) && e.getItem() != null) {
				ItemStack item = e.getItem();
				if(item.getType() == Material.DIAMOND_HOE) {
					if(session.getPlayers().length > 0) {
						boolean enoughTeams = true;
						if(session.isTeams()) {
							int teamsWithPlayers = 0;
							for(SessionTeam team : session.getTeams()) {
								if(team.getPlayers().length > 0) teamsWithPlayers++;
							}
							if(teamsWithPlayers < 2) enoughTeams = false;
						}
						if(enoughTeams) session.start(true);
						else Session.sendMessage(p, "§cThere must be at least 2 teams with at least 1 player in it!");
					} else Session.sendMessage(p, "§cNot enough players!");
				} else if(item.getType() == Material.PAPER) {
					if(session.votingMap() && !session.hasPlayerVoted.get(p)) openMapVoteInv(p);
				} else if(item.getType() == Material.LEATHER_CHESTPLATE) {
					openTeamChooseInv(p);
				} else if(item.getType() == Material.PLAYER_HEAD) {
					openInvitationInv(p);
				} else if(item.getType() == Material.DIAMOND_HELMET) {
					openAddAdminInv(p);
				} else if(item.getType() == Material.CLOCK) {
					openTimeInv(p);
				} else if(item.getType() == Material.BARRIER) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.minigames, new Runnable() {
						@Override
						public void run() {
							session.leavePlayer(p);
						}
					}, 20);
				} 
			}
		}
		e.setCancelled(true);
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
		lessMeta.setDisplayName(displayName);
		less.setItemMeta(lessMeta);
		return less;
	}
	
	public static void openNewSessionInv(Player p) {
		Inventory inv = Bukkit.createInventory(null, 9*1, "§0Set teams amount (1 = §1solo§0)");
		
		ItemStack amount = new ItemStack(Material.BARRIER);
		ItemMeta aMeta = amount.getItemMeta();
		aMeta.setDisplayName("§cNo Teams -> §7(§bSOLO§7)");
		aMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		amount.setItemMeta(aMeta);
		
		inv.setItem(2, getSubstractionItem("§c§l-1 §r§bTeam"));
		inv.setItem(4, amount);
		inv.setItem(6, getAdditionItem("§a§l+1 §r§bTeam"));
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
				skullMeta.setDisplayName("§b"+op.getName());
				skullMeta.setOwningPlayer(op);
				skull.setItemMeta(skullMeta);
				inv.setItem(9 + ii, skull);
				ii++;
			}
		}
		

		inv.setItem((9*(1+playerRows))-1, getNextItem());
		
		p.closeInventory();
		p.openInventory(inv);
	}
	
	public static void openTimeInv(Player p) {
		Session session = Session.getPlayerSession(p);
		if(session == null) return;
		
		Inventory inv = Bukkit.createInventory(null, 9*1, "§1Set session time (in minutes)");
		
		long time = 5;
		if(session.isTimeSet()) time = session.getTime(TimeFormat.MINUTES);
		if(session.getTime(TimeFormat.SECONDS) < 60) time = 1;
		ItemStack timeItem = new ItemStack(Material.CLOCK, (int) time);
		ItemMeta timeMeta = timeItem.getItemMeta();
		
		timeMeta.setDisplayName("§bTime: §r"+time+" Minutes");
		timeItem.setItemMeta(timeMeta);

		inv.setItem(2, getSubstractionItem("§c§l-1 §r§cminute"));
		inv.setItem(4, timeItem);
		inv.setItem(6, getAdditionItem("§a§l+1 §r§aminute"));
		inv.setItem(8, getNextItem());
		
		p.closeInventory();
		p.openInventory(inv);
	}

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
		
		int i = 9;
		for(Map m : Map.maps) {
			ItemStack item = new ItemStack(Material.FILLED_MAP);
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setDisplayName("§r§b"+m.getName());
			itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			item.setItemMeta(itemMeta);
			inv.setItem(i++, item);
		}
		
		p.closeInventory();
		p.openInventory(inv);
	}

	public static void openTeamsInv(Player p) {
		Inventory inv = Bukkit.createInventory(null, 9*1, "§5Set amount of teams");
		
		ItemStack amount = new ItemStack(Material.LEATHER_CHESTPLATE, 2);
		LeatherArmorMeta aMeta = (LeatherArmorMeta) amount.getItemMeta();
		aMeta.setColor(new LasertagColor(LtColorNames.Red).getColor());
		aMeta.setDisplayName("§aTeams: §b2");
		aMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE);
		amount.setItemMeta(aMeta);
		
		inv.setItem(2, getSubstractionItem("§c§l-1 §r§bTeam"));
		inv.setItem(4, amount);
		inv.setItem(6, getAdditionItem("§a§l+1 §r§bTeam"));
		inv.setItem(8, getNextItem());
		
		p.closeInventory();
		p.openInventory(inv);
	}
	
	public static void setPlayerLobbyInv(Player p) {
		Session session = Session.getPlayerSession(p);
		if(session == null) return;
		p.getInventory().clear();
		
		
		ItemStack map = new ItemStack(Material.PAPER);
		ItemMeta mapMeta = map.getItemMeta();
		String mapTitle = "§eVote map";
		if(!session.votingMap() && session.getMap() != null) mapTitle = "§eMap: §b"+session.getMap().getName();
		mapMeta.setDisplayName(mapTitle);
		map.setItemMeta(mapMeta);
		
		if(session.isTeams()) {
			ItemStack team = new ItemStack(Material.LEATHER_CHESTPLATE);
			LeatherArmorMeta teamMeta = (LeatherArmorMeta) team.getItemMeta();
			teamMeta.setColor(session.getPlayerColor(p).getColor());
			teamMeta.setDisplayName(session.getPlayerColor(p).getChatColor()+"Choose team");
			teamMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE);
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
			goMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
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
			addAdminMeta.setDisplayName("§ePromote player to admin");
			addAdminMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			addAdminItem.setItemMeta(addAdminMeta);
			p.getInventory().setItem(5, addAdminItem);
			
			ItemStack setTimeItem = new ItemStack(Material.CLOCK);
			ItemMeta setTimeMeta = setTimeItem.getItemMeta();
			setTimeMeta.setDisplayName("§bChange time");
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
		int rows = 1;
		if(Lasertag.maps.size() > 9) rows = 2;
		if(Lasertag.maps.size() > 18) rows = 3;
		if(Lasertag.maps.size() > 27) rows = 4;
		if(Lasertag.maps.size() > 36) rows = 5; 
		Inventory inv = Bukkit.createInventory(null, 9*rows, "§1Vote for a Map");
		
		int i = 0;
		for(Map m : Map.maps) {
			ItemStack item = new ItemStack(Material.FILLED_MAP);
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setDisplayName("§r"+m.getName());
			itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			item.setItemMeta(itemMeta);
			inv.setItem(i++, item);
		}
		
		p.openInventory(inv);
	}
	public static void openTeamChooseInv(Player p) {
		Session session = Session.getPlayerSession(p);
		if(session == null) return;
		session.hasTeamChooseInvOpen.add(p);

		Inventory inv = Bukkit.createInventory(null, 9, "§1Choose your team!");

		for(SessionTeam t : session.getTeams()) {
			inv.setItem(t.getTeamChooserSlot(), t.getTeamChooser());
		}

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
		Inventory inv = Bukkit.createInventory(null, 9*(rows+1), "§1Choose a new admin:");
		
		ItemStack all = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
		ItemMeta allMeta = all.getItemMeta();
		allMeta.setDisplayName("§aMake everyone an admin");
		all.setItemMeta(allMeta);
		inv.setItem(4, all);
		
		int ii = 0;
		for(Player op : session.getPlayers()) {
			if (p != op && !session.isAdmin(op)) {
				ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
				SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
				skullMeta.setDisplayName("§b"+op.getName());
				skullMeta.setOwningPlayer(op);
				skull.setItemMeta(skullMeta);
				inv.setItem(9+ii, skull);
				ii++;
			}
		}
		
		p.openInventory(inv);
	}
	
}

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
import org.bukkit.plugin.PluginManager;

import me.noobedidoob.minigames.lasertag.Lasertag.LasertagColor;
import me.noobedidoob.minigames.lasertag.methods.Weapons.Weapon;
import me.noobedidoob.minigames.Minigames;
import me.noobedidoob.minigames.utils.Map;
import me.noobedidoob.minigames.utils.Utils;
import me.noobedidoob.minigames.utils.Utils.TimeFormat;
import org.bukkit.scheduler.BukkitRunnable;

public class SessionInventories implements Listener{
	
	private final Minigames minigames;
	public SessionInventories(Minigames minigames) {
		this.minigames = minigames;
		PluginManager pluginManeger = Bukkit.getPluginManager();
		pluginManeger.registerEvents(this, minigames);
	}
	
	int mapInvCounter = 0;
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerClickInventory(InventoryClickEvent e) {
		try {
			Player p = (Player) e.getWhoClicked();
			Inventory inv = e.getClickedInventory();
			int slot = e.getSlot();
			if(inv == null | inv.getItem(slot) == null) return;
			Session session = Session.getPlayerSession(p);
			if(session == null) {
				if(inv.getItem(4) != null && inv.getItem(4).getType() == Material.LEATHER_CHESTPLATE | inv.getItem(4).getType() == Material.BARRIER) {
					if(slot == 2 && inv.getItem(2).getType() == Material.RED_STAINED_GLASS_PANE) {
						if(inv.getItem(4).getAmount() > 1) {
							inv.getItem(4).setAmount(inv.getItem(4).getAmount()-1);
							if(inv.getItem(4).getAmount() == 1) {
								inv.getItem(4).setType(Material.BARRIER);
								ItemMeta timeMeta = inv.getItem(4).getItemMeta();
								timeMeta.setDisplayName("�cNo Teams -> �7(�bSOLO�7)");
								inv.getItem(4).setItemMeta(timeMeta);
							} else {
								ItemMeta timeMeta = inv.getItem(4).getItemMeta();
								timeMeta.setDisplayName("�a"+inv.getItem(4).getAmount()+" �bTeams");
								inv.getItem(4).setItemMeta(timeMeta);
							}
						}
					} else if(slot == 6 && inv.getItem(6).getType() == Material.LIME_STAINED_GLASS_PANE) {
						if(inv.getItem(4).getAmount() < 8) {
							inv.getItem(4).setAmount(inv.getItem(4).getAmount()+1);
							if(inv.getItem(4).getType() == Material.BARRIER) {
								ItemStack amount = new ItemStack(Material.LEATHER_CHESTPLATE, 2);
								LeatherArmorMeta aMeta = (LeatherArmorMeta) amount.getItemMeta();
								aMeta.setColor(LasertagColor.Red.getColor());
								aMeta.setDisplayName("�aTeams: �b2");
								aMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE);
								amount.setItemMeta(aMeta);
								inv.setItem(4, amount);
							} else {
								ItemMeta timeMeta = inv.getItem(4).getItemMeta();
								timeMeta.setDisplayName("�a"+inv.getItem(4).getAmount()+" �bTeams");
								inv.getItem(4).setItemMeta(timeMeta);
							}
						}
					} else if(slot == 8 && inv.getItem(8).getType() == Weapon.LASERGUN.getType()) {
						if(inv.getItem(4).getAmount() < 2) Session.sendMessage(p, "�aCreated a new solo session!");
						else Session.sendMessage(p, "�aCreated a new teams session with �d"+inv.getItem(4).getAmount()+" �ateams!");
						new Session(minigames, p, inv.getItem(4).getAmount());
						p.closeInventory();
					}
				}
			} else {
				if(session.tagging()) return;
				if(session.isAdmin(p)) {
					try {
					/*if(inv.getItem(4).getType() == Material.LIME_STAINED_GLASS_PANE) {
						try {
							if(slot == 4 && inv.getItem(4).getType() == Material.LIME_STAINED_GLASS_PANE) {
								for(Player op : Bukkit.getOnlinePlayers()) {
									if(op != p) {
										if(!session.invitedPlayers.contains(op)) {
											session.sendInvitation(op);
										}
									}
								}
								Session.sendMessage(p, "�aInvited everyone!");
								session.setInvitationSent(true);
								if(!session.isTimeSet()) openTimeInv(p);
							} else if(slot > 8 && slot < inv.getSize()-1) {
								Player ip = Bukkit.getPlayer(inv.getItem(slot).getItemMeta().getDisplayName().substring(2));
								if(!session.invitedPlayers.contains(ip)) {
									session.sendInvitation(ip);
								}
								else Session.sendMessage(p, "�cThis player was already invited!");
								Session.sendMessage(p, "�aInvited �b"+ip.getName());
								inv.setItem(slot, new ItemStack(Material.AIR));
							} else if(slot == inv.getSize()-1 && inv.getItem(slot).getType() == Weapon.LASERGUN.getType()) {
								session.setInvitationSent(true);
								if(!session.isTimeSet()) openTimeInv(p);
							}
						} catch (Exception e1) {}
					} else */


						if(inv.getItem(4) != null && inv.getItem(4).getType() == Material.CLOCK){
							if(slot == 2 && inv.getItem(2).getType() == Material.RED_STAINED_GLASS_PANE) {
								if(inv.getItem(4).getAmount() > 1) inv.getItem(4).setAmount(inv.getItem(4).getAmount()-1);
							} else if(slot == 6 && inv.getItem(6).getType() == Material.LIME_STAINED_GLASS_PANE) {
								if(inv.getItem(4).getAmount() < 60) inv.getItem(4).setAmount(inv.getItem(4).getAmount()+1);
								else Session.sendMessage(p, "�cMaxi time is 1 hour!");
							} else if(slot == 8 && inv.getItem(8).getType() == Weapon.LASERGUN.getType()) {
								session.setTime(inv.getItem(4).getAmount(), TimeFormat.MINUTES, true);
								//						Session.sendMessage(p, "Time set to �b"+inv.getItem(4).getAmount()+" �eminutes!");
								if(session.isMapNull()) openMapInv(p);
								else p.closeInventory();
							}
							ItemMeta timeMeta = inv.getItem(4).getItemMeta();
							timeMeta.setDisplayName("�bTime: �r"+inv.getItem(4).getAmount()+" Minutes");
							inv.getItem(4).setItemMeta(timeMeta);
						}


						else if(inv.getItem(4) != null && inv.getItem(4).getType() == Material.PURPLE_STAINED_GLASS_PANE) {
							if(slot == 4 && inv.getItem(4).getType() == Material.PURPLE_STAINED_GLASS_PANE) {
								session.setMap(null);
								Session.sendMessage(p, "�aMap vote enabled!");
								p.closeInventory();
								if(session.isTeams() && !session.isTeamsAmountSet()) openTeamsInv(p);
								session.setAllPlayersInv();
							} else if(slot > 8 && slot-9 < Map.MAPS.size() && inv.getItem(slot).getType() == Material.FILLED_MAP) {
								Map m = Map.getMapByName(inv.getItem(slot).getItemMeta().getDisplayName().toLowerCase().substring(2));
								session.setMap(m);
								p.closeInventory();
								for(Player ap : session.getPlayers()) {
									setPlayerSessionWaitingInv(ap);
								}
								if(session.isTeams() && !session.isTeamsAmountSet()) openTeamsInv(p);
								session.setAllPlayersInv();
							}
						}


						else if(inv.getItem(4) != null && (inv.getItem(4).getType() == Material.LEATHER_CHESTPLATE | inv.getItem(4).getType() == Material.BARRIER)) {
							if(slot == 2 && inv.getItem(2).getType() == Material.RED_STAINED_GLASS_PANE) {
								if(inv.getItem(4).getAmount() > 1) {
									inv.getItem(4).setAmount(inv.getItem(4).getAmount()-1);
									if(inv.getItem(4).getAmount() == 1) {
										inv.getItem(4).setType(Material.BARRIER);
										inv.getItem(4).getItemMeta().setDisplayName("�cNo Teams -> �7(�bSOLO�7)");
									}
								}
							} else if(slot == 6 && inv.getItem(6).getType() == Material.LIME_STAINED_GLASS_PANE) {
								if(inv.getItem(4).getAmount() < 8) inv.getItem(4).setAmount(inv.getItem(4).getAmount()+1);
							} else if(slot == 8 && inv.getItem(8).getType() == Weapon.LASERGUN.getType()) {
								session.setTeamsAmount(inv.getItem(4).getAmount());
								p.closeInventory();
								session.refreshScoreboard();
								session.setAllPlayersInv();
							}
							ItemMeta timeMeta = inv.getItem(4).getItemMeta();
							timeMeta.setDisplayName("�a"+inv.getItem(4).getAmount()+" �bTeams");
							inv.getItem(4).setItemMeta(timeMeta);
						}


						else if(inv.getItem(4) != null && inv.getItem(4).getType() == Material.BLUE_STAINED_GLASS_PANE) {
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
									Session.sendMessage(p, "�aEverybody is an �badmin �anow!");
									session.broadcast("�d" + p.getName() + " �amade everybody an �badmin�a!", p);
								}
							} else if(slot > 8) {
								Player ap = Bukkit.getPlayer(inv.getItem(slot).getItemMeta().getDisplayName().substring(2));
								if(session.isInSession(ap) && !session.isAdmin(ap)) {
									session.addAdmin(ap);
									Session.sendMessage(p, "�aMade �b"+ap.getName()+" �aan �eadmin");
								}
								inv.setItem(slot, new ItemStack(Material.AIR));
							}

						}
						e.setCancelled(true);
					} catch (NullPointerException e1) {
						e1.printStackTrace();
					}
				} else if(session.isAdmin(p))


					if(session.hasTeamChooseInvOpen.contains(p)) {
						if(inv.getItem(inv.first(Material.LEATHER_CHESTPLATE)).getItemMeta().getDisplayName().toUpperCase().contains("RED")) {
							SessionTeam chosenTeam = SessionTeam.getTeamByCooserSlot.get(slot);
							SessionTeam currentTeam = session.getPlayerTeam(p);

							if(chosenTeam != currentTeam) {
								session.addPlayerToTeam(p, chosenTeam);
								for(Player ip : session.hasTeamChooseInvOpen) {
									ip.getOpenInventory().getTopInventory().setItem(currentTeam.getTeamChooserSlot(), currentTeam.getTeamChooser());
									ip.getOpenInventory().getTopInventory().setItem(chosenTeam.getTeamChooserSlot(), chosenTeam.getTeamChooser());
								}
								Session.sendMessage(p, "�aYou're now in "+chosenTeam.getChatColor()+"team "+chosenTeam.getLasertagColor());
								LeatherArmorMeta meta = (LeatherArmorMeta) p.getInventory().getItem(p.getInventory().first(Material.LEATHER_CHESTPLATE)).getItemMeta();
								meta.setColor(chosenTeam.getColor());
								p.getInventory().getItem(p.getInventory().first(Material.LEATHER_CHESTPLATE)).setItemMeta(meta);
							}
						}
					}

				if(session.votingMap() && inv.getItem(0) != null && inv.getItem(0).getType() == Material.FILLED_MAP) {
					session.playerVoteMap(p, Map.getMapByName(inv.getItem(slot).getItemMeta().getDisplayName().toLowerCase().substring(0, inv.getItem(slot).getItemMeta().getDisplayName().length()-10)));
					p.closeInventory();
					p.getInventory().getItem(p.getInventory().first(Material.PAPER)).getItemMeta().setDisplayName("�eVoted for: �d"+Map.MAPS.get(slot).getName());
				}

				if(e.getInventory().getItem(1) != null && e.getInventory().getItem(1).getType() == Weapon.SHOTGUN.getType()){
					int i = 1;
					if(session.isAdmin(p)) i = 2;
					if(session.isTeams()) {
						if(session.isAdmin(p)) i = 3;
					}
					if(slot == 1) {
						session.setPlayerSecondaryWeapon(p, Weapon.SHOTGUN);
						Session.sendMessage(p, "�eYou chose the �dShotgun �eas secondary weapon");
						p.getInventory().getItem(i).setType(Weapon.SHOTGUN.getType());
						p.closeInventory();
					} else if(slot == 7){
						session.setPlayerSecondaryWeapon(p, Weapon.SNIPER);
						Session.sendMessage(p, "�eYou chose the �dSniper �eas secondary weapon");
						p.getInventory().getItem(i).setType(Weapon.SNIPER.getType());
						p.closeInventory();
					} else if(slot == 4 && session.isAdmin(p)){
						session.setWithMultiWeapons(false);
						session.broadcast("�cDisabled multiweapons");
						p.closeInventory();
					}
				}
			}
			e.setCancelled(true);
		} catch (NullPointerException npe){
			npe.printStackTrace();
		}
	}
	
	
	
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerCloseInventory(InventoryCloseEvent e) {
		try {
			Player p = (Player) e.getPlayer();
			Inventory inv = e.getInventory();
			Session session = Session.getPlayerSession(p);
			if(session == null) return;
			if(session.tagging()) return;
			if(session.getOwner() == p) {
				new BukkitRunnable(){
					@Override
					public void run() {
						try {
							if (inv.getItem(4).getType() == Material.CLOCK) {
								if (!session.isTimeSet()) openTimeInv(p);
							} else if (inv.getItem(4).getType() == Material.PURPLE_STAINED_GLASS_PANE) {
								if (session.isMapNull()) openMapInv(p);
							} else if (inv.getItem(4).getType() == Material.LEATHER_CHESTPLATE) {
								if (session.isTeams() && !session.isTeamsAmountSet()) openTeamsInv(p);
							} else if(inv.getItem(1).getType() == Weapon.SHOTGUN.getType()) {
								if(session.withMultiweapons() && !session.isPlayerReady(p)) openSecondaryWeaponChooserInv(p);
							}
						} catch (NullPointerException e) {
						}
					}
				}.runTaskLater(minigames,5);
			}

			if(inv.contains(Material.LEATHER_CHESTPLATE) && inv.getItem(inv.first(Material.LEATHER_CHESTPLATE)).getItemMeta().getDisplayName().toUpperCase().contains("RED")) {
				session.hasTeamChooseInvOpen.remove(p);
			}
		} catch (Exception exp) {
			exp.printStackTrace();
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
				if(item.getType() == Weapon.LASERGUN.getType()) {
					if(session.withMultiweapons() && !session.isEveryBodyReady()) {
						Session.sendMessage(p, "�cNot everybody is ready!");
						for(Player up : session.getNotReadyPlayers()) {
							openSecondaryWeaponChooserInv(up);
						}
					} else {
						if(session.getPlayers().length > 1) {
							boolean enoughTeams = true;
							if(session.isTeams()) {
								int teamsWithPlayers = 0;
								for(SessionTeam team : session.getTeams()) {
									if(team.getPlayers().length > 0) teamsWithPlayers++;
								}
								if(teamsWithPlayers < 2) enoughTeams = false;
							} 
							if(enoughTeams && !session.justStopped) session.start(true);
							else Session.sendMessage(p, "�cThere must be at least 2 teams with at least 1 player in it!");
						} else Session.sendMessage(p, "�cNot enough players!");
					}
				} else if(item.getType() == Material.PAPER) {
					if(session.votingMap()) {
						if(!session.hasPlayerVoted.get(p)) openMapVoteInv(p);
						else if(session.isAdmin(p)) openMapInv(p);
					} else if(session.isAdmin(p)) openMapInv(p);
				} else if(item.getType() == Material.LEATHER_CHESTPLATE) {
					openTeamChooseInv(p);
				} else if(item.getType() == Material.END_CRYSTAL) {
					openTeamsInv(p);
				} else if(item.getType() == Material.DIAMOND_HELMET) {
					openAddAdminInv(p);
				} else if(item.getType() == Material.CLOCK) {
					openTimeInv(p);
				} else if(item.getType() == Material.BARRIER) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(minigames, () -> session.leavePlayer(p), 1);
				} else if(session.withMultiweapons() && (item.getType() == Weapon.DAGGER.getType() || item.getType() == Weapon.SHOTGUN.getType() || item.getType() == Weapon.SNIPER.getType())) {
					openSecondaryWeaponChooserInv(p);
				} else if(!session.withMultiweapons() && item.getType() == Weapon.DAGGER.getType()) {
					Session.sendMessage(p, "�aEnabled �bmultiweapons!");
					session.setWithMultiWeapons(true);
				}
			}
		}
		e.setCancelled(true);
	}
	
	
	private static ItemStack getNextItem() {
		return Utils.getItemStack(Weapon.LASERGUN.getType(), "�aNext");
	}
	private static ItemStack getAdditionItem(String displayName) {
		return Utils.getItemStack(Material.LIME_STAINED_GLASS_PANE, displayName);
	}
	private static ItemStack getSubtractionItem(String displayName) {
		return Utils.getItemStack(Material.RED_STAINED_GLASS_PANE, displayName);
	}
	
	public static void openNewSessionInv(Player p) {
		Inventory inv = Bukkit.createInventory(null, 9, "�0Set teams amount (1 = �1solo�0)");

		inv.setItem(2, getSubtractionItem("�c�l-1 �r�bTeam"));
		inv.setItem(4, Utils.getItemStack(Material.BARRIER, "�cNo Teams -> �7(�bSOLO�7)"));
		inv.setItem(6, getAdditionItem("�a�l+1 �r�bTeam"));
		inv.setItem(8, getNextItem());
		
		p.closeInventory();
		p.openInventory(inv);
	}
	
	public static void openTimeInv(Player p) {
		Session session = Session.getPlayerSession(p);
		if(session == null) return;
		
		Inventory inv = Bukkit.createInventory(null, 9, "�1Set session time (in minutes)");
		
		long time = 5;
		if(session.isTimeSet()) time = session.getTime(TimeFormat.MINUTES);
		if(session.getTime(TimeFormat.SECONDS) < 60) time = 1;

		inv.setItem(2, getSubtractionItem("�c�l-1 �r�cminute"));
		inv.setItem(4, Utils.getItemStack(Material.CLOCK, "�bTime: �r"+time+" Minutes", (int) time));
		inv.setItem(6, getAdditionItem("�a�l+1 �r�aminute"));
		inv.setItem(8, getNextItem());
		
		p.closeInventory();
		p.openInventory(inv);
	}

	public static void openMapInv(Player p) {
		Session session = Session.getPlayerSession(p);
		if(session == null) return;


		Inventory inv = Bukkit.createInventory(null, 9+(9*((Map.MAPS.size()-1)/9+1)), "�1Choose Map");
		inv.setItem(4, Utils.getItemStack(Material.PURPLE_STAINED_GLASS_PANE, "�aLet players vote"));
		
		int i = 9;
		for(Map m : Map.MAPS) {
			if(session.isMapPlayable(m) && !m.isUsed()) {
				inv.setItem(i++, Utils.getItemStack(Material.FILLED_MAP, "�r�b"+m.getName()));
			}
		}
		
		p.closeInventory();
		p.openInventory(inv);
	}

	public static void openTeamsInv(Player p) {
		Inventory inv = Bukkit.createInventory(null, 9, "�5Set amount of teams");
		
		if(Session.getPlayerSession(p) != null && Session.getPlayerSession(p).getTeamsAmount() == 2) {

			inv.setItem(4, Utils.getItemStack(Material.BARRIER, "�cNo Teams -> �7(�bSOLO�7)"));
		} else {

			inv.setItem(4, Utils.getLeatherArmorItem(Material.LEATHER_CHESTPLATE, "�aTeams: �b2", LasertagColor.Red.getColor(), 2));
		}
		
		
		
		inv.setItem(2, getSubtractionItem("�c�l-1 �r�bTeam"));
		inv.setItem(6, getAdditionItem("�a�l+1 �r�bTeam"));
		inv.setItem(8, getNextItem());
		
		p.closeInventory();
		p.openInventory(inv);
	}
	
	
	public static void setPlayerSessionWaitingInv(Player p) {
		Session session = Session.getPlayerSession(p);
		if(session == null) return;
		for(int i = 0; i < 9; i++) {
			p.getInventory().setItem(i, new ItemStack(Material.AIR));
		}

		String mapTitle = (!session.votingMap() && session.getMap() != null)? "�eMap: �b"+session.getMap().getName() : "�eVote map";
		if(session.isAdmin(p)) mapTitle += " �o�7[�6Click to change�7]";
		ItemStack map = /*new ItemStack(Material.PAPER);*/ Utils.getItemStack(Material.PAPER, mapTitle);

		if(session.isTeams()) {
			ItemStack team = /*new ItemStack(Material.LEATHER_CHESTPLATE);*/ Utils.getLeatherArmorItem(Material.LEATHER_CHESTPLATE, session.getPlayerColor(p).getChatColor()+"Change team",session.getPlayerColor(p).getColor(), 1);

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
		
		if(session.withMultiweapons()) {
			Material m = Weapon.DAGGER.getType();
			if(session.getPlayerSecondaryWeapon(p) != null) {
				 m = (session.getPlayerSecondaryWeapon(p) == Weapon.SHOTGUN)? Weapon.SHOTGUN.getType() : Weapon.SNIPER.getType();
			}
			ItemStack weapon = /*new ItemStack(m);*/ Utils.getItemStack(m, "�eYour secondary weapon �7[�6Click to change�7]");

			if(session.isTeams()) {
				if(session.isAdmin(p)) {
					p.getInventory().setItem(3, weapon);
				} else {
					p.getInventory().setItem(2, weapon);
				}
			} else {
				if(session.isAdmin(p)) {
					p.getInventory().setItem(2, weapon);
				} else {
					p.getInventory().setItem(1, weapon);
				}
			}
		}
		if(session.isAdmin(p)) {
			int addition = (session.isSolo())? 0:1;
			p.getInventory().setItem(0, Utils.getItemStack(Weapon.LASERGUN.getType(),"�a�lSTART"));

			p.getInventory().setItem(((session.withMultiweapons())?4:3)+addition, Utils.getItemStack(Material.CLOCK,"�bChange time"));

			if(!session.withMultiweapons()) {
				p.getInventory().setItem(4+addition, Utils.getItemStack(Weapon.DAGGER.getType(),"�aWith Multiweapons"));
			}

			p.getInventory().setItem(5+addition, Utils.getItemStack(Material.END_CRYSTAL,"�6Change mode"));

			p.getInventory().setItem(6+addition, Utils.getItemStack(Material.DIAMOND_HELMET,"�ePromote player to admin"));
		}

		p.getInventory().setItem(8, Utils.getItemStack(Material.BARRIER,"�cLeave"));
	}
	
	public static void openMapVoteInv(Player p) {
		Session session = Session.getPlayerSession(p);
		if(session == null) return;
		
		Inventory inv = Bukkit.createInventory(null, 9*((Map.MAPS.size()-1)/9+1), "�1Vote for a Map");
		
		int i = 0;
		for(Map m : Map.MAPS) {
			if(session.isMapPlayable(m) && !m.isUsed()) {
				inv.setItem(i++, Utils.getItemStack(Material.FILLED_MAP,m.getName()+" �7(�a"+ ((session.mapVotes.get(m) != null)?session.mapVotes.get(m):0)+"�7)"));
			}
		}
		
		p.openInventory(inv);
	}
	public static void openTeamChooseInv(Player p) {
		Session session = Session.getPlayerSession(p);
		if(session == null) return;
		session.hasTeamChooseInvOpen.add(p);

		Inventory inv = Bukkit.createInventory(null, 9, "�1Choose your team!");

		for(SessionTeam t : session.getTeams()) {
			inv.setItem(t.getTeamChooserSlot(), t.getTeamChooser());
		}

		p.openInventory(inv);
	}
	
	public static void openAddAdminInv(Player p) {
		Session session = Session.getPlayerSession(p);
		if(session == null) return;
		
		Inventory inv = Bukkit.createInventory(null, 9+(9*((session.getPlayers().length-session.getAdmins().length-1)/9+1)), "�1Choose a new admin:");

		inv.setItem(4, Utils.getItemStack(Material.BLUE_STAINED_GLASS_PANE, "�aMake everyone an admin"));
		
		int i = 9;
		for(Player op : session.getPlayers()) {
			if (p != op && !session.isAdmin(op)) {
				inv.setItem(i++, Utils.getPlayerSkullItem(op,"�b"+op.getName()));
			}
		}
		
		p.openInventory(inv);
	}
	
	public static void openSecondaryWeaponChooserInv(Player p) {
		Session session = Session.getPlayerSession(p);
		if(session == null) return;
		
		Inventory weaponsInv = Bukkit.createInventory(null, 9, "�0Choose your secondary weapon!");

		LasertagColor playerColor = session.getPlayerColor(p);
		ItemStack shotgun = Utils.getItemStack(Weapon.SHOTGUN.getType(), playerColor.getChatColor()+"Shotgun #"+(playerColor.ordinal()+1));
		ItemStack sniper = Utils.getItemStack(Weapon.SNIPER.getType(), playerColor.getChatColor()+"Sniper #"+(playerColor.ordinal()+1));
		ItemStack disable = Utils.getItemStack(Material.BARRIER,"�cDisable Multiweapons");

		weaponsInv.setContents(new ItemStack[] {null, shotgun, null, null, (session.isAdmin(p))?disable:null, null, null, sniper, null});
		p.closeInventory();
		p.openInventory(weaponsInv);
	}
	
}
package me.noobedidoob.minigames.lasertag.methods;

//import org.bukkit.Bukkit;
//import org.bukkit.boss.BarColor;
//import org.bukkit.boss.BarStyle;
//import org.bukkit.boss.BossBar;
//import org.bukkit.entity.Player;
//
//import me.noobedidoob.minigames.lasertag.Lasertag;

public class PointEvents {
//	
//	private static Player[] allPlayers;
//	private static int prepareTime = 10;
//	private static int eventTime = 60;
//	private static boolean eventRunning = false;
//	public static void laserEvents() {
//		if(Modifiers.withEvents) {
//			allPlayers = Game.players();
//			if(!eventRunning) runRandomEvent();
//		}
//	}
//	public static void runRandomEvent() {
//		int v = (int) Math.round((Math.random()*(((7)-1)+1))+1);
//		eventRunning = true;
//		if(v == 1) points(allPlayers);
//		else if(v == 2) snipe(allPlayers);
//		else if(v == 3) closeRange(allPlayers);
//		else if(v == 4) pvp(allPlayers);
//		else if(v == 5) headShot(allPlayers);
//		else if(v == 6) highlightPlayer(allPlayers);
//		else if(v == 7) throughBlocks(allPlayers);
//		else if(v == 8) biggerHitbox(allPlayers);
//	}
//	public static void points(Player[] players) {
//		Modifiers.resetModifiers();
//		BossBar eventBar = Bukkit.createBossBar("§c§lNext up: §r§e§nPoints", BarColor.GREEN, BarStyle.SOLID);
//		int extra = 5;
//		startPrepCountdown(eventBar, "§aPoints for a normal shot ae §d"+extra+" §aPoints!");
//		for(Player p : players) {
//			eventBar.addPlayer(p);
//		}
//		Bukkit.getScheduler().scheduleSyncDelayedTask(Lasertag.minigames, new Runnable() {
//			@Override
//			public void run() {
//				Modifiers.points = extra;
//			}
//		}, 20*(prepareTime+1));
//		Bukkit.getScheduler().scheduleSyncDelayedTask(Lasertag.minigames, new Runnable() {
//			@Override
//			public void run() {
//				Modifiers.resetModifiers();
//			}
//		}, 20*((eventTime+1)+prepareTime));
//	}
//	public static void snipe(Player[] players) {
//		Modifiers.resetModifiers();
//		BossBar eventBar = Bukkit.createBossBar("§c§lNext up: §r§e§nSniper", BarColor.GREEN, BarStyle.SOLID);
//		int extra = 10;
//		startPrepCountdown(eventBar, "§aSnipershots give extra §d"+extra+" §aPoints!");
//		for(Player p : players) {
//			eventBar.addPlayer(p);
//		}
//		Bukkit.getScheduler().scheduleSyncDelayedTask(Lasertag.minigames, new Runnable() {
//			@Override
//			public void run() {
//				Modifiers.snipeShotsExtra = extra;
//			}
//		}, 20*(prepareTime+1));
//		Bukkit.getScheduler().scheduleSyncDelayedTask(Lasertag.minigames, new Runnable() {
//			@Override
//			public void run() {
//				Modifiers.resetModifiers();
//			}
//		}, 20*((eventTime+1)+prepareTime));
//	}
//	public static void closeRange(Player[] players) {
//		Modifiers.resetModifiers();
//		BossBar eventBar = Bukkit.createBossBar("§c§lNext up: §r§e§nClose Range", BarColor.GREEN, BarStyle.SOLID);
//		int extra = 5;
//		startPrepCountdown(eventBar, "§aClose Range Shots give extra §d"+extra+" §aPoints!");
//		for(Player p : players) {
//			eventBar.addPlayer(p);
//		}
//		Bukkit.getScheduler().scheduleSyncDelayedTask(Lasertag.minigames, new Runnable() {
//			@Override
//			public void run() {
//				Modifiers.closeRangeExtra = extra;
//			}
//		}, 20*(prepareTime+1));
//		Bukkit.getScheduler().scheduleSyncDelayedTask(Lasertag.minigames, new Runnable() {
//			@Override
//			public void run() {
//				Modifiers.resetModifiers();
//			}
//		}, 20*((eventTime+1)+prepareTime));
//	}
//	public static void pvp(Player[] players) {
//		Modifiers.resetModifiers();
//		BossBar eventBar = Bukkit.createBossBar("§c§lNext up: §r§e§nMelee", BarColor.GREEN, BarStyle.SOLID);
//		int extra = 5;
//		startPrepCountdown(eventBar, "§aMelee kills give extra §d"+extra+" §aPoints!");
//		for(Player p : players) {
//			eventBar.addPlayer(p);
//		}
//		Bukkit.getScheduler().scheduleSyncDelayedTask(Lasertag.minigames, new Runnable() {
//			@Override
//			public void run() {
//				Modifiers.pvpExtra = extra;
//			}
//		}, 20*(prepareTime+1));
//		Bukkit.getScheduler().scheduleSyncDelayedTask(Lasertag.minigames, new Runnable() {
//			@Override
//			public void run() {
//				Modifiers.resetModifiers();
//			}
//		}, 20*((eventTime+1)+prepareTime));
//	}
//	public static void headShot(Player[] players) {
//		Modifiers.resetModifiers();
//		BossBar eventBar = Bukkit.createBossBar("§c§lNext up: §r§e§nHeadshot", BarColor.GREEN, BarStyle.SOLID);
//		int extra = 5;
//		startPrepCountdown(eventBar, "§aHeadshots give extra §d"+extra+" §aPoints!");
//		for(Player p : players) {
//			eventBar.addPlayer(p);
//		}
//		Bukkit.getScheduler().scheduleSyncDelayedTask(Lasertag.minigames, new Runnable() {
//			@Override
//			public void run() {
//				Modifiers.headshotExtra = extra;
//			}
//		}, 20*(prepareTime+1));
//		Bukkit.getScheduler().scheduleSyncDelayedTask(Lasertag.minigames, new Runnable() {
//			@Override
//			public void run() {
//				Modifiers.resetModifiers();
//			}
//		}, 20*((eventTime+1)+prepareTime));
//	}
//	public static void throughBlocks(Player[] players) {
//		Modifiers.resetModifiers();
//		BossBar eventBar = Bukkit.createBossBar("§c§lNext up: §r§e§nShoot throug blocks", BarColor.GREEN, BarStyle.SOLID);
//		startPrepCountdown(eventBar, "§aYou are able to shoot through blocks!");
//		for(Player p : players) {
//			eventBar.addPlayer(p);
//		}
//		Bukkit.getScheduler().scheduleSyncDelayedTask(Lasertag.minigames, new Runnable() {
//			@Override
//			public void run() {
//				Modifiers.shootThroughBlocks = true;
//			}
//		}, 20*(prepareTime+1));
//		Bukkit.getScheduler().scheduleSyncDelayedTask(Lasertag.minigames, new Runnable() {
//			@Override
//			public void run() {
//				Modifiers.resetModifiers();
//			}
//		}, 20*((eventTime+1)+prepareTime));
//	}
//	public static void highlightPlayer(Player[] players) {
//		Modifiers.resetModifiers();
//		BossBar eventBar = Bukkit.createBossBar("§c§lNext up: §r§e§nHighlighted players", BarColor.GREEN, BarStyle.SOLID);
//		startPrepCountdown(eventBar, "§aAll players are highlighted");
//		for(Player p : players) {
//			eventBar.addPlayer(p);
//		}
//		Bukkit.getScheduler().scheduleSyncDelayedTask(Lasertag.minigames, new Runnable() {
//			@Override
//			public void run() {
//				Modifiers.highLightPlayers = true;
//			}
//		}, 20*(prepareTime+1));
//		Bukkit.getScheduler().scheduleSyncDelayedTask(Lasertag.minigames, new Runnable() {
//			@Override
//			public void run() {
//				Modifiers.resetModifiers();
//			}
//		}, 20*((eventTime+1)+prepareTime));
//	}
//	public static void biggerHitbox(Player[] players) {
//		Modifiers.resetModifiers();
//		BossBar eventBar = Bukkit.createBossBar("§c§lNext up: §r§e§nBigger hitbox", BarColor.GREEN, BarStyle.SOLID);
//		startPrepCountdown(eventBar, "§aThe Hitbox of a player is twice as wide and one block higher!");
//		for(Player p : players) {
//			eventBar.addPlayer(p);
//		}
//		Bukkit.getScheduler().scheduleSyncDelayedTask(Lasertag.minigames, new Runnable() {
//			@Override
//			public void run() {
//				Modifiers.widthAddon = 1;
//				Modifiers.heightAddon = 1;
//			}
//		}, 20*(prepareTime+1));
//		Bukkit.getScheduler().scheduleSyncDelayedTask(Lasertag.minigames, new Runnable() {
//			@Override
//			public void run() {
//				Modifiers.resetModifiers();
//			}
//		}, 20*((eventTime+1)+prepareTime));
//	}
//	
//	private static int prepTimer;
//	private static int prepTask;
//	public static void startPrepCountdown(BossBar bar, String newTitle) {
//		prepTimer = prepareTime;
//		prepTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Lasertag.minigames, new Runnable() {
//			@Override
//			public void run() {
//				if(prepTimer >= 0) {
//					float f = (float) prepTimer / prepareTime;
//					bar.setProgress(f);
//					prepTimer--;
//				} else {
//					Bukkit.getScheduler().cancelTask(prepTask);
//					startEventCountdown(bar, newTitle);
//				}
//			}
//		}, 0, 20);
//	}
//	
//	private static int eventTimer;
//	private static int eventTask;
//	public static void startEventCountdown(BossBar bar, String title) {
//		bar.setTitle(title);
//		bar.setColor(BarColor.YELLOW);
//		eventTimer = eventTime;
//		eventTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Lasertag.minigames, new Runnable() {
//			@Override
//			public void run() {
//				if(eventTimer >= 0) {
//					float f = (float) eventTimer / eventTime;
//					bar.setProgress(f);
//					eventTimer--;
//				} else {
//					Bukkit.getScheduler().cancelTask(eventTask);
//				}
//			}
//		}, 0, 20);
//	}
//
}

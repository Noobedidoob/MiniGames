package me.noobedidoob.minigames.lasertag.methods;

import java.util.HashMap;

public class Modifiers {

	
	public static HashMap<String, Integer> integerModifier = new HashMap<String, Integer>();
	public static HashMap<String, Double> doubleModifier = new HashMap<String, Double>();
	public static HashMap<String, Boolean> statusModifier = new HashMap<String, Boolean>();
	public static int points = 1;
	public static int snipeShotsExtra = 1;
	public static int minSnipeDistance = 35;
	public static int closeRangeExtra = 0;
	public static int backstabExtra = 0;
	public static int pvpExtra = 0;
	public static int headshotExtra = 1;
	public static int streakExtra = 2;
	public static int streakShutdown = 1;
	public static int minKillsForStreak = 5;
	public static int multiKillsExtra = 2;
	public static int glowingAmplifier = 257;
	public static int spawnProtSecs = 10;
	public static double widthAddon = 0;
	public static double heightAddon = 0;
	public static boolean withEvents = false;
	public static boolean shootThroughBlocks = false;
	public static boolean highLightPlayers = false;
//	public static boolean spawnAtBases = true;
	public static boolean withAmmo = false;
	public static int bulletsInMagazine = 50;
	public static int magazinReloadTime = 20;
	public static int lasergunCooldown = 12;
	public static int sniperCooldown = 100;
	public static int shotgunCooldown = 40;
	public static int sniperAmmoBeforeCooldown = 2;
	public static boolean withMinigun = false;
	public static boolean multiWeapons = false;
	public static int minigunAmmo = 100;
	
	public static int lasergunNormalDamage = 100;
	public static int lasergunMWDamage = 6;
	public static int lasergunPVPDamage = 10;
	public static int shotgunDamage = 11;
	public static int sniperdamage = 100;
	public static int stabberDamage = 10;
	
	public static void registerModifiers() {
		integerModifier.put("points", points);
		integerModifier.put("snipe", snipeShotsExtra);
		integerModifier.put("minsnipedistance", minSnipeDistance);
		integerModifier.put("closerange", closeRangeExtra);
		integerModifier.put("pvp", pvpExtra);
		integerModifier.put("headshot", headshotExtra);
		integerModifier.put("streak", streakExtra);
		integerModifier.put("minkillstreak", minKillsForStreak);
		integerModifier.put("mulitkill", multiKillsExtra);
		integerModifier.put("glowamplifier", glowingAmplifier);
		integerModifier.put("bulletsinmagazine", bulletsInMagazine);
		integerModifier.put("magazinreloadtime", magazinReloadTime);
		integerModifier.put("laserguncooldown", lasergunCooldown);
		integerModifier.put("snipercooldown", sniperCooldown);
		integerModifier.put("shotguncooldown", shotgunCooldown);
		integerModifier.put("backstab", backstabExtra);
		doubleModifier.put("widthaddon", widthAddon);
		doubleModifier.put("heightaddon", heightAddon);
		integerModifier.put("spawnprotectiontime", 0);
		statusModifier.put("shootthroughblocks", shootThroughBlocks);
		statusModifier.put("highlightplayers", highLightPlayers);
//		statusModifier.put("spawnatbase", spawnAtBases); 
		statusModifier.put("withevents", withEvents);
		statusModifier.put("withammo", withAmmo);
		statusModifier.put("withminigun", withMinigun);
	}
	
	public static void setModifier(String name, int number) {
		if(integerModifier.get(name) != null) integerModifier.put(name, number);
		refreshModifiers();
	}
	public static void setModifier(String name, double number) {
		if(doubleModifier.get(name) != null) doubleModifier.put(name, number);
		refreshModifiers();
	}
	public static void setModifier(String name, boolean status) {
		if(statusModifier.get(name) != null) statusModifier.put(name, status);
		refreshModifiers();
	}
	
	
	public static void refreshModifiers() {
		points = integerModifier.get("points");
		snipeShotsExtra = integerModifier.get("snipe");
		minSnipeDistance = integerModifier.get("minsnipedistance");
		closeRangeExtra = integerModifier.get("closerange");
		pvpExtra = integerModifier.get("pvp");
		backstabExtra = integerModifier.get("backstab");
		headshotExtra = integerModifier.get("headshot");
		streakExtra = integerModifier.get("streak");
		minKillsForStreak = integerModifier.get("minkillstreak");
		multiKillsExtra = integerModifier.get("mulitkill");
		glowingAmplifier = integerModifier.get("glowamplifier");
		spawnProtSecs = integerModifier.get("spawnprotectiontime");
		widthAddon = doubleModifier.get("widthaddon");
		heightAddon = doubleModifier.get("heightaddon");
		shootThroughBlocks = statusModifier.get("shootthroughblocks");
		highLightPlayers = statusModifier.get("highlightplayers");
		withEvents = statusModifier.get("withevents");
//		spawnAtBases = statusModifier.get("spawnatbase");
		withAmmo = statusModifier.get("withammo");
		bulletsInMagazine = integerModifier.get("bulletsinmagazine");
		magazinReloadTime = integerModifier.get("magazinreloadtime");
		lasergunCooldown = integerModifier.get("laserguncooldown");
		sniperCooldown = integerModifier.get("snipercooldown");
		shotgunCooldown = integerModifier.get("shotguncooldown");
		withMinigun = statusModifier.get("withminigun");
	}
}

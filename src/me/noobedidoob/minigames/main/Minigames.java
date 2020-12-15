package me.noobedidoob.minigames.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.WorldCreator;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.noobedidoob.minigames.hideandseek.HideAndSeek;
import me.noobedidoob.minigames.hideandseek.HideCommands;
import me.noobedidoob.minigames.lasertag.LaserCommands;
import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.utils.MgUtils;

public class Minigames extends JavaPlugin implements Listener{
	
	public Lasertag lasertag;
	public HideAndSeek hideAndSeek;
	
	public LaserCommands laserCommands;
	public HideCommands hideCommands;
	
	public static String texturepackURL = "https://www.dropbox.com/s/d3l1ciaf58gvpbf/Laserguns.zip?dl=1";
	
	public static String worldName;
	
	public static World world;
	public static Location spawn;
	public static Location winnerPodium;
	public boolean worldFound = true;
	public boolean waitingForName = false;
	
	public List<String> exceptionStackTraces = new ArrayList<String>();
	
//	public static HashMap<String, Boolean> isPlayerMoving = new HashMap<String, Boolean>();
//	HashMap<String, Location> pLastLocation = new HashMap<String, Location>();
//	HashMap<Player, Boolean> wasMoving = new HashMap<Player, Boolean>();
	
	public void onEnable() {
		reloadConfig();
		setStaticMain();
		
//		new GUI(Bukkit.getServer().getName());
		
		Thread.setDefaultUncaughtExceptionHandler((Thread t, Throwable e) -> {
			StringWriter stackTraceWriter = new StringWriter();
        	e.printStackTrace(new PrintWriter(stackTraceWriter));
			String stackTrace = stackTraceWriter.toString();
			Bukkit.broadcastMessage(stackTrace);
		});
		
		lasertag = new Lasertag(this);
		hideAndSeek = new HideAndSeek(this);
		laserCommands = new LaserCommands(this);
		hideCommands = new HideCommands(this, hideAndSeek);
		getCommand("lasertag").setExecutor(laserCommands);
		getCommand("lasertag").setTabCompleter(laserCommands);
		getCommand("hideandseek").setExecutor(hideCommands);
		getCommand("hideandseek").setTabCompleter(hideCommands);
		Commands commands = new Commands(this);
		getCommand("minigames").setExecutor(commands);
		getCommand("minigames").setTabCompleter(commands);
		getCommand("lobby").setExecutor(commands);
		getCommand("lobby").setTabCompleter(commands);
		reloadConfig();
		if (!(new File(this.getDataFolder(), "config.yml").exists())) {
			MgUtils.inform("config.yml was not found! Creating config.yml...");
			getConfig().options().copyDefaults(true);
			saveConfig();
		}
		
		worldName = getConfig().getString("world");
		
		MgUtils.inform("Refreshing \"config_README.TXT\" ...");
		try {
			Files.copy(getClass().getResourceAsStream("/config_README.txt"), Paths.get(this.getDataFolder().getPath()+"/config_manual.txt"), StandardCopyOption.REPLACE_EXISTING);
			MgUtils.inform("Successfully refreshed\"config_README.TXT\"!");
		} catch (Exception e) {
			MgUtils.warn("Failed to refresh \"config_README.TXT\"! Caused by: "+e.getMessage());
			saveException(e);
			
		}
		
		setWorld();
		setServerResourcepack();
		
		lasertag.enable();
		hideAndSeek.enable();
		
		for(Entity e : world.getEntities()) {
			if(e.getType().equals(EntityType.ARMOR_STAND)) {
				((ArmorStand) e).remove();
				((ArmorStand) e).damage(100);
				((ArmorStand) e).setVisible(true);
			}
		}
		
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(p.getGameMode().equals(GameMode.ADVENTURE)) p.setAllowFlight(true);
		}
		
	}
	public void onDisable() {
		reloadConfig();
		lasertag.disable();
		hideAndSeek.disable();
		
		Bukkit.unloadWorld(world, !getConfig().getBoolean("resetworld"));
	}
	
	public static Minigames minigames;
	public void setStaticMain() {
		minigames = this;
	}
	
	@SuppressWarnings("deprecation")
	public void setWorld() {
		MgUtils.inform("Setting world...");
		for (World w : Bukkit.getWorlds()) {if (w.getEnvironment() == Environment.NORMAL) {Minigames.world = w;}}
		try {
			File serverFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getParentFile();
			if (!new File(serverFile.getPath()+"/"+worldName).exists()) {
				unzipWorld();
				MgUtils.inform("Loading world...");
				this.getServer().createWorld(new WorldCreator(worldName));
				Minigames.world = Bukkit.getWorld(worldName);
				if(Minigames.world == null) throw new NullPointerException("World not found");
				else MgUtils.inform("Created world successfully!");
			} else {
				this.getServer().createWorld(new WorldCreator(worldName));
				MgUtils.inform("Sucessfully set world. Use the command 'mg replaceworld' to replace the existing world with the original world");
			}
			spawn = new Location(world, 220.5, 7, -139.5);
			world.setSpawnLocation(spawn);
			Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "setworldspawn 220 7 -139");
			winnerPodium = new Location(world, 220.5, 7, -139.5);
			try { world.setGameRuleValue("keepInventory", "true");} catch (NullPointerException nexp) {}
		} catch (Exception e) {
			System.err.println("FAILED to set world! Caused by "+e.getMessage());
			e.printStackTrace();
			saveException(e);
			askForWorld();
		}
		
	}
	
	public void replaceWorld() {
		
	}
	
	public void askForWorld() {
		for (World w : Bukkit.getWorlds()) {if (w.getEnvironment() == Environment.NORMAL) {Minigames.world = w;}}
		worldFound = false;
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run() {
				Bukkit.broadcastMessage("");
				Bukkit.broadcastMessage("§cError occured while setting main world for Minigames to §d\"Minigames_world\"");
				Bukkit.broadcastMessage("§cPlease import the world and select it with §e/mg setworld <worldname>");
				Bukkit.broadcastMessage("§c-->or set the new main wolrd to one of the worlds below with §e/mg setworld §c by either entering the §e<name or the number> §cof the world.");
				Bukkit.broadcastMessage("§bCurrently available worlds:");
				int i = 1;
				for (World w : Bukkit.getWorlds()) {
					if (w.getEnvironment() == Environment.NORMAL) {
						world = w;
						Bukkit.broadcastMessage("§f"+i + ". " + w.getName());
						i++;
					}
				}
				waitingForName = true;
			}
		}, 20*5);
	}
	
	
	public void setServerResourcepack() {
		MgUtils.inform("Setting server resourcepack...");
		Path path = Paths.get(getDataFolder().getParentFile().getAbsolutePath()).getParent().resolve("server.properties");
        try {
            List<String> ogLines = Files.readAllLines(path);
            List<String> newLines = new ArrayList<String>();
            boolean empty = false;
            for(String line : ogLines) {
            	if(line.contains("resource") && !line.contains("sha1") && line.length() < 15) {
            		newLines.add("resource-pack="+texturepackURL);
            		empty = true;
            	} else {
            		newLines.add(line);
            	}
            }
            if(empty == true) {
            	Files.write(path, newLines);
            	MgUtils.inform("Server has no resourcepack. Setting server resourcepack to Minigames-texturepack!");
            } else {
            	MgUtils.inform("Server already has resourcepack");
            }
        } catch (IOException e) {
        	System.err.println("FAILED to set server resourcepack! Caused by: "+e.getMessage());
        	saveException(e);
        }
	}
	
	
	public void unzipWorld() throws URISyntaxException, IOException {
		String zipFilePath = "/Minigames_world.zip";
		File serverFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getParentFile();
		String destDir = serverFile.getPath()+"/"+worldName;
		String tempFilePath = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath()+"/TemporaryFile_please-delete.zip";
		
		InputStream zipStream = getClass().getResourceAsStream(zipFilePath);
		Path zipDestPath = Paths.get(tempFilePath);
		Files.copy(zipStream, zipDestPath, StandardCopyOption.REPLACE_EXISTING);
		
		File tempFile = new File(tempFilePath);
		File dir = new File(destDir);
        if(!dir.exists()) dir.mkdirs();
        byte[] buffer = new byte[1024];
		try {
			System.out.println(" "); System.out.println(" "); System.out.println(" "); System.out.println(" ");
			System.out.println("Extracting resource"+zipFilePath+" to "+destDir+"...");
			System.out.println("Creating temporary file \"TemporaryFile_please-delete.zip\" in \""+tempFile.getParentFile().getPath()+"\"...");
			FileInputStream fis = new FileInputStream(tempFile);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null){
                try {
					String fileName = ze.getName();
					File newFile = new File(destDir + File.separator + fileName);
					System.out.println("Extracting file: "+ze.getName());
					//create directories for sub directories in zip
					new File(newFile.getParent()).mkdirs();
					FileOutputStream fos = new FileOutputStream(newFile);
					int len;
					while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
					}
					fos.close();
					//close this ZipEntry
				} catch (FileNotFoundException e) {
					System.out.println("Error occured!");
				}
                zis.closeEntry();
				ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
		} catch (IOException e) {
			tempFile.delete();
			throw e;
		}
        if(tempFile.delete()) System.out.println("DELETED temporary file SUCCESSFULLY!");
		else System.out.println("FAILED to DELETE temporary file!");
        System.out.println("Extraction COMPLETE!");
        System.out.println(" "); System.out.println(" "); System.out.println(" "); System.out.println(" ");
    }
	
//	public void unzipWorld() throws URISyntaxException, IOException {
//		String zipFilePath = "/Minigames_world.zip";
//		File serverFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getParentFile();
//		String destDir = serverFile.getPath()+"/"+worldName;
//		String tempFilePath = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath()+"/TemporaryFile_please-delete.zip";
//		
//		InputStream zipStream = getClass().getResourceAsStream(zipFilePath);
//		Path zipDestPath = Paths.get(tempFilePath);
//		Files.copy(zipStream, zipDestPath, StandardCopyOption.REPLACE_EXISTING);
//		
//		File tempFile = new File(tempFilePath);
//		ZipFile zipFile = new ZipFile(new File(tempFilePath));
//		try {
//			System.out.println(" "); System.out.println(" "); System.out.println(" "); System.out.println(" ");
//			System.out.println("Extracting resource"+zipFilePath+" to "+destDir+"...");
//			System.out.println("Creating temporary file \"TemporaryFile_please-delete.zip\" in \""+tempFile.getParentFile().getPath()+"\"...");
//	        Enumeration<? extends ZipEntry> entries = zipFile.entries();
//	        while(entries.hasMoreElements()){
//	            ZipEntry entry = entries.nextElement();
//	            if(entry.isDirectory()){
//	                System.out.println("Extracting directory : " + entry.getName());
//	                String destPath = destDir + File.separator + entry.getName();
//	                File file = new File(destPath);
//	                file.mkdirs();
//	            } else {
//	                try {
//						String destPath = destDir + File.separator + entry.getName();
//						ZipInputStream zis = new ZipInputStream(new FileInputStream(tempFile));
//						byte[] buffer = new byte[1024];
//						FileOutputStream fos = new FileOutputStream(destPath);
//						int len;
//						while ((len = zis.read(buffer)) > 0) {
//							fos.write(buffer, 0, len);
//						}
//						fos.close();
//						zis.close();
//						System.out.println("Extracting file      : " + entry.getName());
//					} catch (FileNotFoundException e) {
//						System.out.println("Error occurd: "+e.getMessage());
//					} 
//	            }
//	        }
//		} catch (IOException e) {
//			tempFile.delete();
//			throw e;
//		}
//        zipFile.close();
//        if(tempFile.delete()) System.out.println("DELETED temporary file SUCCESSFULLY!");
//		else System.out.println("FAILED to DELETE temporary file!");
//        System.out.println("Extraction COMPLETE!");
//        System.out.println(" "); System.out.println(" "); System.out.println(" "); System.out.println(" ");
//    }
	
	public void saveException(Exception e) {
		StringWriter stackTraceWriter = new StringWriter();
    	e.printStackTrace(new PrintWriter(stackTraceWriter));
    	String exceptionMessage = stackTraceWriter.toString();
		exceptionStackTraces.add(exceptionMessage);
		
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		
		File exceptionFile = new File(this.getDataFolder().getPath()+"/ExceptionStackTraces.txt");
		try {
			String breaks = "";
			if(exceptionFile.createNewFile()) {
				MgUtils.inform("Created ExceptionStackTraces.txt");
				breaks = "\n\n";
			}
			FileWriter writer = new FileWriter(exceptionFile);
			writer.write(breaks+"["+format.format(date)+"]\n"+exceptionMessage);
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
}

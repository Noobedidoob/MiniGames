package me.noobedidoob.minigames.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.noobedidoob.minigames.hideandseek.HideAndSeek;
import me.noobedidoob.minigames.lasertag.Lasertag;

public class Minigames extends JavaPlugin implements Listener{
	
	public Lasertag lasertag;
	public HideAndSeek hideAndSeek;
	
	public static String texturepackURL = "https://www.dropbox.com/s/d3l1ciaf58gvpbf/Laserguns.zip?dl=1";
	
	public static String worldName;
	
	public static World world;
	public static Location spawn;
	public static Location winnerPodium;
	public boolean worldFound = true;
	public boolean waitingForName = false;
	
	public List<String> exceptionStackTraces = new ArrayList<String>();

	public static Minigames minigames;
	public void onEnable() {
		minigames = this;
		reloadConfig();
		
		
		Commands commands = new Commands(this);
		getCommand("minigames").setExecutor(commands);
		getCommand("minigames").setTabCompleter(commands);
		getCommand("lobby").setExecutor(commands);
		getCommand("test").setExecutor(new Test());
		new Listeners(this);
		
		reloadConfig();
		if (!(new File(this.getDataFolder(), "config.yml").exists())) {
			inform("config.yml was not found! Creating config.yml...");
			getConfig().options().copyDefaults(true);
			saveConfig();
		}
		
		worldName = getConfig().getString("world");
		
//		try {
//			Files.copy(getClass().getResourceAsStream("/config_README.txt"), Paths.get(this.getDataFolder().getPath()+"/config_manual.txt"), StandardCopyOption.REPLACE_EXISTING);
//			inform("Successfully refreshed\"config_README.TXT\"!");
//		} catch (Exception e) {
//			warn("Failed to refresh \"config_README.TXT\"! Caused by: "+e.getMessage());
//			saveException(e);
//			
//		}
		
		setWorld();
		if(getConfig().getBoolean("set-server-texturepack")) setServerResourcepack();
		
		lasertag = new Lasertag(this);
		lasertag.enable();
//		hideAndSeek = new HideAndSeek(this);
//		hideAndSeek.enable();
		
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(p.getGameMode().equals(GameMode.ADVENTURE)) p.setAllowFlight(true);
		}
		
		
	}
	public void onDisable() {
		reloadConfig();
		if(lasertag != null) lasertag.disable();
		if(hideAndSeek != null) hideAndSeek.disable();
		
		Bukkit.unloadWorld(world, !getConfig().getBoolean("resetworld"));
		
		Bukkit.getOnlinePlayers().forEach(p ->{
			p.getInventory().clear();
			p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			p.teleport(spawn);
		});
	}
	
	
	@SuppressWarnings("deprecation")
	public void setWorld() {
		if (Bukkit.getWorld(worldName) == null) {
			try {
				File serverFile = new File(getDataFolder().getParentFile().getParentFile().getParentFile().getPath());
				if (!new File(serverFile.getPath() + "/" + worldName).exists()) {
					unzipWorld();
					inform("Loading world...");
					this.getServer().createWorld(new WorldCreator(worldName));
					world = Bukkit.getWorld(worldName);
					if (Minigames.world == null) throw new NullPointerException("World not found after extraction");
					else inform("Created world successfully!");
				} else {
					this.getServer().createWorld(new WorldCreator(worldName));
					world = Bukkit.getWorld(worldName);
					if (Minigames.world == null) throw new NullPointerException("World not found after loading!");
					inform("Sucessfully loaded minigame world");
				}
			} catch (Exception e) {
				e.printStackTrace();
				severe("An error occured while trying to get the Minigames world \"" + worldName + "\"! Please set the world manually or try again! Disabeling...");
				Bukkit.getPluginManager().disablePlugin(this);
			} 
		} 
		world = Bukkit.getWorld(worldName);
		spawn = new Location(world, 220.5, 7, -139.5);
		world.setSpawnLocation(spawn);
		Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "setworldspawn 220 7 -139");
		winnerPodium = new Location(world, 220.5, 7, -139.5);
		try {
			world.setGameRuleValue("keepInventory", "true");
		} catch (NullPointerException nexp) {
		}
	}
	
	public void setServerResourcepack() {
//		Path path = Paths.get(getDataFolder().getParentFile().getAbsolutePath()).getParent().resolve("server.properties");
		File propsFile = Paths.get(getDataFolder().getParentFile().getAbsolutePath()).getParent().resolve("server.properties").toFile();
        try {
        	Properties props = new Properties();
        	props.load(new FileInputStream(propsFile));
        	if(props.getProperty("resource-pack", "") == "") {
        		props.setProperty("resource-pack", texturepackURL);
        		props.store(new FileOutputStream(propsFile), null);
        	} else if(props.getProperty("resource-pack", "") != texturepackURL){
        		warn("Server already has resourcepack! Please remove the resourcepack in order to set the Minigames-texturepack!");
        	}
        	
//            List<String> ogLines = Files.readAllLines(path);
//            List<String> newLines = new ArrayList<String>();
//            boolean empty = false;
//            for(String line : ogLines) {
//            	if(line.contains("resource") && !line.contains("sha1")) {
//            		newLines.add("resource-pack="+texturepackURL);
//            		empty = true;
//            	} else {
//            		newLines.add(line);
//            	}
//            }
//            if(empty == true) {
//            	Files.write(path, newLines);
//            	inform("Successfully set server resourcepack to Minigames-texturepack!");
//            } else {
//            	warn("Server already has resourcepack! Please remove the resourcepack first!");
//            }
        } catch (IOException e) {
        	System.err.println("FAILED to set server resourcepack! Caused by: "+e.getMessage());
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
	
	
	public static void sendMessage(CommandSender target, String... msgs) {
		for(String msg : msgs) {
			target.sendMessage("§7[§6Minigames§7] §e"+msg);
		}
	}

	public static Logger logger = Bukkit.getLogger();
	public static void inform(String msg) {
		logger.log(Level.INFO, "[Minigames] "+msg);
	}
	public static void warn(String msg) {
		logger.warning("[Minigames] "+msg);
	}
	public static void severe(String msg) {
		logger.severe("[Minigames] "+msg);
	}
}

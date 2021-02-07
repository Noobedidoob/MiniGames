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
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
		getCommand("lobby").setTabCompleter(commands);
		getCommand("test").setExecutor(new Test());
		new Listeners(this);
		
		reloadConfig();
		if (!(new File(this.getDataFolder(), "config.yml").exists())) {
			inform("config.yml was not found! Creating config.yml...");
			getConfig().options().copyDefaults(true);
			saveConfig();
		}
		
		worldName = getConfig().getString("world");
		
		try {
			Files.copy(getClass().getResourceAsStream("/config_README.txt"), Paths.get(this.getDataFolder().getPath()+"/config_manual.txt"), StandardCopyOption.REPLACE_EXISTING);
			inform("Successfully refreshed\"config_README.TXT\"!");
		} catch (Exception e) {
			warn("Failed to refresh \"config_README.TXT\"! Caused by: "+e.getMessage());
			saveException(e);
			
		}
		
		setWorld();
		setServerResourcepack();
		
		lasertag = new Lasertag(this);
		lasertag.enable();
		hideAndSeek = new HideAndSeek(this);
		hideAndSeek.enable();
		
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(p.getGameMode().equals(GameMode.ADVENTURE)) p.setAllowFlight(true);
		}
		
		
	}
	public void onDisable() {
		reloadConfig();
		lasertag.disable();
		hideAndSeek.disable();
		
		Bukkit.unloadWorld(world, !getConfig().getBoolean("resetworld"));
		
		Bukkit.getOnlinePlayers().forEach(p ->{
			p.getInventory().clear();
			p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			p.teleport(spawn);
		});
	}
	
	
	@SuppressWarnings("deprecation")
	public void setWorld() {
		for (World w : Bukkit.getWorlds()) {if (w.getEnvironment() == Environment.NORMAL) {Minigames.world = w;}}
		try {
			File serverFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getParentFile();
			if (!new File(serverFile.getPath()+"/"+worldName).exists()) {
				unzipWorld();
				inform("Loading world...");
				this.getServer().createWorld(new WorldCreator(worldName));
				Minigames.world = Bukkit.getWorld(worldName);
				if(Minigames.world == null) throw new NullPointerException("World not found after extraction");
				else inform("Created world successfully!");
			} else {
				this.getServer().createWorld(new WorldCreator(worldName));
				inform("Sucessfully loaded minigame world");
			}
			if(getConfig().getBoolean("disable-other-worlds")) disableServerWorlds();
			else {
				inform("Consider activating the \"disable-other-worlds\" mode in the config.yml in order to start the server faster!");
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
	
	public void disableServerWorlds(){
		try {
			File propFile = Paths.get(getDataFolder().getParentFile().getAbsolutePath()).getParent().resolve("server.properties").toFile();
			FileInputStream in = new FileInputStream(propFile);
			Properties props = new Properties();
			props.load(in);
			in.close();
			
			File bukkFile = Paths.get(getDataFolder().getParentFile().getAbsolutePath()).getParent().resolve("bukkit.yml").toFile();
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(bukkFile);

			if(!(props.getProperty("level-name") != "Minigames_world") | !(props.getProperty("allow-nether") != "false") | !(cfg.getString("settings.allow-end") != "false")) {
				inform("Disabeling other worlds on this server...");
				FileOutputStream out = new FileOutputStream(propFile);
				props.setProperty("level-name", "Minigames_world");
				props.setProperty("allow-nether", "false");
				props.store(out, null);
				out.close();
				
				cfg.set("settings.allow-end", "false");
				cfg.save(bukkFile);
				inform("Successfully disabled the other worlds from the server in the server.properties and bukkit.yml files! This changes will take effect when restarting the server!");
			}
				
		} catch (IOException e) {
			warn("Error occured while disabeling the other worlds from the server! Please try again or do it manually!");
			warn("Error: "+e.getLocalizedMessage());
		}

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
		inform("Setting server resourcepack...");
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
            	inform("Server has no resourcepack. Setting server resourcepack to Minigames-texturepack!");
            } else {
            	inform("Server already has resourcepack");
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
				inform("Created ExceptionStackTraces.txt");
				breaks = "\n\n";
			}
			FileWriter writer = new FileWriter(exceptionFile);
			writer.write(breaks+"["+format.format(date)+"]\n"+exceptionMessage);
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	
//	public static void sendPlayerActionBarMessage(Player p, ChatColor color ,String msg) {
//		p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.BOLD+""+color+msg));
//	}

	public static Logger logger = Bukkit.getLogger();
	public static void inform(String msg) {
		logger.log(Level.INFO, "[MiniGamse] "+msg);
	}
	public static void warn(String msg) {
		logger.warning("[MiniGamse] "+msg);
	}
	public static void severe(String msg) {
		logger.severe("[MiniGamse] "+msg);
	}
}

package me.noobedidoob.minigames;

import me.noobedidoob.minigames.lasertag.Lasertag;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Minigames extends JavaPlugin implements Listener{
	
	public static String TEXTUREPACK_URL = "https://www.dropbox.com/s/d3l1ciaf58gvpbf/Laserguns.zip?dl=1";
	public static String WORLD_NAME;
	
	public Lasertag lasertag;

	public World world;
	public Location spawn;
	public Location winnerPodium;

	public static Minigames INSTANCE;

	public void onEnable() {
		INSTANCE = this;
		
		Commands commands = new Commands(this);
		Objects.requireNonNull(getCommand("minigames")).setExecutor(commands);
		Objects.requireNonNull(getCommand("minigames")).setTabCompleter(commands);
		Objects.requireNonNull(getCommand("lobby")).setExecutor(commands);
		Objects.requireNonNull(getCommand("test")).setExecutor(new Test(this));
		new Listeners(this);
		
		reloadConfig();
		if (!(new File(this.getDataFolder(), "config.yml").exists())) {
			inform("config.yml was not found! Creating config.yml...");
			getConfig().options().copyDefaults(true);
			saveConfig();
		}
		
		WORLD_NAME = getConfig().getString("world");
		
		setWorld();
		if(getConfig().getBoolean("set-server-texturepack")) setServerResourcepack();
		
		lasertag = new Lasertag(this);
		lasertag.enable();

		for(Player p : Bukkit.getOnlinePlayers()) {
			if(p.getGameMode().equals(GameMode.ADVENTURE)) p.setAllowFlight(true);
		}
		
		
	}
	public void onDisable() {
		reloadConfig();
		if(lasertag != null) lasertag.disable();

		Bukkit.unloadWorld(world, !getConfig().getBoolean("resetworld"));
		
		Bukkit.getOnlinePlayers().forEach(p ->{
			p.getInventory().clear();
			p.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard());
			p.teleport(spawn);
		});
	}
	
	
	private void setWorld() {
		if (Bukkit.getWorld(WORLD_NAME) == null) {
			try {
				File serverFile = new File(getDataFolder().getParentFile().getParentFile().getParentFile().getPath());
				if (!new File(serverFile.getPath() + "/" + WORLD_NAME).exists()) {
					unzipWorld();
					inform("Loading world...");
					this.getServer().createWorld(new WorldCreator(WORLD_NAME));
					world = Bukkit.getWorld(WORLD_NAME);
					if (world == null) throw new NullPointerException("World not found after extraction");
					else inform("Created world successfully!");
				} else {
					this.getServer().createWorld(new WorldCreator(WORLD_NAME));
					world = Bukkit.getWorld(WORLD_NAME);
					if (world == null) throw new NullPointerException("World not found after loading!");
					inform("Sucessfully loaded minigame world");
				}
			} catch (Exception e) {
				e.printStackTrace();
				severe("An error occured while trying to get the Minigames world \"" + WORLD_NAME + "\"! Please set the world manually or try again! Disabeling...");
				Bukkit.getPluginManager().disablePlugin(this);
			} 
		}
		world = Bukkit.getWorld(WORLD_NAME);
		spawn = new Location(world, 220.5, 7, -139.5);
		assert world != null;
		world.setSpawnLocation(spawn);
		winnerPodium = new Location(world, 220.5, 7, -139.5);
		world.setGameRule(GameRule.KEEP_INVENTORY, true);
		world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
		world.setTime(6000);
	}
	
	private void setServerResourcepack() {
//		Path path = Paths.get(getDataFolder().getParentFile().getAbsolutePath()).getParent().resolve("server.properties");
		File propsFile = Paths.get(getDataFolder().getParentFile().getAbsolutePath()).getParent().resolve("server.properties").toFile();
        try {
        	Properties props = new Properties();
        	props.load(new FileInputStream(propsFile));
        	if(props.getProperty("resource-pack", "").equals("")) {
        		props.setProperty("resource-pack", TEXTUREPACK_URL);
        		props.store(new FileOutputStream(propsFile), null);
        	} else if(!props.getProperty("resource-pack", "").equals(TEXTUREPACK_URL)){
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
	
	
	
	
	private void unzipWorld() throws URISyntaxException, IOException {
		String zipFilePath = "/Minigames_world.zip";
		File serverFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getParentFile();
		String destDir = serverFile.getPath()+"/"+WORLD_NAME;
		String tempFilePath = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath()+"/TemporaryFile_please-delete.zip";
		
		InputStream zipStream = getClass().getResourceAsStream(zipFilePath);
		Path zipDestPath = Paths.get(tempFilePath);
		Files.copy(zipStream, zipDestPath, StandardCopyOption.REPLACE_EXISTING);
		
		File tempFile = new File(tempFilePath);
		File dir = new File(destDir);
        if(!dir.exists()) {
        	if(dir.mkdirs()){
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
							if(new File(newFile.getParent()).mkdirs()) {
								FileOutputStream fos = new FileOutputStream(newFile);
								int len;
								while ((len = zis.read(buffer)) > 0) {
									fos.write(buffer, 0, len);
								}
								fos.close();
								//close this ZipEntry
							}
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
					//noinspection ResultOfMethodCallIgnored
					tempFile.delete();
					throw e;
				}
				if(tempFile.delete()) System.out.println("DELETED temporary file SUCCESSFULLY!");
				else System.out.println("FAILED to DELETE temporary file!");
				System.out.println("Extraction COMPLETE!");
				System.out.println(" "); System.out.println(" "); System.out.println(" "); System.out.println(" ");
			} else {
				System.out.println("Error occurred while creating the world directory");
			}
		}

    }
	
	
	public static void teleportPlayersToSpawn(Player... players) {
		for(Player p : players) {
			p.teleport(INSTANCE.spawn);
		}
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

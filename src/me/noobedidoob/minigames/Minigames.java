package me.noobedidoob.minigames;

import me.noobedidoob.minigames.lasertag.Lasertag;
import me.noobedidoob.minigames.utils.Area;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
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
	
	public static String TEXTUREPACK_URL = "https://www.dropbox.com/s/7usi9cz2hwq53jx/Lasertag-Texturepack.zip?dl=1";
	public static String WORLD_NAME;
	
	public Lasertag lasertag;

	public World world;
	public Location spawn;
	public Area lobbyArea;
	public Location winnerPodium;

	public static Minigames INSTANCE;

	public void onEnable() {
		INSTANCE = this;

	
		reloadConfig();
		if (!(new File(this.getDataFolder(), "config.yml").exists())) {
			inform("config.yml was not found! Creating config.yml...");
			getConfig().options().copyDefaults(true);
			saveConfig();
		}
		if(getConfig().getBoolean("set-server-texturepack")) setServerResourcepack();

		WORLD_NAME = getConfig().getString("world");

		// TODO: 23.02.2021 Fix Minigames_world.zip export
		exportZips();
		if(!setWorld()) return;

		Commands commands = new Commands(this);
		getCommand("minigames").setExecutor(commands);
		getCommand("minigames").setTabCompleter(commands);
		getCommand("lobby").setExecutor(commands);
		getCommand("test").setExecutor(new Test(this));
		new Listeners(this);



		lasertag = new Lasertag(this);
		lasertag.enable();

		for(Player p : Bukkit.getOnlinePlayers()) {
			if(p.getGameMode().equals(GameMode.ADVENTURE)) p.setAllowFlight(true);
		}
	}
	public void onDisable() {
		try {
			reloadConfig();
			if(lasertag != null) lasertag.disable();

			Bukkit.unloadWorld(world, !getConfig().getBoolean("resetworld"));

			Bukkit.getOnlinePlayers().forEach(p ->{
				p.getInventory().clear();
				p.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard());
				if(!lobbyArea.isInside(p.getLocation()) && !Lasertag.isPlayerTesting(p)) p.teleport(spawn);
			});
		} catch (Exception ignored) {
		}
	}

	private void setServerResourcepack() {
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
		} catch (IOException e) {
			System.err.println("FAILED to set server resourcepack! Caused by: "+e.getMessage());
		}
	}

	private void exportZips(){
		if(!new File(getDataFolder()+ "/Minigames_world.zip").exists()) {
			try {
//				System.out.println(getClass().getResourceAsStream("/Minigames_world.zip") == null);
				Files.copy(getClass().getResourceAsStream("/Minigames_world.zip"), Paths.get(getDataFolder()+ "/Minigames_world.zip"), StandardCopyOption.REPLACE_EXISTING);
			} catch (Exception e) {
				Minigames.warn("Error occured while copying \"Minigames_world.zip\" to plugins datafolder");
//				e.printStackTrace();
			}
		}
		if(!new File(getDataFolder()+"/Lasertag-Texturepack.zip").exists()) {
			try {
//				System.out.println(getClass().getResourceAsStream("/Lasertag-Texturepack.zip") == null);
				Files.copy(getClass().getResourceAsStream("/Lasertag-Texturepack.zip"), Paths.get(getDataFolder()+"/Lasertag-Texturepack.zip"), StandardCopyOption.REPLACE_EXISTING);
			} catch (Exception e) {
				Minigames.warn("Error occured while copying \"Lasertag-Texturepack.zip\" to plugins datafolder");
//				e.printStackTrace();
			}
		}
	}


	private boolean setWorld() {
		if (Bukkit.getWorld(WORLD_NAME) == null) {
			try {
				File serverFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getParentFile();
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
				return false;
			}
		}
		world = Bukkit.getWorld(WORLD_NAME);
		spawn = new Location(world, 220.5, 7, -139.5);
		lobbyArea = new Area(195,4,-171,245,22,-109);
		assert world != null;
		world.setSpawnLocation(spawn);
		winnerPodium = new Location(world, 220.5, 7, -139.5);
		world.setGameRule(GameRule.KEEP_INVENTORY, true);
		world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
		world.setTime(6000);
		return true;
	}


	
	
	private void unzipWorld() throws Exception {
		String zipFilePath = "/Minigames_world.zip";
		File serverFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getParentFile();
		String destDir = serverFile.getPath()+"/"+WORLD_NAME;
		String tempFilePath = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath()+"/TemporaryFile_please-delete.zip";
		
		InputStream zipStream = getClass().getResourceAsStream(zipFilePath);
		if(zipStream == null) throw new NullPointerException("Could not find resource \"Minigames_world.zip\"");
		Path zipDestPath = Paths.get(tempFilePath);
		Files.copy(zipStream, zipDestPath, StandardCopyOption.REPLACE_EXISTING);
		File tempFile = new File(tempFilePath);
		File dir = new File(destDir);
        if(!dir.exists()) {
        	if(dir.mkdirs()){
				byte[] buffer = new byte[1024];
				try {
					System.out.println(" ");
					System.out.println(" ");
					System.out.println(" ");
					System.out.println(" ");
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
							System.out.println("Error occured while extracting Lasertag-Texturepack.zip");
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
				if(tempFile.delete()) {
					System.out.println("DELETED temporary file SUCCESSFULLY!");
				}
				else System.out.println("FAILED to delete temporary file at "+tempFilePath);
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

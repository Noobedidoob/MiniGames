package me.noobedidoob.minigames.lasertag.methods;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.noobedidoob.minigames.main.Minigames;
import me.noobedidoob.minigames.utils.MgUtils;

public class PlayerTeleporter {
	
	public static Location getPlayerSpawnLoc(Player p) {
		if(Game.teams()) {
			if(Game.spawnAtBases) {
				return Game.map().getTeamSpawnLoc(Game.getTeamColor(Game.getPlayerTeam(p)).getChatColor());
			} else {
				return Game.map().getRandomSpawnLocation();
			}
		} else {
			if(Game.spawnAtBases) {
				return Game.map().getTeamSpawnLoc(Game.getPlayerColor(p).getChatColor());
			} else {
				return Game.map().getRandomSpawnLocation();
			}
		}
	}
	
	
	
	public static void gatherPlayers(List<Player> winners) {
		for(Player p : Game.players()) {
			boolean isWinner = false;
			for(Player winner : winners) if(winner == p) isWinner = true;
			
			if(isWinner) p.teleport(Minigames.winnerPodium);
			else p.teleport(Minigames.spawn.subtract(10, 0, 10).add(MgUtils.randomDouble(0, 20), 0, MgUtils.randomDouble(0, 20)));
		}
	}
	
	
}

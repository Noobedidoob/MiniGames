package me.noobedidoob.minigames.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Tagger{

    private final UUID playerUUID;
    public Tagger(Player p){
        this.playerUUID = p.getUniqueId();
        PLAYER_TAGGER.put(p.getUniqueId(),this);
    }

    public Player getPlayer(){
        return Bukkit.getPlayer(playerUUID);
    }

    private static final HashMap<UUID, Tagger> PLAYER_TAGGER = new HashMap<>();
    public static Tagger getTaggerFromPlayer(Player p){
        return PLAYER_TAGGER.get(p.getUniqueId());
    }

}

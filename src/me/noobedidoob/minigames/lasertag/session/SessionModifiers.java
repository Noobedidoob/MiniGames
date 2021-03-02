package me.noobedidoob.minigames.lasertag.session;

import java.util.HashMap;

import me.noobedidoob.minigames.Minigames;

public class SessionModifiers {
	
	public SessionModifiers() {
		for(Mod m : Mod.values()) {
			modValues.put(m, m.getOg());
		}
	}
	public void reset() {
		for(Mod m : Mod.values()) {
			modValues.put(m, m.getOg());
		}
	}
	
	public HashMap<Mod, Object> modValues = new HashMap<>();
	
	public Object get(Mod m) {
		return modValues.get(m);
	}
	public int getInt(Mod m) {
	 	try {
			return (int) modValues.get(m);
		} catch (Exception e) {
			return 0;
		}
	}
    public double getDouble(Mod m) {
	    try {
	    	return (double) modValues.get(m);
		} catch (Exception e) {
			return 0;
		}
    }
    public boolean getBoolean(Mod m) {
        try {
        	return (boolean) modValues.get(m);
		} catch (Exception e) {
			return false;
		}
    }
    
    public void set(Mod m, Object value) {
    	if(value.getClass() == m.getOg().getClass() | (value instanceof Integer && m.getOg() instanceof Double)) {
        	modValues.put(m, value);
    	} else {
    		System.out.println("Error at setting mod! Value types are not the same!");
    	}
    }

	public enum Mod{
		POINTS(1, "Normal amount of points a player gets"),
		SHOT_KILL_EXTRA_POINTS(0, "Extra points when a player shot normal"),
		SNIPER_KILL_EXTRA_POINTS(1, "Extra points when killing with snipe-shot"),
		HEADSHOT_EXTRA_POINTS(1, "Extra ponts when killing with headshot"),
		BACKSTAB_EXTRA_POINTS(2, "Extra points when backstabbing"),
		PVP_KILL_EXTRA_POINTS(0, "Extra ponts when killed at melee"),
		STREAK_EXTRA_POINTS(2, "Extra points when having a streak"),
		STREAK_SHUTDOWN_EXTRA_POINTS(2, "Extra points when shutting down a streak"),
		MULTIKILLS_EXTRA_POINTS(3, "Extra points when killing multiple players at once"),
		CAPTURE_THE_FLAG_POINTS(2, "Points a player gets when delivering an enemys flag"),

		MINIMAL_SNIPE_DISTANCE(30, "Minimal distance of a shot to be a sniper shot"),

		MINIMAL_KILLS_FOR_STREAK(5, "Minimal kill amount required for a streak"),

		SPAWNPROTECTION_SECONDS(10, "Seconds a player is protected after spawning"),

		WIDTH_ADDON(0D, "ADDON TO A PLAYERS HITBOX WIDTH"),
		HEIGHT_ADDON(0d, "Addon to a players hitbox height"),
		
		SHOOT_THROUGH_BLOCKS(false, "Shoot through blocks"),
		HIGHLIGHT_PLAYERS(false, "Making players glow and more visible"),
		HIGHLIGHT_POWER(255, "Glowing power"),
		
		LASERGUN_COOLDOWN_TICKS(12, "Ticks a lasergun takes to cool down"),
		LASERGUN_MULTIWEAPONS_COOLDOWN_TICKS(2, "Ticks a lasergun takes to cool down when playing with multiple weapons"),
		SNIPER_COOLDOWN_TICKS(100, "Ticks a sniperrifle takes to cool down"),
		SHOTGUN_COOLDOWN_TICKS(40, "Ticks a shotgun takes to cool down"),
		SNIPER_AMMO_BEFORE_COOLDOWN(3, "Maximal sniper ammo"),
		
		LASERGUN_NORMAL_DAMAGE(100, "Normal lasergun shot damage"),
		LASERGUN_MULTIWEAPONS_DAMAGE(7, "lasergun shot damage when playing with multiple weapons"),
		LASERGUN_PVP_DAMAGE(8, "Lasergun melee damage (only without multiweapons"),
		SHOTGUN_DAMAGE(11, "Shotgun shot damage"),
		SNIPER_DAMAGE(100, "Sniper shot damage"),
		DAGGER_DAMAGE(13, "Stabber melee damage"),
		
		HEADSHOT_MULTIPLIKATOR(1.3d, "Extra damage when hitting the head"),
		SNIPER_SHOT_MULTIPLIKATOR(1.3d, "Extra damage when sniping");
		
		private Object ogValue;
		private final String description;
		private final String valueTypeName;
		Mod(Object value, String description) {
            this.ogValue = value;
            this.description = description;
            if(value instanceof Integer) valueTypeName = "Full-Number";
            else if(value instanceof Double) valueTypeName = "Number";
            else valueTypeName = "true/false";
        }
		

        public Object getOg() {
            return ogValue;
        }
        public int getOgInt() {
        	try {
				return (int) ogValue;
			} catch (Exception e) {
				return 0;
			}
        }
        public double getOgDouble() {
        	try {
				return (double) ogValue;
			} catch (Exception e) {
				return 0;
			}
        }
        public boolean getOgBoolean() {
        	try {
				return (boolean) ogValue;
			} catch (Exception e) {
				return false;
			}
        }
        
        public void setOgValue(Object value) {
        	if(value == null) return;
        	if(value.getClass() == this.getOg().getClass() | (value instanceof Integer && ogValue instanceof Double)) {
            	ogValue = value;
        	} else {
        		System.out.println("Error at setting mod! Value types are not the same!");
        	}
        }
        
        
        public String getDescription() {
        	return description;
        }
        public String getValueTypeName() {
        	return valueTypeName;
        }
        
        public static Mod getMod(String name) {
        	for(Mod m : Mod.values()) {
        		if(m.name().equalsIgnoreCase(name.replace(" ", ""))) return m;
        	}
        	return null;
        }
        
        
        
        public static void registerMods(Minigames minigames) {
        	for(Mod m : Mod.values()) {
        		String configModName = "Lasertag.mods."+m.name().toLowerCase().replace("_", "-");
				if(minigames.getConfig().contains(configModName)) m.setOgValue(minigames.getConfig().get(configModName));
				else minigames.getConfig().set(configModName,m.ogValue);
        	}
        }
	}
}

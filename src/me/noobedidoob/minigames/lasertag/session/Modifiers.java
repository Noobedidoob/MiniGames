package me.noobedidoob.minigames.lasertag.session;

import java.util.HashMap;

public class Modifiers {
	
	public Modifiers() {
		for(Mod m : Mod.values()) {
			modValues.put(m, m.getOg());
		}
	}
	public void reset() {
		for(Mod m : Mod.values()) {
			modValues.put(m, m.getOg());
		}
	}
	
	private HashMap<Mod, Object> modValues = new HashMap<Mod, Object>();
	
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
    	if(value.getClass() == m.getOg().getClass()) {
        	modValues.put(m, value);
    	}
    }

    public boolean withMultiweapons() { return getBoolean(Mod.WITH_MULTIWEAPONS); }
    public boolean multiWeapons() { return getBoolean(Mod.WITH_MULTIWEAPONS); }
    
	public enum Mod{
		POINTS(1, "Normal amount of points a player gets"),
		WITH_MULTIWEAPONS(false, "Playing with multiple weapons"),
		SNIPER_SHOT_EXTRA(1, "Extra points when killing with snipe-shot"),
		MINIMAL_SNIPE_DISTANCE(35, "Minimal distance of a shot to be a sniper shot"),
		NORMAL_SHOT_EXTRA(0, "Extra points when a player shot normal"),
		BACKSTAB_EXTRA(0, "Extra points when backstabbing"),
		PVP_EXTRA(0, "Extra ponts when killed at melee"),
		HEADSHOT_EXTRA(1, "Extra ponts when killing with headshot"),
		STREAK_EXTRA(2, "Extra points when having a streak"),
		STREAK_SHUTDOWN(2, "Extra points when shutting down a streak"),
		MIN_KILLS_FOR_STREAK(5, "Minimal kill amount required for a streak"),
		MULTIKILLS_EXTRA(2, "Extra points when killing multiple players at once"),
		SPAWNPROTECTION_SECONDS(10, "Seconds a player is protected after spawning"),
		WIDTH_ADDON(0d, "Addon to a players hitbox width"),
		HEIGHT_ADDON(0d, "Addon to a players hitbox height"),
		SHOOT_THROUGH_BLOCKS(false, "Shoot through blocks"),
		HIGHLIGHT_PLAYERS(false, "Making players glow and more visible"),
		HIGHLIGHT_POWER(255, "Glowing power"),
		LASERGUN_COOLDOWN_TICKS(12, "Ticks a lasergun takes to cool down"),
		LASERGUN_MULTIWEAPONS_COOLDOWN_TICKS(2, "Ticks a lasergun takes to cool down when playing with multiple weapons"),
		SNIPER_COOLDOWN_TICKS(100, "Ticks a sniperrifle takes to cool down"),
		SHOTGUN_COOLDOWN_TICKS(40, "Ticks a shotgun takes to cool down"),
		SNIPER_AMMO_BEFORE_COOLDOWN(2, "Maximal sniper ammo"),
		LASERGUN_NORMAL_DAMAGE(100, "Normal lasergun shot damage"),
		LASERGUN_MULTIWEAPONS_DAMAGE(9, "lasergun shot damage when playing with multiple weapons"),
		LASERGUN_PVP_DAMAGE(10, "Lasergun melee damage (only without multiweapons"),
		SHOTGUN_DAMAGE(11, "Shotgun shot damage"),
		SNIPER_DAMAGE(100, "Sniper shot damage"),
		STABBER_DAMAGE(10, "Stabber melee damage");
		
		
		
		private Object ogValue;
		private String description;
		private String valueTypeName;
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
        
        
        
        
        public String getDescription() {
        	return description;
        }
        public String getValueTypeName() {
        	return valueTypeName;
        }
        
	}
}

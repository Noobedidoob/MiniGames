package me.noobedidoob.minigames.lasertag.session;

import java.util.HashMap;

import me.noobedidoob.minigames.Minigames;
import me.noobedidoob.minigames.lasertag.methods.Mod;

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


}

package me.noobedidoob.minigames.lasertag.methods;

import me.noobedidoob.minigames.Minigames;
import me.noobedidoob.minigames.utils.Pair;
import me.noobedidoob.minigames.utils.Utils.ValueType;

public enum Mod{
    POINTS(1, true, 1, 5, "Normal amount of points a player gets"),
    SHOT_KILL_EXTRA_POINTS(0, true,-2, 5, "Extra points when a player shot normal"),
    SNIPER_KILL_EXTRA_POINTS(1, true, -2, 10, "Extra points when killing with snipe-shot"),
    HEADSHOT_EXTRA_POINTS(1, true, -2, 10, "Extra ponts when killing with headshot"),
    BACKSTAB_EXTRA_POINTS(2, true, -2, 10, "Extra points when backstabbing"),
    GRENADE_KILL_EXTRA_POINTS(1, true, -2, 10, "Extra points when killing with an grenade"),
    PVP_KILL_EXTRA_POINTS(0, true, -2, 5, "Extra ponts when killed at melee"),
    STREAK_EXTRA_POINTS(2, true, -1, 10, "Extra points when having a streak"),
    STREAK_SHUTDOWN_EXTRA_POINTS(2, true, 2, 10, "Extra points when shutting down a streak"),
    MULTIKILLS_EXTRA_POINTS(3, true, 3, 10, "Extra points when killing multiple players at once"),
    CAPTURE_THE_FLAG_POINTS(2, true, -1, 10, "Points a player gets when delivering an enemys flag"),

    MINIMAL_SNIPE_DISTANCE(30, false, null, null, "Minimal distance of a shot to be a sniper shot"),

    MINIMAL_KILLS_FOR_STREAK(5, false, null, null, "Minimal kill amount required for a streak"),

    SPAWNPROTECTION_SECONDS(10, false, null, null, "Seconds a player is protected after spawning"),

    WIDTH_ADDON(0D, true, -0.5D, 1, "Addon to the width of a players hitbox"),
    HEIGHT_ADDON(0d, true, -0.5D, 1, "ddon to the height of a players hitbox"),

    SHOOT_THROUGH_BLOCKS(false, true, false, true, "Shoot through blocks"),
    HIGHLIGHT_PLAYERS(false, true, false, true, "Making players glow and more visible"),

    GRENADE_EFFECT_RADUIS(2,true, 1, 5,"Radius in wich a player is affected by the grenade explosion"),

    LASERGUN_COOLDOWN_TICKS(12, true, 1, 30, "Ticks (20 ticks = 1 second) a lasergun takes to cool down"),
    LASERGUN_MULTIWEAPONS_COOLDOWN_TICKS(2, false, null, null, "Ticks a lasergun takes to cool down when playing with multiple weapons"),
    SNIPER_COOLDOWN_TICKS(100, true, 10, 100,"Ticks a sniperrifle takes to cool down"),
    SHOTGUN_COOLDOWN_TICKS(40, true, 5, 40,"Ticks a shotgun takes to cool down"),
    GRENADE_COOLDOWN_TICKS(100, true, 10, 100,"Ticks a grenade takes to cool down"),
    SNIPER_AMMO_BEFORE_COOLDOWN(3, true, 1, 10, "Maximal sniper ammo"),

    LASERGUN_NORMAL_DAMAGE(100, true, 50 , 100, "Normal lasergun shot damage"),
    LASERGUN_MULTIWEAPONS_DAMAGE(7, true, 7, 15, "lasergun shot damage when playing with multiple weapons"),
    LASERGUN_PVP_DAMAGE(8, true, 8, 20, "Lasergun melee damage (only without multiweapons)"),
    SHOTGUN_DAMAGE(11, true, 5, 20, "Shotgun shot damage"),
    SNIPER_DAMAGE(100, true, 10, 20, "Sniper shot damage"),
    DAGGER_DAMAGE(13, true, 5, 20, "Stabber melee damage"),
    GRENADE_DAMAGE(15, true, 5, 20, "Grenade explosion damage"),

    HEADSHOT_DAMAGE_MULTIPLIKATOR(1.3d, true, 0.5D, 5D, "Extra damage when hitting the head"),
    SNIPER_SHOT_DAMAGE_MULTIPLIKATOR(1.3d, true, 0.5D, 5D, "Extra damage when sniping");

    private Object ogValue;
    private final String description;
    private final ValueType valueType;

    public final boolean inPointEvents;
    public final Pair eventChangeRange;
    Mod(Object value, boolean inPointEvents, Object min, Object max, String description) {
        this.ogValue = value;
        this.description = description;
        if(value instanceof Double) valueType = ValueType.DOUBLE;
        else if(value instanceof Integer) valueType = ValueType.INTEGER;
        else valueType = ValueType.BOOLEAN;

        this.inPointEvents = inPointEvents;
        this.eventChangeRange = new Pair(min, max);
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
    public ValueType getValueType() {
        return valueType;
    }

//    public String getName(){
//        return this.name().charAt(0)+this.name().substring(1).toLowerCase()
//    }

    public static Mod getMod(String name) {
        for(Mod m : Mod.values()) {
            if(m.name().equalsIgnoreCase(name.replace(" ", "").replace("-", "_"))) return m;
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
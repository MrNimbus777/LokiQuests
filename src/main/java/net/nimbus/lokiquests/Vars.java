package net.nimbus.lokiquests;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public class Vars {
    public static String PREFIX;
    public static World DUNGEON_WORLD;
    public static void init(){
        PREFIX = Utils.toColor(LQuests.a.getConfig().getString("Settings.prefix"));
        DUNGEON_WORLD = Bukkit.createWorld(new WorldCreator("world_dungeons").generator(new Utils.VoidGenerator()));
    }
}

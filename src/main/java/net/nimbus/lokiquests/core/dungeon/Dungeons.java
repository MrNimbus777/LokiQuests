package net.nimbus.lokiquests.core.dungeon;

import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.Utils;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class Dungeons {
    private static Map<Long, Dungeon> map = new HashMap<>();

    private static Map<UUID, Dungeon> selected_dundeon = new HashMap<>();
    private static Map<UUID, Dungeon.Spawner> selected_spawner = new HashMap<>();

    public static List<Dungeon> getAll(){
        return new ArrayList<>(map.values());
    }
    public static void register(Dungeon dungeon) {
        map.put(dungeon.getId(), dungeon);
    }

    public static void unregister(Dungeon dungeon){
        map.remove(dungeon.getId());
    }
    public static Dungeon get(Long id){
        if(id == null) return null;
        return map.getOrDefault(id, null);
    }
    public static void select(UUID uuid, Dungeon dungeon){
        selected_dundeon.put(uuid, dungeon);
    }
    public static Dungeon getDungeonSelection(UUID uuid){
        return selected_dundeon.getOrDefault(uuid, null);
    }
    public static Dungeon getDungeonSelection(Player player) {
        return getDungeonSelection(player.getUniqueId());
    }
    public static void select(UUID uuid, Dungeon.Spawner spawner){
        selected_spawner.put(uuid, spawner);
    }
    public static Dungeon.Spawner getSpawnerSelection(UUID uuid){
        return selected_spawner.getOrDefault(uuid, null);
    }
    public static Dungeon.Spawner getSpawnerSelection(Player player) {
        return getSpawnerSelection(player.getUniqueId());
    }
    public static Dungeon getDungeon(Player player){
        for(Dungeon dungeon : getAll()) {
            if(dungeon.getPlayers().contains(player)) return dungeon;
        }
        return null;
    }
    private static double distance(Location loc1, Location loc2) {
        return loc1.toVector().subtract(loc2.toVector()).length();
    }
    public static Dungeon getDungeonCloseToPlayer(Player player){
        List<Dungeon> list = getAll();
        if(list.isEmpty()) return null;
        int closest = 0;
        for(int i = 1; i < list.size(); i++){
            if(distance(list.get(closest).getLocation(), (player.getLocation())) >
                    distance(list.get(i).getLocation(), player.getLocation())) closest = i;
        }
        return list.get(closest);
    }
    public static Dungeon.Wall getWallCloseToPlayer(Player player){
        Dungeon dungeon = getDungeonSelection(player);
        if(dungeon == null) return null;
        List<Dungeon.Wall> list = dungeon.getWalls();
        if(list.isEmpty()) return null;
        int closest = 0;
        for(int i = 1; i < list.size(); i++) {
            if(distance(list.get(closest).getCenter(), (player.getLocation())) >
                    distance(list.get(i).getCenter(), player.getLocation())) closest = i;
        }
        return list.get(closest);
    }
    public static Dungeon.Spawner getSpawner(Entity entity) {
        for(Dungeon dungeon : getAll()) {
            for(Dungeon.Spawner spawner : dungeon.getSpawners()){
                if(spawner.getMobs().contains(entity)) return spawner;
            }
        }
        return null;
    }
    public static void load(){
        File file = new File(LQuests.a.getDataFolder(), "dungeons.yml");
        if(!file.exists()) return;
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        for(String o : configuration.getKeys(false)) {
            long id = Long.parseLong(o);

            Location location = configuration.getLocation(o+".location");
            short limit = (short) configuration.getInt(o+".limit");

            Dungeon dungeon = new Dungeon(id, location, limit);
            dungeon.setName(Utils.toColor(configuration.getString(o+".name")));

            for(String s : configuration.getStringList(o+".spawners")){
                Dungeon.Spawner spawner = Dungeon.Spawner.fromString(s);
                if(spawner != null) dungeon.addSpawner(spawner);
            }
            for(String s : configuration.getStringList(o+".boss")){
                Dungeon.BossSpawner spawner = Dungeon.BossSpawner.fromString(s);
                if(spawner != null) dungeon.addSpawner(spawner);
            }
            for(String s : configuration.getStringList(o+".walls")){
                Dungeon.Wall wall = Dungeon.Wall.fromString(s);
                if(wall != null) dungeon.addWall(wall);
            }
            for(String s : configuration.getStringList(o+".actions")){
                dungeon.addAction(s);
            }
            register(dungeon);
        }
    }


    public static void clearRAM(){
        getAll().forEach(Dungeon::stop);
        map.clear();
    }
}

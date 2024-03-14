package net.nimbus.lokiquests.core.dungeon;

import net.nimbus.lokiquests.LQuests;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dungeons {
    public static Map<Long, Dungeon> map = new HashMap<>();

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
    public static Dungeon getDungeon(Player player){
        for(Dungeon dungeon : getAll()) {
            if(dungeon.getPlayers().contains(player)) return dungeon;
        }
        return null;
    }
    private static double distance(Location loc1, Location loc2) {
        return loc1.toVector().subtract(loc2.toVector()).length();
    }
    public static Dungeon getDungeon(Location location){
        List<Dungeon> list = getAll();
        if(list.isEmpty()) return null;
        int closest = 0;
        for(int i = 1; i < list.size(); ){
            if(distance(list.get(closest).getLocation(), (location)) > distance(list.get(i).getLocation(), location)) closest = i;
        }
        return list.get(closest);
    }
    public static Dungeon.Spawner getSpawner(Location location){
        Dungeon dungeon = getDungeon(location);
        if(dungeon == null) return null;
        List<Dungeon.Spawner> list = dungeon.getSpawners();
        if(list.isEmpty()) return null;
        int closest = 0;
        for(int i = 0; i < list.size(); i++) {
            if(distance(list.get(closest).getLocation(), (location)) > distance(list.get(i).getLocation(), location)) closest = i;
        }
        return list.get(closest);
    }
    public static Dungeon.Spawner getSpawner(long id){
        for(Dungeon dungeon : getAll()){
            for(Dungeon.Spawner spawner : dungeon.getSpawners()){
                if(id == spawner.getId()) return spawner;
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

            for(String s : configuration.getStringList(o+".spawners")){
                Dungeon.Spawner spawner = Dungeon.Spawner.fromString(s);
                if(spawner != null) dungeon.addSpawner(spawner);
            }
            for(String s : configuration.getStringList(o+".boss")){
                Dungeon.BossSpawner spawner = Dungeon.BossSpawner.fromString(s);
                if(spawner != null) dungeon.addSpawner(spawner);
            }
            register(dungeon);
        }
    }


    public static void clearRAM(){
        getAll().forEach(Dungeon::stop);
        map.clear();
    }
}

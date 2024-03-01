package net.nimbus.lokiquests.core.dungeon;

import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.core.dungeon.spawnertask.SpawnerTask;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

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

    public static Dungeon get(Long id){
        if(id == null) return null;
        return map.getOrDefault(id, null);
    }
    public static Dungeon getDungeon(Location location){
        List<Dungeon> list = getAll();
        if(list.isEmpty()) return null;
        int closest = 0;
        for(int i = 1; i < list.size(); ){
            if(list.get(closest).getLocation().distance(location) > list.get(i).getLocation().distance(location)) closest = i;
        }
        return list.get(closest);
    }

    public static SpawnerTask getSpawner(Location location){
        Dungeon dungeon = getDungeon(location);
        if(dungeon == null) return null;
        List<SpawnerTask> list = dungeon.getSpawners();
        if(list.isEmpty()) return null;
        int closest = 0;
        for(int i = 0; i < list.size(); i++) {
            if(list.get(closest).getLocation().distance(location) > list.get(i).getLocation().distance(location)) closest = i;
        }
        return list.get(closest);
    }
    public static SpawnerTask getSpawner(long id){
        for(Dungeon dungeon : getAll()){
            for(SpawnerTask spawner : dungeon.getSpawners()){
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
                SpawnerTask spawner = SpawnerTask.fromString(s);
                if(spawner != null) dungeon.addSpawner(spawner);
            }
            register(dungeon);
        }
    }


    public static void clearRAM(){
        getAll().forEach(Dungeon::stop);
        getAll().forEach(Dungeon::save);
        map.clear();
    }
}

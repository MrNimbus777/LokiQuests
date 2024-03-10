package net.nimbus.lokiquests.core.dungeon.mobspawner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MobSpawners {
    private static Map<String, MobSpawner> map = new HashMap<>();

    public static List<MobSpawner> getAll(){
        return new ArrayList<>(map.values());
    }
    public static MobSpawner get(String id) {
        return map.getOrDefault(id, null);
    }
    public static void register(MobSpawner spawner){
        map.put(spawner.id(), spawner);
    }
    public static void clearRAM(){
        map.clear();
    }
}

package net.nimbus.lokiquests.core.dungeon.mobspawner;

import java.util.HashMap;
import java.util.Map;

public class MobSpawners {
    private static Map<String, MobSpawner> map = new HashMap<>();

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

package net.nimbus.lokiquests.core.reward.rewardprocessors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewardProcessors {
    private static Map<String, RewardProcessor> map = new HashMap<>();

    public static List<RewardProcessor> getAll(){
        return new ArrayList<>(map.values());
    }

    public static void register(String id, RewardProcessor processor){
        map.put(id, processor);
    }
    public static void unregister(String id){
        map.remove(id);
    }
    public static RewardProcessor get(String id){
        return map.getOrDefault(id, null);
    }
}

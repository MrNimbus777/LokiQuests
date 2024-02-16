package net.nimbus.lokiquests.core.questplayers;

import org.bukkit.entity.Player;

import java.util.*;

public class QuestPlayers {
    private static final Map<UUID, QuestPlayer> map = new HashMap<>();

    public static List<QuestPlayer> getAll(){
        return new ArrayList<>(map.values());
    }

    public static QuestPlayer get(UUID uuid){
        return map.get(uuid);
    }
    public static QuestPlayer get(Player player) {
        return get(player.getUniqueId());
    }

    public static void register(QuestPlayer player){
        map.put(player.getUUID(), player);
    }
    public static void unregister(QuestPlayer player) {
        map.remove(player.getUUID());
    }

    public static QuestPlayer load(UUID uuid){
        //TODO
        return null;
    }
    public static QuestPlayer load(Player player) {
        return load(player.getUniqueId());
    }




    public static void clearRAM(){
        map.clear();
    }
}

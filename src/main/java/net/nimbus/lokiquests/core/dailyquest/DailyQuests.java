package net.nimbus.lokiquests.core.dailyquest;

import net.nimbus.lokiquests.core.dailyquest.dailyquests.DQCraft;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class DailyQuests {
    public static Map<String, Constructor<? extends DailyQuest>> map = new HashMap<>();

    public static void load(){
        register("craft", getConstructor(DQCraft.class, ItemStack.class, int.class));
    }
    private static Constructor<? extends DailyQuest> getConstructor(Class<? extends DailyQuest> dailyQuest, Class<?>... classes){
        Class<?>[] classes1 = new Class<?>[classes.length + 1];
        classes1[0] = QuestPlayer.class;
        for (int i = 0; i < classes.length; i++){
            classes1[i+1] = classes[i];
        }
        try {
            return dailyQuest.getConstructor(classes1);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void register(String id, Constructor<? extends DailyQuest> constructor){
        map.put(id, constructor);
    }
    public static Constructor<? extends DailyQuest> get(String id){
        return map.getOrDefault(id, null);
    }
    public static DailyQuest createQuest(String id, QuestPlayer player, Object... objects){
        try {
            Object[] objects1 = new Object[objects.length + 1];
            objects1[0] = player;
            for (int i = 0; i < objects.length; i++){
                objects1[i+1] = objects[i];
            }
            return get(id).newInstance(objects1);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void clearRAM(){
        map.clear();
    }
}

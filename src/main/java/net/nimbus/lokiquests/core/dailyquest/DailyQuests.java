package net.nimbus.lokiquests.core.dailyquest;

import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.Utils;
import net.nimbus.lokiquests.core.dailyquest.dailyquests.DQCraft;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import net.nimbus.lokiquests.core.questplayers.QuestPlayers;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

public class DailyQuests {
    public static Map<String, Class<? extends DailyQuest>> map = new HashMap<>();

    public static void load(){
        register("craft", DQCraft.class);

        new BukkitRunnable(){
            int day = new Date().getDate();
            @Override
            public void run() {
                int now = new Date().getDate();
                if(day != now) {
                    Bukkit.getOnlinePlayers().forEach(p -> {
                        QuestPlayers.get(p).generateDailyQuests();
                        p.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Actions.daily_quest_updated")));
                    });
                    day = now;
                }
            }
        }.runTaskTimer(LQuests.a, 1200, 1200);
    }

    public static void register(String id, Class<? extends DailyQuest> clazz){
        map.put(id, clazz);
    }
    public static String getID(Class<? extends DailyQuest> clazz){
        for(String key : map.keySet()) {
            if(map.get(key) == clazz) {
                return key;
            }
        }
        return null;
    }
    public static Class<? extends DailyQuest> get(String id){
        return map.getOrDefault(id, null);
    }
    public static DailyQuest createQuest(String id, QuestPlayer player, String vars){
        if (vars == null) return null;
        try {
            return get(id).getConstructor(player.getClass(), String.class).newInstance(player, vars);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static DailyQuest createQuest(String id, QuestPlayer player, Object... objects){
        try {
            Object[] objects1 = new Object[objects.length + 1];
            objects1[0] = player;
            for (int i = 0; i < objects.length; i++){
                objects1[i+1] = objects[i];
            }
            Class<?>[] classes = new Class[objects1.length];
            Arrays.stream(objects1)
                    .map(Object::getClass)
                    .toList()
                    .toArray(classes);
            return get(id).getConstructor(classes).newInstance(objects1);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static DailyQuest generateRandom(QuestPlayer player, String level){
        String[] split = LQuests.a.getDailyQuests().getString("rewards."+level).split("-");
        int reward = LQuests.a.r.nextInt(Integer.parseInt(split[0]), Integer.parseInt(split[1])+1);
        List<String> types = new ArrayList<>(LQuests.a.getDailyQuests().getConfigurationSection(level).getKeys(false));
        String type = types.get(LQuests.a.r.nextInt(types.size()));
        Class<? extends DailyQuest> clazz = get(type);
        try {
            Method staticGenerate = clazz.getMethod("generate", QuestPlayer.class, Integer.class, ConfigurationSection.class);
            return (DailyQuest) staticGenerate.invoke(null, player, reward, LQuests.a.getDailyQuests().getConfigurationSection(level+"."+type));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void clearRAM(){
        map.clear();
    }
}

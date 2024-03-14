package net.nimbus.lokiquests.core.quest;

import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.Utils;
import net.nimbus.lokiquests.core.quest.quests.DungeonQuest;
import net.nimbus.lokiquests.core.quest.quests.ItemCollectQuest;
import net.nimbus.lokiquests.core.quest.quests.LocationQuest;
import net.nimbus.lokiquests.core.quest.quests.MobKillQuest;
import net.nimbus.lokiquests.core.reward.Reward;
import net.nimbus.lokiquests.core.reward.rewardprocessors.RewardProcessor;
import net.nimbus.lokiquests.core.reward.rewardprocessors.RewardProcessors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Quests {
    private static final Map<String, Quest> map = new HashMap<>();

    public static List<Quest> getAll(){
        return new ArrayList<>(map.values());
    }

    public static Quest get(String id){
        return map.getOrDefault(id, null);
    }
    public static void register(Quest quest){
        map.put(quest.getId(), quest);
    }
    public static void clearRAM(){
        map.clear();
    }

    public static void load(){
        loadMobKill();
        loadItemCollecting();
        loadLocation();
        loadDungeon();
    }

    private static void loadMobKill(){
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(new InputStreamReader(LQuests.a.getResource("quests/mobkill.yml")));
        File file = new File(LQuests.a.getDataFolder(), "quests/mobkill.yml");
        if(!file.exists()) {
            if(!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try {
                file.createNewFile();
                configuration.save(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            configuration = YamlConfiguration.loadConfiguration(file);
        }
        for(String id : configuration.getKeys(false)){
            MobKillQuest quest = new MobKillQuest(
                    id,
                    Utils.toColor(configuration.getString(id+".name")),
                    EntityType.valueOf(configuration.getString(id+".type")),
                    configuration.getInt(id+".amount")
            );
            quest.setDisplay(configuration.getBoolean(id+".display", true));

            List<Reward> rewards = new ArrayList<>();
            for(String s : configuration.getStringList(id+".rewards")){
                String processorId = s.split(":")[0];
                RewardProcessor processor = RewardProcessors.get(processorId);
                if(processor == null) {
                    LQuests.a.getLogger().severe("Undefined reward for quest " + id + " from file " + file.getPath());
                    continue;
                }
                String[] split = s.replaceFirst(processorId+":", "").split("[|]");
                rewards.add(new Reward(split[1], split[0], processor));
            }
            quest.setRewards(rewards);

            Quests.register(quest);
        }
    }
    private static void loadItemCollecting(){
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(new InputStreamReader(LQuests.a.getResource("quests/itemcollect.yml")));
        File file = new File(LQuests.a.getDataFolder(), "quests/itemcollect.yml");
        if(!file.exists()) {
            if(!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try {
                file.createNewFile();
                configuration.save(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            configuration = YamlConfiguration.loadConfiguration(file);
        }
        for(String id : configuration.getKeys(false)){
            ItemCollectQuest quest = new ItemCollectQuest(
                    id,
                    Utils.toColor(configuration.getString(id+".name")),
                    configuration.getString(id+".type", ""),
                    configuration.getInt(id+".amount"),
                    configuration.getBoolean(id+".remove")
            );
            quest.setDisplay(configuration.getBoolean(id+".display", true));

            List<Reward> rewards = new ArrayList<>();
            for(String s : configuration.getStringList(id+".rewards")){
                String processorId = s.split(":")[0];
                RewardProcessor processor = RewardProcessors.get(processorId);
                if(processor == null) {
                    LQuests.a.getLogger().severe("Undefined reward for quest " + id + " from file " + file.getPath());
                    continue;
                }
                String[] split = s.replaceFirst(processorId+":", "").split("[|]");
                rewards.add(new Reward(split[1], split[0], processor));
            }
            quest.setRewards(rewards);

            Quests.register(quest);
        }
    }
    private static void loadLocation(){
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(new InputStreamReader(LQuests.a.getResource("quests/location.yml")));
        File file = new File(LQuests.a.getDataFolder(), "quests/location.yml");
        if(!file.exists()) {
            if(!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try {
                file.createNewFile();
                configuration.save(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            configuration = YamlConfiguration.loadConfiguration(file);
        }
        for(String id : configuration.getKeys(false)){
            String[] locSplit = configuration.getString(id+".location").split(",");
            Location location = new Location(Bukkit.getWorld(locSplit[0]), Integer.parseInt(locSplit[1]), Integer.parseInt(locSplit[2]), Integer.parseInt(locSplit[3]));
            LocationQuest quest = new LocationQuest(
                    id,
                    Utils.toColor(configuration.getString(id+".name")),
                    location,
                    configuration.getInt(id+".distance")
            );
            quest.setDisplay(configuration.getBoolean(id+".display", true));

            List<Reward> rewards = new ArrayList<>();
            for(String s : configuration.getStringList(id+".rewards")){
                String processorId = s.split(":")[0];
                RewardProcessor processor = RewardProcessors.get(processorId);
                if(processor == null) {
                    LQuests.a.getLogger().severe("Undefined reward for quest " + id + " from file " + file.getPath());
                    continue;
                }
                String[] split = s.replaceFirst(processorId+":", "").split("[|]");
                rewards.add(new Reward(split[1], split[0], processor));
            }
            quest.setRewards(rewards);

            Quests.register(quest);
        }
    }
    private static void loadDungeon(){
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(new InputStreamReader(LQuests.a.getResource("quests/dungeon.yml")));
        File file = new File(LQuests.a.getDataFolder(), "quests/dungeon.yml");
        if(!file.exists()) {
            if(!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try {
                file.createNewFile();
                configuration.save(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            configuration = YamlConfiguration.loadConfiguration(file);
        }
        for(String id : configuration.getKeys(false)){
            DungeonQuest quest = new DungeonQuest(
                    id,
                    Utils.toColor(configuration.getString(id+".name")),
                    configuration.getLong(id+".dungeon")
            );
            quest.setDisplay(configuration.getBoolean(id+".display", true));

            List<Reward> rewards = new ArrayList<>();
            for(String s : configuration.getStringList(id+".rewards")){
                String processorId = s.split(":")[0];
                RewardProcessor processor = RewardProcessors.get(processorId);
                if(processor == null) {
                    LQuests.a.getLogger().severe("Undefined reward for quest " + id + " from file " + file.getPath());
                    continue;
                }
                String[] split = s.replaceFirst(processorId+":", "").split("[|]");
                rewards.add(new Reward(split[1], split[0], processor));
            }
            quest.setRewards(rewards);

            Quests.register(quest);
        }
    }
}

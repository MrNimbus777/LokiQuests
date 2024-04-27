package net.nimbus.lokiquests.core.questplayers;

import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.core.dailyquest.DailyQuest;
import net.nimbus.lokiquests.core.dailyquest.DailyQuests;
import net.nimbus.lokiquests.core.dialogues.Dialogue;
import net.nimbus.lokiquests.core.dialogues.Dialogues;
import net.nimbus.lokiquests.core.quest.Quest;
import net.nimbus.lokiquests.core.quest.Quests;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
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
        File file = new File(LQuests.a.getDataFolder(), "players/"+uuid.toString()+".json");
        if(!file.exists()) {
            return new QuestPlayer(uuid);
        }
        try {
            FileReader reader = new FileReader(file);
            JSONObject obj = (JSONObject) new JSONParser().parse(reader);
            reader.close();

            QuestPlayer player = new QuestPlayer(uuid);

            JSONObject dialogs = (JSONObject) obj.getOrDefault("dialogues", new JSONObject());
            for(Object o : dialogs.keySet()) {
                String id = o.toString();
                Dialogue dialog = Dialogues.get(id);
                if(dialog == null) continue;
                dialog.putPlayerProgress(uuid, dialogs.get(o).toString());
            }

            JSONObject active = (JSONObject) obj.getOrDefault("active", new JSONObject());
            for(Object o : active.keySet()) {
                String id = o.toString();
                Quest quest = Quests.get(id);
                if(quest == null) continue;
                quest.setProgress(uuid, Integer.parseInt(active.get(o).toString()));
                player.addActiveQuest(quest);
            }

            JSONObject daily = (JSONObject) obj.getOrDefault("daily", new JSONObject());
            int day = Integer.parseInt(daily.getOrDefault("day", -1).toString());
            if(day == new Date().getDate() || !player.getPlayer().hasPermission("lq.admin")) { //permission checking for tests
                DailyQuest[] dailyQuests = new DailyQuest[3];
                for (int i = 0; i < 3; i++) {
                    String s = (String) daily.getOrDefault(i + "", null);
                    if (s == null) continue;
                    String id = s.split(":")[0];
                    dailyQuests[i] = DailyQuests.createQuest(id, player, s.replaceFirst(id + ":", ""));
                }
                player.setDailyQuests(dailyQuests);
                player.setDailyQuestDay(day);
            } else {
                player.generateDailyQuests();
            }

            player.setFinishedQuests(getQuestsFromJson(obj.getOrDefault("finished", new JSONArray())));
            player.setCompletedQuests(getQuestsFromJson(obj.getOrDefault("completed", new JSONArray())));

            if(obj.containsKey("indicator")) {
                String[] split = obj.get("indicator").toString().split(",");
                Location loc = new Location(
                        Bukkit.getWorld(split[0]),
                        Double.parseDouble(split[1]),
                        Double.parseDouble(split[2]),
                        Double.parseDouble(split[3])
                );
                player.setIndicator(loc);
            }

            return player;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new QuestPlayer(uuid);
    }
    public static QuestPlayer load(Player player) {
        return load(player.getUniqueId());
    }

    private static List<Quest> getQuestsFromJson(Object obj) {
        List<String> list = Arrays.stream(((JSONArray) obj).toArray()).map(Object::toString).toList();
        List<Quest> result = new ArrayList<>();
        for(String id : list) {
            Quest quest = Quests.get(id);
            if(quest == null) continue;
            result.add(quest);
        }
        return result;
    }


    public static void clearRAM(){
        map.clear();
    }
}

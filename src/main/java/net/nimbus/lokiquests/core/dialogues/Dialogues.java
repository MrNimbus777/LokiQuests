package net.nimbus.lokiquests.core.dialogues;

import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.core.dialogues.Dialogue;
import net.nimbus.lokiquests.core.quest.Quest;
import net.nimbus.lokiquests.core.quest.Quests;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import net.nimbus.lokiquests.core.questplayers.QuestPlayers;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dialogues {
    public static final Map<String, Dialogue> map = new HashMap<>();

    public static List<Dialogue> getAll(){
        return new ArrayList<>(map.values());
    }
    public static Dialogue get(String id){
        return map.getOrDefault(id, null);
    }

    public static void register(Dialogue dialogue){
        map.put(dialogue.getId(), dialogue);
    }

    public static void clearRAM(){
        getAll().forEach(Dialogue::clear);
        map.clear();
    }

    public static void load(){
        try {
            for(String name : List.of(new File(LQuests.a.getDataFolder(), "dialogues").list())) {
                Dialogue dialogue = new Dialogue(name.replace(".yml", ""));
                dialogue.setConfiguration(YamlConfiguration.loadConfiguration(new File(LQuests.a.getDataFolder(), "dialogues/"+name)));
                Dialogues.register(dialogue);
            }
        } catch (Exception e) {
            File example = new File(LQuests.a.getDataFolder(), "dialogues/example.yml");
            if(!example.exists()) {
                if(!example.getParentFile().exists()) {
                    example.getParentFile().mkdirs();
                }
                try {
                    example.createNewFile();
                    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(new InputStreamReader(LQuests.a.getResource("dialogues/example.yml")));
                    configuration.save(example);
                    Dialogue dialogue = new Dialogue("example");
                    dialogue.setConfiguration(configuration);
                    Dialogues.register(dialogue);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    public static boolean processCondition(Player player, String condition){
        QuestPlayer qp = QuestPlayers.get(player);
        String[] split = condition.split(":");
        Quest quest = Quests.get(split[0]);
        if(quest == null) return false;
        int i = Integer.parseInt(split[1]);
        return switch (i) {
            case 0 -> (!quest.isCompleted(qp) && !quest.isFinished(qp));
            case 1 -> quest.isFinished(qp);
            case 2 -> quest.isCompleted(qp);
            default -> false;
        };
    }
}

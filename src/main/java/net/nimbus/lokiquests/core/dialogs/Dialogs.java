package net.nimbus.lokiquests.core.dialogs;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.nimbus.lokiquests.LQuests;
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
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Dialogs {
    public static final Map<String, Dialog> map = new HashMap<>();

    public static List<Dialog> getAll(){
        return new ArrayList<>(map.values());
    }
    public static Dialog get(String id){
        return map.getOrDefault(id, null);
    }

    public static void register(Dialog dialog){
        map.put(dialog.getId(), dialog);
    }

    public static void clearRAM(){
        getAll().forEach(Dialog::clear);
        map.clear();
    }

    public static void load(){
        try {
            for(String name : List.of(new File(LQuests.a.getDataFolder(), "dialogs").list())) {
                Dialog dialog = new Dialog(name.replace(".yml", ""));
                dialog.setConfiguration(YamlConfiguration.loadConfiguration(new File(LQuests.a.getDataFolder(), "dialogs/"+name)));
                Dialogs.register(dialog);
            }
        } catch (Exception e) {
            File example = new File(LQuests.a.getDataFolder(), "dialogs/example.yml");
            if(!example.exists()) {
                if(!example.getParentFile().exists()) {
                    example.getParentFile().mkdirs();
                }
                try {
                    example.createNewFile();
                    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(new InputStreamReader(LQuests.a.getResource("dialogs/example.yml")));
                    configuration.save(example);
                    Dialog dialog = new Dialog("example");
                    dialog.setConfiguration(configuration);
                    Dialogs.register(dialog);
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

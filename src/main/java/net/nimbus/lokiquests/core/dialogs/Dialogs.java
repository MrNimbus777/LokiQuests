package net.nimbus.lokiquests.core.dialogs;

import net.nimbus.lokiquests.core.quest.Quest;
import net.nimbus.lokiquests.core.quest.Quests;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import net.nimbus.lokiquests.core.questplayers.QuestPlayers;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Dialogs {
    public static final Map<String, Dialog> map = new HashMap<>();

    public static Dialog get(String id){
        return map.getOrDefault(id, null);
    }

    public static void register(Dialog dialog){
        map.put(dialog.getId(), dialog);
    }

    public static void load(){

    }

    public static boolean processCondition(Player player, String condition){
        QuestPlayer qp = QuestPlayers.get(player);
        String[] split = condition.split(":");
        Quest quest = Quests.get(split[0]);
        if(quest == null) return false;
        int i = Integer.parseInt(split[1]);
        return switch (i) {
            case 0 -> (!qp.getCompletedQuests().contains(quest) && !qp.getFinishedQuests().contains(quest));
            case 1 -> qp.getFinishedQuests().contains(quest);
            case 2 -> qp.getCompletedQuests().contains(quest);
            default -> false;
        };
    }
}

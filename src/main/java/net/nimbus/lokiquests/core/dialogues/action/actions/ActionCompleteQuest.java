package net.nimbus.lokiquests.core.dialogues.action.actions;

import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.core.dialogues.action.Action;
import net.nimbus.lokiquests.core.quest.Quest;
import net.nimbus.lokiquests.core.quest.Quests;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import net.nimbus.lokiquests.core.questplayers.QuestPlayers;
import org.bukkit.entity.Player;

public class ActionCompleteQuest implements Action {
    @Override
    public void execute(Player player, String vars) {
        QuestPlayer qp = QuestPlayers.get(player);
        Quest quest = Quests.get(vars);
        if(quest == null) {
            LQuests.a.getLogger().severe(vars + " - quest not found.");
            return;
        }
        quest.complete(qp);
    }
}

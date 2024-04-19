package net.nimbus.lokiquests.events.player;

import net.nimbus.lokiquests.core.dailyquest.DailyQuest;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import net.nimbus.lokiquests.core.questplayers.QuestPlayers;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public class CraftItemEvents implements Listener {
    @EventHandler
    public void onEvent(CraftItemEvent e){
        QuestPlayer qp = QuestPlayers.get(e.getWhoClicked().getUniqueId());
        if(qp == null) return;
        for (DailyQuest dailyQuest : qp.getDailyQuests()) {
            if(dailyQuest != null) if(dailyQuest.isCompleted(e)) dailyQuest.complete();
        }
    }
}

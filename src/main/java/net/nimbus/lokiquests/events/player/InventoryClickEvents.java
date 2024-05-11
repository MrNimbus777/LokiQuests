package net.nimbus.lokiquests.events.player;

import net.nimbus.lokiquests.core.dailyquest.DailyQuest;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import net.nimbus.lokiquests.core.questplayers.QuestPlayers;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickEvents implements Listener {
    @EventHandler
    public void onEvent(InventoryClickEvent e){
        QuestPlayer qp = QuestPlayers.get(e.getWhoClicked().getUniqueId());
        if(qp == null) return;
        for (DailyQuest dailyQuest : qp.getDailyQuests()) {
            if(dailyQuest != null) if(dailyQuest.isCompleted(e)) dailyQuest.complete();
        }
    }
}

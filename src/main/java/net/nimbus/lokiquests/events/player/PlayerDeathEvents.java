package net.nimbus.lokiquests.events.player;

import net.nimbus.lokiquests.core.dailyquest.DailyQuest;
import net.nimbus.lokiquests.core.dungeon.Dungeon;
import net.nimbus.lokiquests.core.dungeon.Dungeons;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import net.nimbus.lokiquests.core.questplayers.QuestPlayers;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathEvents implements Listener {
    @EventHandler
    public void onEvent(PlayerDeathEvent e) {
        Dungeon dungeon = Dungeons.getDungeon(e.getEntity());
        if(dungeon != null) {
            dungeon.leave(e.getEntity());
        }
        if(e.getEntity().getKiller() != null) {
            QuestPlayer qp = QuestPlayers.get(e.getEntity().getKiller());
            if(qp == null) return;
            for (DailyQuest dailyQuest : qp.getDailyQuests()) {
                if(dailyQuest != null) if(dailyQuest.isCompleted(e)) dailyQuest.complete();
            }
        }
    }
}

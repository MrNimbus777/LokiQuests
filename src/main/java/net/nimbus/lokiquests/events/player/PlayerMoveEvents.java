package net.nimbus.lokiquests.events.player;

import net.nimbus.lokiquests.core.dungeon.Dungeon;
import net.nimbus.lokiquests.core.dungeon.Dungeons;
import net.nimbus.lokiquests.core.quest.Quest;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import net.nimbus.lokiquests.core.questplayers.QuestPlayers;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveEvents implements Listener {
    @EventHandler
    public void onEvent(PlayerMoveEvent e){
        QuestPlayer qp = QuestPlayers.get(e.getPlayer());
        for(Quest quest : qp.getActiveQuests()){
            quest.process(e);
        }
        for(Dungeon.Wall wall : Dungeon.Wall.UP){
            if(wall.isColliding(e.getTo())) {

            }
        }
    }
}

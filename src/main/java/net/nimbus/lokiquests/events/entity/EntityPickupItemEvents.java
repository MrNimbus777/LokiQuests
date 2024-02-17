package net.nimbus.lokiquests.events.entity;

import net.nimbus.lokiquests.core.quest.Quest;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import net.nimbus.lokiquests.core.questplayers.QuestPlayers;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class EntityPickupItemEvents implements Listener {
    @EventHandler
    public void onEvent(EntityPickupItemEvent e){
        if(!(e.getEntity() instanceof Player p)) return;
        QuestPlayer player = QuestPlayers.get(p);
        for(Quest quest : player.getActiveQuests()) {
            quest.process(e);
        }
    }
}

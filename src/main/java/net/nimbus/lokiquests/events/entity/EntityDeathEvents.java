package net.nimbus.lokiquests.events.entity;

import net.nimbus.lokiquests.Utils;
import net.nimbus.lokiquests.core.dungeon.Dungeon;
import net.nimbus.lokiquests.core.dungeon.Dungeons;
import net.nimbus.lokiquests.core.quest.Quest;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import net.nimbus.lokiquests.core.questplayers.QuestPlayers;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeathEvents implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEvent(EntityDeathEvent e){
        if(e.getEntity().getKiller() == null) return;
        QuestPlayer player = QuestPlayers.get(e.getEntity().getKiller());
        String spawner_id = Utils.readMetadata(e.getEntity(), "spawner");
        if(!spawner_id.isEmpty()){
            Dungeon.Spawner spawner = Dungeons.getSpawner(Long.parseLong(spawner_id));
            if(spawner != null) if(!spawner.isCompleted()) {
                spawner.removeEntity(e.getEntity());
                if(spawner.isCompleted()) spawner.complete();
            }
        }
        for(Quest quest : player.getActiveQuests()) {
            quest.process(e);
        }
    }
}

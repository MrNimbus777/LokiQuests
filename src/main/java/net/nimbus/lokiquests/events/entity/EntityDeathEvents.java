package net.nimbus.lokiquests.events.entity;

import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.core.dialogues.action.Action;
import net.nimbus.lokiquests.core.dialogues.action.Actions;
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
    public void onEvent(EntityDeathEvent e) {
        Dungeon.Spawner spawner = Dungeons.getSpawner(e.getEntity());
        if (spawner != null) {
            if (!spawner.isCompleted(false)) {
                spawner.removeEntity(e.getEntity());
                if (spawner.isCompleted(false)) {
                    spawner.complete();
                    if (e.getEntity().getKiller() == null) {
                        Dungeon dungeon = spawner.getDungeon();
                        if(dungeon.getPlayers().isEmpty()) return;
                        spawner.getActions().forEach(a -> {
                            String action_id = a.split(":")[0];
                            Action action = Actions.get(action_id);
                            if(action == null) return;
                            String vars = a.replaceFirst(action_id+":", "");
                            action.execute(dungeon.getPlayers().get(LQuests.a.r.nextInt(dungeon.getPlayers().size())), vars);
                        });
                    } else {
                        spawner.getActions().forEach(a -> {
                            String action_id = a.split(":")[0];
                            Action action = Actions.get(action_id);
                            if(action == null) return;
                            String vars = a.replaceFirst(action_id+":", "");
                            action.execute(e.getEntity().getKiller(), vars);
                        });
                    }
                }
            }
        }
        if (e.getEntity().getKiller() == null) return;
        QuestPlayer player = QuestPlayers.get(e.getEntity().getKiller());
        for (Quest quest : player.getActiveQuests()) {
            quest.process(e);
        }
    }
}

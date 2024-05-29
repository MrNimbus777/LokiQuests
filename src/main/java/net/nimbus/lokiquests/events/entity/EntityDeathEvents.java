package net.nimbus.lokiquests.events.entity;

import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.core.dailyquest.DailyQuest;
import net.nimbus.lokiquests.core.dialogues.action.Action;
import net.nimbus.lokiquests.core.dialogues.action.Actions;
import net.nimbus.lokiquests.core.dialogues.action.actions.ActionReward;
import net.nimbus.lokiquests.core.dungeon.Dungeon;
import net.nimbus.lokiquests.core.dungeon.Dungeons;
import net.nimbus.lokiquests.core.quest.Quest;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import net.nimbus.lokiquests.core.questplayers.QuestPlayers;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class EntityDeathEvents implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEvent(EntityDeathEvent e) {
        Dungeon.Spawner spawner = Dungeons.getSpawner(e.getEntity());
        if (spawner != null) {
            if (!spawner.isCompleted(false)) {
                spawner.removeEntity(e.getEntity());
                if (spawner.isCompleted(false)) {
                    if (e.getEntity().getKiller() == null) {
                        Dungeon dungeon = spawner.getDungeon();
                        if(dungeon.getPlayers().isEmpty()) return;
                        spawner.getActions().forEach(a -> {
                            String action_id = a.split(":")[0];
                            Action action = Actions.get(action_id);
                            if(action == null) return;
                            String vars = a.replaceFirst(action_id+":", "");
                            QuestPlayer qp = QuestPlayers.get(dungeon.getPlayers().get(LQuests.a.r.nextInt(dungeon.getPlayers().size())));
                            if(action instanceof ActionReward){
                                if(!qp.getCompletedDungeons().contains(dungeon.getId())){
                                    action.execute(qp.getPlayer(), vars);
                                }
                            } else {
                                action.execute(qp.getPlayer(), vars);
                            }
                        });
                    } else {
                        spawner.getActions().forEach(a -> {
                            Dungeon dungeon = spawner.getDungeon();
                            String action_id = a.split(":")[0];
                            Action action = Actions.get(action_id);
                            if(action == null) return;
                            String vars = a.replaceFirst(action_id+":", "");
                            QuestPlayer qp = QuestPlayers.get(dungeon.getPlayers().get(LQuests.a.r.nextInt(dungeon.getPlayers().size())));
                            if(action instanceof ActionReward){
                                if(!qp.getCompletedDungeons().contains(dungeon.getId())){
                                    action.execute(qp.getPlayer(), vars);
                                }
                            } else {
                                action.execute(qp.getPlayer(), vars);
                            }
                        });
                    }
                    spawner.complete();
                }
            }
        }

        // The Dragon's drop: dragon_heart
        if(e.getEntity().getType() == EntityType.ENDER_DRAGON){
            try {
                ItemStack itemStack = LQuests.a.getItems().getItemStack("dragon_heart").clone();
                e.getEntity().getWorld().dropItem(e.getEntity().getLocation(), itemStack);
            } catch (Exception ex) {}
        }
        if (e.getEntity().getKiller() == null) return;
        QuestPlayer player = QuestPlayers.get(e.getEntity().getKiller());
        for (Quest quest : player.getActiveQuests()) {
            quest.process(e);
        }
        for (DailyQuest dailyQuest : player.getDailyQuests()) {
            if(dailyQuest != null) if(dailyQuest.isCompleted(e)) dailyQuest.complete();
        }
    }
}

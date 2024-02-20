package net.nimbus.lokiquests.core.quest.quests;

import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.Utils;
import net.nimbus.lokiquests.core.quest.Quest;
import net.nimbus.lokiquests.core.questplayers.QuestPlayers;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MobKillQuest extends Quest {

    private final EntityType type;
    private final int amount;

    private final Map<UUID, Integer> map;

    public MobKillQuest(String id, String name, EntityType type, int amount) {
        super(id, name);
        this.type = type;
        this.amount = amount;
        map = new HashMap<>();
    }

    @Override
    public void process(Event event) {
        if(event instanceof EntityDeathEvent e) {
            if(e.getEntity().getKiller() == null) return;
            if(e.getEntity().getType() != this.type) return;
            Player killer = e.getEntity().getKiller();
            int kills = map.getOrDefault(killer.getUniqueId(), 0) + 1;
            killer.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Actions.quest_progress").
                    replace("%progress%", kills+"").
                    replace("%goal%", amount+"").
                    replace("%percentage%", (kills*100/amount)+"")
            ));
            if(kills >= amount) {
                finish(QuestPlayers.get(killer));
            } else setProgress(killer.getUniqueId(), kills);
        }
    }

    @Override
    public int getProgress(UUID uuid) {
        return map.getOrDefault(uuid, 0);
    }

    @Override
    public void setProgress(UUID uuid, int progress) {
        map.put(uuid, progress);
    }
}

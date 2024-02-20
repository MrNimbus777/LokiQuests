package net.nimbus.lokiquests.core.quest.quests;

import net.nimbus.lokiquests.core.quest.Quest;
import org.bukkit.event.Event;

import java.util.UUID;

public class DungeonQuest extends Quest {
    public DungeonQuest(String id, String name) {
        super(id, name);
    }

    @Override
    public void process(Event event) {

    }

    @Override
    public int getProgress(UUID uuid) {
        return 0;
    }

    @Override
    public void setProgress(UUID uuid, int progress) {

    }
}

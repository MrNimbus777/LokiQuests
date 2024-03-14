package net.nimbus.lokiquests.core.quest.quests;

import net.nimbus.lokiquests.Utils;
import net.nimbus.lokiquests.core.dungeon.Dungeons;
import net.nimbus.lokiquests.core.quest.Quest;
import net.nimbus.lokiquests.core.reward.Reward;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DungeonQuest extends Quest {
    private long dungeon_id;
    public DungeonQuest(String id, String name, long dungeon_id) {
        super(id, name);
        this.dungeon_id = dungeon_id;
    }

    @Override
    public void process(Event event) {

    }
    public long getDungeon(){
        return dungeon_id;
    }
    @Override
    public int getProgress(UUID uuid) {
        return 0;
    }

    @Override
    public void setProgress(UUID uuid, int progress) {

    }
}

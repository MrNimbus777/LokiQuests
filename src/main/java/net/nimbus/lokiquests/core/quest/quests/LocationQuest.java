package net.nimbus.lokiquests.core.quest.quests;

import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.core.quest.Quest;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import net.nimbus.lokiquests.core.questplayers.QuestPlayers;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class LocationQuest extends Quest {

    private final Location location;
    private final int distance;
    public LocationQuest(String id, String name, Location location, int distance) {
        super(id, name);
        this.location = location;
        this.distance = distance;
    }

    @Override
    public void process(Event event) {
        if(event instanceof PlayerMoveEvent e) {
            int r = LQuests.a.r.nextInt(7);
            if (r == 0) {
                Location move = e.getTo();
                QuestPlayer player = QuestPlayers.get(e.getPlayer());
                if(move.getWorld().equals(location.getWorld())) {
                    if(location.distance(move) <= distance) {
                        finish(player);
                    }
                }
            }
        }
    }

    @Override
    public int getProgress(UUID uuid) {
        return 0;
    }

    @Override
    public void setProgress(UUID uuid, int progress) {

    }
}

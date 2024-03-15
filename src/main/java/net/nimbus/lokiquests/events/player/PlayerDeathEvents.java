package net.nimbus.lokiquests.events.player;

import net.nimbus.lokiquests.core.dungeon.Dungeon;
import net.nimbus.lokiquests.core.dungeon.Dungeons;
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
    }
}

package net.nimbus.lokiquests.events.player;

import net.nimbus.lokiquests.Vars;
import net.nimbus.lokiquests.core.dungeon.Dungeon;
import net.nimbus.lokiquests.core.dungeon.Dungeons;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerTeleportEvents implements Listener {
    @EventHandler
    public void onEvent(PlayerTeleportEvent e) {
        Dungeon dungeon = Dungeons.getDungeon(e.getPlayer());
        if(dungeon == null) return;
        if(dungeon.isCompleted()) return;
        if(e.getFrom().getWorld().equals(Vars.DUNGEON_WORLD)){
            if(!e.getTo().getWorld().equals(Vars.DUNGEON_WORLD)){
                e.setCancelled(true);
            }
        }
    }
}

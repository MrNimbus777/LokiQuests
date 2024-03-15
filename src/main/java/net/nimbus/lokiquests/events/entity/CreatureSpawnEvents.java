package net.nimbus.lokiquests.events.entity;

import net.nimbus.lokiquests.Vars;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CreatureSpawnEvents implements Listener {
    @EventHandler
    public void onEvent(CreatureSpawnEvent e){
        if(e.getLocation().getWorld().equals(Vars.DUNGEON_WORLD)) {
            if(e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) e.setCancelled(true);
        }
    }
}

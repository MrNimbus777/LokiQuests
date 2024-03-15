package net.nimbus.lokiquests.events.entity;

import net.nimbus.lokiquests.Vars;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class ProjectileLaunchEvents implements Listener {
    @EventHandler
    public void onEvent(ProjectileLaunchEvent e){
        if(e.getEntity().getType() != EntityType.ENDER_PEARL) return;
        if(!(e.getEntity().getShooter() instanceof Player p)) return;
        if(p.getLocation().getWorld().equals(Vars.DUNGEON_WORLD)) e.setCancelled(true);
    }
}

package net.nimbus.lokiquests.events.player;

import net.nimbus.lokiquests.Vars;
import net.nimbus.lokiquests.core.dungeon.Dungeon;
import net.nimbus.lokiquests.core.dungeon.Dungeons;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractEvents implements Listener {
    @EventHandler
    public void onEvent(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block b = e.getClickedBlock();
        Location loc = new Location(b.getWorld(), b.getX(), b.getY(), b.getZ());
        Dungeon dungeon = Dungeons.get(Vars.SIGNS_MAP.get(loc));
        if(dungeon == null) return;
        e.setCancelled(true);
        dungeon.teleport(p);
    }
}

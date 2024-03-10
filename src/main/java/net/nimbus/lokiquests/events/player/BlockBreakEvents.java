package net.nimbus.lokiquests.events.player;

import net.nimbus.lokiquests.Utils;
import net.nimbus.lokiquests.Vars;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakEvents implements Listener {
    @EventHandler
    public void onEvent(BlockBreakEvent e){
        Player p = e.getPlayer();
        Block b = e.getBlock();
        Location loc = new Location(b.getWorld(), b.getX(), b.getY(), b.getZ());
        if(!Vars.SIGNS_MAP.containsKey(loc)) return;
        if(!(p.hasPermission("lq.admin"))) {
            e.setCancelled(true);
            return;
        }
        Vars.SIGNS_MAP.remove(loc);
        Utils.saveSigns();
    }
}

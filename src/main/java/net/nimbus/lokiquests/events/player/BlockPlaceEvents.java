package net.nimbus.lokiquests.events.player;

import net.nimbus.lokiquests.Utils;
import net.nimbus.lokiquests.Vars;
import net.nimbus.lokiquests.core.dungeon.Dungeon;
import net.nimbus.lokiquests.core.dungeon.Dungeons;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockPlaceEvents implements Listener {
    @EventHandler
    public void onEvent(BlockPlaceEvent e){
        Player p = e.getPlayer();
        if(!(p.hasPermission("lq.admin"))) return;

        ItemStack item = e.getItemInHand();

        String val = Utils.readTag(item, "teleport_to_id");
        if(val.isEmpty()) return;
        long id = Long.parseLong(val);
        Dungeon dungeon = Dungeons.get(id);
        if(dungeon ==  null) return;

        Block b = e.getBlock();
        Vars.PLATES_MAP.put(new Location(b.getWorld(), b.getX(), b.getY(), b.getZ()), id);
        Utils.savePlates();
    }
}

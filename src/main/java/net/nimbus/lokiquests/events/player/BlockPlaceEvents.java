package net.nimbus.lokiquests.events.player;

import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.Utils;
import net.nimbus.lokiquests.Vars;
import net.nimbus.lokiquests.core.dungeon.Dungeon;
import net.nimbus.lokiquests.core.dungeon.Dungeons;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

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
        BlockData before = e.getBlock().getBlockData().clone();
        e.setCancelled(true);

        new BukkitRunnable(){
            @Override
            public void run() {
                Block b = e.getBlock();
                b.setType(Material.OAK_SIGN);
                Vars.SIGNS_MAP.put(new Location(b.getWorld(), b.getX(), b.getY(), b.getZ()), id);
                Utils.saveSigns();

                b.setBlockData(before);

                Sign sign = (Sign) b.getState();
                sign.setLine(0, Utils.toColor("&9[Teleport]"));
                sign.setLine(2, Utils.toColor("&aTeleport to"));
                sign.setLine(3, Utils.toColor("&adungeon."));
                sign.update();
            }
        }.runTaskLater(LQuests.a, 0);
    }
}

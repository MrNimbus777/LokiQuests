package net.nimbus.lokiquests.events.player;

import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.Utils;
import net.nimbus.lokiquests.Vars;
import net.nimbus.lokiquests.core.dungeon.Dungeon;
import net.nimbus.lokiquests.core.dungeon.Dungeons;
import net.nimbus.lokiquests.core.party.Parties;
import net.nimbus.lokiquests.core.party.Party;
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
        if(dungeon.getPlayers().contains(p)) return;
        if(dungeon.getPlayers().size() >= dungeon.getLimit() && dungeon.getLimit() > -1) {
            p.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Actions.dungeon_full")));
            return;
        }
        Party player_party = Parties.get(p);
        if(player_party == null) {
            p.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Actions.join_no_party")));
            return;
        }
        if(!dungeon.getPlayers().isEmpty()) {
            Party dungeon_party = Parties.get(dungeon.getPlayers().get(0));
            if(!dungeon_party.equals(player_party)) {
                p.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Actions.other_party")));
                return;
            }
        }
        dungeon.join(p);
        p.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Actions.dungeon_join")));
    }
}

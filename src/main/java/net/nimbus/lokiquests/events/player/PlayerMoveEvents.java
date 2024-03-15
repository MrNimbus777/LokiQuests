package net.nimbus.lokiquests.events.player;

import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.core.dungeon.Dungeon;
import net.nimbus.lokiquests.core.quest.Quest;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import net.nimbus.lokiquests.core.questplayers.QuestPlayers;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class PlayerMoveEvents implements Listener {
    @EventHandler
    public void onEvent(PlayerMoveEvent e){
        QuestPlayer qp = QuestPlayers.get(e.getPlayer());
        for(Quest quest : qp.getActiveQuests()){
            quest.process(e);
        }
        for(Dungeon.Wall wall : Dungeon.Wall.UP){
            if(wall.isColliding(e.getTo())) {
                e.getPlayer().sendMessage(e.getPlayer().getClass().getName());
                Vector v1 = new Location(e.getTo().getWorld(), e.getTo().getBlockX()+0.5, e.getTo().getBlockY(), e.getTo().getBlockZ()+0.5).toVector();
                Vector v2 = e.getFrom().toVector();
                Vector dir = v2.subtract(v1).setY(0);
                dir.multiply(1/Math.max(Math.abs(dir.getX()), Math.abs(dir.getZ()))).setX(dir.getBlockX()).setZ(dir.getBlockZ()).normalize().setY(0.1).multiply(1.5);
                e.setCancelled(true);
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        e.getPlayer().teleport(e.getFrom().add(dir));
                    }
                }.runTaskLater(LQuests.a, 0);
                break;
            }
        }
    }
}

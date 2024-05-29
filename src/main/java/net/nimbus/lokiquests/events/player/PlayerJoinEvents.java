package net.nimbus.lokiquests.events.player;

import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import net.nimbus.lokiquests.core.questplayers.QuestPlayers;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public class PlayerJoinEvents implements Listener {
    @EventHandler
    public void onEvent(PlayerJoinEvent e){
        if(!new File(LQuests.a.getDataFolder(), "players/"+e.getPlayer().getUniqueId()+".json").exists()) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            e.getPlayer().teleport(LQuests.a.getConfig().getLocation("SpawnLocation"));
                        } catch (Exception ignored) {
                            ignored.printStackTrace();
                        }
                    }
                }.runTaskLater(LQuests.a, 1);
        }
        QuestPlayer player = QuestPlayers.load(e.getPlayer());
        QuestPlayers.register(player);
        player.runIndicator();
    }
}

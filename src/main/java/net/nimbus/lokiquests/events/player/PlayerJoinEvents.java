package net.nimbus.lokiquests.events.player;

import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import net.nimbus.lokiquests.core.questplayers.QuestPlayers;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinEvents implements Listener {
    @EventHandler
    public void onEvent(PlayerJoinEvent e){
        if(!e.getPlayer().hasPlayedBefore()){
            try {
                e.getPlayer().teleport(LQuests.a.getConfig().getLocation("SpawnLocation"));
            } catch (Exception ignored) {}
        }
        QuestPlayer player = QuestPlayers.load(e.getPlayer());
        QuestPlayers.register(player);
        player.runIndicator();
    }
}

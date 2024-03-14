package net.nimbus.lokiquests.events.player;

import net.nimbus.lokiquests.core.dungeon.Dungeon;
import net.nimbus.lokiquests.core.dungeon.Dungeons;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import net.nimbus.lokiquests.core.questplayers.QuestPlayers;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitEvents implements Listener {
    @EventHandler
    public void onEvent(PlayerQuitEvent e){
        Dungeon dungeon = Dungeons.getDungeon(e.getPlayer());
        if(dungeon != null) dungeon.leave(e.getPlayer());
        QuestPlayer player = QuestPlayers.get(e.getPlayer());
        player.save();
        QuestPlayers.unregister(player);
    }
}

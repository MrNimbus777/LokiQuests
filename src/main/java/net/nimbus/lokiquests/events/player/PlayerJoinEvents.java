package net.nimbus.lokiquests.events.player;

import net.nimbus.lokiquests.core.quest.Quest;
import net.nimbus.lokiquests.core.quest.Quests;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import net.nimbus.lokiquests.core.questplayers.QuestPlayers;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinEvents implements Listener {
    @EventHandler
    public void onEvent(PlayerJoinEvent e){
        QuestPlayer player = QuestPlayers.load(e.getPlayer());
        QuestPlayers.register(player);
        Quest quest = Quests.get("5zombie");
        quest.start(player);
        player.getPlayer().sendMessage(""+quest.getRewards().size());
    }
}

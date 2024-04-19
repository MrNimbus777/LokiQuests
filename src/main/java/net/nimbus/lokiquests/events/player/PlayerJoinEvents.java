package net.nimbus.lokiquests.events.player;

import me.clip.placeholderapi.PlaceholderAPI;
import net.nimbus.lokiquests.core.dailyquest.DailyQuest;
import net.nimbus.lokiquests.core.dailyquest.DailyQuests;
import net.nimbus.lokiquests.core.dailyquest.dailyquests.DQCraft;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import net.nimbus.lokiquests.core.questplayers.QuestPlayers;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerJoinEvents implements Listener {
    @EventHandler
    public void onEvent(PlayerJoinEvent e){
        QuestPlayer player = QuestPlayers.load(e.getPlayer());
        QuestPlayers.register(player);
        player.runIndicator();
        player.setDailyQuests(new DailyQuest[]{DailyQuests.createQuest("craft", player, new ItemStack(Material.OAK_STAIRS), 12), null, null});
    }
}

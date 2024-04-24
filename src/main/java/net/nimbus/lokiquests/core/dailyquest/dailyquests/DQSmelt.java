package net.nimbus.lokiquests.core.dailyquest.dailyquests;

import net.nimbus.lokiquests.core.dailyquest.DailyQuest;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

public class DQSmelt extends DailyQuest {

    public DQSmelt(QuestPlayer player, Integer reward) {
        super(player, reward);
    }

    @Override
    public ItemStack getDisplay() {
        return null;
    }

    @Override
    public void run() {

    }

    @Override
    public boolean isCompleted(Event event) {
        return false;
    }

    @Override
    public boolean isCompleted() {
        return false;
    }

    @Override
    public String saveToString() {
        return null;
    }
}

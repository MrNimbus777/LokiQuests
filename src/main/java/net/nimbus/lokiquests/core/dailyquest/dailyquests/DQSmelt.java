package net.nimbus.lokiquests.core.dailyquest.dailyquests;

import net.nimbus.lokiquests.core.dailyquest.DailyQuest;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import org.bukkit.event.Event;

public class DQSmelt extends DailyQuest {

    public DQSmelt(QuestPlayer player) {
        super(player);
    }

    @Override
    public void run() {

    }

    @Override
    public boolean isCompleted(Event event) {
        return false;
    }

    @Override
    public String toString() {
        return null;
    }
}

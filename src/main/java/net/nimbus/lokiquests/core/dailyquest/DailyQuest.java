package net.nimbus.lokiquests.core.dailyquest;

import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import net.nimbus.lokiquests.core.reward.Reward;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

public abstract class DailyQuest {

    private final QuestPlayer player;

    public DailyQuest(QuestPlayer player){
        this.player = player;

        rewards = new ArrayList<>();
    }

    private static List<Reward> rewards;

    public static void setRewards(List<Reward> rewards) {
        DailyQuest.rewards = rewards;
    }

    public static List<Reward> getRewards() {
        return DailyQuest.rewards;
    }

    public void complete(){
        DailyQuest.rewards.forEach(r -> r.reward(this.player.getPlayer()));
        this.player.removeDailyQuest(this);
    }
    public abstract void run();
    public abstract boolean isCompleted(Event event);
    public abstract String toString();
}
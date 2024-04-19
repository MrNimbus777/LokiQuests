package net.nimbus.lokiquests.core.dailyquest;

import net.nimbus.lokiquests.core.reward.Reward;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class DailyQuest {

    private final Player player;

    public DailyQuest(Player player){
        this.player = player;

        rewards = new ArrayList<>();
    }

    private static List<Reward> rewards;

    public void complete(){
        rewards.forEach(r -> r.reward(this.player));
    }
    public abstract void run();
    public abstract boolean isCompleted();
}
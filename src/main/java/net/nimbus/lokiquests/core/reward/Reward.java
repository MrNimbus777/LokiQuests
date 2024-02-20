package net.nimbus.lokiquests.core.reward;

import net.nimbus.lokiquests.Utils;
import net.nimbus.lokiquests.core.reward.rewardprocessors.RewardProcessor;
import org.bukkit.entity.Player;

public class Reward {
    final String name;
    protected final String reward;

    final RewardProcessor processor;

    public Reward(String name, String reward, RewardProcessor processor){
        this.name = name;
        this.reward = reward;
        this.processor = processor;
    }

    public void reward(Player player){
        processor.executeReward(player, reward);
        player.sendMessage(Utils.toPrefix("You got " + processor.processMessage(name)));
    }
}

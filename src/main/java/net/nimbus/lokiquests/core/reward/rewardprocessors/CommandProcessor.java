package net.nimbus.lokiquests.core.reward.rewardprocessors;

import org.bukkit.entity.Player;

public class CommandProcessor implements RewardProcessor {
    @Override
    public void executeReward(Player player, String reward) {

    }

    @Override
    public String processMessage(String message) {
        return message;
    }
}

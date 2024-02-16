package net.nimbus.lokiquests.core.reward.rewardprocessors;

import org.bukkit.entity.Player;

public interface RewardProcessor {
    void executeReward(Player player, String reward);
    String processMessage(String message);
}

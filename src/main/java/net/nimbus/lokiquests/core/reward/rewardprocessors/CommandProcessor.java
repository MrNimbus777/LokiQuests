package net.nimbus.lokiquests.core.reward.rewardprocessors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandProcessor implements RewardProcessor {
    @Override
    public void executeReward(Player player, String reward) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), reward.replace("%player%", player.getName()));
    }

    @Override
    public String processMessage(String message) {
        return message;
    }
}

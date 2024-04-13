package net.nimbus.lokiquests.core.reward.rewardprocessors;

import net.nimbus.lokiquests.LQuests;
import org.bukkit.entity.Player;

public class ItemProcessor implements RewardProcessor {
    @Override
    public void executeReward(Player player, String reward) {
        player.getInventory().addItem(LQuests.a.getItems().getItemStack(reward));
    }

    @Override
    public String processMessage(String message) {
        return message;
    }
}

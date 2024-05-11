package net.nimbus.lokiquests.core.dialogues.action.actions;

import net.nimbus.lokiquests.core.dialogues.action.Action;
import net.nimbus.lokiquests.core.reward.rewardprocessors.RewardProcessor;
import net.nimbus.lokiquests.core.reward.rewardprocessors.RewardProcessors;
import org.bukkit.entity.Player;

public class ActionReward implements Action {
    @Override
    public void execute(Player player, String vars) {
        String processor_id = vars.split(":")[0];
        RewardProcessor processor = RewardProcessors.get(processor_id);
        processor.executeReward(player, vars.replace(processor_id + ":", ""));
    }
}
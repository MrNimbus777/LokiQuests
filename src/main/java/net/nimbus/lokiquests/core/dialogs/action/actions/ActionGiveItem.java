package net.nimbus.lokiquests.core.dialogs.action.actions;

import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.core.dialogs.action.Action;
import org.bukkit.entity.Player;

public class ActionGiveItem implements Action {
    @Override
    public void execute(Player player, String vars) {
        player.getInventory().addItem(LQuests.a.getItems().getItemStack(vars));
    }
}

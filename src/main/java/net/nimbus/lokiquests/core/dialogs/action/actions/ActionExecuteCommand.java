package net.nimbus.lokiquests.core.dialogs.action.actions;

import net.nimbus.lokiquests.core.dialogs.action.Action;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ActionExecuteCommand implements Action {
    @Override
    public void execute(Player player, String vars) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), vars.replace("%player%", player.getName()));
    }
}

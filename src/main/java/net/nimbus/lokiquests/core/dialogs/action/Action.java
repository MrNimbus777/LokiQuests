package net.nimbus.lokiquests.core.dialogs.action;

import org.bukkit.entity.Player;

public interface Action {
    void execute(Player player, String vars);
}

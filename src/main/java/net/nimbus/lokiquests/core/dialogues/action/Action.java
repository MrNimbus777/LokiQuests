package net.nimbus.lokiquests.core.dialogues.action;

import org.bukkit.entity.Player;

public interface Action {
    void execute(Player player, String vars);
}

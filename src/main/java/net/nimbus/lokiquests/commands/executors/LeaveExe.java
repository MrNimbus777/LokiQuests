package net.nimbus.lokiquests.commands.executors;

import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.Utils;
import net.nimbus.lokiquests.core.dungeon.Dungeon;
import net.nimbus.lokiquests.core.dungeon.Dungeons;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LeaveExe implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player p)) {
            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.player-only")));
            return true;
        }
        Dungeon dungeon = Dungeons.getDungeon(p);
        if(dungeon == null) {
            if(p.hasPermission("lq.admin")) {
                dungeon = Dungeons.getDungeonCloseToPlayer(p);
                if(dungeon != null) dungeon.teleportBack(p);
            }
            return true;
        }
        if(!dungeon.isCompleted()){
            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Actions.dungeon_not_complete")));
            return true;
        }
        dungeon.leave(p);
        return true;
    }
}

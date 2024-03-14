package net.nimbus.lokiquests.commands.executors;

import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.Utils;
import net.nimbus.lokiquests.core.quest.Quest;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import net.nimbus.lokiquests.core.questplayers.QuestPlayers;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class QuestExe implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player p)) {
            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.player-only")));
            return true;
        }
        if(args.length == 0) {
            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.quest.usage")));
            return true;
        }
        if(args[0].equalsIgnoreCase("list")){
            QuestPlayer qp = QuestPlayers.get(p);
            if(qp.getActiveQuests().isEmpty()) {
                sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.quest.empty")));
            } else {
                sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.quest.list")));
                for(Quest quest : qp.getActiveQuests()) {
                    quest.getDescription().forEach(p::sendMessage);
                }
            }
        }
        return true;
    }
}

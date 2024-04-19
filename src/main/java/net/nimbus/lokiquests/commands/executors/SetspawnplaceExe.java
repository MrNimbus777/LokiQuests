package net.nimbus.lokiquests.commands.executors;

import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetspawnplaceExe implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player p)) {
            sender.sendMessage("Player-only");
            return true;
        }
        if(!p.hasPermission("lq.admin")) {
            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.no-permission")));
            return true;
        }
        LQuests.a.getConfig().set("SpawnLocation", p.getLocation());
        LQuests.a.saveConfig();
        p.sendMessage("Spawn set");
        return true;
    }
}

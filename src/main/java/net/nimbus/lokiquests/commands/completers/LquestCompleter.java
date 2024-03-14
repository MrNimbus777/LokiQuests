package net.nimbus.lokiquests.commands.completers;

import net.nimbus.lokiquests.core.dialogs.Dialog;
import net.nimbus.lokiquests.core.dialogs.Dialogs;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LquestCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> result = new ArrayList<>();
        if(!sender.hasPermission("lq.admin")) return result;
        if(args.length == 0) return result;
        if(args.length == 1) {
            List<String> options = List.of("process", "item");
            for(String option : options) {
                if(option.startsWith(args[0].toLowerCase())) result.add(option);
            }
            return result;
        }
        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("process")) {
                List<String> options = Dialogs.getAll().stream().map(Dialog::getId).toList();
                for(String option : options) {
                    if(option.toLowerCase().startsWith(args[1].toLowerCase())) result.add(option);
                }
                return result;
            }
        }
        if(args.length == 3) {
            if(args[0].equalsIgnoreCase("process")) {
                List<String> options = Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
                for(String option : options) {
                    if(option.toLowerCase().startsWith(args[2].toLowerCase())) result.add(option);
                }
                return result;
            }
        }
        return result;
    }
}
package net.nimbus.lokiquests.commands.completers;

import net.nimbus.lokiquests.core.dialogues.Dialogue;
import net.nimbus.lokiquests.core.dialogues.Dialogues;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LquestCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> result = new ArrayList<>();
        if(!sender.hasPermission("lq.admin")) return result;
        if(args.length == 0) return result;
        if(args.length == 1) {
            List<String> options = List.of("process", "item", "reload", "generateDailyQuests");
            for(String option : options) {
                if(option.startsWith(args[0].toLowerCase())) result.add(option);
            }
            return result;
        }
        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("process")) {
                List<String> options = Dialogues.getAll().stream().map(Dialogue::getId).toList();
                for(String option : options) {
                    if(option.toLowerCase().startsWith(args[1].toLowerCase())) result.add(option);
                }
                return result;
            } else if(args[0].equalsIgnoreCase("generatedailyquests")){
                List<String> options = Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
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
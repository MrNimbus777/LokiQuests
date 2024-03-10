package net.nimbus.lokiquests.commands.completers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PartyCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> result = new ArrayList<>();
        if(args.length == 0) return result;
        if(args.length == 1) {
            List<String> options = List.of("create", "leave", "invite", "kick", "remove", "disband", "accept", "reject", "setLeader");
            for(String option : options) {
                if(option.toLowerCase().startsWith(args[0])) result.add(option);
            }
            return result;
        }
        if(args.length == 2) {
            switch (args[1].toLowerCase()) {
                case "invite" :
                case "setleader" :
                case "kick" : {

                }
            }
        }
        return result;
    }
}

package net.nimbus.lokiquests.commands.completers;

import net.nimbus.lokiquests.core.dungeon.mobspawner.MobSpawner;
import net.nimbus.lokiquests.core.dungeon.mobspawner.MobSpawners;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DungeonCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> result = new ArrayList<>();
        if(!sender.hasPermission("lq.admin")) return result;
        if(args.length == 0) return result;
        if(args.length == 1) {
            List<String> options = List.of("create", "spawner", "delete", "remove", "getSign");
            for(String option : options) {
                if(option.toLowerCase().startsWith(args[0].toLowerCase())) result.add(option);
            }
            return result;
        }
        if(args.length == 2) {
            if (args[0].toLowerCase().equals("spawner")) {
                List<String> options = List.of("create", "delete", "remove");
                for(String option : options) {
                    if(option.startsWith(args[1].toLowerCase())) result.add(option);
                }
                return result;
            }
            return result;
        }
        if(args.length == 3) {
            if (args[0].toLowerCase().equals("spawner")) {
                if (args[1].toLowerCase().equals("create")) {
                    List<String> options = List.of("1", "2", "3", "4", "5");
                    for (String option : options) {
                        if (option.startsWith(args[2].toLowerCase())) result.add(option);
                    }
                    return result;
                }
            }
            return result;
        }
        if(args.length == 4) {
            if (args[0].toLowerCase().equals("spawner")) {
                if (args[1].toLowerCase().equals("create")) {
                    List<String> options = MobSpawners.getAll().stream().map(MobSpawner::id).toList();
                    for (String option : options) {
                        if (option.toLowerCase().startsWith(args[3].toLowerCase())) result.add(option);
                    }
                    return result;
                }
            }
            return result;
        }
        if(args.length == 5) {
            if (args[0].toLowerCase().equals("spawner")) {
                if (args[1].toLowerCase().equals("create")) {
                    List<String> options = List.of("zombie", "creeper", "skeleton");
                    for (String option : options) {
                        if (option.startsWith(args[4].toLowerCase())) result.add(option);
                    }
                    return result;
                }
            }
            return result;
        }
        if(args.length == 6) {
            if (args[0].toLowerCase().equals("spawner")) {
                if (args[1].toLowerCase().equals("create")) {
                    List<String> options = List.of("10", "15", "25");
                    for (String option : options) {
                        if (option.startsWith(args[5].toLowerCase())) result.add(option);
                    }
                    return result;
                }
            }
            return result;
        }
        if(args.length == 7) {
            if (args[0].toLowerCase().equals("spawner")) {
                if (args[1].toLowerCase().equals("create")) {
                    List<String> options = List.of("1", "5", "25", "50");
                    for (String option : options) {
                        if (option.startsWith(args[6].toLowerCase())) result.add(option);
                    }
                    return result;
                }
            }
            return result;
        }
        return result;
    }
}

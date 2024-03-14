package net.nimbus.lokiquests.commands.completers;

import net.nimbus.lokiquests.core.dungeon.mobspawner.MobSpawner;
import net.nimbus.lokiquests.core.dungeon.mobspawner.MobSpawners;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
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
            List<String> options = List.of("create", "spawner", "delete", "remove", "getSign", "wall", "start", "stop");
            for(String option : options) {
                if(option.toLowerCase().startsWith(args[0].toLowerCase())) result.add(option);
            }
            return result;
        }
        if(args.length == 2) {
            if (args[0].equalsIgnoreCase("spawner") || args[0].equalsIgnoreCase("wall")) {
                List<String> options = List.of("create", "delete", "remove");
                for(String option : options) {
                    if(option.startsWith(args[1].toLowerCase())) result.add(option);
                }
                return result;
            }
            return result;
        }
        if(args.length == 3) {
            if (args[0].equalsIgnoreCase("spawner")) {
                if (args[1].equalsIgnoreCase("create")) {
                    List<String> options = MobSpawners.getAll().stream().map(MobSpawner::id).toList();
                    for (String option : options) {
                        if (option.toLowerCase().startsWith(args[2].toLowerCase())) result.add(option);
                    }
                    return result;
                }
            } else if(args[0].equalsIgnoreCase("wall")){
                if (args[1].toLowerCase().equals("create")) {
                    if(sender instanceof Player p) {
                        return List.of(p.getLocation().getBlockX()+"");
                    }
                }
            }
            return result;
        }
        if(args.length == 4) {
            if (args[0].equalsIgnoreCase("spawner")) {
                if (args[1].equalsIgnoreCase("create")) {
                    MobSpawner spawner = MobSpawners.get(args[2]);
                    List<String> options = spawner.types();
                    for (String option : options) {
                        if (option.toLowerCase().startsWith(args[3].toLowerCase())) result.add(option);
                    }
                    return result;
                }
            } else if(args[0].equalsIgnoreCase("wall")){
                if (args[1].equalsIgnoreCase("create")) {
                    if(sender instanceof Player p) {
                        return List.of(p.getLocation().getBlockY()+"");
                    }
                }
            }
            return result;
        }
        if(args.length == 5) {
            if (args[0].equalsIgnoreCase("spawner")) {
                if (args[1].equalsIgnoreCase("create")) {
                    List<String> options = List.of("5", "10", "15", "...");
                    for (String option : options) {
                        if (option.startsWith(args[4].toLowerCase())) result.add(option);
                    }
                    return result;
                }
            } else if(args[0].equalsIgnoreCase("wall")){
                if (args[1].equalsIgnoreCase("create")) {
                    if(sender instanceof Player p) {
                        return List.of(p.getLocation().getBlockZ()+"");
                    }
                }
            }
            return result;
        }
        if(args.length == 6) {
            if (args[0].equalsIgnoreCase("wall")) {
                if (args[1].equalsIgnoreCase("create")) {
                    if(sender instanceof Player p) {
                        return List.of(p.getLocation().getBlockX()+"");
                    }
                }
            }
            return result;
        }
        if(args.length == 7) {
            if (args[0].equalsIgnoreCase("wall")) {
                if (args[1].equalsIgnoreCase("create")) {
                    if(sender instanceof Player p) {
                        return List.of(p.getLocation().getBlockY()+"");
                    }
                }
            }
            return result;
        }
        if(args.length == 8) {
            if (args[0].equalsIgnoreCase("wall")) {
                if (args[1].equalsIgnoreCase("create")) {
                    if(sender instanceof Player p) {
                        return List.of(p.getLocation().getBlockZ()+"");
                    }
                }
            }
            return result;
        }
        return result;
    }
}

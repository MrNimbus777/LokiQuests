package net.nimbus.lokiquests.commands.completers;

import net.nimbus.lokiquests.core.party.Parties;
import net.nimbus.lokiquests.core.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PartyCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> result = new ArrayList<>();
        if(!(sender instanceof Player p)) return result;
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
                case "create" : {
                    return List.of("<name>");
                }
                case "invite" : {
                    Party party = Parties.get(p);
                    if(party == null) return result;
                    List<String> options = Bukkit.getOnlinePlayers().stream().filter(m -> party.getMembers().contains(m.getUniqueId())).map(Player::getName).toList();
                    for(String option : options) {
                        if(option.toLowerCase().startsWith(args[1])) result.add(option);
                    }
                    return result;
                }
                case "setleader" :
                case "kick" : {
                    Party party = Parties.get(p);
                    if(party == null) return result;
                    List<String> options = Bukkit.getOnlinePlayers().stream().filter(m -> !party.getMembers().contains(m.getUniqueId())).map(Player::getName).toList();
                    for(String option : options) {
                        if(option.toLowerCase().startsWith(args[1])) result.add(option);
                    }
                    return result;
                }
            }
        }
        return result;
    }
}

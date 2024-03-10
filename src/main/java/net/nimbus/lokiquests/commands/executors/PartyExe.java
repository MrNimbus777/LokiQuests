package net.nimbus.lokiquests.commands.executors;

import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.Utils;
import net.nimbus.lokiquests.core.party.Parties;
import net.nimbus.lokiquests.core.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PartyExe implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player p)) {
            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.player-only")));
            return true;
        }
        if(args.length == 0) {
            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.usage")));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "create" : {
                Party party = Parties.get(p);
                if(party != null) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.exists")));
                    return true;
                }
                party = new Party(p);
                Parties.add(party);
                sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.create")));
                return true;
            }
            case "leave" : {
                Party party = Parties.get(p);
                if(party == null) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.no_party")));
                    return true;
                }
                if(party.getLeader().equals(p)) {
                    party.setLeader(party.getMembers().get(0));
                }
                party.removePlayer(p);
                sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.leave")));
                return true;
            }
            case "invite" : {
                if(args.length == 1) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.invite.usage")));
                    return true;
                }
                Party party = Parties.get(p);
                if(party == null) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.no_party")));
                    return true;
                }
                if(!party.getLeader().equals(p)) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.not_leader")));
                    return true;
                }
                if(party.getLimit() <= party.getMembers().size()) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.full")));
                    return true;
                }
                Player invited = Bukkit.getPlayer(args[1]);
                if(invited == null) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.no_player").
                            replace("%player%", args[1])));
                    return true;
                }
                if(Parties.exists(Parties.invitations.get(invited))){
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.invite.has_invitation")));
                    return true;
                }
                Party invited_party = Parties.get(invited);
                if(invited_party != null) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.invite.has_party")));
                    return true;
                }
                Parties.invitations.put(invited, party);
                p.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.invite.success")
                        .replace("%player%", invited.getName())));
                invited.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.invite.receive")
                        .replace("%leader%", p.getName())));
                return true;
            }
            case "kick" : {
                if(args.length == 1) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.kick_usage")));
                    return true;
                }
                Party party = Parties.get(p);
                if(party == null) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.no_party")));
                    return true;
                }
                if(!party.getLeader().equals(p)) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.not_leader")));
                    return true;
                }
                Player kicked = Bukkit.getPlayer(args[1]);
                if(kicked == null) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.no_player").
                            replace("%player%", args[1])));
                    return true;
                }
                if (!party.getMembers().contains(kicked)) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.no_member").
                            replace("%player%", args[1])));
                    return true;
                }
                party.removePlayer(kicked);
                sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.kick").
                        replace("%player%", kicked.getName())));
                kicked.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.kicked")));
                return true;
            }
            case "disband" : {
                Party party = Parties.get(p);
                if(party == null) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.no_party")));
                    return true;
                }
                if(!party.getLeader().equals(p)) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.not_leader")));
                    return true;
                }
                Parties.remove(party);
                party.getAllMembers().forEach(m -> m.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.disband"))));
                return true;
            }
            case "accept" : {
                Party party = Parties.invitations.get(p);
                if(!Parties.exists(party)){
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.invite.no_invitation")));
                    return true;
                }
                if(party.getLimit() <= party.getMembers().size()) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.full")));
                    return true;
                }
                Parties.invitations.remove(p);
                p.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.accept").
                        replace("%leader%", party.getLeader().getName())));
                party.getLeader().sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.accept-feedback").
                        replace("%player%", p.getName())));
                return true;
            }
            case "reject" : {
                Party party = Parties.invitations.get(p);
                if(!Parties.exists(party)){
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.invite.no_invitation")));
                    return true;
                }
                Parties.invitations.remove(p);
                p.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.reject").
                        replace("%leader%", party.getLeader().getName())));
                party.getLeader().sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.reject-feedback").
                        replace("%player%", p.getName())));
                return true;
            }
            case "setleader" : {
                if(args.length == 1) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.setLeader_usage")));
                    return true;
                }
                Party party = Parties.get(p);
                if(party == null) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.no_party")));
                    return true;
                }
                if(!party.getLeader().equals(p)) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.not_leader")));
                    return true;
                }
                Player leader = Bukkit.getPlayer(args[1]);
                if(leader == null) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.no_player").
                            replace("%player%", args[1])));
                    return true;
                }
                if(party.getMembers().contains(leader));
                party.setLeader(leader);
                party.getMembers().forEach(m -> m.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.setLeader"))));
            }
            default: {
                sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.usage")));
                return true;
            }
        }
    }
}

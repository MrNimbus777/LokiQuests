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
                if(args.length == 1) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.create_usage")));
                    return true;
                }
                Party party = Parties.get(p);
                if(party != null) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.exists")));
                    return true;
                }
                party = new Party(args[1]);
                party.setLeader(p.getUniqueId());
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
                if(party.getLeader().equals(p.getUniqueId())) {
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
                if(!party.getLeader().equals(p.getUniqueId())) {
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
                if(Parties.exists(Parties.invitations.get(invited.getUniqueId()))){
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.invite.has_invitation")));
                    return true;
                }
                Party invited_party = Parties.get(invited);
                if(invited_party != null) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.invite.has_party")));
                    return true;
                }
                Parties.invitations.put(invited.getUniqueId(), party);
                p.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.invite.success")
                        .replace("%player%", invited.getName())));
                invited.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.invite.receive").
                        replace("%party%", party.getName())));
                return true;
            }
            case "kick" : {
                if(args.length == 1) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.kick.usage")));
                    return true;
                }
                Party party = Parties.get(p);
                if(party == null) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.no_party")));
                    return true;
                }
                if(!party.getLeader().equals(p.getUniqueId())) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.not_leader")));
                    return true;
                }
                Player kicked = Bukkit.getPlayer(args[1]);
                if(kicked == null) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.no_player").
                            replace("%player%", args[1])));
                    return true;
                }
                if (!party.getMembers().contains(kicked.getUniqueId())) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.no_member").
                            replace("%player%", args[1])));
                    return true;
                }
                party.removePlayer(kicked);
                sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.kick").
                        replace("%player%", kicked.getName())));
                kicked.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.kick.kicked")));
                return true;
            }
            case "disband" : {
                Party party = Parties.get(p);
                if(party == null) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.no_party")));
                    return true;
                }
                if(!party.getLeader().equals(p.getUniqueId())) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.not_leader")));
                    return true;
                }
                Parties.remove(party);
                party.getAllMembers().forEach(m -> {
                    try {
                        Bukkit.getPlayer(party.getLeader()).sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.disband")));
                    } catch (Exception e){}
                });
                return true;
            }
            case "accept" : {
                Party party = Parties.invitations.get(p.getUniqueId());
                if(!Parties.exists(party)){
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.invite.no_invitation")));
                    return true;
                }
                if(party.getLimit() <= party.getMembers().size()) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.full")));
                    return true;
                }
                Parties.invitations.remove(p.getUniqueId());
                party.addMember(p);
                p.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.accept").
                        replace("%party%", party.getName())));
                try {
                    Bukkit.getPlayer(party.getLeader()).sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.accept-feedback").
                            replace("%player%", p.getName())));
                } catch (Exception e) {}
                return true;
            }
            case "reject" : {
                Party party = Parties.invitations.get(p.getUniqueId());
                if(!Parties.exists(party)){
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.invite.no_invitation")));
                    return true;
                }
                Parties.invitations.remove(p.getUniqueId());
                p.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.reject").
                        replace("%party%", party.getName())));
                try {
                    Bukkit.getPlayer(party.getLeader()).sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.reject-feedback").
                            replace("%player%", p.getName())));
                } catch (Exception e) {}
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
                if(!party.getLeader().equals(p.getUniqueId())) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.not_leader")));
                    return true;
                }
                Player leader = Bukkit.getPlayer(args[1]);
                if(leader == null) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.no_player").
                            replace("%player%", args[1])));
                    return true;
                }
                if(party.getMembers().contains(leader.getUniqueId())){
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.no_member").
                            replace("%player%", args[1])));
                    return true;
                }
                party.setLeader(leader.getUniqueId());
                party.getMembers().forEach(m -> {
                    try {
                        Bukkit.getPlayer(party.getLeader()).sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.setLeader")));
                    } catch (Exception e) {}
                });
            }
            default: {
                sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.party.usage")));
                return true;
            }
        }
    }
}

package net.nimbus.lokiquests.core.party;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Parties {
    private static final List<Party> list = new ArrayList<>();
    public static final HashMap<UUID, Party> invitations = new HashMap<>();
    public static void add(Party party) {
        if(list.contains(party)) return;
        list.add(party);
    }
    public static List<Party> getAll(){
        return list;
    }
    public static void remove(Party party){
        list.remove(party);
    }
    public static Party get(Player player) {
        for(Party party : getAll()){
            if(party.getAllMembers().contains(player.getUniqueId())) return party;
        }
        return null;
    }

    public static boolean exists(Party party) {
        return list.contains(party);
    }

    public static void clearRAM(){
        list.clear();
        invitations.clear();
    }
}

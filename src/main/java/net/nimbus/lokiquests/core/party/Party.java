package net.nimbus.lokiquests.core.party;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Party {
    private UUID leader;
    private final String name;
    private final List<UUID> members;
    private static final short limit = 5;

    public Party(String name){
        this.name = name;
        members = new ArrayList<>();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj instanceof Party p) {
            return p.getLeader().equals(getLeader());
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public void setLeader(UUID player) {
        members.remove(player);
        if(leader != null) members.add(leader);
        this.leader = player;
    }
    public void addMember(Player player){
        if(members.contains(player.getUniqueId())) return;
        members.add(player.getUniqueId());
    }
    public void removePlayer(Player player){
        members.remove(player.getUniqueId());
    }


    public List<UUID> getAllMembers(){
        List<UUID> list = new ArrayList<>(members);
        list.add(leader);
        return list;
    }
    public List<UUID> getMembers() {
        return members;
    }

    public UUID getLeader() {
        return leader;
    }

    public short getLimit() {
        return limit;
    }
}

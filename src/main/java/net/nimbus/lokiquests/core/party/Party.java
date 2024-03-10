package net.nimbus.lokiquests.core.party;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Party {
    private Player leader;
    private List<Player> members;
    private static final short limit = 3;

    public Party(Player leader){
        this.leader = leader;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj instanceof Player p) {
            return p.getUniqueId().equals(getLeader().getUniqueId());
        }
        return false;
    }

    public void setLeader(Player player) {
        members.remove(player);
        members.add(leader);
        this.leader = player;
    }
    public void addMember(Player player){
        if(members.contains(player)) return;
        members.add(player);
    }
    public void removePlayer(Player player){
        members.remove(player);
    }


    public List<Player> getAllMembers(){
        List<Player> list = new ArrayList<>(members);
        list.add(leader);
        return list;
    }
    public List<Player> getMembers() {
        return members;
    }

    public Player getLeader() {
        return leader;
    }

    public short getLimit() {
        return limit;
    }
}

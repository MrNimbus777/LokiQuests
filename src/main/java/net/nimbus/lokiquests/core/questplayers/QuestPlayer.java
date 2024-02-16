package net.nimbus.lokiquests.core.questplayers;

import net.nimbus.lokiquests.core.quest.Quest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QuestPlayer {
    private final UUID uuid;

    private List<Quest> activeQuests;
    private List<Quest> accomplishedQuests;

    public QuestPlayer(UUID uuid){
        this.uuid = uuid;
        activeQuests = new ArrayList<>();
        accomplishedQuests = new ArrayList<>();
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setActiveQuests(List<Quest> list) {
        this.activeQuests = list;
    }
    public boolean addActiveQuest(Quest quest){
        if(!this.activeQuests.contains(quest)){
            this.activeQuests.add(quest);
            return true;
        }
        return false;
    }
    public void removeActiveQuest(Quest quest){
        this.activeQuests.remove(quest);
    }


    public List<Quest> getActiveQuests(){
        return this.activeQuests;
    }

    public void setAccomplishedQuests(List<Quest> list) {
        this.accomplishedQuests = list;
    }
    public boolean addAccomplishedQuest(Quest quest){
        if(!this.accomplishedQuests.contains(quest)){
            this.accomplishedQuests.add(quest);
            return true;
        }
        return false;
    }
    public List<Quest> getAccomplishedQuests(){
        return accomplishedQuests;
    }

    public Player getPlayer(){
        return Bukkit.getPlayer(getUUID());
    }
}

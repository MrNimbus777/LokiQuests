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
    private List<Quest> finishedQuests;
    private List<Quest> completedQuests;

    public QuestPlayer(UUID uuid){
        this.uuid = uuid;
        activeQuests = new ArrayList<>();
        finishedQuests = new ArrayList<>();
        completedQuests = new ArrayList<>();
    }

    public UUID getUUID() {
        return uuid;
    }


    public void save(){

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
        return new ArrayList<>(this.activeQuests);
    }

    public void setFinishedQuests(List<Quest> list) {
        this.finishedQuests = list;
    }
    public boolean addFinishedQuest(Quest quest){
        if(!this.finishedQuests.contains(quest)){
            this.finishedQuests.add(quest);
            return true;
        }
        return false;
    }
    public void removeFinishedQuest(Quest quest){
        this.finishedQuests.remove(quest);
    }
    public List<Quest> getFinishedQuests(){
        return new ArrayList<>(this.finishedQuests);
    }

    public void setCompletedQuests(List<Quest> list) {
        this.completedQuests = list;
    }
    public boolean addCompletedQuest(Quest quest){
        if(!this.completedQuests.contains(quest)){
            this.completedQuests.add(quest);
            return true;
        }
        return false;
    }
    public List<Quest> getCompletedQuests(){
        return new ArrayList<>(this.completedQuests);
    }

    public Player getPlayer(){
        return Bukkit.getPlayer(getUUID());
    }
}

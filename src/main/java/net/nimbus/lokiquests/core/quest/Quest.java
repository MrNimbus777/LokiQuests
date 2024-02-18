package net.nimbus.lokiquests.core.quest;

import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import net.nimbus.lokiquests.core.reward.Reward;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

public abstract class Quest {

    private final String id;
    private List<Reward> rewards;
    private final String name;
    private boolean display;
    public Quest(String id, String name){
        this.id = id;
        this.name = name;
        this.rewards = new ArrayList<>();
        this.display = true;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    public void setRewards(List<Reward> rewards){
        this.rewards = rewards;
    }

    public void start(QuestPlayer player) {
        player.addActiveQuest(this);
        if(display) player.getPlayer().sendMessage("You have a new quest: " + name);
    }

    public void finish(QuestPlayer player) {
        if(display) player.getPlayer().sendMessage("You finished quest " + name);
        player.removeActiveQuest(this);
        player.addFinishedQuest(this);
    }

    public void complete(QuestPlayer player){
        if(display) player.getPlayer().sendMessage("You accomplished quest " + name);
        for(Reward reward : rewards){
            reward.reward(player.getPlayer());
        }
        player.removeFinishedQuest(this);
        player.addCompletedQuest(this);
    }
    public abstract void process(Event event);
}

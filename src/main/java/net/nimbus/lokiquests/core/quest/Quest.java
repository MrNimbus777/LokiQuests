package net.nimbus.lokiquests.core.quest;

import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.Utils;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import net.nimbus.lokiquests.core.reward.Reward;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Quest {

    private final String id;
    private List<Reward> rewards;
    private final String name;
    protected boolean display;
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
        if(display) player.getPlayer().sendMessage(Utils.toPrefix(LQuests.a.getMessage("Actions.quest_start").replace("%name%", name)));
    }

    public void finish(QuestPlayer player) {
        if(display) player.getPlayer().sendMessage(Utils.toPrefix(LQuests.a.getMessage("Actions.quest_finish").replace("%name%", name)));
        player.removeActiveQuest(this);
        player.addFinishedQuest(this);
    }

    public void complete(QuestPlayer player){
        if(display) player.getPlayer().sendMessage(Utils.toPrefix(LQuests.a.getMessage("Actions.quest_complete").replace("%name%", name)));
        for(Reward reward : getRewards()){
            reward.reward(player.getPlayer());
        }
        player.removeFinishedQuest(this);
        player.addCompletedQuest(this);
    }

    public boolean isStarted(QuestPlayer player){
        return player.getActiveQuests().contains(this);
    }
    public boolean isFinished(QuestPlayer player){
        return player.getFinishedQuests().contains(this);
    }
    public boolean isCompleted(QuestPlayer player) {
        return player.getCompletedQuests().contains(this);
    }

    public abstract void process(Event event);
    public abstract int getProgress(UUID uuid);
    public abstract void setProgress(UUID uuid, int progress);
}

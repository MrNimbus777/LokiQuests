package net.nimbus.lokiquests.core.dailyquest;

import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

public abstract class DailyQuest {

    protected final QuestPlayer player;
    protected final int reward;

    public DailyQuest(QuestPlayer player, Integer reward){
        this.player = player;
        this.reward = reward;
    }
    public DailyQuest(QuestPlayer player, String str) {
        this.player = player;
        this.reward = Integer.parseInt(str.split(";")[0]);
    }


    public void complete(){
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), LQuests.a.getDailyQuests().getString("reward_command")
                .replace("%player%", player.getPlayer().getName())
                .replace("%amount%", reward+""));
        this.player.removeDailyQuest(this);
    }
    @Override
    public String toString(){
        return DailyQuests.getID(getClass())+":"+reward+";"+saveToString();
    }
    public abstract ItemStack getDisplay();
    public abstract void run();
    public abstract boolean isCompleted(Event event);
    public abstract boolean isCompleted();
    public abstract String saveToString();
}
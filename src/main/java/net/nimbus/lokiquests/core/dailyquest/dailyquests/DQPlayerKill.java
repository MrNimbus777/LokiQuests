package net.nimbus.lokiquests.core.dailyquest.dailyquests;

import net.nimbus.api.modules.gui.core.itembuilder.ItemBuilder;
import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.Utils;
import net.nimbus.lokiquests.core.dailyquest.DailyQuest;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DQPlayerKill extends DailyQuest {

    private final int amount;

    private int progress;

    public DQPlayerKill(QuestPlayer player, Integer reward, Integer amount) {
        super(player, reward);

        this.amount = amount;

        this.progress = 0;
    }
    public DQPlayerKill(QuestPlayer player, String str) {
        super(player, str);
        str = str.replaceFirst(reward+";", "");

        String[] split = str.split(",");

        this.amount = Integer.parseInt(split[1]);
        this.progress = Integer.parseInt(split[0]);
    }

    @Override
    public ItemStack getDisplay() {
        return new ItemBuilder(Material.IRON_SWORD)
                .setName("&#dea2baKill &#e872a1Players")
                .setLore(
                        "",
                        "  &#e0b8e6Reward:       &#ff9100" + reward + "&#a2ff29 $    ",
                        "  &#e0b8e6Progress:    &#a3daff" + progress + " &#e0b8e6/&#ff668a " + amount+"    ",
                        ""
                )
                .setEnchanted()
                .build();
    }

    @Override
    public void run() {

    }

    @Override
    public boolean isCompleted(Event event) {
        if(event == null) return isCompleted();
        if(isCompleted()) return true;
        if(event instanceof PlayerDeathEvent e) {
            progress++;
        }
        return isCompleted();
    }

    @Override
    public boolean isCompleted() {
        return progress >= amount;
    }

    @Override
    public String saveToString() {
        return progress+","+amount;
    }

    public static DQPlayerKill generate(QuestPlayer player, Integer reward, ConfigurationSection section) {
        try {
            String[] split = section.getString("amount").split("-");
            int amount = LQuests.a.r.nextInt(Integer.parseInt(split[0]), Integer.parseInt(split[1])+1);
            return new DQPlayerKill(player, reward, amount);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

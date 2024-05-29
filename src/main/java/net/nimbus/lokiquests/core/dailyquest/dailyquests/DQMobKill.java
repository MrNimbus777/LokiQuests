package net.nimbus.lokiquests.core.dailyquest.dailyquests;

import net.minecraft.world.entity.Entity;
import net.nimbus.api.modules.gui.core.itembuilder.ItemBuilder;
import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.core.dailyquest.DailyQuest;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DQMobKill extends DailyQuest {

    private final EntityType mob;
    private final int amount;

    private int progress;

    public DQMobKill(QuestPlayer player, Integer reward, EntityType mob, Integer amount) {
        super(player, reward);

        this.mob = mob;
        this.amount = amount;

        this.progress = 0;
    }

    public DQMobKill(QuestPlayer player, String str) {
        super(player, str);
        str = str.replaceFirst(reward+";", "");

        String[] split = str.split(",");

        this.mob = EntityType.valueOf(split[2].toUpperCase());
        this.amount = Integer.parseInt(split[1]);

        this.progress = Integer.parseInt(split[0]);
    }

    private String getMobName(){
        String low = mob.name()
                .replace("_", " ")
                .toLowerCase();
        List<String> list = new ArrayList<>();
        for(String split : low.split(" ")) {
            char[] word = split.toCharArray();
            word[0] = Character.toUpperCase(word[0]);
            list.add(String.copyValueOf(word));
        }
        return String.join(" ", list);
    }

    @Override
    public ItemStack getDisplay() {
        return new ItemBuilder(Material.GOLDEN_SWORD)
                .setName("&#ac95c2Kill Mob &#ae72e8" + getMobName())
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
        if(event instanceof EntityDeathEvent e) {
            if(e.getEntity().getType() == this.mob) {
                progress++;
            }
        }
        return isCompleted();
    }

    @Override
    public boolean isCompleted() {
        return progress >= amount;
    }

    @Override
    public String saveToString() {
        return progress+","+amount+","+mob.name();
    }

    public static DQMobKill generate(QuestPlayer player, Integer reward, ConfigurationSection section) {
        try {
            List<String> amounts = new ArrayList<>(section.getKeys(false));
            int amount = Integer.parseInt(amounts.get(LQuests.a.r.nextInt(amounts.size())));
            List<String> types = new ArrayList<>(section.getStringList(""+amount));
            amount = (int) (0.5+(amount*(1+(LQuests.a.r.nextInt(11)-5)/100.0)));
            EntityType type = EntityType.valueOf(types.get(LQuests.a.r.nextInt(types.size())).toUpperCase());
            return new DQMobKill(player, reward, type, amount);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

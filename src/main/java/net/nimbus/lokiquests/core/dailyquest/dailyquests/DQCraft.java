package net.nimbus.lokiquests.core.dailyquest.dailyquests;

import net.nimbus.lokiquests.core.dailyquest.DailyQuest;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DQCraft extends DailyQuest {

    private final ItemStack result;
    private final int amount;

    public DQCraft(QuestPlayer player, ItemStack result, int amount) {
        super(player);
        this.result = result.clone();
        this.result.setAmount(1);
        this.amount = amount;

        progress = 0;
    }
    private int progress;

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }

    @Override
    public void run() {

    }

    @Override
    public boolean isCompleted(Event event) {
        if(event == null) return progress >= amount;
        if(event instanceof CraftItemEvent e) {
            ItemStack clone = e.getRecipe().getResult().clone();
            clone.setAmount(1);
            int amount_per_craft = e.getRecipe().getResult().getAmount();
            if(clone.serialize().equals(result.serialize())){
                switch (e.getClick()) {
                    case LEFT, RIGHT, NUMBER_KEY -> {
                        progress+=amount_per_craft;
                    }
                    case SHIFT_LEFT, SHIFT_RIGHT -> {
                        int amountOfCraft = getCraftingAmount(e);
                        int amountOfSpace = getSpaceFor((Player) e.getWhoClicked(), e.getRecipe().getResult()) / e.getRecipe().getResult().getAmount();
                        progress += (Math.min(amountOfCraft, amountOfSpace))*amount_per_craft;
                    }
                }
            }
        }
        return progress >= amount;
    }
    private int getSpaceFor(Player player, ItemStack itemStack) {
        ItemStack search = itemStack.clone();
        search.setAmount(1);
        int space = 0;
        for(int i = 0; i < 36; i++){
            ItemStack compare = player.getInventory().getItem(i);
            if(compare == null) {
                space += search.getMaxStackSize();
                continue;
            }
            compare = compare.clone();
            compare.setAmount(1);
            if(compare.serialize().equals(search.serialize())) {
                space += search.getMaxStackSize() - player.getInventory().getItem(i).getAmount();
            }
        }
        return space;
    }
    private int getCraftingAmount(CraftItemEvent e){
        List<Integer> list = new ArrayList<>();
        for(int i = 1; i < 10; i++) {
            if(e.getInventory().getContents()[i] == null) continue;
            if(e.getInventory().getContents()[i].getType() == Material.AIR) continue;
            list.add(e.getInventory().getContents()[i].getAmount());
        }
        if(list.isEmpty()) return 0;
        int min = 0;
        for(int i = 1; i < list.size(); i ++) {
            if(list.get(min) > list.get(i)) min = i;
        }
        return list.get(min);
    }
    @Override
    public String toString() {
        return null;
    }
}

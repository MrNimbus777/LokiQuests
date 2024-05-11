package net.nimbus.lokiquests.core.dailyquest.dailyquests;

import net.nimbus.api.modules.gui.core.itembuilder.ItemBuilder;
import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.Utils;
import net.nimbus.lokiquests.core.dailyquest.DailyQuest;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DQCraft extends DailyQuest {

    private final ItemStack result;
    private final int amount;

    public DQCraft(QuestPlayer player, Integer reward, ItemStack result, Integer amount) {
        super(player, reward);
        this.result = result.clone();
        this.result.setAmount(1);
        this.amount = amount;

        progress = 0;
    }

    public DQCraft(QuestPlayer player, String vars) {
        super(player, vars);
        vars = vars.replaceFirst(reward+";", "");

        YamlConfiguration c = new YamlConfiguration();
        try {
            c.loadFromString(vars);
        } catch (Exception e) {
            e.printStackTrace();
        }

        result = c.getItemStack("item");
        result.setAmount(1);

        amount = c.getInt("amount");
        setProgress(c.getInt("progress", 0));
    }

    @Override
    public ItemStack getDisplay() {
        return new ItemBuilder(result)
                .setName("&#c2c095Craft &#fffbb0" + Utils.getLocalisedName(result.getType()))
                .setLore(
                        "",
                        "  &#e0b8e6Reward:       &#ff9100" + reward + "&#a2ff29 $    ",
                        "  &#e0b8e6Progress:    &#a3daff" + progress + " &#e0b8e6/&#ff668a " + amount+"    ",
                        ""
                )
                .setEnchanted()
                .hideAttributes()
                .build();
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
        if(event == null) return isCompleted();
        if(isCompleted()) return true;
        if(event instanceof CraftItemEvent e) {
            ItemStack clone = e.getRecipe().getResult().clone();
            clone.setAmount(1);
            int amount_per_craft = e.getRecipe().getResult().getAmount();
            if(clone.serialize().equals(result.serialize())){
                switch (e.getClick()) {
                    case LEFT, RIGHT -> {
                        if(e.getCursor() == null) {
                            progress+=amount_per_craft;
                            break;
                        }
                        ItemStack cursor = e.getCursor().clone();
                        int amount = cursor.getAmount();
                        cursor.setAmount(1);
                        if(!e.getCursor().serialize().equals(this.result.serialize())) break;
                        int space = cursor.getMaxStackSize() - amount;
                        if(space > amount_per_craft) progress+=amount_per_craft;
                    }
                    case NUMBER_KEY -> {
                        if(player.getPlayer().getInventory().getItem(e.getHotbarButton()) == null) progress+=amount_per_craft;
                    }
                    case SHIFT_LEFT, SHIFT_RIGHT -> {
                        int amountOfCraft = getCraftingAmount(e);
                        int amountOfSpace = getSpaceFor((Player) e.getWhoClicked(), e.getRecipe().getResult()) / e.getRecipe().getResult().getAmount();
                        progress += (Math.min(amountOfCraft, amountOfSpace))*amount_per_craft;
                    }
                }
            }
        }
        return isCompleted();
    }

    @Override
    public boolean isCompleted() {
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
    public String saveToString() {
        YamlConfiguration c = new YamlConfiguration();
        c.set("item", result);
        c.set("amount", amount);
        c.set("progress", getProgress());
        return c.saveToString();
    }

    public static DQCraft generate(QuestPlayer player, Integer reward, ConfigurationSection section) {
        try {
            List<String> amounts = new ArrayList<>(section.getKeys(false));
            int amount = Integer.parseInt(amounts.get(LQuests.a.r.nextInt(amounts.size())));
            List<String> types = new ArrayList<>(section.getStringList(""+amount));
            amount = (int) (0.5+(amount*(1+(LQuests.a.r.nextInt(11)-5)/100.0)));
            Material material = Material.valueOf(types.get(LQuests.a.r.nextInt(types.size())).toUpperCase());
            return new DQCraft(player, reward, new ItemStack(material), amount);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

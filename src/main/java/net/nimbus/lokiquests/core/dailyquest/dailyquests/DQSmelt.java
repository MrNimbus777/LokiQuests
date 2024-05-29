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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DQSmelt extends DailyQuest {
    private int progress;
    private final ItemStack result;
    private final int amount;
    public DQSmelt(QuestPlayer player, Integer reward, ItemStack result, Integer amount) {
        super(player, reward);

        this.result = result;
        this.result.setAmount(1);
        this.amount = amount;

        this.progress = 0;
    }
    public DQSmelt(QuestPlayer player, String vars) {
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
                .setName("&#c29d95Smelt &#ffb3b0" + Utils.getLocalisedName(result.getType()))
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

    @Override
    public void run() {

    }

    @Override
    public boolean isCompleted(Event event) {
        if(event == null) return isCompleted();
        if(isCompleted()) return true;
        if(event instanceof InventoryClickEvent e) {
            if(e.getInventory().getType() == InventoryType.FURNACE ||
                    e.getInventory().getType() == InventoryType.BLAST_FURNACE ||
                    e.getInventory().getType() == InventoryType.SMOKER) {
                if(e.getSlot() == 2) {
                    ItemStack result = e.getInventory().getItem(2);
                    if (result != null) {
                        result = result.clone();
                        int res_amount = result.getAmount();
                        result.setAmount(1);
                        if(this.result.serialize().equals(result.serialize())) {
                            switch (e.getClick()) {
                                case RIGHT -> {
                                    if(e.getCursor() == null) {
                                        progress += (int) (res_amount/2.0 + 0.5);
                                        break;
                                    }
                                    ItemStack cursor = e.getCursor().clone();
                                    int cur_amount = cursor.getAmount();
                                    cursor.setAmount(1);
                                    if(!e.getCursor().serialize().equals(this.result.serialize())) break;
                                    int space = cursor.getMaxStackSize() - cur_amount;
                                    if(space > res_amount) progress += res_amount;
                                }
                                case LEFT -> {
                                    if(e.getCursor() == null) {
                                        progress += res_amount;
                                        break;
                                    }
                                    ItemStack cursor = e.getCursor().clone();
                                    int cur_amount = cursor.getAmount();
                                    cursor.setAmount(1);
                                    if(!e.getCursor().serialize().equals(this.result.serialize())) break;
                                    int space = cursor.getMaxStackSize() - cur_amount;
                                    if(space > res_amount) progress+=res_amount;
                                }
                                case NUMBER_KEY -> {
                                    if(player.getPlayer().getInventory().getItem(e.getHotbarButton()) == null) progress+=res_amount;
                                }
                                case SHIFT_LEFT, SHIFT_RIGHT -> {
                                    int space = getSpaceFor(player.getPlayer(), result);
                                    progress += Math.min(res_amount, space);
                                }
                            }
                        }
                    }
                }
            }
        }
        return isCompleted();
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
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
    @Override
    public String saveToString() {
        YamlConfiguration c = new YamlConfiguration();
        c.set("item", result);
        c.set("amount", amount);
        c.set("progress", getProgress());
        return c.saveToString();
    }

    public static DQSmelt generate(QuestPlayer player, Integer reward, ConfigurationSection section) {
        try {
            List<String> amounts = new ArrayList<>(section.getKeys(false));
            int amount = Integer.parseInt(amounts.get(LQuests.a.r.nextInt(amounts.size())));
            List<String> types = new ArrayList<>(section.getStringList(""+amount));
            amount = (int) (0.5+(amount*(1+(LQuests.a.r.nextInt(11)-5)/100.0)));
            Material material = Material.valueOf(types.get(LQuests.a.r.nextInt(types.size())).toUpperCase());
            return new DQSmelt(player, reward, new ItemStack(material), amount);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

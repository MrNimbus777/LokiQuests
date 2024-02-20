package net.nimbus.lokiquests.core.quest.quests;

import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.Utils;
import net.nimbus.lokiquests.core.quest.Quest;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import net.nimbus.lokiquests.core.reward.Reward;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.UUID;

public class ItemCollectQuest extends Quest {

    private final String item;
    private final Processor processor;
    private final int amount;
    private final boolean remove;
    public ItemCollectQuest(String id, String name, String item, int amount, boolean remove) {
        super(id, name);

        this.item = item.toUpperCase(Locale.ROOT);
        this.amount = amount;
        this.remove = remove;

        Material material = Material.matchMaterial(item);
        if(material == null){
            String[] split = item.split(":");
            if(split.length == 2) {
                processor = (item1, var) -> {
                    String[] split1 = var.split(":");
                    return Utils.readTag(item1, split1[0]).equals(split1[1]);
                };
            } else processor = (item12, var) -> false;
        } else {
            processor = (item13, var) -> {
                if(item13 == null) return false;
                return item13.getType().name().equalsIgnoreCase(var);
            };
        }
    }

    @Override
    public void process(Event event) {
        if(event instanceof EntityPickupItemEvent e) {
            if(e.getEntity() instanceof Player player) {
                if(processor.isItem(e.getItem().getItemStack(), item)) {
                    int progress = e.getItem().getItemStack().getAmount() + getProgress(player.getUniqueId());
                    if(progress < amount){
                        player.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Actions.quest_progress").
                                replace("%progress%", progress+"").
                                replace("%goal%", amount+"").
                                replace("%percentage%", (progress*100/amount)+"")
                        ));
                    } else {
                        player.sendMessage(Utils.toPrefix("You got enough items."));
                    }
                }
            }
        }
    }

    @Override
    public int getProgress(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if(player == null) return 0;
        int amount = 0;
        for(int i = 0; i < 36; i ++) {
            ItemStack itemStack = player.getInventory().getItem(i);
            if(processor.isItem(itemStack, item)) {
                amount += itemStack.getAmount();
            }
        }
        return amount;
    }

    @Override
    public boolean isFinished(QuestPlayer player) {
        if(!isStarted(player)) return false;
        int progress = getProgress(player.getUUID());
        return progress >= amount;
    }

    @Override
    public void complete(QuestPlayer player) {
        player.removeActiveQuest(this);
        if(remove) {
            Player p = player.getPlayer();
            int amount = this.amount;
            for (int i = 0; i < 36 && amount > 0; i++) {
                ItemStack item = p.getInventory().getItem(i);
                if(item == null) continue;
                if(processor.isItem(item, this.item)){
                    if(amount > item.getAmount()) {
                        amount -= item.getAmount();
                        item.setAmount(0);
                    } else {
                        item.setAmount(item.getAmount()-amount);
                        break;
                    }
                    p.getInventory().setItem(i, item);
                }
            }
        }
        if(display) player.getPlayer().sendMessage(Utils.toPrefix(LQuests.a.getMessage("Actions.quest_complete").replace("%name%", getName())));
        for(Reward reward : getRewards()){
            reward.reward(player.getPlayer());
        }
        player.addCompletedQuest(this);
    }

    @Override
    public void setProgress(UUID uuid, int progress) {

    }

    private interface Processor{
        boolean isItem(ItemStack item, String var);
    }
}

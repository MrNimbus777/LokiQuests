package net.nimbus.lokiquests.events.player;

import net.nimbus.lokiquests.Vars;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerItemConsumeEvents implements Listener {
    @EventHandler
    public void onEvent(PlayerItemConsumeEvent e){
        if(!e.getPlayer().getLocation().getWorld().equals(Vars.DUNGEON_WORLD)) return;
        if(e.getItem().getType() == Material.CHORUS_FRUIT) e.setCancelled(true);
        ItemStack item = e.getPlayer().getEquipment().getItem(e.getHand());
        item.setAmount(item.getAmount()-1);
        e.getPlayer().getEquipment().setItem(e.getHand(), item);
        e.getPlayer().setFoodLevel(e.getPlayer().getFoodLevel() > 18 ? 20 : e.getPlayer().getFoodLevel()+4);
    }
}

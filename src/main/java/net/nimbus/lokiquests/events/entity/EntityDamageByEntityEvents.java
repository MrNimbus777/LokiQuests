package net.nimbus.lokiquests.events.entity;

import net.nimbus.lokiquests.core.party.Parties;
import net.nimbus.lokiquests.core.party.Party;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageByEntityEvents implements Listener {
    @EventHandler
    public void onEvent(EntityDamageByEntityEvent e){
        if (e.getEntity() instanceof Player player) {
            if(e.getDamager() instanceof Player damager) {
                Party p1 = Parties.get(player);
                Party p2 = Parties.get(damager);
                if(p1 == null) {
                    return;
                }
                if(p1.equals(p2)){
                    e.setCancelled(true);
                }
            } else if(e.getDamager() instanceof Projectile pro){
                if(pro.getShooter() instanceof Player damager){
                    Party p1 = Parties.get(player);
                    Party p2 = Parties.get(damager);
                    if(p1 == null) {
                        return;
                    }
                    if(p1.equals(p2)){
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
}

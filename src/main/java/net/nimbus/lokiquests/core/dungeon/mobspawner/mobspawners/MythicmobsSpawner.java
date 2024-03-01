package net.nimbus.lokiquests.core.dungeon.mobspawner.mobspawners;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import net.nimbus.lokiquests.core.dungeon.mobspawner.MobSpawner;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class MythicmobsSpawner implements MobSpawner {

    @Override
    public Entity spawn(Location location, String type) {
        MythicMob mob = MythicBukkit.inst().getMobManager().getMythicMob(type).orElse(null);
        ActiveMob living = mob.spawn(BukkitAdapter.adapt(location),1);
        return living.getEntity().getBukkitEntity();
    }

    @Override
    public String id() {
        return "mythicmobs";
    }
}

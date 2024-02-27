package net.nimbus.lokiquests.core.dungeon.mobspawner;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface MobSpawner {
    Entity spawn(Location location, String type);
    String id();
}

package net.nimbus.lokiquests.core.dungeon.mobspawner;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.List;

public interface MobSpawner {
    Entity spawn(Location location, String type);
    String id();
    List<String> types();
}

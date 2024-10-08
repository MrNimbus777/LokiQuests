package net.nimbus.lokiquests.core.dungeon.mobspawner.mobspawners;

import net.nimbus.lokiquests.core.dungeon.mobspawner.MobSpawner;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.List;

public class MinecraftSpawner implements MobSpawner {
    @Override
    public Entity spawn(Location location, String type) {
        try {
            return location.getWorld().spawnEntity(location, EntityType.valueOf(type.toUpperCase()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String id() {
        return "vanilla";
    }

    @Override
    public List<String> types() {
        return Arrays.stream(EntityType.values()).map(Enum::name).toList();
    }
}

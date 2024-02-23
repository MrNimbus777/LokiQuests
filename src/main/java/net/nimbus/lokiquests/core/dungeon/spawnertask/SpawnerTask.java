package net.nimbus.lokiquests.core.dungeon.spawnertask;

import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.core.dungeon.mobspawner.MobSpawner;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpawnerTask {
    private static final short limit = 15;
    private final int power;
    private final Location location;
    private final MobSpawner spawner;
    private final String type;
    private final int complete;
    private int kills;
    private boolean completed;

    public SpawnerTask(int power, Location location, MobSpawner spawner, String type, int complete){
        this.power = power;
        this.location = location;
        this.spawner = spawner;
        this.type = type;
        this.complete = complete;
        list = new ArrayList<>();
        kills = 0;
        completed = false;
    }
    private final List<Entity> list;
    public void addEntity(Entity entity){
        list.add(entity);
    }
    public void removeEntity(Entity entity){
        list.remove(entity);
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    public void complete(){
        setCompleted(true);
        stop();

    }
    public void addKill(){
        kills++;
        if(kills >= complete){
            complete();
        }
    }
    BukkitRunnable task;
    public void start() {
        task = new BukkitRunnable() {
            private final Random random = new Random();
            private boolean isASpawnLocation(Location location){
                return location.getBlock().isEmpty() && location.clone().add(0, 1, 0).getBlock().isEmpty();
            }
            @Override
            public void run() {
                if(isCompleted()) return;
                if(location.getWorld().getPlayers().stream().filter(p -> p.getLocation().distance(location) <= 64).toList().isEmpty()){
                    if(!list.isEmpty()) clearMobs();
                    return;
                }
                if(location.getWorld().getPlayers().stream().filter(p -> p.getLocation().distance(location) <= 20).toList().isEmpty()){
                    return;
                }
                for(Entity ent : list) {
                    if(ent.isDead()) {
                        removeEntity(ent);
                    }
                }
                int amount = (int) (random.nextDouble(0.5, 1.1)*power);
                if(amount > limit-list.size()) amount = limit - list.size();
                for(int o = 0; o < amount && list.size() < limit; o++) {
                    double x = random.nextDouble(10) - 5;
                    double z = random.nextDouble(10) - 5;
                    final Location toSpawn = location.clone().add(x, 0, z);
                    for (int i = -5; i <= 5; i++) {
                        if (isASpawnLocation(toSpawn.clone().add(0, i, 0))) {
                            toSpawn.add(0, i, 0);
                            break;
                        }
                    }
                    addEntity(spawner.spawn(toSpawn, type));
                }
            }
        };
        task.runTaskTimer(LQuests.a, 0, 100);
    }
    public void stop(){
        if(task != null) task.cancel();
        clearMobs();
    }
    public void clearMobs(){
        list.forEach(Entity::remove);
        list.clear();
        kills = 0;
    }
}

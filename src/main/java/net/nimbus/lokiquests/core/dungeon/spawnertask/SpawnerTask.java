package net.nimbus.lokiquests.core.dungeon.spawnertask;

import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.HologramLines;
import me.filoghost.holographicdisplays.api.hologram.line.HologramLine;
import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.Utils;
import net.nimbus.lokiquests.Vars;
import net.nimbus.lokiquests.core.dungeon.Dungeon;
import net.nimbus.lokiquests.core.dungeon.Dungeons;
import net.nimbus.lokiquests.core.dungeon.mobspawner.MobSpawner;
import net.nimbus.lokiquests.core.dungeon.mobspawner.MobSpawners;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpawnerTask {
    private final short limit;
    private final int power;
    private final Location location;
    private final MobSpawner spawner;
    private final String type;
    private final int complete;
    private int kills;
    private boolean completed;

    private final Hologram hologram;
    private final long id;

    public SpawnerTask(int power, Location location, MobSpawner spawner, String type, int complete, short limit){
        this.power = power;
        this.location = new Location(Vars.DUNGEON_WORLD, location.getBlockX()+0.5, location.getBlockY(), location.getBlockZ()+0.5);
        this.spawner = spawner;
        this.type = type;
        this.complete = complete;
        this.limit = limit;
        id = System.currentTimeMillis();
        hologram = LQuests.a.hdapi.createHologram(location.clone().add(0, 3, 0));
        createHologram();
        list = new ArrayList<>();
        kills = 0;
        completed = false;
    }

    public long getId() {
        return id;
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

    public Location getLocation() {
        return location;
    }

    public void addKill(){
        kills++;
        if(kills >= complete){
            complete();
        }
    }
    BukkitRunnable task;
    public void start() {
        if(task != null) {
            task.cancel();
            task = null;
        }
        task = new BukkitRunnable() {
            private final Random random = new Random();
            private boolean isASpawnLocation(Location location){
                return !location.clone().add(0, -1, 0).getBlock().isEmpty() && location.getBlock().isEmpty() && location.clone().add(0, 1, 0).getBlock().isEmpty();
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
                for(Entity ent : new ArrayList<>(list)) {
                    if(ent.isDead()) {
                        removeEntity(ent);
                    }
                }
                int amount = (int) (random.nextDouble(0.5, 1.1)*power);
                if(amount > limit-list.size()) amount = limit - list.size();
                if(amount > complete-kills-list.size()) amount = complete - kills - list.size();
                for(int o = 0; o < amount; o++) {
                    double x = random.nextDouble(10) - 5;
                    double z = random.nextDouble(10) - 5;
                    final Location toSpawn = location.clone().add(x, 0, z);
                    for (int i = -5; i <= 5; i++) {
                        if (isASpawnLocation(toSpawn.clone().add(0, i, 0))) {
                            toSpawn.add(0, i, 0);
                            break;
                        }
                    }
                    addEntity(Utils.setMetadata(Utils.setMetadata(spawner.spawn(toSpawn, type), "spawner", getId()+""), "dungeon", getDungeon().getId()+""));
                }
            }
        };
        task.runTaskTimer(LQuests.a, 0, 100);
    }
    public void stop(){
        if(task != null) {
            task.cancel();
            task = null;
        }
        clearMobs();
    }
    public void clearMobs(){
        list.forEach(Entity::remove);
        list.clear();
        kills = 0;
    }

    public Dungeon getDungeon(){
        for(Dungeon dungeon : Dungeons.getAll()){
            if(dungeon.getSpawners().contains(this)) return dungeon;
        }
        return null;
    }

    private void createHologram(){
        hologram.getLines().appendItem(new ItemStack(Material.SPAWNER));
        hologram.getLines().appendText(Utils.toColor("&fSpawns: &e" + type));
        if (kills < complete) {
            hologram.getLines().appendText(Utils.toColor("&fCompleted: &a" + kills + "&f/&2" + complete + " &7&o(" + (kills * 100 / complete) + "%)"));
        } else hologram.getLines().appendText(Utils.toColor("&6COMPLETED"));
    }
    public void updateHologram() {
        if (!completed) {
            hologram.getLines().insertText(2, Utils.toColor("&fCompleted: &a" + kills + "&f/&2" + complete + " &7&o(" + (kills * 100 / complete) + "%)"));
        } else hologram.getLines().insertText(2, Utils.toColor("&6COMPLETED"));
        hologram.getLines().remove(3);
    }
    public void removeHologram(){
        hologram.delete();
    }
    @Override
    public String toString() {
        return power+","+
                location.getX()+","+
                location.getY()+","+
                location.getZ()+","+
                spawner.id()+","+
                type+","+
                complete+","+
                limit;
    }

    public static SpawnerTask fromString(String str) {
        try {
            String[] split = str.split(",");
            int power = Integer.parseInt(split[0]);
            Location loc = new Location(Vars.DUNGEON_WORLD, Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
            MobSpawner spawner = MobSpawners.get(split[4]);
            int complete = Integer.parseInt(split[6]);
            short limit = Short.parseShort(split[7]);
            return new SpawnerTask(power, loc, spawner, split[5], complete, limit);
        } catch (Exception e) {
            return null;
        }
    }
}

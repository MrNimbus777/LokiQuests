package net.nimbus.lokiquests.core.dungeon;

import net.nimbus.lokiquests.Vars;
import net.nimbus.lokiquests.core.dungeon.spawnertask.SpawnerTask;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Dungeon {
    private final List<SpawnerTask> spawners;
    private final List<Player> players;
    private final Location join;
    private final short limit;
    private final long id;
    public Dungeon(long id, Location join, short limit){
        this.join = new Location(Vars.DUNGEON_WORLD, join.getBlockX()+0.5, join.getBlockY(), join.getBlockZ()+0.5);
        this.join.setWorld(Vars.DUNGEON_WORLD);
        this.id = id;
        this.limit = limit;

        this.spawners = new ArrayList<>();
        this.players = new ArrayList<>();
    }
    public Dungeon(Location join, short limit){
        this.join = new Location(Vars.DUNGEON_WORLD, join.getBlockX()+0.5, join.getBlockY(), join.getBlockZ()+0.5);
        this.id = System.currentTimeMillis();
        this.limit = limit;

        this.spawners = new ArrayList<>();
        this.players = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void addSpawner(SpawnerTask spawner){
        this.spawners.add(spawner);
    }

    public Location getLocation() {
        return join;
    }

    public List<SpawnerTask> getSpawners() {
        return spawners;
    }

    public void join(Player player) {
        addPlayer(player);
        teleport(player);
    }
    public void teleport(Player player) {
        Location toTeleport = join.clone();
        toTeleport.setYaw(player.getLocation().getYaw());
        toTeleport.setPitch(player.getLocation().getPitch());
        player.teleport(toTeleport);
    }
    public List<Player> getPlayers() {
        return players;
    }
    public void addPlayer(Player player){
        this.players.add(player);
    }
    public void removePlayer(Player player){
        this.players.remove(player);
    }

    public void start(){
        this.spawners.forEach(SpawnerTask::start);
    }

    public void stop(){
        spawners.forEach(SpawnerTask::stop);
    }

}
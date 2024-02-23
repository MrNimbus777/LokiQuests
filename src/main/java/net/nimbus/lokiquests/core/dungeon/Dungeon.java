package net.nimbus.lokiquests.core.dungeon;

import net.nimbus.lokiquests.core.dungeon.spawnertask.SpawnerTask;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Dungeon {
    private final List<SpawnerTask> spawners;
    private final List<Player> players;
    private final Location join;
    public Dungeon(Location join){
        this.join = join.clone();

        this.spawners = new ArrayList<>();
        this.players = new ArrayList<>();
    }

    public void addSpawner(SpawnerTask spawner){
        this.spawners.add(spawner);
    }

    public void join(Player player) {
        addPlayer(player);
        player.teleport(join);
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
package net.nimbus.lokiquests.core.dungeon;

import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.Utils;
import net.nimbus.lokiquests.Vars;
import net.nimbus.lokiquests.core.dungeon.spawnertask.SpawnerTask;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public void removeSpawner(SpawnerTask spawner){
        this.spawners.remove(spawner);
        spawner.stop();
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
        updateSigns();
        if(players.size() == 1) start();
    }
    public void leave(Player player) {
        removePlayer(player);
        updateSigns();
    }
    public void teleport(Player player) {
        Location toTeleport = join.clone();
        toTeleport.setYaw(player.getLocation().getYaw());
        toTeleport.setPitch(player.getLocation().getPitch());
        player.teleport(toTeleport);
    }

    public short getLimit() {
        return limit;
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
        this.spawners.forEach(SpawnerTask::stop);
    }

    public void save(){
        File file = new File(LQuests.a.getDataFolder(), "dungeons.yml");
        if(!file.exists()) {
            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        List<String> spawners = getSpawners().stream().map(SpawnerTask::toString).collect(Collectors.toList());
        configuration.set(getId()+".location", getLocation());
        configuration.set(getId()+".limit", getLimit());
        configuration.set(getId()+".spawners", spawners);
        try {
            configuration.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void remove(){
        File file = new File(LQuests.a.getDataFolder(), "dungeons.yml");
        if(!file.exists()) {
            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        configuration.set(getId()+"", null);
        try {
            configuration.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void updateSigns(){
        for(Location loc : Vars.SIGNS_MAP.keySet()) {
            if(Vars.SIGNS_MAP.get(loc) == getId()) {
                Sign sign = (Sign) loc.getBlock().getState();
                sign.setLine(0, Utils.toColor("&9[Teleport]"));
                sign.setLine(1, Utils.toColor(getPlayers().size()+"/"+getLimit()));
                sign.setLine(2, Utils.toColor("&aTeleport to"));
                sign.setLine(3, Utils.toColor("&adungeon."));
                sign.update();
            }
        }
    }
    public boolean isCompleted(){
        for(SpawnerTask task : getSpawners()) {
            if(!task.isCompleted()) return false;
        }
        return true;
    }
}
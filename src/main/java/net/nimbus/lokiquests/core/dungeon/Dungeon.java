package net.nimbus.lokiquests.core.dungeon;

import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.Utils;
import net.nimbus.lokiquests.Vars;
import net.nimbus.lokiquests.core.dungeon.mobspawner.MobSpawner;
import net.nimbus.lokiquests.core.dungeon.mobspawner.MobSpawners;
import net.nimbus.lokiquests.core.quest.Quest;
import net.nimbus.lokiquests.core.quest.quests.DungeonQuest;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import net.nimbus.lokiquests.core.questplayers.QuestPlayers;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Dungeon {
    protected static final HashMap<Player, Location> previousLocs = new HashMap<>();
    private final List<Spawner> spawners;
    private final List<Player> players;
    private final List<Wall> walls;
    private final Location join;
    private final short limit;
    private final long id;
    private String name;
    public Dungeon(long id, Location join, short limit){
        this.join = new Location(Vars.DUNGEON_WORLD, join.getBlockX()+0.5, join.getBlockY(), join.getBlockZ()+0.5);
        this.join.setWorld(Vars.DUNGEON_WORLD);
        this.id = id;
        this.limit = limit;
        this.name = "Normal";

        this.spawners = new ArrayList<>();
        this.players = new ArrayList<>();
        this.walls = new ArrayList<>();
    }
    public Dungeon(Location join, short limit){
        this.join = new Location(Vars.DUNGEON_WORLD, join.getBlockX()+0.5, join.getBlockY(), join.getBlockZ()+0.5);
        this.id = System.currentTimeMillis();
        this.limit = limit;
        this.name = "Normal";

        this.spawners = new ArrayList<>();
        this.players = new ArrayList<>();
        this.walls = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void addSpawner(Spawner spawner){
        this.spawners.add(spawner);
    }
    public void removeSpawner(Spawner spawner){
        this.spawners.remove(spawner);
        spawner.cancel();
        spawner.clearMobs();
    }

    public Location getLocation() {
        return join;
    }

    public List<Spawner> getSpawners() {
        return spawners;
    }

    public void join(Player player) {
        addPlayer(player);
        teleport(player);
        updateSigns();
        if(players.size() == 1) start();
        player.sendTitle("Welcome to "+getName(), "", 20, 50, 20);
    }
    public void leave(Player player) {
        removePlayer(player);
        updateSigns();
        player.teleport(previousLocs.get(player));
        previousLocs.remove(player);
    }
    public void teleport(Player player) {
        Location toTeleport = join.clone();
        previousLocs.put(player, player.getLocation().clone());
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
        this.spawners.forEach(Spawner::run);
    }

    public void stop(){
        this.spawners.forEach(st -> {
            st.cancel();
            st.clearMobs();
        });
        for(Player p : new ArrayList<>(getPlayers())){
            leave(p);
        }
    }

    public void wallsUp(){
        new BukkitRunnable(){
            int countdown = 10;
            @Override
            public void run() {
                getPlayers().forEach(p -> {
                    p.sendTitle(Utils.toColor("&eWalls up!"), Utils.toColor("In &c" + countdown + "&f seconds."), 0, 20, 10);
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                });
                countdown--;
                if(countdown > 0) return;
                for(Wall wall : walls) {
                    wall.up();
                }
                cancel();
            }
        }.runTaskTimer(LQuests.a, 0, 20);
    }

    public void wallsDown() {
        getPlayers().forEach(p -> {
            p.sendTitle(Utils.toColor("&5Boss fell!"), "", 5, 20, 10);
        });
        new BukkitRunnable() {
            int countdown = 3;

            @Override
            public void run() {
                getPlayers().forEach(p -> {
                    p.sendTitle(Utils.toColor("&eWalls down!"), Utils.toColor("In &c" + countdown + "&f seconds."), 0, 20, 10);
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                });
                countdown--;
                if (countdown > 0) return;
                for(Wall wall : walls) {
                    wall.down();
                }
                cancel();
            }
        }.runTaskTimer(LQuests.a, 20, 20);
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
        List<String> spawners = getSpawners().stream().filter(s -> !(s instanceof BossSpawner)).map(Spawner::toString).collect(Collectors.toList());
        List<String> boss = getSpawners().stream().filter(s -> s instanceof BossSpawner).map(Spawner::toString).collect(Collectors.toList());
        configuration.set(getId()+".location", getLocation());
        configuration.set(getId()+".limit", getLimit());
        configuration.set(getId()+".spawners", spawners);
        configuration.set(getId()+".boss", boss);
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
                sign.setLine(2, Utils.toColor("&0Go to dungeon"));
                sign.setLine(3, Utils.toColor("&a" + getName() + "&f."));
                sign.update();
            }
        }
    }

    public void complete(){
        this.spawners.forEach(sp -> {
            sp.cancel();
            sp.clearMobs();
        });
        for(Player p : new ArrayList<>(getPlayers())) {
            QuestPlayer qp = QuestPlayers.get(p);
            for(Quest quest : qp.getActiveQuests()) {
                if(quest instanceof DungeonQuest dq) {
                    if(dq.getDungeon() == getId()) {
                        quest.finish(qp);
                    }
                }
            }
            p.sendTitle("Dungeon completed!", Utils.toColor("&aCongratulations!"), 20, 50, 20);
            p.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Actions.dungeon.complete")));
        }
        new BukkitRunnable(){
            @Override
            public void run() {
                for(Player p : new ArrayList<>(getPlayers())){
                    leave(p);
                }
            }
        }.runTaskLater(LQuests.a, 600);
    }
    public boolean isCompleted(){
        for(Spawner task : getSpawners()) {
            if(!task.isCompleted()) return false;
        }
        return true;
    }

    public static class Wall {

        public static List<Wall> UP = new ArrayList<>();


        private Dungeon dungeon;

        private final int x1, y1, z1, x2, y2, z2;

        public Wall(int x1, int y1, int z1, int x2, int y2, int z2){
            this.x1 = Math.min(x1, x2);
            this.y1 = Math.min(y1, y2);
            this.z1 = Math.min(z1, z2);
            this.x2 = Math.max(x2, x1);
            this.y2 = Math.max(y2, y1);
            this.z2 = Math.max(z2, z1);
        }

        public Dungeon getDungeon() {
            return dungeon;
        }

        public void setDungeon(Dungeon dungeon) {
            this.dungeon = dungeon;
        }

        BukkitRunnable visualisation;
        public void up(){
            if(UP.contains(this)) return;
            Location sound = new Location(Vars.DUNGEON_WORLD, (x1+x2)/2.0, (y1+y2)/2.0, (z1+z2)/2.0);
            sound.getWorld().playSound(sound, Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1, 1);
            UP.add(this);
            visualisation = new BukkitRunnable() {
                @Override
                public void run() {
                    for(int x = x1; x <= x2; x++) {
                        for(int y = y1; y <= y2; y++) {
                            for(int z = z1; z <= z2; z++) {
                                Location to = new Location(Vars.DUNGEON_WORLD, x+.5, y+.5, z+.5);
                                to.getWorld().spawnParticle(Particle.REDSTONE, to, 7, 0.5, 0.5, 0.5, new Particle.DustOptions(Color.PURPLE, 1));
                                to.getWorld().spawnParticle(Particle.REDSTONE, to, 7, 0.5, 0.5, 0.5, new Particle.DustOptions(Color.BLACK, 1));
                            }
                        }
                    }
                }
            };
            visualisation.runTaskTimer(LQuests.a, 0, 3);
        }

        public void down(){
            if(!UP.contains(this)) return;
            Location sound = new Location(Vars.DUNGEON_WORLD, (x1+x2)/2.0, (y1+y2)/2.0, (z1+z2)/2.0);
            sound.getWorld().playSound(sound, Sound.ENTITY_WITHER_BREAK_BLOCK, 1, 1);
            UP.remove(this);
            visualisation.cancel();
        }
        public boolean isColliding(Location loc){
            return x1 <= loc.getBlockX() && loc.getBlockX() <= x2 &&
                    y1 <= loc.getBlockY() && loc.getBlockY() <= y2 &&
                    z1 <= loc.getBlockZ() && loc.getBlockZ() <= z2;
        }

        @Override
        public String toString() {
            return x1+","+
                    y1+","+
                    z1+","+
                    x2+","+
                    y2+","+
                    z2;
        }

        public static Wall fromString(String string){
            String[] split = string.split(",");
            return new Wall(
                    Integer.parseInt(split[0]),
                    Integer.parseInt(split[1]),
                    Integer.parseInt(split[2]),
                    Integer.parseInt(split[3]),
                    Integer.parseInt(split[4]),
                    Integer.parseInt(split[5])
            );
        }
    }

    public static class Spawner {
        private final int radius = 32;
        private final int amount;
        protected final Location location;
        protected final MobSpawner spawner;
        protected final String type;
        protected final long id;

        public Spawner(Location location, MobSpawner spawner, String type, int amount){
            this.location = new Location(Vars.DUNGEON_WORLD, location.getBlockX()+0.5, location.getBlockY(), location.getBlockZ()+0.5);
            this.spawner = spawner;
            this.type = type;
            this.amount = amount;
            id = System.currentTimeMillis();
            list = new ArrayList<>();
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
            if(task != null) if(!task.isCancelled()) return false;
            return list.isEmpty();
        }

        public Location getLocation() {
            return location;
        }

        private boolean isASpawnLocation(Location location){
            return !location.clone().add(0, -1, 0).getBlock().isEmpty() && location.getBlock().isEmpty() && location.clone().add(0, 1, 0).getBlock().isEmpty();
        }
        public void spawn() {
            final Random random = new Random();
            for (int o = 0; o < amount; o++) {
                double x = random.nextDouble(10) - 5;
                double z = random.nextDouble(10) - 5;
                final Location toSpawn = location.clone().add(x, 0, z);
                for (int i = -5; i <= 5; i++) {
                    if (isASpawnLocation(toSpawn.clone().add(0, i, 0))) {
                        toSpawn.add(0, i, 0);
                        break;
                    }
                }
                addEntity(Utils.setMetadata(Utils.setMetadata(spawner.spawn(toSpawn, type), "spawner", getId() + ""), "dungeon", getDungeon().getId() + ""));
            }
        }

        public void complete(){
            if(getDungeon().isCompleted()) {
                getDungeon().complete();
            }
        }

        public void clearMobs(){
            list.forEach(Entity::remove);
            list.clear();
        }
        private BukkitRunnable task;
        public void run() {
            task = new BukkitRunnable() {
                @Override
                public void run() {
                    System.out.println(type);
                    if(!Vars.DUNGEON_WORLD.getPlayers().stream().filter(p -> p.getLocation().distance(Spawner.this.getLocation()) <= radius).toList().isEmpty()){
                        Spawner.this.cancel();
                        Spawner.this.spawn();
                    }
                }
            };
            task.runTaskTimer(LQuests.a, 0, 50);
        }
        public void cancel(){
            if(task != null) {
                task.cancel();
                task = null;
            }
        }

        public Dungeon getDungeon(){
            for(Dungeon dungeon : Dungeons.getAll()){
                if(dungeon.getSpawners().contains(this)) return dungeon;
            }
            return null;
        }

        @Override
        public String toString() {
            return location.getX()+","+
                    location.getY()+","+
                    location.getZ()+","+
                    spawner.id()+","+
                    type+","+
                    amount;
        }

        public static Spawner fromString(String str) {
            try {
                String[] split = str.split(",");
                Location loc = new Location(Vars.DUNGEON_WORLD, Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
                MobSpawner spawner = MobSpawners.get(split[3]);
                int amount = Integer.parseInt(split[5]);
                return new Spawner(loc, spawner, split[4], amount);
            } catch (Exception e) {
                return null;
            }
        }
    }

    public static class BossSpawner extends Spawner {

        public BossSpawner(Location location, MobSpawner spawner, String type) {
            super(location, spawner, type, 1);
        }

        @Override
        public void spawn() {
            getDungeon().wallsUp();
            new BukkitRunnable(){
                @Override
                public void run() {
                    spawner.spawn(location, type);
                }
            }.runTaskLater(LQuests.a, 200);
        }

        @Override
        public void complete() {
            super.complete();
            getDungeon().wallsDown();
        }

        @Override
        public String toString() {
            return location.getX()+","+
                    location.getY()+","+
                    location.getZ()+","+
                    spawner.id()+","+
                    type;
        }

        public static BossSpawner fromString(String str) {
            try {
                String[] split = str.split(",");
                Location loc = new Location(Vars.DUNGEON_WORLD, Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
                MobSpawner spawner = MobSpawners.get(split[3]);
                return new BossSpawner(loc, spawner, split[4]);
            } catch (Exception e) {
                return null;
            }
        }
    }
}
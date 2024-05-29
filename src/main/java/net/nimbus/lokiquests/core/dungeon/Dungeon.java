package net.nimbus.lokiquests.core.dungeon;

import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.Utils;
import net.nimbus.lokiquests.Vars;
import net.nimbus.lokiquests.core.dialogues.action.Action;
import net.nimbus.lokiquests.core.dialogues.action.Actions;
import net.nimbus.lokiquests.core.dialogues.action.actions.ActionReward;
import net.nimbus.lokiquests.core.dungeon.mobspawner.MobSpawner;
import net.nimbus.lokiquests.core.dungeon.mobspawner.MobSpawners;
import net.nimbus.lokiquests.core.quest.Quest;
import net.nimbus.lokiquests.core.quest.quests.DungeonQuest;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import net.nimbus.lokiquests.core.questplayers.QuestPlayers;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
    private final List<String> actions;
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
        this.actions = new ArrayList<>();
    }
    public Dungeon(Location join, short limit){
        this.join = new Location(Vars.DUNGEON_WORLD, join.getBlockX()+0.5, join.getBlockY(), join.getBlockZ()+0.5);
        this.id = System.currentTimeMillis();
        this.limit = limit;
        this.name = "Normal";

        this.spawners = new ArrayList<>();
        this.players = new ArrayList<>();
        this.walls = new ArrayList<>();
        this.actions = new ArrayList<>();
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

    public void addAction(String action){
        this.actions.add(action);
    }
    public void removeAction(int i){
        this.actions.remove(i);
    }

    public List<String> getActions() {
        return actions;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public void addWall(Wall wall){
        walls.add(wall);
    }

    public void removeWall(Wall wall){
        walls.remove(wall);
        wall.down();
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
        if(players.isEmpty()) {
            stop();
            if(kick != null){
                kick.cancel();
                kick = null;
            }
        }
        teleportBack(player);
    }
    public void teleport(Player player) {
        Location toTeleport = join.clone();
        previousLocs.put(player, player.getLocation().clone());
        toTeleport.setYaw(player.getLocation().getYaw());
        toTeleport.setPitch(player.getLocation().getPitch());
        player.teleport(toTeleport);
    }
    public void teleportBack(Player player) {
        if(previousLocs.containsKey(player)) {
            player.teleport(previousLocs.get(player));
            previousLocs.remove(player);
        }
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
        walls.forEach(Wall::down);
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
                if(countdown >= 0) return;
                for(Wall wall : walls) {
                    wall.up();
                }
                cancel();
            }
        }.runTaskTimer(LQuests.a, 0, 20);
    }

    public void wallsDown() {
        getPlayers().forEach(p -> {
            p.sendTitle(Utils.toColor("&5Boss fell!"), "", 5, 20, 5);
        });
        new BukkitRunnable() {
            int countdown = 3;

            @Override
            public void run() {
                getPlayers().forEach(p -> {
                    p.sendTitle(Utils.toColor("&eWalls down!"), Utils.toColor("In &c" + countdown + "&f seconds."), 0, 20, 5);
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                });
                countdown--;
                if (countdown >= 0) return;
                for(Wall wall : walls) {
                    wall.down();
                }
                cancel();
            }
        }.runTaskTimer(LQuests.a, 30, 20);
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
        List<String> walls = getWalls().stream().map(Wall::toString).collect(Collectors.toList());
        configuration.set(getId()+".location", getLocation());
        configuration.set(getId()+".limit", getLimit());
        configuration.set(getId()+".spawners", spawners);
        configuration.set(getId()+".boss", boss);
        configuration.set(getId()+".walls", walls);
        configuration.set(getId()+".name", getName());
        configuration.set(getId()+".actions", getActions());
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
        for(Location loc : new ArrayList<>(Vars.SIGNS_MAP.keySet())) {
            if(Vars.SIGNS_MAP.get(loc) == getId()) {
                try {
                    Sign sign = (Sign) loc.getBlock().getState();
                    sign.setLine(0, Utils.toColor("&9[&oClick&9]"));
                    sign.setLine(1, Utils.toColor(getPlayers().size() + "/" + getLimit()));
                    sign.setLine(2, Utils.toColor("&0Go to dungeon"));
                    sign.setLine(3, Utils.toColor("&a" + getName() + "&f."));
                    sign.setEditable(true);
                    sign.update();
                } catch (Exception e) {
                    Vars.SIGNS_MAP.remove(loc);
                }
            }
        }
    }
    BukkitRunnable kick;
    public void complete(){
        this.spawners.forEach(sp -> {
            sp.cancel();
            sp.clearMobs();
        });
        actions.forEach(a -> {
            String action_id = a.split(":")[0];
            Action action = Actions.get(action_id);
            if(action == null) return;
            String vars = a.replaceFirst(action_id+":", "");
            try{
                for (Player player : getPlayers()) {
                    if(action instanceof ActionReward){
                        QuestPlayer qp = QuestPlayers.get(player);
                        if(qp.getCompletedDungeons().contains(getId())){
                            continue;
                        }
                    }
                    action.execute(player, vars);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        for(Player p : new ArrayList<>(getPlayers())) {
            QuestPlayer qp = QuestPlayers.get(p);
            if(!qp.getCompletedDungeons().contains(this.getId())) qp.addCompletedDungeon(this);
            for(Quest quest : qp.getActiveQuests()) {
                if(quest instanceof DungeonQuest dq) {
                    if(dq.getDungeon() == getId()) {
                        quest.finish(qp);
                    }
                }
            }
            p.sendTitle("Dungeon completed!", Utils.toColor("&aCongratulations!"), 20, 50, 20);
            p.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Actions.dungeon_complete")));
        }
        kick = new BukkitRunnable(){
            @Override
            public void run() {
                for(Player p : new ArrayList<>(getPlayers())){
                    leave(p);
                }
            }
        };
        kick.runTaskLater(LQuests.a, 600);
    }
    public boolean isCompleted(){
        for(Spawner task : getSpawners()) {
            if(!task.isCompleted(true)) return false;
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

        public void up(){
            if(UP.contains(this)) return;
            Location sound = new Location(Vars.DUNGEON_WORLD, (x1+x2)/2.0, (y1+y2)/2.0, (z1+z2)/2.0);
            sound.getWorld().playSound(sound, Sound.ENTITY_ELDER_GUARDIAN_CURSE, 10, 1);
            UP.add(this);
            for(int x = x1; x <= x2; x++) {
                for (int y = y1; y <= y2; y++) {
                    for (int z = z1; z <= z2; z++) {
                        Location to = new Location(Vars.DUNGEON_WORLD, x+.5, y+.5, z+.5);
                        if(to.getBlock().getType().name().contains("AIR")) to.getBlock().setType(Material.TINTED_GLASS);
                    }
                }
            }
        }

        public Location getCenter(){
            return new Location(Vars.DUNGEON_WORLD, (x1+x2)/2.0, (y1+y2)/2.0, (z1+z2)/2.0);
        }


        public void down(){
            if(!UP.contains(this)) return;
            Location sound = getCenter();
            sound.getWorld().playSound(sound, Sound.ENTITY_WITHER_BREAK_BLOCK, 10, 1);
            UP.remove(this);
            for(int x = x1; x <= x2; x++) {
                for (int y = y1; y <= y2; y++) {
                    for (int z = z1; z <= z2; z++) {
                        Location to = new Location(Vars.DUNGEON_WORLD, x+.5, y+.5, z+.5);
                        if(to.getBlock().getType() == Material.TINTED_GLASS) to.getBlock().setType(Material.AIR);
                    }
                }
            }
        }
        public boolean isColliding(Location loc){
            return x1 <= loc.getBlockX() && loc.getBlockX() <= x2 &&
                    y1 <= loc.getBlockY() && loc.getBlockY() <= y2 &&
                    z1 <= loc.getBlockZ() && loc.getBlockZ() <= z2;
        }

        @Override
        public String toString() {
            return x1+";"+
                    y1+";"+
                    z1+";"+
                    x2+";"+
                    y2+";"+
                    z2;
        }

        public static Wall fromString(String string){
            String[] split = string.split(";");
            try {
                return new Wall(
                        Integer.parseInt(split[0]),
                        Integer.parseInt(split[1]),
                        Integer.parseInt(split[2]),
                        Integer.parseInt(split[3]),
                        Integer.parseInt(split[4]),
                        Integer.parseInt(split[5])
                );
            } catch (Exception e) {
                return null;
            }
        }
    }

    public static class Spawner {
        protected int radius;
        private final int amount;
        protected final Location location;
        protected final MobSpawner spawner;
        protected final String type;

        public Spawner(Location location, MobSpawner spawner, String type, int amount){
            this.location = new Location(Vars.DUNGEON_WORLD, location.getBlockX()+0.5, location.getBlockY(), location.getBlockZ()+0.5);
            this.spawner = spawner;
            this.type = type;
            this.amount = amount;

            actions = new ArrayList<>();
            mobs = new ArrayList<>();
            radius = 32;
        }

        protected final List<String> actions;
        public void addAction(String action){
            actions.add(action);
        }
        public void removeAction(int i){
            actions.remove(i);
        }
        public List<String> getActions() {
            return actions;
        }

        private final List<Entity> mobs;
        public void addEntity(Entity entity){
            mobs.add(entity);
        }
        public void removeEntity(Entity entity){
            mobs.remove(entity);
        }

        public List<Entity> getMobs() {
            return mobs;
        }

        public String getType() {
            return type;
        }

        public int getAmount(){
            return amount;
        }

        public MobSpawner getSpawner() {
            return spawner;
        }

        public boolean isCompleted(boolean clear) {
            if(task != null) if(!task.isCancelled()) return false;
            if(clear) for(Entity e : new ArrayList<>(mobs)){
                if(e.isDead()) removeEntity(e);
            }
            return mobs.isEmpty();
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
                addEntity(spawner.spawn(toSpawn, type));
            }
        }

        public void complete(){
            if(getDungeon().isCompleted()) {
                getDungeon().complete();
            }
        }

        public void clearMobs(){
            mobs.forEach(Entity::remove);
            mobs.clear();
        }
        private BukkitRunnable task;
        public void run() {
            task = new BukkitRunnable() {
                @Override
                public void run() {
                    if(!Vars.DUNGEON_WORLD.getPlayers().stream().filter(p -> p.getLocation().distance(Spawner.this.getLocation()) <= radius).toList().isEmpty()){
                        Spawner.this.cancel();
                        Spawner.this.spawn();
                    }
                }
            };
            task.runTaskTimer(LQuests.a, 0, 10);
        }
        public void cancel(){
            if(task != null) {
                task.cancel();
                task = null;
            }
        }

        public int getId() {
            int id = 0;
            for(Spawner task : getDungeon().spawners){
                if(task == this) return id;
                id++;
            }
            return id;
        }

        public Dungeon getDungeon(){
            for(Dungeon dungeon : Dungeons.getAll()){
                if(dungeon.getSpawners().contains(this)) return dungeon;
            }
            return null;
        }

        @Override
        public String toString() {
            JSONObject obj = new JSONObject();
            JSONArray actions = new JSONArray();
            actions.addAll(this.actions);
            obj.put("actions", actions);
            return location.getX()+";"+
                    location.getY()+";"+
                    location.getZ()+";"+
                    spawner.id()+";"+
                    type+";"+
                    amount+";"+
                    obj.toJSONString();
        }

        public static Spawner fromString(String str) {
            try {
                String[] split = str.split(";");
                Location loc = new Location(Vars.DUNGEON_WORLD, Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
                MobSpawner spawner = MobSpawners.get(split[3]);
                int amount = Integer.parseInt(split[5]);
                Spawner s = new Spawner(loc, spawner, split[4], amount);
                try{
                    JSONObject obj = (JSONObject) new JSONParser().parse(split[6]);
                    JSONArray actions = (JSONArray) obj.get("actions");
                    for (Object action : actions) {
                        s.actions.add((String) action);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return s;
            } catch (Exception e) {
                return null;
            }
        }
    }

    public static class BossSpawner extends Spawner {


        public BossSpawner(Location location, MobSpawner spawner, String type, int radius) {
            super(location, spawner, type, 1);
            this.radius = radius;
        }
        private boolean hasSpawned = true;

        @Override
        public boolean isCompleted(boolean clear) {
            return super.isCompleted(clear) && hasSpawned;
        }

        @Override
        public void spawn() {
            getDungeon().wallsUp();
            hasSpawned = false;
            new BukkitRunnable(){
                @Override
                public void run() {
                    addEntity(spawner.spawn(location, type));
                    hasSpawned = true;
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
            JSONObject obj = new JSONObject();
            JSONArray actions = new JSONArray();
            actions.addAll(this.actions);
            obj.put("actions", actions);
            return location.getX()+";"+
                    location.getY()+";"+
                    location.getZ()+";"+
                    spawner.id()+";"+
                    type+";"+
                    radius+";"+
                    obj.toJSONString();
        }

        public static BossSpawner fromString(String str) {
            try {
                String[] split = str.split(";");
                Location loc = new Location(Vars.DUNGEON_WORLD, Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
                MobSpawner spawner = MobSpawners.get(split[3]);
                int radius = Integer.parseInt(split[5]);
                BossSpawner s = new BossSpawner(loc, spawner, split[4], radius);
                try{
                    JSONObject obj = (JSONObject) new JSONParser().parse(split[6]);
                    JSONArray actions = (JSONArray) obj.get("actions");
                    for (Object action : actions) {
                        s.actions.add((String) action);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return s;
            } catch (Exception e) {
                return null;
            }
        }
    }
}
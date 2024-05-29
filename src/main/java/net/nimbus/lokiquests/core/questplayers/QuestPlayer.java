package net.nimbus.lokiquests.core.questplayers;

import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.core.dailyquest.DailyQuest;
import net.nimbus.lokiquests.core.dailyquest.DailyQuests;
import net.nimbus.lokiquests.core.dialogues.Dialogue;
import net.nimbus.lokiquests.core.dialogues.Dialogues;
import net.nimbus.lokiquests.core.dungeon.Dungeon;
import net.nimbus.lokiquests.core.quest.Quest;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class QuestPlayer {
    private final UUID uuid;

    private List<Quest> activeQuests;
    private List<Quest> finishedQuests;
    private List<Quest> completedQuests;

    private DailyQuest[] dailyQuests;

    private List<Long> completed_dungeons;

    private Location indicator;

    public QuestPlayer(UUID uuid){
        this.uuid = uuid;
        activeQuests = new ArrayList<>();
        finishedQuests = new ArrayList<>();
        completedQuests = new ArrayList<>();
        dailyQuests = new DailyQuest[3];
        completed_dungeons = new ArrayList<>();
    }

    public UUID getUUID() {
        return uuid;
    }

    private int dailyQuestDay;

    public void setDailyQuestDay(int dailyQuestDay) {
        this.dailyQuestDay = dailyQuestDay;
    }

    public void save(){
        File file = new File(LQuests.a.getDataFolder(), "players/"+getUUID().toString()+".json");
        if(!file.exists()) {
            if(!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try {
                file.createNewFile();
            } catch (Exception e){
                e.printStackTrace();
                return;
            }
        }
        JSONObject obj = new JSONObject();

        JSONObject dialogs = new JSONObject();
        for(Dialogue dialog : Dialogues.getAll()) {
            if(dialog.getPlayerProgress(getUUID()) != null) dialogs.put(dialog.getId(), dialog.getPlayerProgress(getUUID()));
        }
        obj.put("dialogues", dialogs);

        JSONObject active = new JSONObject();
        for(Quest quest : getActiveQuests()) {
            active.put(quest.getId(), quest.getProgress(getUUID()));
        }
        obj.put("active", active);

        JSONArray finished = new JSONArray();
        finished.addAll(getFinishedQuests().stream().map(Quest::getId).toList());
        obj.put("finished", finished);

        JSONArray completed = new JSONArray();
        completed.addAll(getCompletedQuests().stream().map(Quest::getId).toList());
        obj.put("completed", completed);

        JSONObject daily = new JSONObject();
        for(int i = 0; i < 3; i++) {
            if(getDailyQuests()[i] != null) daily.put(i, getDailyQuests()[i].toString());
        }
        daily.put("day", dailyQuestDay);
        obj.put("daily", daily);

        if(indicator != null){
            String indicator = getIndicator().getWorld().getName()+","+getIndicator().getX()+","+getIndicator().getY()+","+getIndicator().getZ();
            obj.put("indicator", indicator);
        }

        JSONArray completed_dungeons = new JSONArray();
        getCompletedDungeons().forEach(id -> completed_dungeons.add(id+""));
        obj.put("completed_dungeons", completed_dungeons);

        try {
            FileWriter writer = new FileWriter(file);
            writer.write(obj.toJSONString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Long> getCompletedDungeons() {
        return completed_dungeons;
    }

    public void setCompletedDungeons(List<Long> completed_dungeons) {
        this.completed_dungeons = completed_dungeons;
    }

    public void addCompletedDungeon(Dungeon dungeon){
        addCompletedDungeon(dungeon.getId());
    }
    public void addCompletedDungeon(Long id){
        this.completed_dungeons.add(id);
    }

    public void setIndicator(Location location){
        if(location == null){
            this.indicator = null;
            return;
        }
        this.indicator = location.clone();
    }

    public Location getIndicator() {
        return indicator;
    }

    public void runIndicator(Location indicator) {
        setIndicator(indicator);
        runIndicator();
    }

    public void runIndicator(){
        if(this.indicator == null) return;
        new BukkitRunnable(){
            Player player = QuestPlayer.this.getPlayer();
            @Override
            public void run() {
                if(player == null) {
                    cancel();
                    return;
                }
                if(!player.isOnline() || QuestPlayer.this.indicator == null) {
                    cancel();
                    return;
                }
                if(!player.getLocation().getWorld().equals(QuestPlayer.this.indicator.getWorld())) return;
                Vector vec = QuestPlayer.this.indicator.toVector().add(new Vector(0.5, 0.1, 0.5)).subtract(player.getLocation().toVector());
                if(vec.length() > 4) {
                    vec.normalize().multiply(4);
                } else {
                    cancel();
                    setIndicator(null);
                    return;
                }
                Location particle = player.getEyeLocation().clone().add(vec);
                player.spawnParticle(Particle.REDSTONE, particle, 3, 0.1, 0.1, 0.1, 0, new Particle.DustOptions(Color.fromRGB(74, 255, 177), 1));
                player.spawnParticle(Particle.REDSTONE, particle, 3, 0.1, 0.1, 0.1, 0, new Particle.DustOptions(Color.fromRGB(74, 243, 255), 1));
                player.spawnParticle(Particle.REDSTONE, particle, 3, 0.1, 0.1, 0.1, 0, new Particle.DustOptions(Color.fromRGB(255, 152, 74), 1));
            }
        }.runTaskTimer(LQuests.a, 0, 30);
    }

    public void setActiveQuests(List<Quest> list) {
        this.activeQuests = list;
    }
    public boolean addActiveQuest(Quest quest){
        if(!this.activeQuests.contains(quest)){
            this.activeQuests.add(quest);
            return true;
        }
        return false;
    }
    public void removeActiveQuest(Quest quest){
        this.activeQuests.remove(quest);
    }
    public List<Quest> getActiveQuests(){
        return new ArrayList<>(this.activeQuests);
    }

    public void setFinishedQuests(List<Quest> list) {
        this.finishedQuests = list;
    }
    public boolean addFinishedQuest(Quest quest){
        if(!this.finishedQuests.contains(quest)){
            this.finishedQuests.add(quest);
            return true;
        }
        return false;
    }
    public void removeFinishedQuest(Quest quest){
        this.finishedQuests.remove(quest);
    }
    public List<Quest> getFinishedQuests(){
        return new ArrayList<>(this.finishedQuests);
    }

    public void setCompletedQuests(List<Quest> list) {
        this.completedQuests = list;
    }
    public boolean addCompletedQuest(Quest quest){
        if(!this.completedQuests.contains(quest)){
            this.completedQuests.add(quest);
            return true;
        }
        return false;
    }
    public List<Quest> getCompletedQuests(){
        return new ArrayList<>(this.completedQuests);
    }

    public Player getPlayer(){
        return Bukkit.getPlayer(getUUID());
    }

    public void setDailyQuests(DailyQuest[] dailyQuests) {
        this.dailyQuests = dailyQuests;
        for (DailyQuest dailyQuest : dailyQuests) {
            if(dailyQuest != null) dailyQuest.run();
        }
    }
    public void setDailyQuest(int index, DailyQuest dailyQuest){
        dailyQuests[index] = dailyQuest;
    }
    public void removeDailyQuest(DailyQuest quest) {
        for(int i = 0; i < 3; i++) {
            if(getDailyQuests()[i] == quest){
                getDailyQuests()[i] = null;
                return;
            }
        }
    }
    public void removeDailyQuest(int index) {
        getDailyQuests()[index] = null;
    }
    public DailyQuest[] getDailyQuests() {
        return dailyQuests;
    }


    public void generateDailyQuests(){
        dailyQuestDay = new Date().getDate();
        int low = LQuests.a.r.nextInt(2)+1;
        int medium = LQuests.a.r.nextInt(low, 4);
        int hard = 3 - medium;
        for(int i = 0; i < low; i++) {
            dailyQuests[i] = DailyQuests.generateRandom(this, "low");
            dailyQuests[i].run();
        }
        for(int i = low; i < medium; i++) {
            dailyQuests[i] = DailyQuests.generateRandom(this, "medium");
            dailyQuests[i].run();
        }
        for(int i = 0; i < hard; i++) {
            dailyQuests[2-i] = DailyQuests.generateRandom(this, "hard");
            dailyQuests[2-i].run();
        }
    }
}

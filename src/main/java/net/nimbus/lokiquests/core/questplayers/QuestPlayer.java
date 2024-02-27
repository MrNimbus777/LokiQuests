package net.nimbus.lokiquests.core.questplayers;

import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.core.dialogs.Dialog;
import net.nimbus.lokiquests.core.dialogs.Dialogs;
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
import java.util.List;
import java.util.UUID;

public class QuestPlayer {
    private final UUID uuid;

    private List<Quest> activeQuests;
    private List<Quest> finishedQuests;
    private List<Quest> completedQuests;
    private Location indicator;

    public QuestPlayer(UUID uuid){
        this.uuid = uuid;
        activeQuests = new ArrayList<>();
        finishedQuests = new ArrayList<>();
        completedQuests = new ArrayList<>();
    }

    public UUID getUUID() {
        return uuid;
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
        for(Dialog dialog : Dialogs.getAll()) {
            if(dialog.getPlayerProgress(getUUID()) != null) dialogs.put(dialog.getId(), dialog.getPlayerProgress(getUUID()));
        }
        obj.put("dialogs", dialogs);

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

        if(indicator != null){
            String indicator = getIndicator().getWorld().getName()+","+getIndicator().getX()+","+getIndicator().getY()+","+getIndicator().getZ();
            obj.put("indicator", indicator);
        }

        try {
            FileWriter writer = new FileWriter(file);
            writer.write(obj.toJSONString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setIndicator(Location location){
        this.indicator = location.clone();
    }

    public Location getIndicator() {
        return indicator;
    }

    public void runIndicator(Location indicator) {
        setIndicator(indicator.clone());
        runIndicator();
    }

    public void runIndicator(){
        if(this.indicator == null) return;
        new BukkitRunnable(){
            Player player = QuestPlayer.this.getPlayer();
            @Override
            public void run() {
                if(!player.isOnline() || QuestPlayer.this.indicator == null) {
                    cancel();
                    return;
                }
                Vector vec = QuestPlayer.this.indicator.toVector().add(new Vector(0.5, 0.1, 0.5)).subtract(player.getLocation().toVector());
                if(vec.length() > 4) {
                    vec.normalize().multiply(4);
                }
                Location particle = player.getEyeLocation().clone().add(vec);
                player.spawnParticle(Particle.REDSTONE, particle, 2, 0.1, 0.1, 0.1, 0, new Particle.DustOptions(Color.AQUA, 1));
                player.spawnParticle(Particle.REDSTONE, particle, 3, 0.1, 0.1, 0.1, 0, new Particle.DustOptions(Color.LIME, 1));
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
}

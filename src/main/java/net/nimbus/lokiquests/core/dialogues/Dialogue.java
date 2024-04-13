package net.nimbus.lokiquests.core.dialogues;

import net.nimbus.lokiquests.Utils;
import net.nimbus.lokiquests.core.dialogues.action.Action;
import net.nimbus.lokiquests.core.dialogues.action.Actions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Dialogue {
    private final String id;
    private YamlConfiguration configuration;

    private final Map<UUID, String> map;

    public Dialogue(String id){
        this.id = id;
        configuration = new YamlConfiguration();
        map = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setConfiguration(YamlConfiguration configuration){
        this.configuration = configuration;
    }

    public void putPlayerProgress(UUID uuid, String reply){
        map.put(uuid, reply);
    }
    public void putPlayerProgress(Player player, String reply) {
        putPlayerProgress(player.getUniqueId(), reply);
    }

    public String getPlayerProgress(UUID uuid){
        return map.get(uuid);
    }
    public String getPlayerProgress(Player player){
        return getPlayerProgress(player.getUniqueId());
    }

    public void clear(){
        map.clear();
    }

    public void processNext(Player player){
        String progress = getPlayerProgress(player);
        if(progress != null) if(progress.equals("100%")) return;
        ConfigurationSection section = configuration.getConfigurationSection("dialog."+progress);
        if(section == null) {
            section = configuration.getConfigurationSection("start");
        }
        if(section == null) return;
        String condition = section.getString("condition");
        if(condition == null) {
            player.sendMessage(Utils.toColor(section.getString("text").replace("%player%", player.getName())));
            String next = section.getString("next");
            putPlayerProgress(player, Objects.requireNonNullElse(next, "100%"));
            for(String s : section.getStringList("actions")) {
                String action_id = s.split(":")[0];
                Action action = Actions.get(action_id);
                if(action == null) continue;
                String vars = s.replaceFirst(action_id+":", "");
                action.execute(player, vars);
            }
        } else if(Dialogues.processCondition(player, condition)) {
            player.sendMessage(Utils.toColor(section.getString("text").replace("%player%", player.getName())));
            String next = section.getString("next");
            putPlayerProgress(player, Objects.requireNonNullElse(next, "100%"));
            for(String s : section.getStringList("actions")) {
                String action_id = s.split(":")[0];
                Action action = Actions.get(action_id);
                if(action == null) continue;
                String vars = s.replaceFirst(action_id+":", "");
                action.execute(player, vars);
            }
        } else {
            player.sendMessage(Utils.toColor(section.getString("conditionText").replace("%player%", player.getName())));
        }
    }
}

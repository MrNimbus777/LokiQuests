package net.nimbus.lokiquests;

import net.nimbus.lokiquests.core.reward.rewardprocessors.*;
import net.nimbus.lokiquests.events.entity.EntityDeathEvents;
import net.nimbus.lokiquests.events.entity.EntityPickupItemEvents;
import net.nimbus.lokiquests.events.player.PlayerJoinEvents;
import net.nimbus.lokiquests.events.player.PlayerQuitEvents;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStreamReader;

public class LQuests extends JavaPlugin {

    public static LQuests a;


    private YamlConfiguration messages;
    private YamlConfiguration items;


    public void loadConfig(boolean reload){
        File config = new File(getDataFolder(), "config.yml");
        if (!config.exists()) {
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
            try {
                getConfig().load(config);
                getLogger().info("Created new config.yml file at " + config.getPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (reload) {
            try {
                getConfig().load(config);
                getLogger().info("Config reloaded.");
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        //Vars.init();
    }

    void loadEvents(){
        loadEvent(new PlayerJoinEvents());
        loadEvent(new PlayerQuitEvents());

        loadEvent(new EntityDeathEvents());
        loadEvent(new EntityPickupItemEvents());
    }
    void loadCommands(){

    }

    public void onEnable() {
        a = this;

        loadConfig(false);
        loadMessages();

        loadEvents();
        loadCommands();

        RewardProcessors.register("cmd", new CommandProcessor());
        RewardProcessors.register("item", new ItemProcessor());

        loadItems();

    }

    public void onDisable() {

    }

    void loadEvent(Listener listener){
        try {
            getServer().getPluginManager().registerEvents(listener, this);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    void loadCommand(String cmd, CommandExecutor executor){
        try {
            getCommand(cmd).setExecutor(executor);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    void loadCommand(String cmd, CommandExecutor executor, TabCompleter completer){
        try {
            loadCommand(cmd, executor);
            getCommand(cmd).setTabCompleter(completer);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void loadMessages(){
        File messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            if(!messagesFile.getParentFile().exists()) messagesFile.getParentFile().mkdirs();
            try {
                messages = YamlConfiguration.loadConfiguration(new InputStreamReader(getResource("messages.yml")));
                messages.save(messagesFile);
                getLogger().info("Created new messages.yml file at " + messagesFile.getPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                messages = YamlConfiguration.loadConfiguration(messagesFile);
                getLogger().info("Messages file loaded.");
            } catch (Exception exception) {
            }
        }
    }

    public YamlConfiguration getMessages() {
        return messages;
    }

    public String getMessage(String key) {
        return getMessages().getString(key);
    }


    public void loadItems(){
        File file = new File(getDataFolder(), "items.yml");
        if (!file.exists()) {
            if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
            try {
                items = YamlConfiguration.loadConfiguration(new InputStreamReader(getResource("items.yml")));
                items.save(file);
                getLogger().info("Created new items.yml file at " + file.getPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                items = YamlConfiguration.loadConfiguration(file);
                getLogger().info("Messages file loaded.");
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
    public void saveItems(){
        try {
            File file = new File(getDataFolder(), "items.yml");
            items.save(file);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    public YamlConfiguration getItems() {
        return items;
    }
}

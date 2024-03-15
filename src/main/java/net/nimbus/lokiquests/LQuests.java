package net.nimbus.lokiquests;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import net.nimbus.lokiquests.commands.completers.*;
import net.nimbus.lokiquests.commands.executors.*;
import net.nimbus.lokiquests.core.dialogs.Dialogs;
import net.nimbus.lokiquests.core.dialogs.action.Actions;
import net.nimbus.lokiquests.core.dialogs.action.actions.*;
import net.nimbus.lokiquests.core.dungeon.Dungeons;
import net.nimbus.lokiquests.core.dungeon.mobspawner.MobSpawners;
import net.nimbus.lokiquests.core.dungeon.mobspawner.mobspawners.*;
import net.nimbus.lokiquests.core.party.Parties;
import net.nimbus.lokiquests.core.quest.Quests;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import net.nimbus.lokiquests.core.questplayers.QuestPlayers;
import net.nimbus.lokiquests.core.reward.rewardprocessors.*;
import net.nimbus.lokiquests.events.entity.*;
import net.nimbus.lokiquests.events.player.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStreamReader;
import java.util.Random;

public class LQuests extends JavaPlugin {

    public static LQuests a;
    public Random r;
    public String version;
    public HolographicDisplaysAPI hdapi;


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
        Vars.init();
    }

    void loadEvents(){
        loadEvent(new BlockBreakEvents());
        loadEvent(new BlockPlaceEvents());
        loadEvent(new PlayerDeathEvents());
        loadEvent(new PlayerInteractEvents());
        loadEvent(new PlayerItemConsumeEvents());
        loadEvent(new PlayerJoinEvents());
        loadEvent(new PlayerMoveEvents());
        loadEvent(new PlayerQuitEvents());
        loadEvent(new PlayerTeleportEvents());

        loadEvent(new CreatureSpawnEvents());
        loadEvent(new EntityDeathEvents());
        loadEvent(new EntityPickupItemEvents());
        loadEvent(new ProjectileLaunchEvents());
    }
    void loadCommands(){
        loadCommand("lquest", new LquestExe(), new LquestCompleter());
        loadCommand("dungeon", new DungeonExe(), new DungeonCompleter());
        loadCommand("party", new PartyExe(), new PartyCompleter());
        loadCommand("leave", new LeaveExe());
        loadCommand("quest", new QuestExe());
    }

    public void onEnable() {
        a = this;
        r = new Random();
        hdapi = HolographicDisplaysAPI.get(a);
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        version = packageName.substring(packageName.lastIndexOf(".") + 1);

        loadConfig(false);
        loadMessages();

        loadEvents();
        loadCommands();
        loadItems();

        Utils.loadSigns();

        RewardProcessors.register("cmd", new CommandProcessor());
        RewardProcessors.register("item", new ItemProcessor());

        Actions.register("completeQuest", new ActionCompleteQuest());
        Actions.register("cmd", new ActionExecuteCommand());
        Actions.register("give", new ActionGiveItem());
        Actions.register("indicator", new ActionPointIndicator());
        Actions.register("startQuest", new ActionStartQuest());

        MobSpawners.register(new MinecraftSpawner());
        MobSpawners.register(new MythicmobsSpawner());

        Dungeons.load();

        Quests.load();
        Dialogs.load();

        for(Player p : Bukkit.getOnlinePlayers()) {
            QuestPlayer qp = QuestPlayers.load(p);
            QuestPlayers.register(qp);
            qp.runIndicator();
        }
    }

    public void onDisable() {
        for(QuestPlayer player : QuestPlayers.getAll()) {
            player.save();
        }
        Parties.clearRAM();
        QuestPlayers.clearRAM();
        Dialogs.clearRAM();
        Quests.clearRAM();
        Actions.clearRAM();
        Dungeons.clearRAM();
        MobSpawners.clearRAM();
        RewardProcessors.clearRAM();
    }

    void loadEvent(Listener listener){
        try {
            a.getServer().getPluginManager().registerEvents(listener, this);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    void loadCommand(String cmd, CommandExecutor executor){
        try {
            a.getCommand(cmd).setExecutor(executor);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    void loadCommand(String cmd, CommandExecutor executor, TabCompleter completer){
        try {
            loadCommand(cmd, executor);
            a.getCommand(cmd).setTabCompleter(completer);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void loadMessages(){
        File messagesFile = new File(getDataFolder(), "messages.yml");
        if(!messagesFile.exists()) {
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
                getLogger().info("Item file loaded.");
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

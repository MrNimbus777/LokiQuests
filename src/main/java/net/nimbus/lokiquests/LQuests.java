package net.nimbus.lokiquests;

import net.nimbus.lokiquests.commands.completers.*;
import net.nimbus.lokiquests.commands.executors.*;
import net.nimbus.lokiquests.core.dailyquest.DailyQuests;
import net.nimbus.lokiquests.core.dialogues.Dialogues;
import net.nimbus.lokiquests.core.dialogues.action.Actions;
import net.nimbus.lokiquests.core.dialogues.action.actions.*;
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


    private YamlConfiguration messages;
    private YamlConfiguration items;
    private YamlConfiguration dailyquests;


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
        loadEvent(new CraftItemEvents());
        loadEvent(new PlayerDeathEvents());
        loadEvent(new PlayerInteractEvents());
        loadEvent(new PlayerItemConsumeEvents());
        loadEvent(new PlayerJoinEvents());
        loadEvent(new PlayerMoveEvents());
        loadEvent(new PlayerQuitEvents());
        loadEvent(new PlayerTeleportEvents());
        loadEvent(new InventoryClickEvents());

        loadEvent(new CreatureSpawnEvents());
        loadEvent(new EntityDamageByEntityEvents());
        loadEvent(new EntityDeathEvents());
        loadEvent(new EntityPickupItemEvents());
        loadEvent(new ProjectileLaunchEvents());
    }
    void loadCommands(){
        loadCommand("lquest", new LquestExe(), new LquestCompleter());
        loadCommand("dungeon", new DungeonExe(), new DungeonCompleter());
        loadCommand("party", new PartyExe(), new PartyCompleter());
        loadCommand("leave", new LeaveExe());
        loadCommand("quest", new QuestExe(), new QuestCompleter());
        loadCommand("setspawnplace", new SetspawnplaceExe());
    }

    public void enable(){
        loadConfig(true);
        loadMessages();

        loadItems();
        loadDailyQuests();

        Utils.loadSigns();

        RewardProcessors.register("cmd", new CommandProcessor());
        RewardProcessors.register("item", new ItemProcessor());

        Actions.register("completeQuest", new ActionCompleteQuest());
        Actions.register("cmd", new ActionExecuteCommand());
        Actions.register("give", new ActionGiveItem());
        Actions.register("indicator", new ActionPointIndicator());
        Actions.register("reward", new ActionReward());
        Actions.register("startQuest", new ActionStartQuest());

        MobSpawners.register(new MinecraftSpawner());
        MobSpawners.register(new MythicmobsSpawner());

        Dungeons.load();

        DailyQuests.load();

        Quests.load();
        Dialogues.load();
        Parties.clearRAM();

        for(Player p : Bukkit.getOnlinePlayers()) {
            QuestPlayer qp = QuestPlayers.load(p);
            QuestPlayers.register(qp);
            qp.runIndicator();
        }

        LQGuis.load();
    }
    public void onEnable() {
        a = this;
        r = new Random();
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        version = packageName.substring(packageName.lastIndexOf(".") + 1);
        loadEvents();
        loadCommands();

        enable();

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new Placeholders().register();
        }
    }

    public void onDisable() {
        for(QuestPlayer player : QuestPlayers.getAll()) {
            player.save();
        }
        try {
            Parties.clearRAM();
        } catch (Exception ignored) {}
        QuestPlayers.clearRAM();
        Dialogues.clearRAM();
        Quests.clearRAM();
        DailyQuests.clearRAM();
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


    public void loadDailyQuests(){
        File f = new File(getDataFolder(), "dailyquests.yml");
        if(!f.exists()) {
            if(!f.getParentFile().exists()) {
                f.getParentFile().mkdirs();
            }
            try {
                dailyquests = YamlConfiguration.loadConfiguration(new InputStreamReader(getResource("dailyquests.yml")));
                f.createNewFile();
                dailyquests.save(f);
                getLogger().info("Created new dailyquests.yml file at " + f.getPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            dailyquests = YamlConfiguration.loadConfiguration(f);
            getLogger().info("Daily Quests file loaded.");
        }
    }
    public YamlConfiguration getDailyQuests(){
        return dailyquests;
    }
}

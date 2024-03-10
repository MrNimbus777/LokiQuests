package net.nimbus.lokiquests.commands.executors;

import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.Utils;
import net.nimbus.lokiquests.core.dungeon.Dungeon;
import net.nimbus.lokiquests.core.dungeon.Dungeons;
import net.nimbus.lokiquests.core.dungeon.mobspawner.MobSpawner;
import net.nimbus.lokiquests.core.dungeon.mobspawner.MobSpawners;
import net.nimbus.lokiquests.core.dungeon.spawnertask.SpawnerTask;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DungeonExe implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player p)) {
            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.player-only")));
            return true;
        }
        if(!p.hasPermission("lq.admin")) {
            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.no-permission")));
            return true;
        }
        if(args.length == 0) {
            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.usage")));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "create" : {
                if(args.length == 1) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.create.usage")));
                    return true;
                }
                short limit;
                try {
                    limit = Short.parseShort(args[1]);
                } catch (Exception e) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.nan").replace("%NAN%", args[1])));
                    return true;
                }

                Dungeon dungeon = new Dungeon(p.getLocation(), limit);
                dungeon.getLocation().clone().add(0, -1, 0).getBlock().setType(Material.STONE);
                Dungeons.register(dungeon);

                ItemStack item = Utils.setTag(new ItemStack(Material.OAK_SIGN), "teleport_to_id", dungeon.getId()+"");
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(Utils.toColor("&aTeleporting sign"));
                meta.setLore(List.of(
                        "",
                        Utils.toColor("&8&oPlace wherever in world to join point to the dungeon."),
                        "",
                        Utils.toColor("&8&oDungeon id: "+dungeon.getId())
                ));
                item.setItemMeta(meta);
                p.getInventory().addItem(item);

                sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.create.success").
                        replace("%location%", Utils.locToString(dungeon.getLocation()))));
                return true;
            }
            case "spawner" : {
                if(args.length == 1) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.spawner.usage")));
                    return true;
                }
                switch (args[1].toLowerCase()) {
                    case "create" : {
                        if(args.length < 6) {
                            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.spawner.create.usage")));
                            return true;
                        }
                        Dungeon dungeon = Dungeons.getDungeon(p.getLocation());
                        if(dungeon == null) {
                            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.no_dungeon")));
                            return true;
                        }
                        int power;
                        try {
                            power = Integer.parseInt(args[2]);
                        } catch (Exception e) {
                            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.nan")
                                    .replace("%NAN%", args[2])));
                            return true;
                        }
                        MobSpawner spawner = MobSpawners.get(args[3].toLowerCase());
                        if(spawner == null) {
                            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.spawner.create.no_source")
                                    .replace("%source%", args[3].toLowerCase())));
                            return true;
                        }
                        String type = args[4];
                        short limit;
                        try {
                            limit = Short.parseShort(args[5]);
                        } catch (Exception e) {
                            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.nan")
                                    .replace("%NAN%", args[5])));
                            return true;
                        }
                        short complete;
                        try {
                            complete = Short.parseShort(args[6]);
                        } catch (Exception e) {
                            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.nan")
                                    .replace("%NAN%", args[6])));
                            return true;
                        }
                        SpawnerTask spawnerTask = new SpawnerTask(power, p.getLocation(), spawner, type, complete, limit);
                        dungeon.addSpawner(spawnerTask);
                        spawnerTask.updateHologram();
                        p.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.spawner.create.success").
                                replace("%location%", Utils.locToString(dungeon.getLocation())).
                                replace("%power%", power+"").
                                replace("%source%", spawner.id()).
                                replace("%type%", type).
                                replace("%limit%", limit+"").
                                replace("%complete%", complete+"")
                        ));
                        return true;
                    }
                    case "remove" :
                    case "delete" : {
                        SpawnerTask task = Dungeons.getSpawner(p.getLocation());
                        if(task == null) {
                            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.spawner.remove.no_spawner")));
                            return true;
                        }
                        task.stop();
                        Dungeon dungeon = Dungeons.getDungeon(p.getLocation());
                        if(dungeon == null) {
                            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.no_dungeon")));
                            return true;
                        }
                        task.removeHologram();
                        dungeon.removeSpawner(task);
                        sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.spawner.remove.success").
                                replace("%location%", Utils.locToString(task.getLocation()))));
                        return true;
                    }
                    default: {
                        sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.spawner.usage")));
                        return true;
                    }
                }
            }
            case "remove" :
            case "delete" : {
                Dungeon dungeon = Dungeons.getDungeon(p.getLocation());
                if(dungeon == null) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.no_dungeon")));
                    return true;
                }
                dungeon.stop();
                dungeon.getSpawners().forEach(SpawnerTask::removeHologram);
                Dungeons.unregister(dungeon);
                dungeon.remove();
                sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.remove").
                        replace("%id%", dungeon.getId()+"").
                        replace("%location%", Utils.locToString(p.getLocation()))
                ));
                return true;
            }
            default: {
                sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.usage")));
                return true;
            }
        }
    }
}

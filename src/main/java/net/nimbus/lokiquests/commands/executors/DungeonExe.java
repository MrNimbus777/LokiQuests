package net.nimbus.lokiquests.commands.executors;

import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.Utils;
import net.nimbus.lokiquests.core.dungeon.Dungeon;
import net.nimbus.lokiquests.core.dungeon.Dungeons;
import net.nimbus.lokiquests.core.dungeon.mobspawner.MobSpawner;
import net.nimbus.lokiquests.core.dungeon.mobspawner.MobSpawners;
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
                if(args.length > 2) dungeon.setName(Utils.toColor(args[2]));
                dungeon.getLocation().clone().add(0, -1, 0).getBlock().setType(Material.STONE);
                Dungeons.register(dungeon);

                ItemStack item = Utils.setTag(new ItemStack(Material.OAK_SIGN), "teleport_to_id", dungeon.getId()+"");
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(Utils.toColor("&aTeleporting sign"));
                meta.setLore(List.of(
                        "",
                        Utils.toColor("&8&oPlace wherever in world to create a join point to the dungeon."),
                        "",
                        Utils.toColor("&8&oDungeon id: "+dungeon.getId())
                ));
                item.setItemMeta(meta);
                p.getInventory().addItem(item);

                dungeon.save();

                sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.create.success").
                        replace("%location%", Utils.locToString(dungeon.getLocation()))));
                return true;
            }
            case "getsign" : {
                Dungeon dungeon = Dungeons.getDungeon(p.getLocation());
                if(dungeon == null) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.no_dungeon")));
                    return true;
                }
                ItemStack item = Utils.setTag(new ItemStack(Material.OAK_SIGN), "teleport_to_id", dungeon.getId()+"");
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(Utils.toColor("&aTeleporting sign"));
                meta.setLore(List.of(
                        "",
                        Utils.toColor("&8&oPlace wherever in world to create a join point to the dungeon."),
                        "",
                        Utils.toColor("&8&oDungeon id: "+dungeon.getId())
                ));
                item.setItemMeta(meta);
                p.getInventory().addItem(item);
                return true;
            }
            case "spawner" : {
                if(args.length == 1) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.spawner.usage")));
                    return true;
                }
                switch (args[1].toLowerCase()) {
                    case "create" : {
                        if(args.length < 4) {
                            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.spawner.create.usage")));
                            return true;
                        }
                        Dungeon dungeon = Dungeons.getDungeon(p.getLocation());
                        if(dungeon == null) {
                            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.no_dungeon")));
                            return true;
                        }
                        MobSpawner spawner = MobSpawners.get(args[2].toLowerCase());
                        if(spawner == null) {
                            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.spawner.create.no_source")
                                    .replace("%source%", args[2].toLowerCase())));
                            return true;
                        }
                        String type = args[3];
                        int amount;
                        try {
                            amount = Integer.parseInt(args[4]);
                        } catch (Exception e) {
                            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.nan")
                                    .replace("%NAN%", args[4])));
                            return true;
                        }
                        Dungeon.Spawner spawnerTask = new Dungeon.Spawner(p.getLocation(), spawner, type, amount);
                        dungeon.addSpawner(spawnerTask);
                        dungeon.save();
                        p.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.spawner.create.success").
                                replace("%location%", Utils.locToString(dungeon.getLocation())).
                                replace("%source%", spawner.id()).
                                replace("%type%", type).
                                replace("%amount%", amount+"")
                        ));
                        return true;
                    }
                    case "remove" :
                    case "delete" : {
                        Dungeon.Spawner task = Dungeons.getSpawner(p.getLocation());
                        if(task == null) {
                            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.spawner.remove.no_spawner")));
                            return true;
                        }
                        task.cancel();
                        task.clearMobs();
                        Dungeon dungeon = Dungeons.getDungeon(p.getLocation());
                        if(dungeon == null) {
                            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.no_dungeon")));
                            return true;
                        }
                        dungeon.removeSpawner(task);
                        dungeon.save();
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
                Dungeons.unregister(dungeon);
                dungeon.remove();
                sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.remove").
                        replace("%id%", dungeon.getId()+"").
                        replace("%location%", Utils.locToString(p.getLocation()))
                ));
                return true;
            }
            case "name" : {
                if(args.length == 1) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.name.usage")));
                    return true;
                }
                Dungeon dungeon = Dungeons.getDungeon(p.getLocation());
                if(dungeon == null) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.no_dungeon")));
                    return true;
                }
                dungeon.setName(Utils.toColor(args[1]));
                sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.name.success").
                        replace("%name%", dungeon.getName())));
            }
            default: {
                sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.usage")));
                return true;
            }
        }
    }
}

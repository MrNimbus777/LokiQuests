package net.nimbus.lokiquests.commands.executors;

import net.nimbus.api.modules.gui.core.GUI;
import net.nimbus.api.modules.gui.core.GUIs;
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
                Dungeon dungeon = Dungeons.getSelection(p);
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
                        Dungeon dungeon = Dungeons.getSelection(p);
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
                    case "boss" : {
                        if(args.length < 4) {
                            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.spawner.boss.usage")));
                            return true;
                        }
                        Dungeon dungeon = Dungeons.getSelection(p);
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
                        int radius;
                        try {
                            radius = Integer.parseInt(args[4]);
                        } catch (Exception e) {
                            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.nan")
                                    .replace("%NAN%", args[4])));
                            return true;
                        }
                        Dungeon.BossSpawner spawnerTask = new Dungeon.BossSpawner(p.getLocation(), spawner, type, radius);
                        dungeon.addSpawner(spawnerTask);
                        dungeon.save();
                        p.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.spawner.boss.success").
                                replace("%location%", Utils.locToString(dungeon.getLocation())).
                                replace("%source%", spawner.id()).
                                replace("%type%", type)
                        ));
                        return true;
                    }
                    case "addaction" : {
                        if(args.length < 3) {
                            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.spawner.addAction.usage")));
                            return true;
                        }
                        Dungeon.Spawner task = Dungeons.getSpawnerSelection(p);
                        if(task == null) {
                            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.spawner.remove.no_spawner")));
                            return true;
                        }
                        StringBuilder action = new StringBuilder(args[2]);
                        for(int i = 3; i < args.length; i++) {
                            action.append(" ").append(args[i]);
                        }
                        task.addAction(action.toString());
                        task.getDungeon().save();
                        sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.spawner.addAction.success").
                                replace("%action%", action.toString()).
                                replace("%location%", Utils.locToString(task.getLocation()))));
                        return true;
                    }
                    case "removeaction" : {
                        if(args.length < 3) {
                            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.spawner.removeAction.usage")));
                            return true;
                        }
                        Dungeon.Spawner task = Dungeons.getSpawnerSelection(p);
                        if(task == null) {
                            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.spawner.remove.no_spawner")));
                            return true;
                        }
                        int index = 0;
                        try {
                            index = Integer.parseInt(args[2]);
                        } catch (Exception e) {
                            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.nan")
                                    .replace("%NAN%", args[2])));
                            return true;
                        }
                        if(task.getActions().size() <= index) {
                            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.index_out_of_bounds").
                                    replace("%index%", args[2]).
                                    replace("%limit%", task.getActions().size()+"")));
                            return true;
                        }
                        task.removeAction(index);
                        task.getDungeon().save();
                        sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.spawner.removeAction.success").
                                replace("%action%", index+"").
                                replace("%location%", Utils.locToString(task.getLocation()))));
                        return true;
                    }
                    case "remove" :
                    case "delete" : {
                        Dungeon.Spawner task = Dungeons.getSpawnerSelection(p);
                        if(task == null) {
                            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.spawner.remove.no_spawner")));
                            return true;
                        }
                        task.cancel();
                        task.clearMobs();
                        Dungeon dungeon = Dungeons.getSelection(p);
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
                    case "select" : {
                        Dungeon dungeon = Dungeons.getSelection(p);
                        if(dungeon == null) {
                            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.no_dungeon")));
                            return true;
                        }
                        if(dungeon.getSpawners().isEmpty()) {
                            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.no_spawners")));
                            return true;
                        }
                        GUI gui = GUIs.get("spawner_"+dungeon.getId()+"_0");
                        gui.open(p);
                        return true;
                    }
                    default: {
                        sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.spawner.usage")));
                        return true;
                    }
                }
            }
            case "addaction" : {
                if(args.length < 2) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.spawner.addAction.usage")));
                    return true;
                }
                Dungeon dungeon = Dungeons.getSelection(p);
                if(dungeon == null) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.no_dungeon")));
                    return true;
                }
                StringBuilder action = new StringBuilder(args[1]);
                for(int i = 2; i < args.length; i++) {
                    action.append(" ").append(args[i]);
                }
                dungeon.addAction(action.toString());
                dungeon.save();
                sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.addAction.success").
                        replace("%action%", action.toString()).
                        replace("%location%", Utils.locToString(dungeon.getLocation()))));
                return true;
            }
            case "removeaction" : {
                if(args.length < 2) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.spawner.removeAction.usage")));
                    return true;
                }
                Dungeon dungeon = Dungeons.getSelection(p);
                if(dungeon == null) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.no_dungeon")));
                    return true;
                }
                int index;
                try {
                    index = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.nan")
                            .replace("%NAN%", args[1])));
                    return true;
                }
                if(dungeon.getActions().size() <= index) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.index_out_of_bounds").
                            replace("%index%", args[2]).
                            replace("%limit%", dungeon.getActions().size()+"")));
                    return true;
                }
                dungeon.removeAction(index);
                dungeon.save();
                sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.spawner.removeAction.success").
                        replace("%action%", index+"").
                        replace("%location%", Utils.locToString(dungeon.getLocation()))));
                return true;
            }
            case "remove" :
            case "delete" : {
                Dungeon dungeon = Dungeons.getSelection(p);
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
                Dungeon dungeon = Dungeons.getSelection(p);
                if(dungeon == null) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.no_dungeon")));
                    return true;
                }
                dungeon.setName(Utils.toColor(args[1]));
                dungeon.save();
                sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.name.success").
                        replace("%name%", dungeon.getName())));
                return true;
            }
            case "wall" : {
                if(args.length < 7) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.wall.usage")));
                    return true;
                }
                int x1;
                try { x1 = Integer.parseInt(args[1]);} catch (Exception e) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.nan")
                            .replace("%NAN%", args[1])));
                    return true;
                }
                int y1;
                try { y1 = Integer.parseInt(args[2]);} catch (Exception e) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.nan")
                            .replace("%NAN%", args[2])));
                    return true;
                }
                int z1;
                try { z1 = Integer.parseInt(args[3]);} catch (Exception e) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.nan")
                            .replace("%NAN%", args[3])));
                    return true;
                }
                int x2;
                try { x2 = Integer.parseInt(args[4]);} catch (Exception e) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.nan")
                            .replace("%NAN%", args[4])));
                    return true;
                }
                int y2;
                try { y2 = Integer.parseInt(args[5]);} catch (Exception e) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.nan")
                            .replace("%NAN%", args[5])));
                    return true;
                }
                int z2;
                try { z2 = Integer.parseInt(args[6]);} catch (Exception e) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.nan")
                            .replace("%NAN%", args[6])));
                    return true;
                }

                Dungeon dungeon = Dungeons.getSelection(p);
                if(dungeon == null) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.no_dungeon")));
                    return true;
                }
                dungeon.addWall(new Dungeon.Wall(x1, y1, z1, x2, y2, z2));
                dungeon.save();
                return true;
            }
            case "wallremove" : {
                Dungeon dungeon = Dungeons.getDungeon(p);
                if(dungeon == null) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.no_dungeon")));
                    return true;
                }
                Dungeon.Wall wall = Dungeons.getWall(p.getLocation());
                if(wall == null) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.wallRemove.no_wall")));
                    return true;
                }
                dungeon.removeWall(wall);
                dungeon.save();
                sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.wallRemove.success").
                        replace("%location%", Utils.locToString(wall.getCenter()))));
            }
            case "teleport" : {
                Dungeon dungeon = Dungeons.getSelection(p);
                if(dungeon == null) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.no_dungeon")));
                    return true;
                }
                dungeon.teleport(p);
                sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.teleport").replace("%name%", dungeon.getName())));
                return true;
            }
            case "select" : {
                if(Dungeons.getAll().isEmpty()) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.no_dungeons")));
                    return true;
                }
                GUI gui = GUIs.get("dungeon_0");
                gui.open(p);
                return true;
            }
            default: {
                sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.dungeon.usage")));
                return true;
            }
        }
    }
}

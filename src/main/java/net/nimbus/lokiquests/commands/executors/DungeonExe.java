package net.nimbus.lokiquests.commands.executors;

import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.Utils;
import net.nimbus.lokiquests.core.dungeon.Dungeon;
import net.nimbus.lokiquests.core.dungeon.Dungeons;
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
                short limit = 0;
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
            }
        }
        return true;
    }
}

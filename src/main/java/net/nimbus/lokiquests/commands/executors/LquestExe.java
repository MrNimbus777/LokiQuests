package net.nimbus.lokiquests.commands.executors;

import net.minecraft.world.item.CompassItem;
import net.nimbus.lokiquests.LQuests;
import net.nimbus.lokiquests.Utils;
import net.nimbus.lokiquests.core.dialogs.Dialog;
import net.nimbus.lokiquests.core.dialogs.Dialogs;
import net.nimbus.lokiquests.core.dungeon.Dungeon;
import net.nimbus.lokiquests.core.dungeon.Dungeons;
import net.nimbus.lokiquests.core.dungeon.mobspawner.MobSpawners;
import net.nimbus.lokiquests.core.dungeon.spawnertask.SpawnerTask;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import net.nimbus.lokiquests.core.questplayers.QuestPlayers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LquestExe implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player p) if(!p.hasPermission("lq.admin")) {
            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.no-permission")));
            return true;
        }
        if(args.length == 0) {
            sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.lquest.usage")));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "process" : {
                if(args.length < 3) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.lquest.process.usage")));
                    return true;
                }
                Dialog dialog = Dialogs.get(args[1].toLowerCase());
                if(dialog == null) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.lquest.process.no_dialog").replace("%dialog%", args[1].toLowerCase())));
                    return true;
                }
                Player player = Bukkit.getPlayer(args[2]);
                if(player == null) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.lquest.process.no_player").replace("%player%", args[2])));
                    return true;
                }
                dialog.processNext(player);
                return true;
            }
            case "item" :{
                if(args.length == 1) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.lquest.item.usage")));
                    return true;
                }
                if(!(sender instanceof Player p)) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.player-only")));
                    return true;
                }
                ItemStack item = p.getEquipment().getItemInMainHand();
                if(item.getType() == Material.AIR) {
                    sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.lquest.item.no_item")));
                }
                LQuests.a.getItems().set(args[1], item);
                LQuests.a.saveItems();
                sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.lquest.item.success").replace("%name%", args[1])));
                return true;
            }
            default: {
                sender.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Commands.lquest.usage")));
                return true;
            }
        }
    }
}

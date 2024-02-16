package net.nimbus.lokiquests.commands.executors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class LquestExe implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length == 0) {

            return true;
        }
        switch (args[0].toLowerCase()) {
            case "" : {

            }
        }
        return true;
    }
}

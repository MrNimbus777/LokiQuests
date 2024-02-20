package net.nimbus.lokiquests.core.dialogs.action.actions;

import net.nimbus.lokiquests.core.dialogs.action.Action;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import net.nimbus.lokiquests.core.questplayers.QuestPlayers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ActionPointIndicator implements Action {
    @Override
    public void execute(Player player, String vars) {
        String[] split = vars.split(",");
        try {
            Location loc = new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
            QuestPlayer qp = QuestPlayers.get(player);
            qp.runIndicator(loc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

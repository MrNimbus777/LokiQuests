package net.nimbus.lokiquests;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.lorenzo0111.kingdoms.KingdomsPlugin;
import me.lorenzo0111.kingdoms.data.Kingdom;
import net.nimbus.lokiquests.core.party.Parties;
import net.nimbus.lokiquests.core.party.Party;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import net.nimbus.lokiquests.core.questplayers.QuestPlayers;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Placeholders extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "lq";
    }

    @Override
    public @NotNull String getAuthor() {
        return "";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if(player.getPlayer() == null) return "";
        Player p = player.getPlayer();
        return switch (params) {
            case "party" -> {
                Party party = Parties.get(p);
                if(party == null) yield "";
                yield party.getName();
            }
            case "quest" -> {
                QuestPlayer qp = QuestPlayers.get(p);
                if(qp.getActiveQuests().isEmpty()) yield "";
                yield qp.getActiveQuests().get(0).getName();
            }
            case "kingdom" -> {
                String ret = "";
                try {
                    Kingdom kingdom = KingdomsPlugin.getInstance().getManager().getPlayerKingdom(player);
                    if (kingdom != null) {
                        ret = kingdom.getName();
                    }
                } catch (Exception e) {
                    ret = "";
                }
                yield ret;
            }
            case "kingdom_" -> {
                String ret = "";
                try {
                    Kingdom kingdom = KingdomsPlugin.getInstance().getManager().getPlayerKingdom(player);
                    if (kingdom != null) {
                        ret = "&7["+kingdom.getName() + "&7] ";
                    }
                } catch (Exception e) {
                    ret = "";
                }
                yield ret;
            }
            default -> "";
        };
    }
}

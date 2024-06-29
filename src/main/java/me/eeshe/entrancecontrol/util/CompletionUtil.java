package me.eeshe.entrancecontrol.util;

import me.eeshe.entrancecontrol.EntranceControl;
import me.eeshe.entrancecontrol.models.EntranceSelection;
import org.bukkit.entity.Player;

import java.util.List;

public class CompletionUtil {

    /**
     * Gets the entrance selection names of the passed player.
     *
     * @param player Player to get the entrance selection names of.
     * @return The entrance selection names of the player.
     */
    public static List<String> getPlayerEntranceSelectionNames(Player player) {
        return EntranceControl.getInstance().getEntranceSelectionManager().getPlayerEntranceSelections(player)
                .stream().map(EntranceSelection::getDisplayName).toList();
    }
}

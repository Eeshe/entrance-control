package me.eeshe.entrancecontrol.commands;

import me.eeshe.entrancecontrol.EntranceControl;
import me.eeshe.entrancecontrol.models.EntranceSelection;
import me.eeshe.entrancecontrol.models.config.Message;
import me.eeshe.penpenlib.PenPenPlugin;
import me.eeshe.penpenlib.commands.PenCommand;
import me.eeshe.penpenlib.models.config.CommonSound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class CommandLock extends PenCommand {

    public CommandLock(PenPenPlugin plugin, PenCommand parentCommand) {
        super(plugin, parentCommand);

        setName("lock");
        setPermission("entrancecontrol.lock");
        setInfoMessage(Message.LOCK_COMMAND_INFO);
        setUsageMessage(Message.LOCK_COMMAND_USAGE);
        setArgumentAmount(1);
        setPlayerCommand(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(getPlugin() instanceof EntranceControl plugin)) return;

        Player player = (Player) sender;
        int playerSelections = plugin.getEntranceSelectionManager().getPlayerEntranceSelectionAmount(player);
        int playerSelectionsLimit = plugin.getEntranceSelectionManager().computePlayerEntranceSelectionLimit(player);
        if (playerSelections >= playerSelectionsLimit) {
            Message.SELECTION_LIMIT_REACHED.sendError(player);
            return;
        }
        String displayName = args[0];
        if (EntranceSelection.fromName(player, displayName) != null) {
            Message.ALREADY_USED_ID.sendError(player, Map.of("%id%", displayName));
            return;
        }
        EntranceSelection entranceSelection = new EntranceSelection(player, displayName);
        entranceSelection.startHighlightTask(player);
        plugin.getEntranceSelectionEditors().put(player.getUniqueId(), entranceSelection);
        Message.ENTRANCE_SELECTION_INSTRUCTIONS.send(player, CommonSound.INPUT_REQUEST);
    }
}

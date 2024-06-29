package me.eeshe.entrancecontrol.commands;

import me.eeshe.entrancecontrol.models.EntranceSelection;
import me.eeshe.entrancecontrol.models.config.Message;
import me.eeshe.entrancecontrol.models.config.Sound;
import me.eeshe.entrancecontrol.util.CompletionUtil;
import me.eeshe.penpenlib.PenPenPlugin;
import me.eeshe.penpenlib.commands.PenCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Map;

public class CommandOpen extends PenCommand {

    public CommandOpen(PenPenPlugin plugin, PenCommand parentCommand) {
        super(plugin, parentCommand);

        setName("open");
        setPermission("entrancecontrol.open");
        setInfoMessage(Message.OPEN_COMMAND_INFO);
        setUsageMessage(Message.OPEN_COMMAND_USAGE);
        setArgumentAmount(1);
        setPlayerCommand(true);
        setCompletions(Map.of(0, (sender, strings) -> {
            if (!(sender instanceof Player player)) return new ArrayList<>();

            return CompletionUtil.getPlayerEntranceSelectionNames(player);
        }));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String entranceSelectionName = args[0];
        EntranceSelection entranceSelection = EntranceSelection.fromName(player, entranceSelectionName);
        if (entranceSelection == null) {
            Message.ENTRANCE_SELECTION_NOT_FOUND.sendError(player, Map.of("%name%", entranceSelectionName));
            return;
        }
        entranceSelection.openEntrances();
        Message.OPEN_COMMAND_SUCCESS.send(player, Sound.SELECTION_OPEN, Map.of("%name%", entranceSelectionName));
    }
}

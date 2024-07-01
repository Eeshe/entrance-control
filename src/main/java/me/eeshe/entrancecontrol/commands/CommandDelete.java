package me.eeshe.entrancecontrol.commands;

import me.eeshe.entrancecontrol.models.EntranceSelection;
import me.eeshe.entrancecontrol.models.config.Message;
import me.eeshe.entrancecontrol.util.CompletionUtil;
import me.eeshe.penpenlib.PenPenPlugin;
import me.eeshe.penpenlib.commands.PenCommand;
import me.eeshe.penpenlib.util.LibMessager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class CommandDelete extends PenCommand {

    public CommandDelete(PenPenPlugin plugin, PenCommand parentCommand) {
        super(plugin, parentCommand);

        setName("delete");
        setPermission("entrancecontrol.delete");
        setInfoMessage(Message.DELETE_COMMAND_INFO);
        setUsageMessage(Message.DELETE_COMMAND_USAGE);
        setArgumentAmount(2);
        setUniversalCommand(true);
        setCompletions(Map.ofEntries(
                Map.entry(0, (sender, strings) -> null),
                Map.entry(1, (sender, strings) -> {
                    String targetName = strings[0];
                    OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
                    if (!target.hasPlayedBefore()) return new ArrayList<>();

                    return CompletionUtil.getPlayerEntranceSelectionNames(target);
                })
        ));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String targetName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        if (!target.hasPlayedBefore()) {
            LibMessager.sendPlayerNotFoundMessage(sender, targetName);
            return;
        }
        String entranceSelectionId = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        EntranceSelection entranceSelection = EntranceSelection.fromName(target, entranceSelectionId);
        if (entranceSelection == null) {
            Message.ENTRANCE_SELECTION_NOT_FOUND.sendError(sender, Map.of("%name%", entranceSelectionId));
            return;
        }
        entranceSelection.unregister();
        Message.DELETE_COMMAND_SUCCESS_SENDER.sendSuccess(sender, Map.ofEntries(
                Map.entry("%target%", target.getName()),
                Map.entry("%selection_name%", entranceSelectionId)
        ));
        if (target.isOnline() && !target.equals(sender)) {
            Message.DELETE_COMMAND_SUCCESS_TARGET.sendError(target.getPlayer(), Map.of("%selection_name%", entranceSelectionId));
        }
    }
}

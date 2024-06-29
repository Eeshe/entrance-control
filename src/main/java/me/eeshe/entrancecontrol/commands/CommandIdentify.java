package me.eeshe.entrancecontrol.commands;

import me.eeshe.entrancecontrol.EntranceControl;
import me.eeshe.entrancecontrol.models.config.Message;
import me.eeshe.penpenlib.PenPenPlugin;
import me.eeshe.penpenlib.commands.PenCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class CommandIdentify extends PenCommand {

    public CommandIdentify(PenPenPlugin plugin, PenCommand parentCommand) {
        super(plugin, parentCommand);

        setName("identify");
        setPermission("entrancecontrol.identify");
        setInfoMessage(Message.IDENTIFY_COMMAND_INFO);
        setUsageMessage(Message.IDENTIFY_COMMAND_USAGE);
        setArgumentAmount(0);
        setPlayerCommand(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(getPlugin() instanceof EntranceControl plugin)) return;

        Player player = (Player) sender;
        UUID playerUuid = player.getUniqueId();
        Set<UUID> entranceIdentifiers = plugin.getEntranceIdentifiers();
        if (!entranceIdentifiers.contains(playerUuid)) {
            entranceIdentifiers.add(playerUuid);
            Message.IDENTIFY_COMMAND_START.sendSuccess(player);
        } else {
            entranceIdentifiers.remove(playerUuid);
            Message.IDENTIFY_COMMAND_STOP.sendError(player);
        }
    }
}

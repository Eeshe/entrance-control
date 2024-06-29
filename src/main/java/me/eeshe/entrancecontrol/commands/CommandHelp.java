package me.eeshe.entrancecontrol.commands;

import me.eeshe.entrancecontrol.models.config.Message;
import me.eeshe.penpenlib.PenPenPlugin;
import me.eeshe.penpenlib.commands.PenCommand;
import me.eeshe.penpenlib.commands.PenCommandHelp;

public class CommandHelp extends PenCommandHelp {

    public CommandHelp(PenPenPlugin plugin, PenCommand parentCommand) {
        super(plugin, parentCommand);

        setPermission("entrancecontrol.help");
        setInfoMessage(Message.HELP_COMMAND_INFO);
        setUsageMessage(Message.HELP_COMMAND_USAGE);
    }
}

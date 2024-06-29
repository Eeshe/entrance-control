package me.eeshe.entrancecontrol.commands;

import me.eeshe.entrancecontrol.models.config.Message;
import me.eeshe.penpenlib.PenPenPlugin;
import me.eeshe.penpenlib.commands.PenCommand;
import me.eeshe.penpenlib.commands.PenCommandReload;

public class CommandReload extends PenCommandReload {

    public CommandReload(PenPenPlugin plugin, PenCommand parentCommand) {
        super(plugin, parentCommand);

        setPermission("entrancecontrol.reload");
        setInfoMessage(Message.RELOAD_COMMAND_INFO);
        setUsageMessage(Message.RELOAD_COMMAND_USAGE);
    }
}

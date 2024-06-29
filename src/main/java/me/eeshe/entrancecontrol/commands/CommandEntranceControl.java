package me.eeshe.entrancecontrol.commands;

import me.eeshe.penpenlib.PenPenPlugin;
import me.eeshe.penpenlib.commands.PenCommand;

import java.util.List;

public class CommandEntranceControl extends PenCommand {

    public CommandEntranceControl(PenPenPlugin plugin) {
        super(plugin);

        setName("entrancecontrol");
        setPermission("entrancecontrol.base");
        setSubcommands(List.of(
                new CommandLock(plugin, this),
                new CommandManager(plugin, this),
                new CommandOpen(plugin, this),
                new CommandClose(plugin, this),
                new CommandIdentify(plugin, this),
                new CommandDelete(plugin, this),
                new CommandReload(plugin, this),
                new CommandHelp(plugin, this)
        ));
    }
}

package me.eeshe.entrancecontrol.commands;

import me.eeshe.entrancecontrol.inventories.EntranceSelectionManagerMenu;
import me.eeshe.entrancecontrol.models.config.Message;
import me.eeshe.entrancecontrol.models.config.Sound;
import me.eeshe.penpenlib.PenPenPlugin;
import me.eeshe.penpenlib.commands.PenCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandManager extends PenCommand {

    public CommandManager(PenPenPlugin plugin, PenCommand parentCommand) {
        super(plugin, parentCommand);

        setName("manager");
        setPermission("entrancecontrol.manager");
        setInfoMessage(Message.MANAGER_COMMAND_INFO);
        setUsageMessage(Message.MANAGER_COMMAND_USAGE);
        setArgumentAmount(0);
        setPlayerCommand(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        player.openInventory(EntranceSelectionManagerMenu.create(player, 1));
        Sound.ENTRANCE_SELECTION_MANAGER_MENU_OPEN.play(player);
    }
}

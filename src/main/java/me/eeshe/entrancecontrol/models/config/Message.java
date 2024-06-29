package me.eeshe.entrancecontrol.models.config;

import me.eeshe.entrancecontrol.EntranceControl;
import me.eeshe.penpenlib.files.config.ConfigWrapper;
import me.eeshe.penpenlib.models.config.PenMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Message extends PenMessage {
    private static final List<PenMessage> MESSAGES = new ArrayList<>();
    private static final ConfigWrapper CONFIG_WRAPPER = new ConfigWrapper(EntranceControl.getInstance(), null, "messages.yml");
    private static final Map<String, PenMessage> PLACEHOLDERS = new HashMap<>();

    public static final Message BLACKLISTED_WORLD = new Message("blacklisted-world", "&cEntranceControl can't be used in this world.");
    public static final Message BLACKLISTED_REGION = new Message("blacklisted-region", "&cEntranceControl can't be used in this region.");

    public static final Message HELP_COMMAND_INFO = new Message("help-command-info", "Displays this list.");
    public static final Message HELP_COMMAND_USAGE = new Message("help-command-usage", "/entrancecontrol help");

    public static final Message RELOAD_COMMAND_INFO = new Message("reload-command-info", "Reloads the plugin's configuration file.");
    public static final Message RELOAD_COMMAND_USAGE = new Message("reload-command-usage", "/entrancecontrol reload");

    public static final Message LOCK_COMMAND_INFO = new Message("lock-command-info", "Starts an entrance selection creation session.");
    public static final Message LOCK_COMMAND_USAGE = new Message("lock-command-usage", "/entrancecontrol lock <SelectionID>");
    public static final Message SELECTION_LIMIT_REACHED = new Message("selection-limit-reached", "&cYou can't create any more entrance selections.");
    public static final Message ALREADY_USED_ID = new Message("already-used-id", "&cYou are already using ID &l%id% in your entrance selections.");
    public static final Message ENTRANCE_SELECTION_NAME_INSTRUCTIONS = new Message("entrance-selection-name-instructions", "&eEnter the new name of the entrance selection as a chat message.\n&eEnter &ocancel&e to cancel the operation.");
    public static final Message ENTRANCE_SELECTION_INSTRUCTIONS = new Message("entrance-selection-instructions", "&eRight click to add entrances to the selection.\n&eLeft click to remove entrances from the selection.\n&eSneak to end the selection.");
    public static final Message SELECTED_ENTRANCES_ACTION_BAR = new Message("selected-entrances-action-bar", "&7Selected entrances: &l%selected_entrances%/%maximum_entrances%");
    public static final Message ENTRANCE_SELECTION_END_CONFIRMATION = new Message("entrance-selection-end-confirmation", "&eSneak again to end the entrance selection.");
    public static final Message NO_SELECTED_ENTRANCES = new Message("no-selected-entrances", "&cYou didn't select any entrances.");
    public static final Message EXCEEDED_SELECTION_AMOUNT = new Message("exceeded-selection-amount", "&cYou can't select any more entrances in this selection.");
    public static final Message EXCEEDED_SELECTION_DISTANCE = new Message("exceeded-selection-distance", "&cYour selection exceeds your maximum selection distance &l(%maximum_distance%)&c.");
    public static final Message ENTRANCE_SELECTION_END = new Message("entrance-selection-end", "&aEnded entrance selection.");

    public static final Message MANAGER_COMMAND_INFO = new Message("manager-command-info", "Opens the entrance control manager menu.");
    public static final Message MANAGER_COMMAND_USAGE = new Message("manager-command-usage", "/entrancecontrol manager");

    public static final Message OPEN_COMMAND_INFO = new Message("open-command-info", "Opens all the entrances from the specified selection.");
    public static final Message OPEN_COMMAND_USAGE = new Message("open-command-usage", "/entrancecontrol open <SelectionID");
    public static final Message ENTRANCE_SELECTION_NOT_FOUND = new Message("entrance-selection-not-found", "&cEntrance selection &l%name%&c not found.");
    public static final Message OPEN_COMMAND_SUCCESS = new Message("open-command-success", "&aSuccessfully opened all entrances from &l%name%&a.");

    public static final Message CLOSE_COMMAND_INFO = new Message("close-command-info", "Closes all the entrances from the specified selection.");
    public static final Message CLOSE_COMMAND_USAGE = new Message("close-command-usage", "/entrancecontrol close <SelectionID");
    public static final Message CLOSE_COMMAND_SUCCESS = new Message("close-command-success", "&aSuccessfully closed all entrances from &l%name%&a.");

    public static final Message IDENTIFY_COMMAND_INFO = new Message("identify-command-info", "Shows information about the clicked entrances.");
    public static final Message IDENTIFY_COMMAND_USAGE = new Message("identify-command-usage", "/entrancecontrol identify");
    public static final Message IDENTIFY_COMMAND_START = new Message("identify-command-start", "&aRight click the entrance you wish to identify.");
    public static final Message IDENTIFY_COMMAND_STOP = new Message("identify-command-stop", "&cYou are no longer identifying entrances.");
    public static final Message NOT_ENTRANCE_SELECTION = new Message("not-entrance-selection", "&cThis entrance is not locked");
    public static final Message ENTRANCE_IDENTIFY = new Message("entrance-identify", "&eThis entrance is part of selection &l%selection_name%&e owned by &l%selection_owner%&e.");

    public static final Message DELETE_COMMAND_INFO = new Message("delete-command-info", "Deletes the specified selection from the indicated player.");
    public static final Message DELETE_COMMAND_USAGE = new Message("delete-command-usage", "/entrancecontrol delete <Player> <SelectionID>");
    public static final Message DELETE_COMMAND_SUCCESS_SENDER = new Message("delete-command-success-sender", "&aSuccessfully deleted &l%target%'s&a entrance selection &l%selection_name%&a.");
    public static final Message DELETE_COMMAND_SUCCESS_TARGET = new Message("delete-command-success-target", "&cYour entrance selection &l%selection_name%&c has been deleted.");

    public static final Message ALREADY_SELECTED_ENTRANCE = new Message("already-selected-entrance", "&cThis entrance is already protected by a selection.");
    public static final Message NO_ENTRANCE_ACCESS = new Message("no-entrance-access", "&cYou don't have access to this entrance.");
    public static final Message PROTECTED_ENTRANCE_BREAK = new Message("protected-entrance-break", "&cThis entrance is protected.");

    public static final Message ENTRANCE_SELECTION_ADD_MEMBER_INSTRUCTIONS = new Message("entrance-selection-add-member-instructions", "&eEnter the name of the player you wish to add to the selection as a chat message.\n&eEnter &ocancel&e to cancel the operation.");
    public static final Message ENTRANCE_SELECTION_ADD_MEMBER_CANCEL = new Message("entrance-selection-add-member-cancel", "&cCancelled member add.");
    public static final Message ALREADY_MEMBER = new Message("already-member", "&c&l%target%&c is already a member of this entrance selection.");
    public static final Message ENTRANCE_SELECTION_ADD_MEMBER_SUCCESS_SELF = new Message("entrance-selection-add-member-success-self", "&aSuccessfully added &l%target%&a to &l%display_name%&a's entrance selection.");
    public static final Message ENTRANCE_SELECTION_ADD_MEMBER_SUCCESS_TARGET = new Message("entrance-selection-add-member-success-target", "&a&l%owner%&a added you to &l%display_name%&a's entrance selection.");


    public static final Message EXCEEDED_SELECTION_LIMIT_OTHER = new Message("exceeded-selection-limit-target", "&c&l%target%&c has already reached the entrance selection limit.");
    public static final Message PLAYER_NOT_ONLINE = new Message("player-not-online", "&c&l%target%&c isn't online.");
    public static final Message ENTRANCE_SELECTION_TRANSFER_CONFIRMATION = new Message("entrance-selection-transfer-confirmation", "&eClick again to confirm the ownership transfer.");
    public static final Message ENTRANCE_SELECTION_TRANSFER_SUCCESS_SELF = new Message("entrance-selection-transfer-success-self", "&aSuccessfully transferred &l%display_name%&a's ownership to &l%target%&a.");
    public static final Message ENTRANCE_SELECTION_TRANSFER_SUCCESS_TARGET = new Message("entrance-selection-transfer-success-target", "&aEntrance selection &l%display_name%'s&a ownership has been transferred to you.");

    public static final Message ENTRANCE_SELECTION_REMOVE_MEMBER_CONFIRMATION = new Message("entrance-selection-remove-member-confirmation", "&eClick again to confirm the removal of the player from your selection.");
    public static final Message ENTRANCE_SELECTION_REMOVE_MEMBER_SUCCESS_SELF = new Message("entrance-selection-remove-member-success-self", "&aSuccessfully removed &l%target%&a from &l%display_name%&a.");
    public static final Message ENTRANCE_SELECTION_REMOVE_MEMBER_SUCCESS_TARGET = new Message("entrance-selection-remove-member-success-target", "&cYou've been removed from entrance selection &l%display_name%'s&c.");

    public static final Message ENTRANCE_SELECTION_DELETE_CONFIRMATION = new Message("entrance-selection-delete-confirmation", "&cClick again to confirm the deletion of the entrance selection.");

    public Message(String path, String defaultValue) {
        super(path, defaultValue);
    }

    public Message() {
    }

    @Override
    public List<PenMessage> getMessages() {
        return MESSAGES;
    }

    @Override
    public ConfigWrapper getConfigWrapper() {
        return CONFIG_WRAPPER;
    }

    @Override
    public Map<String, PenMessage> getPlaceholders() {
        return PLACEHOLDERS;
    }
}

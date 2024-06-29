package me.eeshe.entrancecontrol.models.config;

import me.eeshe.entrancecontrol.EntranceControl;
import me.eeshe.penpenlib.files.config.ConfigWrapper;
import me.eeshe.penpenlib.models.config.PenSound;

import java.util.ArrayList;
import java.util.List;

public class Sound extends PenSound {
    private static final List<PenSound> SOUNDS = new ArrayList<>();
    private static final ConfigWrapper CONFIG_WRAPPER = new ConfigWrapper(EntranceControl.getInstance(), null, "sounds.yml");

    public static final Sound SELECTION_ADD = new Sound("selection-add", true, org.bukkit.Sound.UI_BUTTON_CLICK, 1.0F, 1.2F);
    public static final Sound SELECTION_REMOVE = new Sound("selection-remove", true, org.bukkit.Sound.UI_BUTTON_CLICK, 1.0F, 0.8F);

    public static final Sound SELECTION_OPEN = new Sound("selection-open", true, org.bukkit.Sound.BLOCK_IRON_DOOR_OPEN, 1.0F, 1F);
    public static final Sound SELECTION_CLOSE = new Sound("selection-close", true, org.bukkit.Sound.BLOCK_IRON_DOOR_CLOSE, 1.0F, 1F);

    public static final Sound ENTRANCE_SELECTION_MANAGER_MENU_OPEN = new Sound("entrance-selection-manager-menu-open", true, org.bukkit.Sound.UI_BUTTON_CLICK, 1.0F, 0.8F);
    public static final Sound ENTRANCE_SELECTION_SETTINGS_MENU_OPEN = new Sound("entrance-selection-settings-menu-open", true, org.bukkit.Sound.UI_BUTTON_CLICK, 1.0F, 0.8F);
    public static final Sound ENTRANCE_SELECTION_MEMBERS_MENU_OPEN = new Sound("entrance-selection-members-menu-open", true, org.bukkit.Sound.UI_BUTTON_CLICK, 1.0F, 0.8F);

    public static final Sound ENTRANCE_SYNC_ON = new Sound("entrance-sync-on", true, org.bukkit.Sound.UI_BUTTON_CLICK, 1.0F, 1.5F);
    public static final Sound ENTRANCE_SYNC_OFF = new Sound("entrance-sync-off", true, org.bukkit.Sound.UI_BUTTON_CLICK, 1.0F, 0.5F);
    public static final Sound BREAK_PROTECTION_ON = new Sound("break-protection-on", true, org.bukkit.Sound.UI_BUTTON_CLICK, 1.0F, 1.5F);
    public static final Sound BREAK_PROTECTION_OFF = new Sound("break-protection-off", true, org.bukkit.Sound.UI_BUTTON_CLICK, 1.0F, 0.5F);
    public static final Sound LOCK_ON = new Sound("lock-on", true, org.bukkit.Sound.UI_BUTTON_CLICK, 1.0F, 1.5F);
    public static final Sound LOCK_OFF = new Sound("lock-off", true, org.bukkit.Sound.UI_BUTTON_CLICK, 1.0F, 0.5F);
    public static final Sound ENTRANCE_SELECTION_DELETE = new Sound("entrance-selection-delete", true, org.bukkit.Sound.BLOCK_GRAVEL_BREAK, 1.0F, 1.3F);

    public static final Sound ENTRANCE_IDENTIFY = new Sound("entrance-identify", true, org.bukkit.Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 1.2F);

    public Sound(String path, boolean defaultEnabled, org.bukkit.Sound defaultSound, float defaultVolume, float defaultPitch) {
        super(path, defaultEnabled, defaultSound, defaultVolume, defaultPitch);
    }

    public Sound() {
    }

    @Override
    public List<PenSound> getSounds() {
        return SOUNDS;
    }

    @Override
    public ConfigWrapper getConfigWrapper() {
        return CONFIG_WRAPPER;
    }
}

package me.eeshe.entrancecontrol.files.config;

import me.eeshe.penpenlib.files.config.ConfigWrapper;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class MainConfig extends ConfigWrapper {
    private static final String WORLD_BLACKLIST_PATH = "world-blacklist";
    private static final String REGION_BLACKLIST_PATH = "region-blacklist";
    private static final String MAXIMUM_ENTRANCE_SELECTION_AMOUNT_PATH = "maximum-entrance-selection-amount";
    private static final String MAXIMUM_ENTRANCE_SELECTION_AMOUNT_PERMISSION_PATH = MAXIMUM_ENTRANCE_SELECTION_AMOUNT_PATH + ".permission";
    private static final String DEFAULT_MAXIMUM_ENTRANCE_SELECTION_AMOUNT_PATH = MAXIMUM_ENTRANCE_SELECTION_AMOUNT_PATH + ".default";
    private static final String MAXIMUM_SELECTED_ENTRANCES_PATH = "maximum-selected-entrances";
    private static final String MAXIMUM_SELECTED_ENTRANCES_PERMISSION_PATH = MAXIMUM_SELECTED_ENTRANCES_PATH + ".permission";
    private static final String DEFAULT_MAXIMUM_SELECTED_ENTRANCES_AMOUNT_PATH = MAXIMUM_SELECTED_ENTRANCES_PATH + ".default";
    private static final String MAXIMUM_SELECTION_DISTANCE_PATH = "maximum-selection-distance";
    private static final String MAXIMUM_SELECTION_DISTANCE_PERMISSION_PATH = MAXIMUM_SELECTION_DISTANCE_PATH + ".permission";
    private static final String DEFAULT_MAXIMUM_SELECTION_DISTANCE_PATH = MAXIMUM_SELECTION_DISTANCE_PATH + ".default";
    private static final String BREAK_PROTECTION_PATH = "break-protection";


    public MainConfig(Plugin plugin) {
        super(plugin, null, "config.yml");
    }

    @Override
    public void writeDefaults() {
        writeBlacklistDefaults();
        writePermissionDefaults();
        writeGeneralDefaults();

        getConfig().options().copyDefaults(true);
        saveConfig();
        reloadConfig();

        // Comments need to be written after the default config is written
        writeComments();

        getConfig().options().copyDefaults(true);
        saveConfig();
        reloadConfig();
    }

    /**
     * Writes the default configurations for blacklists.
     */
    private void writeBlacklistDefaults() {
        FileConfiguration config = getConfig();

        config.addDefault(WORLD_BLACKLIST_PATH, List.of(
                "world_the_end"
        ));
        config.addDefault(REGION_BLACKLIST_PATH, List.of(
                "region1",
                "region2"
        ));
    }

    /**
     * Writes the default configurations for permissions.
     */
    private void writePermissionDefaults() {
        FileConfiguration config = getConfig();

        config.addDefault(MAXIMUM_ENTRANCE_SELECTION_AMOUNT_PERMISSION_PATH, "entrancecontrol.maximum_entrance_selections.");
        config.addDefault(DEFAULT_MAXIMUM_ENTRANCE_SELECTION_AMOUNT_PATH, 10);

        config.addDefault(MAXIMUM_SELECTED_ENTRANCES_PERMISSION_PATH, "entrancecontrol.maximum_selected_entrances.");
        config.addDefault(DEFAULT_MAXIMUM_SELECTED_ENTRANCES_AMOUNT_PATH, 20);

        config.addDefault(MAXIMUM_SELECTION_DISTANCE_PERMISSION_PATH, "entrancecontrol.maximum_selection_distance.");
        config.addDefault(DEFAULT_MAXIMUM_SELECTION_DISTANCE_PATH, 20);
    }

    /**
     * Writes the default configurations for general settings.
     */
    private void writeGeneralDefaults() {
        FileConfiguration config = getConfig();

        config.addDefault(BREAK_PROTECTION_PATH, true);
    }

    private void writeComments() {
        FileConfiguration config = getConfig();

        config.setComments(WORLD_BLACKLIST_PATH, List.of(
                "Worlds where EntranceControl won't handle events in."
        ));
        config.setComments(REGION_BLACKLIST_PATH, List.of(
                "WorldGuard regions where EntranceControl won't handle events in."
        ));

        config.setComments(MAXIMUM_ENTRANCE_SELECTION_AMOUNT_PATH, List.of(
                "Permission that will be checked when computing the total amount of entrance selections a player can have.",
                "Ex. 'entrancecontrol.maximum_entrance_selections.' will allow a player that has the permission 'entrancecontrol.maximum_entrance_selections.10' to have 10 entrance selections.",
                "The default amount will be used if the player doesn't have the configured permission."
        ));
        config.setComments(MAXIMUM_SELECTED_ENTRANCES_PATH, List.of(
                "Permission that will be checked when computing the total amount of entrance a player can select within an entrance selection.",
                "Ex. 'entrancecontrol.maximum_selected_entrances.' will allow a player that has the permission 'entrancecontrol.maximum_selected_entrances.10' to select 10 entrances in their selection.",
                "The default amount will be used if the player doesn't have the configured permission."
        ));
        config.setComments(MAXIMUM_SELECTION_DISTANCE_PATH, List.of(
                "Permission that will be checked when computing the total distance an entrance selection can have between their entrances.",
                "Ex. 'entrancecontrol.maximum_selection_distance.' will allow a player that has the permission 'entrancecontrol.maximum_selection_distance.10' to have a total distance of 10 blocks between the entrances in their selection.",
                "The default amount will be used if the player doesn't have the configured permission."
        ));

        config.setComments(BREAK_PROTECTION_PATH, List.of(
                "Whether break protection will be handled by the plugin."
        ));
    }

    /**
     * Checks if the passed world is configured as a blacklisted world.
     *
     * @param world World to check.
     * @return True if the world is blacklisted, false otherwise.
     */
    public boolean isBlacklistedWorld(World world) {
        return getConfig().getStringList(WORLD_BLACKLIST_PATH).contains(world.getName());
    }

    /**
     * Checks if the passed region ID is configured as a blacklisted region.
     *
     * @param regionId Region ID to check.
     * @return True if the region is blacklisted, false otherwise.
     */
    public boolean isBlacklistedRegion(String regionId) {
        return getConfig().getStringList(REGION_BLACKLIST_PATH).contains(regionId);
    }

    /**
     * Retrieves the maximum EntranceSelection amount permission from the configuration.
     *
     * @return the maximum EntranceSelection amount permission as a string
     */
    public String getMaximumEntranceSelectionAmountPermission() {
        return getConfig().getString(MAXIMUM_ENTRANCE_SELECTION_AMOUNT_PERMISSION_PATH);
    }

    /**
     * Retrieves the default maximum EntranceSelection amount from the configuration.
     *
     * @return the default maximum EntranceSelection amount as an integer
     */
    public int getDefaultMaximumEntranceSelectionAmount() {
        return getConfig().getInt(DEFAULT_MAXIMUM_ENTRANCE_SELECTION_AMOUNT_PATH);
    }

    /**
     * Retrieves the maximum selected entrances permission from the configuration.
     *
     * @return the maximum selected entrances permission as a string
     */
    public String getMaximumSelectedEntrancesPermission() {
        return getConfig().getString(MAXIMUM_SELECTED_ENTRANCES_PERMISSION_PATH);
    }

    /**
     * Retrieves the default maximum selected entrances amount from the configuration.
     *
     * @return the default maximum selected entrances amount as an integer
     */
    public int getDefaultMaximumSelectedEntrancesAmount() {
        return getConfig().getInt(DEFAULT_MAXIMUM_SELECTED_ENTRANCES_AMOUNT_PATH);
    }

    /**
     * Retrieves the maximum selection distance permission from the configuration.
     *
     * @return the maximum selection distance permission as a string
     */
    public String getMaximumSelectionDistancePermission() {
        return getConfig().getString(MAXIMUM_SELECTION_DISTANCE_PERMISSION_PATH);
    }

    /**
     * Retrieves the default maximum selection distance from the configuration.
     *
     * @return the default maximum selection distance as an integer
     */
    public int getDefaultMaximumSelectionDistance() {
        return getConfig().getInt(DEFAULT_MAXIMUM_SELECTION_DISTANCE_PATH);
    }

    /**
     * Checks if break protection is enabled in the configuration.
     *
     * @return true if break protection is enabled, false otherwise
     */
    public boolean isBreakProtectionEnabled() {
        return getConfig().getBoolean(BREAK_PROTECTION_PATH);
    }
}

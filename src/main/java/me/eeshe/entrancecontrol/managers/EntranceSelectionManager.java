package me.eeshe.entrancecontrol.managers;

import me.eeshe.entrancecontrol.EntranceControl;
import me.eeshe.entrancecontrol.files.config.EntranceSelectionFile;
import me.eeshe.entrancecontrol.models.EntranceSelection;
import me.eeshe.penpenlib.managers.DataManager;
import me.eeshe.penpenlib.util.PermissionUtil;
import me.eeshe.penpenlib.util.YAMLUtil;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.*;

public class EntranceSelectionManager extends DataManager {
    private final Map<UUID, EntranceSelection> entranceSelections = new HashMap<>();

    public EntranceSelectionManager(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void load() {
        loadAll();
    }

    /**
     * Loads all the stored entrance selections.
     */
    private void loadAll() {
        File entranceSelectionsFolder = new File(getPlugin().getDataFolder(), "entrance_selections");
        if (!entranceSelectionsFolder.exists()) return;
        File[] entranceSelectionDataFiles = entranceSelectionsFolder.listFiles();
        if (entranceSelectionDataFiles == null) return;
        for (File entranceSelectionDataFile : entranceSelectionDataFiles) {
            EntranceSelection entranceSelection = fetch(entranceSelectionDataFile);
            if (entranceSelection == null) continue;

            entranceSelection.load();
        }
    }

    /**
     * Fetches the EntranceSelection stored in the passed data file.
     *
     * @param entranceSelectionDataFile File containing the entrance selection.
     */
    private EntranceSelection fetch(File entranceSelectionDataFile) {
        EntranceSelectionFile entranceSelectionFile = new EntranceSelectionFile(entranceSelectionDataFile);
        FileConfiguration selectionData = entranceSelectionFile.getData();
        if (selectionData.getKeys(true).isEmpty()) return null;

        UUID uuid = UUID.fromString(selectionData.getString("uuid", ""));
        Set<Location> protectedEntrances = new HashSet<>((List<Location>) selectionData.getList("protected-entrances"));
        List<UUID> members = YAMLUtil.fetchUuidList (selectionData, "members");
        UUID ownerUuid = UUID.fromString(selectionData.getString("owner-uuid", ""));
        String displayName = selectionData.getString("display-name", "");
        boolean syncEntrances = selectionData.getBoolean("sync-entrances", true);
        boolean breakProtection = selectionData.getBoolean("break-protection", true);

        return new EntranceSelection(uuid, protectedEntrances, members, ownerUuid, displayName, syncEntrances, breakProtection);
    }


    @Override
    public void unload() {
        unloadAll();
    }

    /**
     * Unloads all the loaded entrance selections.
     */
    private void unloadAll() {
        for (EntranceSelection entranceSelection : new ArrayList<>(entranceSelections.values())) {
            entranceSelection.unload();
        }
    }

    /**
     * Saves the passed entrance selection.
     *
     * @param entranceSelection The entrance selection to save.
     */
    public void save(EntranceSelection entranceSelection) {
        EntranceSelectionFile entranceSelectionFile = new EntranceSelectionFile(entranceSelection);
        FileConfiguration selectionData = entranceSelectionFile.getData();

        selectionData.set("uuid", entranceSelection.getUuid().toString());
        selectionData.set("protected-entrances", entranceSelection.getProtectedEntrances().stream().toList());
        YAMLUtil.writeUuidList(entranceSelection.getMembers(), selectionData, "members");
        selectionData.set("owner-uuid", entranceSelection.getOwnerUuid().toString());
        selectionData.set("display-name", entranceSelection.getDisplayName());
        selectionData.set("sync-entrances", entranceSelection.shouldSyncEntrances());
        selectionData.set("break-protection", entranceSelection.hasBreakProtection());

        entranceSelectionFile.save();
    }

    /**
     * Deletes the passed entrance selection.
     *
     * @param entranceSelection The entrance selection to delete.
     */
    public void delete(EntranceSelection entranceSelection) {
        new EntranceSelectionFile(entranceSelection).delete();
    }

    public Map<UUID, EntranceSelection> getEntranceSelections() {
        return entranceSelections;
    }

    /**
     * Gets the amount of entrance selections of the passed player.
     *
     * @param player Player to get the amount of entrance selections of.
     * @return The amount of entrance selections of the player.
     */
    public int getPlayerEntranceSelectionAmount(OfflinePlayer player) {
        return getPlayerEntranceSelections(player).size();
    }

    /**
     * Gets the entrance selections of the passed player.
     *
     * @param player Player to get the entrance selections of.
     * @return The entrance selections of the player.
     */
    public List<EntranceSelection> getPlayerEntranceSelections(OfflinePlayer player) {
        List<EntranceSelection> playerEntranceSelections = new ArrayList<>();
        for (EntranceSelection entranceSelection : entranceSelections.values()) {
            if (!entranceSelection.isOwner(player)) continue;

            playerEntranceSelections.add(entranceSelection);
        }
        return playerEntranceSelections;
    }

    /**
     * Computes the entrance selection limit of the passed player.
     *
     * @param player Player to compute the entrance selection limit of.
     * @return The entrance selection limit of the player.
     */
    public int computePlayerEntranceSelectionLimit(Player player) {
        EntranceControl plugin = (EntranceControl) getPlugin();
        return PermissionUtil.computePermissionValue(player,
                plugin.getMainConfig().getMaximumEntranceSelectionAmountPermission(),
                plugin.getMainConfig().getDefaultMaximumEntranceSelectionAmount()
        );
    }

    public int computePlayerSelectedEntrancesLimit(Player player) {
        EntranceControl plugin = (EntranceControl) getPlugin();
        return PermissionUtil.computePermissionValue(player,
                plugin.getMainConfig().getMaximumSelectedEntrancesPermission(),
                plugin.getMainConfig().getDefaultMaximumSelectedEntrancesAmount()
        );
    }
}

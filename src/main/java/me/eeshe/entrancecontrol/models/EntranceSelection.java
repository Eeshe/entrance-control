package me.eeshe.entrancecontrol.models;

import me.eeshe.entrancecontrol.EntranceControl;
import me.eeshe.entrancecontrol.inventories.holders.EntranceSelectionMembersMenuHolder;
import me.eeshe.entrancecontrol.inventories.holders.EntranceSelectionSettingsMenuHolder;
import me.eeshe.entrancecontrol.models.config.Menu;
import me.eeshe.entrancecontrol.models.config.Particle;
import me.eeshe.entrancecontrol.util.BlockOutlineUtil;
import me.eeshe.penpenlib.models.Scheduler;
import me.eeshe.penpenlib.models.config.ConfigMenu;
import me.eeshe.penpenlib.models.config.MenuItem;
import me.eeshe.penpenlib.util.ItemUtil;
import me.eeshe.penpenlib.util.MenuUtil;
import me.eeshe.penpenlib.util.PlaceholderUtil;
import me.eeshe.penpenlib.util.StringUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Gate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class EntranceSelection {
    private static final NamespacedKey ENTRANCE_SELECTION_KEY = new NamespacedKey(EntranceControl.getInstance(), "entrance_selection");
    private final Map<UUID, Scheduler.Task> highlightTasks = new HashMap<>();

    private final UUID uuid;
    private final Set<Location> protectedEntrances;
    private final List<UUID> members;

    private UUID ownerUuid;
    private String displayName;
    private boolean syncEntrances;
    private boolean breakProtection;

    public EntranceSelection(Player player, String displayName) {
        this.uuid = UUID.randomUUID();
        this.ownerUuid = player.getUniqueId();
        this.displayName = ChatColor.stripColor(StringUtil.formatColor(displayName)); // Remove any inputted color

        this.protectedEntrances = new HashSet<>();
        this.members = new ArrayList<>();
        this.syncEntrances = true;
        this.breakProtection = true;
    }

    public EntranceSelection(UUID uuid, Set<Location> protectedEntrances, List<UUID> members, UUID ownerUuid,
                             String displayName, boolean syncEntrances, boolean breakProtection) {
        this.uuid = uuid;
        this.protectedEntrances = protectedEntrances;
        this.members = members;
        this.ownerUuid = ownerUuid;
        this.displayName = displayName;
        this.syncEntrances = syncEntrances;
        this.breakProtection = breakProtection;
    }

    /**
     * Searches for the EntranceSelection stored in the passed item's persistent data container.
     *
     * @param item The item to search for.
     * @return The entrance selection stored in the passed item's persistent data container.
     */
    public static EntranceSelection fromItemStack(ItemStack item) {
        if (item == null || item.getItemMeta() == null) return null;
        PersistentDataContainer persistentDataContainer = item.getItemMeta().getPersistentDataContainer();
        if (!persistentDataContainer.has(ENTRANCE_SELECTION_KEY, PersistentDataType.STRING)) return null;

        return fromUuid(UUID.fromString(persistentDataContainer.get(ENTRANCE_SELECTION_KEY, PersistentDataType.STRING)));
    }

    /**
     * Searches for an entrance selection with the passed uuid.
     *
     * @param uuid The uuid of the entrance selection.
     * @return The entrance selection with the passed uuid.
     */
    public static EntranceSelection fromUuid(UUID uuid) {
        return EntranceControl.getInstance().getEntranceSelectionManager().getEntranceSelections().get(uuid);
    }

    /**
     * Tries to find an entrance selection that contains the passed location.
     *
     * @param location The location to search for.
     * @return The entrance selection that contains the passed location.
     */
    public static EntranceSelection fromLocation(Location location) {
        for (EntranceSelection entranceSelection : EntranceControl.getInstance().getEntranceSelectionManager().getEntranceSelections().values()) {
            if (!entranceSelection.getProtectedEntrances().contains(location)) continue;

            return entranceSelection;
        }
        return null;
    }

    /**
     * Searches and returns an entrance selection with the passed name from the passed player owner.
     *
     * @param owner The player owner of the entrance selection.
     * @param name  The name of the entrance selection.
     * @return The entrance selection with the passed name from the passed player owner.
     */
    public static EntranceSelection fromName(Player owner, String name) {
        for (EntranceSelection entranceSelection : EntranceControl.getInstance().getEntranceSelectionManager().getPlayerEntranceSelections(owner)) {
            if (!entranceSelection.getDisplayName().equals(name)) continue;

            return entranceSelection;
        }
        return null;
    }

    /**
     * Loads the entrance selection and saves its data.
     */
    public void register() {
        load();
        saveData();
    }

    /**
     * Loads the entrance selection to the EntranceSelectionManager class.
     */
    public void load() {
        EntranceControl.getInstance().getEntranceSelectionManager().getEntranceSelections().put(uuid, this);
    }

    /**
     * Unloads the entrance selection and deletes its data.
     */
    public void unregister() {
        unload();
        EntranceControl.getInstance().getEntranceSelectionManager().delete(this);
    }

    /**
     * Unloads the entrance selection from the EntranceSelectionManager class.
     */
    public void unload() {
        EntranceControl.getInstance().getEntranceSelectionManager().getEntranceSelections().remove(uuid);
    }

    /**
     * Saves the entrance selection's data.
     */
    private void saveData() {
        EntranceControl.getInstance().getEntranceSelectionManager().save(this);
    }

    /**
     * Starts the task that will highlight the entrance selection to the passed player.
     */
    public void startHighlightTask(Player player) {
        stopHighlightTask(player);

        highlightTasks.put(player.getUniqueId(), Scheduler.runTimer(EntranceControl.getInstance(),
                () -> highlightEntrances(player), 0L, 20L));
    }

    /**
     * Highlights all the selected entrances.
     */
    private void highlightEntrances(Player player) {
        if (!player.isOnline()) return;
        for (Location protectedEntrance : getProtectedEntrances()) {
            BlockOutlineUtil.spawnBlockOutline(player, protectedEntrance.getBlock(), Particle.ENTRANCE_OUTLINE);
        }
    }

    public void stopHighlightTask(Player player) {
        Scheduler.Task task = highlightTasks.remove(player.getUniqueId());
        if (task == null) return;

        task.cancel();
    }

    /**
     * Creates and returns an inventory with the EntranceSelectionSettingsMenu.
     *
     * @return The created inventory.
     */
    public Inventory createSettingsMenu() {
        ConfigMenu configMenu = Menu.ENTRANCE_SELECTION_SETTINGS.fetch();

        String entranceSyncStatusString = Menu.ENTRANCE_SELECTION_SETTINGS.getAdditionalConfigString("entrance-sync-status." + syncEntrances);
        String breakProtectionStatusString = Menu.ENTRANCE_SELECTION_SETTINGS.getAdditionalConfigString("break-protection-status." + breakProtection);
        // Remove the protection toggle if entrance protection isn't enabled in the config
        List<MenuItem> menuItems = configMenu.getMenuItems();
        menuItems.removeIf(menuItem -> menuItem.getId().equals("break-protection")
                && !EntranceControl.getInstance().getMainConfig().isBreakProtectionEnabled());

        return configMenu.createInventory(new EntranceSelectionSettingsMenuHolder(this), true,
                true, true, true, Map.ofEntries(
                        Map.entry("%display_name%", displayName),
                        Map.entry("%entrance_sync%", entranceSyncStatusString),
                        Map.entry("%break_protection%", breakProtectionStatusString)
                ));
    }

    /**
     * Creates and returns an inventory with the EntranceSelectionMembersMenu.
     *
     * @param page The page to create the inventory for.
     * @return The created inventory.
     */
    public Inventory createMembersMenu(int page) {
        ConfigMenu configMenu = Menu.ENTRANCE_SELECTION_MEMBERS.fetch();
        int membersSize = Math.max(1, members.size()); // Ensure at least 1 member to avoid a 2 row inventory
        Inventory inventory = configMenu.createInventory(new EntranceSelectionMembersMenuHolder(this, page),
                membersSize, page, true, false, true, true,
                Map.of("%display_name%", displayName));

        addMemberItems(configMenu, inventory, page);
        MenuUtil.placeFillerItems(configMenu, inventory);

        return inventory;
    }

    /**
     * Adds the members of the entrance selection to the passed inventory.
     *
     * @param configMenu ConfigMenu being created.
     * @param inventory  Inventory to add the items to.
     * @param page       Page the menu is being opened in.
     */
    private void addMemberItems(ConfigMenu configMenu, Inventory inventory, int page) {
        boolean hasNextPage = false;
        for (int index = MenuUtil.computeInitialIndex(configMenu, page); index < members.size(); index++) {
            if (inventory.firstEmpty() == -1) {
                hasNextPage = true;
                break;
            }
            inventory.addItem(createMemberItem(members.get(index)));
        }
        if (hasNextPage || page > 1) {
            MenuUtil.addPageItems(inventory, configMenu, page, hasNextPage);
        }
    }

    /**
     * Creates and returns an item for the passed player.
     *
     * @param uuid The uuid of the player.
     * @return The created item.
     */
    private ItemStack createMemberItem(UUID uuid) {
        Menu menu = Menu.ENTRANCE_SELECTION_MEMBERS;

        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        ItemStack item = ItemUtil.generateItemStack(Material.PLAYER_HEAD,
                menu.getAdditionalConfigString("member-item.name").replace("%player_name%", player.getName()),
                menu.getAdditionalConfigStringList("member-item.lore")
        );
        if (item.getItemMeta() instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(player);
            item.setItemMeta(skullMeta);
        }
        return item;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Set<Location> getProtectedEntrances() {
        return protectedEntrances;
    }

    /**
     * Opens all the entrances of the entrance selection.
     */
    public void openEntrances() {
        setEntrancesOpenState(true, null);
    }

    public void closeEntrances() {
        setEntrancesOpenState(false, null);
    }

    /**
     * Modifies all of the entrance's state to the passed open state.
     *
     * @param open True if the entrance should be opened, false otherwise.
     * @param blockFace Block face fence gates should have, can be null.
     */
    public void setEntrancesOpenState(boolean open, BlockFace blockFace) {
        for (Location protectedEntranceLocation : protectedEntrances) {
            Block protectedEntrance = protectedEntranceLocation.getBlock();
            BlockData blockData = protectedEntrance.getBlockData();
            if (!(blockData instanceof Openable openable)) continue;
            if (protectedEntrance.getType() == Material.BARREL) continue;

            openable.setOpen(open);
            if (blockFace != null && blockData instanceof Gate gate) {
                gate.setFacing(blockFace);
            }
            protectedEntrance.setBlockData(openable, open);
            protectedEntrance.getState().update(true, true);
        }
    }

    /**
     * Adds a protected entrance to the entrance selection.
     *
     * @param player   Player adding the entrance.
     * @param location Location of the entrance.
     */
    public void addProtectedEntrance(Player player, Location location) {
        getProtectedEntrances().addAll(computeEntranceLocations(location));
        highlightEntrances(player);
        saveData();
    }

    public void removeProtectedEntrance(Location location) {
        getProtectedEntrances().removeAll(computeEntranceLocations(location));
        saveData();
    }

    /**
     * Computes the locations occupied by the entrance in the passed location.
     *
     * @param location Location the entrance is in.
     * @return List of locations occupied by the entrance.
     */
    private List<Location> computeEntranceLocations(Location location) {
        if (!(location.getBlock().getBlockData() instanceof Door door)) return List.of(location);

        Bisected.Half half = door.getHalf();
        int yModifier = half == Bisected.Half.BOTTOM ? 1 : -1;
        return List.of(
                location,
                location.clone().add(0, yModifier, 0)
        );
    }

    public List<UUID> getMembers() {
        return members;
    }

    /**
     * Checks if the passed player can interact with the entrance selection's entrances.
     *
     * @param player Player to check.
     * @return True if the player can interact, false otherwise.
     */
    public boolean canInteract(Player player) {
        if (player.hasPermission("entrancecontrol.admin")) return true;

        return isOwner(player) || getMembers().contains(player.getUniqueId());
    }

    /**
     * Checks if the passed player is a member of the entrance selection.
     *
     * @param player Player to check.
     * @return True if the player is a member, false otherwise.
     */
    public boolean isMember(OfflinePlayer player) {
        return getMembers().contains(player.getUniqueId());
    }

    /**
     * Adds the passed player to the entrance selection.
     *
     * @param player Player to add.
     */
    public void addMember(OfflinePlayer player) {
        getMembers().add(player.getUniqueId());
        saveData();
    }

    /**
     * Removes the passed player from the entrance selection.
     *
     * @param player Player to remove.
     */
    public void removeMember(OfflinePlayer player) {
        getMembers().remove(player.getUniqueId());
        saveData();
    }

    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    public boolean isOwner(OfflinePlayer player) {
        return getOwnerUuid() != null && getOwnerUuid().equals(player.getUniqueId());
    }

    public Player getOwner() {
        return getOwnerUuid() == null ? null : Bukkit.getPlayer(getOwnerUuid());
    }

    public OfflinePlayer getOfflineOwner() {
        return getOwnerUuid() == null ? null : Bukkit.getOfflinePlayer(getOwnerUuid());
    }

    /**
     * Transfers the ownership of the entrance selection to the passed player.
     *
     * @param newOwner New owner of the entrance selection.
     */
    public void transferOwnership(OfflinePlayer newOwner) {
        members.add(ownerUuid);
        members.remove(newOwner.getUniqueId());
        this.ownerUuid = newOwner.getUniqueId();
        saveData();
    }

    public void setOwnerUuid(UUID ownerUuid) {
        this.ownerUuid = ownerUuid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = ChatColor.stripColor(StringUtil.formatColor(displayName)); // Remove any inputted color
        saveData();
    }

    public boolean shouldSyncEntrances() {
        return syncEntrances;
    }

    public void setSyncEntrances(boolean syncEntrances) {
        this.syncEntrances = syncEntrances;
        saveData();
    }

    public boolean hasBreakProtection() {
        return breakProtection;
    }

    public void setBreakProtection(boolean breakProtection) {
        this.breakProtection = breakProtection;
    }

    /**
     * Creates an ItemStack to display the entrance selection in the list menu.
     *
     * @return The created ItemStack.
     */
    public ItemStack createListItemStack() {
        Menu menu = Menu.ENTRANCE_SELECTION_MANAGER;
        Map<String, String> placeholders = createPlaceholders();
        Material material = menu.getAdditionalConfigMaterial("entrance-selection-item.item");
        String displayName = PlaceholderUtil.formatPlaceholders(menu.getAdditionalConfigString("entrance-selection-item.name"), placeholders);
        List<String> lore = PlaceholderUtil.formatPlaceholders(menu.getAdditionalConfigStringList("entrance-selection-item.lore"), placeholders);

        ItemStack item = ItemUtil.generateItemStack(material, displayName, lore);
        ItemMeta meta = item.getItemMeta();

        meta.getPersistentDataContainer().set(ENTRANCE_SELECTION_KEY, PersistentDataType.STRING, uuid.toString());
        item.setItemMeta(meta);

        return item;
    }

    private Map<String, String> createPlaceholders() {
        int maximumSelectedEntrances = EntranceControl.getInstance().getEntranceSelectionManager()
                .computePlayerSelectedEntrancesLimit(getOwner());
        String maximumSelectedEntrancesString = maximumSelectedEntrances == Integer.MAX_VALUE ? "∞" :
                String.valueOf(maximumSelectedEntrances);
        return Map.ofEntries(
                Map.entry("%display_name%", displayName),
                Map.entry("%selected_entrances%", String.valueOf(protectedEntrances.size())),
                Map.entry("%maximum_selected_entrances%", maximumSelectedEntrancesString)
        );
    }
}

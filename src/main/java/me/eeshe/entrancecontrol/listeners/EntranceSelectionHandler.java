package me.eeshe.entrancecontrol.listeners;

import me.eeshe.entrancecontrol.EntranceControl;
import me.eeshe.entrancecontrol.inventories.EntranceSelectionManagerMenu;
import me.eeshe.entrancecontrol.inventories.holders.EntranceSelectionManagerMenuHolder;
import me.eeshe.entrancecontrol.inventories.holders.EntranceSelectionMembersMenuHolder;
import me.eeshe.entrancecontrol.inventories.holders.EntranceSelectionSettingsMenuHolder;
import me.eeshe.entrancecontrol.managers.EntranceSelectionManager;
import me.eeshe.entrancecontrol.models.EntranceSelection;
import me.eeshe.entrancecontrol.models.config.Message;
import me.eeshe.entrancecontrol.models.config.Sound;
import me.eeshe.penpenlib.models.Scheduler;
import me.eeshe.penpenlib.models.config.CommonSound;
import me.eeshe.penpenlib.models.config.MenuItem;
import me.eeshe.penpenlib.util.InputUtil;
import me.eeshe.penpenlib.util.LibMessager;
import me.eeshe.penpenlib.util.MenuUtil;
import me.eeshe.penpenlib.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Gate;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class EntranceSelectionHandler implements Listener {
    private final Map<UUID, EntranceSelection> selectionNameEditors = new HashMap<>();
    private final Map<UUID, EntranceSelection> selectionMemberAdders = new HashMap<>();
    private final Set<UUID> editSelectionEndConfirmations = new HashSet<>();
    private final Set<UUID> selectionDeleteConfirmations = new HashSet<>();
    private final Set<UUID> ownershipTransferConfirmations = new HashSet<>();
    private final Set<UUID> memberRemovers = new HashSet<>();

    private final EntranceControl plugin;

    public EntranceSelectionHandler(EntranceControl plugin) {
        this.plugin = plugin;
    }

    /**
     * Listens when a player interacts with an entrance and handles it.
     *
     * @param event PlayerInteractEvent.
     */
    @EventHandler
    public void onEntranceInteract(PlayerInteractEvent event) {
        if (event.useInteractedBlock() == Event.Result.DENY) return;
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;
        if (!(clickedBlock.getBlockData() instanceof Openable)) return;

        UUID playerUuid = event.getPlayer().getUniqueId();
        if (plugin.getEntranceIdentifiers().contains(playerUuid)) {
            handleEntranceIdentification(event);
            return;
        }
        if (plugin.getEntranceSelectionEditors().containsKey(playerUuid)) {
            // Player is editing an entrance selection
            handleEntranceSelectionEdit(event);
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        handleEntranceInteraction(event);
    }

    /**
     * Handles the identification of the clicked entrance.
     *
     * @param event PlayerInteractEvent.
     */
    private void handleEntranceIdentification(PlayerInteractEvent event) {
        event.setCancelled(true);

        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        EntranceSelection entranceSelection = EntranceSelection.fromLocation(clickedBlock.getLocation());
        if (entranceSelection == null) {
            Message.NOT_ENTRANCE_SELECTION.sendError(player);
            return;
        }
        Message.ENTRANCE_IDENTIFY.send(player, Sound.ENTRANCE_IDENTIFY, Map.ofEntries(
                Map.entry("%selection_name%", entranceSelection.getDisplayName()),
                Map.entry("%selection_owner%", entranceSelection.getOwner().getName())
        ));
    }

    /**
     * Handles the editing of the player's entrance selection,
     *
     * @param event PlayerInteractEvent
     */
    private void handleEntranceSelectionEdit(PlayerInteractEvent event) {
        event.setCancelled(true);

        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        EntranceSelection entranceSelection = plugin.getEntranceSelectionEditors().get(player.getUniqueId());
        Action action = event.getAction();
        Location clickedLocation = clickedBlock.getLocation();
        if (action == Action.RIGHT_CLICK_BLOCK) {
            if (EntranceSelection.fromLocation(clickedLocation) != null) {
                Message.ALREADY_SELECTED_ENTRANCE.sendError(player);
                return;
            }
            int playerMaximumSelectedEntrances = plugin.getEntranceSelectionManager().computePlayerSelectedEntrancesLimit(player);
            if (entranceSelection.getProtectedEntrances().size() >= playerMaximumSelectedEntrances) {
                Message.EXCEEDED_SELECTION_AMOUNT.sendError(player);
                return;
            }
            entranceSelection.addProtectedEntrance(player, clickedLocation);
            showSelectionActionBar(player, entranceSelection);
            Sound.SELECTION_ADD.play(player);
        } else {
            if (!entranceSelection.getProtectedEntrances().contains(clickedLocation)) return;

            entranceSelection.removeProtectedEntrance(clickedLocation);
            showSelectionActionBar(player, entranceSelection);
            Sound.SELECTION_REMOVE.play(player);
        }
    }

    /**
     * Shows the player's selection info action bar.
     *
     * @param player Player to show the action bar for.
     * @param entranceSelection EntranceSelection to show the action bar of.
     */
    private void showSelectionActionBar(Player player, EntranceSelection entranceSelection) {
        int playerMaximumSelectedEntrances = plugin.getEntranceSelectionManager().computePlayerSelectedEntrancesLimit(player);
        if (playerMaximumSelectedEntrances == Integer.MAX_VALUE) return;

        // Player has limited locked entrances, display how many they have left
        Message.SELECTED_ENTRANCES_ACTION_BAR.send(player, true, null, Map.ofEntries(
                Map.entry("%selected_entrances%", StringUtil.formatNumber(entranceSelection.getProtectedEntrances().size())),
                Map.entry("%maximum_entrances%", StringUtil.formatNumber(playerMaximumSelectedEntrances))
        ));
    }

    /**
     * Handles a right-click interaction with an entrance.
     *
     * @param event PlayerInteractEvent.
     */
    private void handleEntranceInteraction(PlayerInteractEvent event) {
        if (event.useInteractedBlock() == Event.Result.DENY) return;

        Block clickedBlock = event.getClickedBlock();
        EntranceSelection entranceSelection = EntranceSelection.fromLocation(event.getClickedBlock().getLocation());
        if (entranceSelection == null) return;

        Player player = event.getPlayer();
        if (!entranceSelection.canInteract(player)) {
            event.setCancelled(true);
            Message.NO_ENTRANCE_ACCESS.send(player, true, CommonSound.ERROR, new HashMap<>());
            return;
        }
        if (!entranceSelection.shouldSyncEntrances()) return;

        syncEntrances(entranceSelection, clickedBlock);
    }

    /**
     * Listens when an entrance is activated by a redstone signal and tries to sync its selection entrances.
     *
     * @param event BlockRedstoneEvent.
     */
    @EventHandler
    public void onEntranceRedstone(BlockRedstoneEvent event) {
        Block block = event.getBlock();
        EntranceSelection entranceSelection = EntranceSelection.fromLocation(block.getLocation());
        if (entranceSelection == null) return;
        if (!entranceSelection.shouldSyncEntrances()) return;

        syncEntrances(entranceSelection, event.getBlock());
    }

    /**
     * Syncs the passed EntranceSelection entrances based on the passed block's open state.
     *
     * @param entranceSelection EntranceSelection to sync.
     * @param block             Block to get the open state from.
     */
    private void syncEntrances(EntranceSelection entranceSelection, Block block) {
        Scheduler.runLater(plugin, () -> {
            BlockData blockData = block.getBlockData();
            BlockFace blockFace = blockData instanceof Gate gate ? gate.getFacing() : null;
            entranceSelection.setEntrancesOpenState(((Openable) blockData).isOpen(), blockFace);
        }, 0L);
    }

    /**
     * Listens when a player sneaks and attempts to end its entrance selection edit session.
     *
     * @param event PlayerToggleSneakEvent.
     */
    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        if (event.isCancelled()) return;
        if (!event.isSneaking()) return;

        Player player = event.getPlayer();
        UUID playerUuid = player.getUniqueId();
        if (!plugin.getEntranceSelectionEditors().containsKey(playerUuid)) return;
        if (InputUtil.askPlayerConfirmation(player, Message.ENTRANCE_SELECTION_END_CONFIRMATION, editSelectionEndConfirmations))
            return;

        editSelectionEndConfirmations.remove(playerUuid);
        EntranceSelection entranceSelection = plugin.getEntranceSelectionEditors().remove(playerUuid);
        Set<Location> protectedEntrances = entranceSelection.getProtectedEntrances();
        if (protectedEntrances.isEmpty()) {
            Message.NO_SELECTED_ENTRANCES.sendError(player);
            return;
        }
        double playerMaximumSelectionDistance = plugin.getEntranceSelectionManager().computePlayerMaximumSelectionDistance(player);
        if (findLargestSelectionDistance(protectedEntrances) > playerMaximumSelectionDistance) {
            Message.EXCEEDED_SELECTION_DISTANCE.sendError(player, Map.of("%maximum_distance%",
                    StringUtil.formatNumber(playerMaximumSelectionDistance)));
            return;
        }
        entranceSelection.register();
        entranceSelection.stopHighlightTask(player);
        Message.ENTRANCE_SELECTION_END.send(player, true, CommonSound.SUCCESS, new HashMap<>());
    }

    public double findLargestSelectionDistance(Set<Location> locations) {
        if (locations == null || locations.size() < 2) {
            return 0.0;
        }
        double maxDistance = 0.0;
        Location[] locArray = locations.toArray(new Location[0]);
        for (int i = 0; i < locArray.length - 1; i++) {
            for (int j = i + 1; j < locArray.length; j++) {
                double distance = locArray[i].distanceSquared(locArray[j]);
                if (distance <= maxDistance) continue;

                maxDistance = distance;
            }
        }
        return Math.sqrt(maxDistance);
    }

    @EventHandler
    public void onEntranceSelectionMenuClick(InventoryClickEvent event) {
        if (event.isCancelled()) return;
        InventoryHolder inventoryHolder = event.getInventory().getHolder();
        if (inventoryHolder instanceof EntranceSelectionManagerMenuHolder) {
            handleEntranceSelectionManagerMenu(event);
        } else if (inventoryHolder instanceof EntranceSelectionSettingsMenuHolder) {
            handleEntranceSelectionSettingsMenu(event);
        } else if (inventoryHolder instanceof EntranceSelectionMembersMenuHolder) {
            handleEntranceSelectionMembersMenu(event);
        }
    }

    /**
     * Listens to the clicks made in the EntranceSelectionManager menu and handles it.
     *
     * @param event InventoryClickEvent.
     */
    private void handleEntranceSelectionManagerMenu(InventoryClickEvent event) {
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        int currentPage = ((EntranceSelectionManagerMenuHolder) event.getInventory().getHolder()).currentPage();
        if (MenuUtil.handleBaseMenuActions(event, null,
                () -> player.openInventory(EntranceSelectionManagerMenu.create(player, currentPage - 1)),
                () -> player.openInventory(EntranceSelectionManagerMenu.create(player, currentPage + 1)))) {
            return;
        }
        EntranceSelection entranceSelection = EntranceSelection.fromItemStack(event.getCurrentItem());
        if (entranceSelection == null) return;

        player.openInventory(entranceSelection.createSettingsMenu());
        Sound.ENTRANCE_SELECTION_SETTINGS_MENU_OPEN.play(player);
    }

    /**
     * Listens to the clicks made in the EntranceSelectionSettings menu and handles it.
     *
     * @param event InventoryClickEvent.
     */
    private void handleEntranceSelectionSettingsMenu(InventoryClickEvent event) {
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        if (MenuUtil.handleBaseMenuActions(event,
                () -> player.openInventory(EntranceSelectionManagerMenu.create(player, 1)),
                null,
                null)) {
            return;
        }
        String menuAction = MenuItem.getMenuAction(event.getCurrentItem());
        if (menuAction == null) return;

        EntranceSelection entranceSelection = ((EntranceSelectionSettingsMenuHolder) event.getInventory().getHolder()).entranceSelection();
        switch (menuAction) {
            case "name" -> {
                selectionNameEditors.put(player.getUniqueId(), entranceSelection);
                player.closeInventory();
                Message.ENTRANCE_SELECTION_NAME_INSTRUCTIONS.send(player, CommonSound.INPUT_REQUEST);
            }
            case "entrance-sync" -> {
                entranceSelection.setSyncEntrances(!entranceSelection.shouldSyncEntrances());
                player.openInventory(entranceSelection.createSettingsMenu());
                (entranceSelection.shouldSyncEntrances() ? Sound.ENTRANCE_SYNC_ON : Sound.ENTRANCE_SYNC_OFF).play(player);
            }
            case "break-protection" -> {
                entranceSelection.setBreakProtection(!entranceSelection.hasBreakProtection());
                player.openInventory(entranceSelection.createSettingsMenu());
                (entranceSelection.hasBreakProtection() ? Sound.BREAK_PROTECTION_ON : Sound.BREAK_PROTECTION_OFF).play(player);
            }
            case "members" -> {
                player.openInventory(entranceSelection.createMembersMenu(1));
                Sound.ENTRANCE_SELECTION_MEMBERS_MENU_OPEN.play(player);
            }
            case "edit-selection" -> {
                plugin.getEntranceSelectionEditors().put(player.getUniqueId(), entranceSelection);
                entranceSelection.startHighlightTask(player);
                player.closeInventory();
                Message.ENTRANCE_SELECTION_INSTRUCTIONS.send(player, CommonSound.INPUT_REQUEST);
            }
            case "delete" -> {
                if (InputUtil.askPlayerConfirmation(player, Message.ENTRANCE_SELECTION_DELETE_CONFIRMATION, selectionDeleteConfirmations))
                    return;

                entranceSelection.unregister();
                player.openInventory(EntranceSelectionManagerMenu.create(player, 1));
                Sound.ENTRANCE_SELECTION_DELETE.play(player);
            }
        }
    }

    /**
     * Handles the clicks made in the EntranceSelectionMembersMenu and handles it.
     *
     * @param event InventoryClickEvent.
     */
    private void handleEntranceSelectionMembersMenu(InventoryClickEvent event) {
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        EntranceSelectionMembersMenuHolder menuHolder = (EntranceSelectionMembersMenuHolder) event.getInventory().getHolder();
        EntranceSelection entranceSelection = menuHolder.entranceSelection();
        int currentPage = menuHolder.currentPage();
        if (MenuUtil.handleBaseMenuActions(event,
                () -> player.openInventory(entranceSelection.createSettingsMenu()),
                () -> player.openInventory(entranceSelection.createMembersMenu(currentPage - 1)),
                () -> player.openInventory(entranceSelection.createMembersMenu(currentPage + 1)))) {
            return;
        }
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;
        String menuAction = MenuItem.getMenuAction(clickedItem);
        if (menuAction == null) {
            // Check if player clicked a member item
            if (clickedItem.getType() != Material.PLAYER_HEAD) return;
            OfflinePlayer offlineTarget = ((SkullMeta) clickedItem.getItemMeta()).getOwningPlayer();
            if (offlineTarget == null) return;
            if (event.getClick().isLeftClick()) {
                attemptOwnershipTransfer(player, entranceSelection, offlineTarget);
            } else if (event.getClick().isRightClick()) {
                attemptMemberRemove(player, entranceSelection, offlineTarget);
            }
        } else if (menuAction.equals("add-member")) {
            selectionMemberAdders.put(player.getUniqueId(), entranceSelection);
            Message.ENTRANCE_SELECTION_ADD_MEMBER_INSTRUCTIONS.send(player, CommonSound.INPUT_REQUEST);
            player.closeInventory();
        }
    }

    /**
     * Attempts to transfer ownership of the entrance selection to the passed player.
     *
     * @param player            Player transferring the ownership.
     * @param entranceSelection Entrance selection being transferred.
     * @param target            New owner of the entrance selection.
     */
    private void attemptOwnershipTransfer(Player player, EntranceSelection entranceSelection, OfflinePlayer target) {
        Player onlineTarget = target.getPlayer();
        if (onlineTarget == null) {
            Message.PLAYER_NOT_ONLINE.sendError(player, Map.of("%target%", target.getName()));
            return;
        }
        EntranceSelectionManager entranceSelectionManager = plugin.getEntranceSelectionManager();
        if (entranceSelectionManager.getPlayerEntranceSelectionAmount(target) > entranceSelectionManager.computePlayerEntranceSelectionLimit(onlineTarget)) {
            Message.EXCEEDED_SELECTION_LIMIT_OTHER.sendError(player, Map.of("%target%", onlineTarget.getName()));
            return;
        }
        if (InputUtil.askPlayerConfirmation(player, Message.ENTRANCE_SELECTION_TRANSFER_CONFIRMATION, ownershipTransferConfirmations)) {
            return;
        }
        entranceSelection.transferOwnership(target);
        Message.ENTRANCE_SELECTION_TRANSFER_SUCCESS_SELF.sendSuccess(player, Map.ofEntries(
                Map.entry("%display_name%", entranceSelection.getDisplayName()),
                Map.entry("%target%", onlineTarget.getName())
        ));
        Message.ENTRANCE_SELECTION_TRANSFER_SUCCESS_TARGET.sendSuccess(onlineTarget, Map.of(
                "%display_name%", entranceSelection.getDisplayName()
        ));
    }

    /**
     * Attempts to remove the passed player from the entrance selection.
     *
     * @param player            Player removing the member.
     * @param entranceSelection EntranceSelection the member is being removed from.
     * @param target            Player being removed.
     */
    private void attemptMemberRemove(Player player, EntranceSelection entranceSelection, OfflinePlayer target) {
        if (InputUtil.askPlayerConfirmation(player, Message.ENTRANCE_SELECTION_REMOVE_MEMBER_CONFIRMATION, memberRemovers)) {
            return;
        }
        entranceSelection.removeMember(target);
        player.openInventory(entranceSelection.createMembersMenu(1));
        Message.ENTRANCE_SELECTION_REMOVE_MEMBER_SUCCESS_SELF.sendSuccess(player, Map.ofEntries(
                Map.entry("%display_name%", entranceSelection.getDisplayName()),
                Map.entry("%target%", target.getName())
        ));
        if (target.isOnline()) {
            Message.ENTRANCE_SELECTION_REMOVE_MEMBER_SUCCESS_TARGET.sendError(target.getPlayer(), Map.of(
                    "%display_name%", entranceSelection.getDisplayName()
            ));
        }
    }

    /**
     * Listens when a player inputs a new member for an EntranceSelection and handles it.
     *
     * @param event AsyncPlayerChatEvent.
     */
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        UUID playerUuid = player.getUniqueId();
        String message = event.getMessage().trim();
        if (selectionNameEditors.containsKey(playerUuid)) {
            handleSelectionNameInput(player, message);
        } else if (selectionMemberAdders.containsKey(playerUuid)) {
            handleSelectionMemberAddInput(player, message);
        } else {
            return;
        }
        event.setCancelled(true);
    }

    /**
     * Handles the input of the name of an entrance selection.
     *
     * @param player Player making the input.
     * @param input  Input message.
     */
    private void handleSelectionNameInput(Player player, String input) {
        UUID playerUuid = player.getUniqueId();
        EntranceSelection entranceSelection = selectionNameEditors.get(playerUuid);
        if (InputUtil.attemptInputCancel(player, input, Message.ENTRANCE_SELECTION_ADD_MEMBER_CANCEL, selectionNameEditors,
                () -> MenuUtil.openSync(player, entranceSelection.createMembersMenu(1)))) {
            return;
        }
        String newSelectionName = input.trim();
        if (EntranceSelection.fromName(player, newSelectionName) != null) {
            Message.ALREADY_USED_ID.sendError(player, Map.of("%id%", newSelectionName));
            return;
        }
        selectionMemberAdders.remove(playerUuid);
        entranceSelection.setDisplayName(newSelectionName);
        MenuUtil.openSync(player, entranceSelection.createSettingsMenu());
        CommonSound.SUCCESS.play(player);
    }

    /**
     * Handles the input of a new member for an entrance selection.
     *
     * @param player Player making the input.
     * @param input  Input message.
     */
    private void handleSelectionMemberAddInput(Player player, String input) {
        UUID playerUuid = player.getUniqueId();
        EntranceSelection entranceSelection = selectionMemberAdders.get(playerUuid);
        if (InputUtil.attemptInputCancel(player, input, Message.ENTRANCE_SELECTION_ADD_MEMBER_CANCEL, selectionMemberAdders,
                () -> MenuUtil.openSync(player, entranceSelection.createMembersMenu(1)))) {
            return;
        }
        String targetName = input.trim();
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        if (!target.hasPlayedBefore()) {
            LibMessager.sendPlayerNotFoundMessage(player, targetName);
            return;
        }
        if (entranceSelection.isMember(target)) {
            Message.ALREADY_MEMBER.sendError(player, Map.of("%target%", targetName));
            return;
        }
        selectionMemberAdders.remove(playerUuid);
        entranceSelection.addMember(target);
        Message.ENTRANCE_SELECTION_ADD_MEMBER_SUCCESS_SELF.sendSuccess(player, Map.ofEntries(
                Map.entry("%display_name%", entranceSelection.getDisplayName()),
                Map.entry("%target%", targetName)
        ));
        if (target.isOnline()) {
            Message.ENTRANCE_SELECTION_ADD_MEMBER_SUCCESS_TARGET.sendSuccess(target.getPlayer(), Map.ofEntries(
                    Map.entry("%display_name%", entranceSelection.getDisplayName()),
                    Map.entry("%owner%", player.getName())
            ));
        }
        MenuUtil.openSync(player, entranceSelection.createMembersMenu(1));
    }

    /**
     * Listeners that handle the protection of EntranceSelections.
     */

    /**
     * Listens when a player breaks an entrance block and handles it.
     *
     * @param event BlockBreakEvent.
     */
    @EventHandler
    public void onEntranceBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        if (!plugin.getMainConfig().isBreakProtectionEnabled()) return;

        Block block = event.getBlock();
        EntranceSelection entranceSelection = EntranceSelection.fromLocation(block.getLocation());
        if (entranceSelection == null) return;
        if (!entranceSelection.hasBreakProtection()) return;
        Player player = event.getPlayer();
        if (entranceSelection.canInteract(player)) return;

        event.setCancelled(true);
        Message.PROTECTED_ENTRANCE_BREAK.send(player, true, CommonSound.ERROR, new HashMap<>());
    }

    /**
     * Listens when a block explodes an EntranceSelection block and handles it.
     *
     * @param event BlockExplodeEvent.
     */
    @EventHandler
    public void onEntranceBlockExplode(BlockExplodeEvent event) {
        if (event.isCancelled()) return;
        if (!plugin.getMainConfig().isBreakProtectionEnabled()) return;

        removeEntranceSelectionBlocks(event.blockList());
    }

    /**
     * Listens when an entity explodes an EntranceSelection block and handles it.
     *
     * @param event EntityExplodeEvent.
     */
    @EventHandler
    public void onEntranceEntityExplode(EntityExplodeEvent event) {
        if (event.isCancelled()) return;
        if (!plugin.getMainConfig().isBreakProtectionEnabled()) return;

        removeEntranceSelectionBlocks(event.blockList());
    }

    /**
     * Removes any EntranceSelection blocks found in the passed collection of blocks.
     * <p>
     * If the EntranceSelection doesn't have break protection, it won't be removed.
     *
     * @param blocks Collection of blocks.
     */
    private void removeEntranceSelectionBlocks(Collection<Block> blocks) {
        Iterator<Block> blockIterator = blocks.iterator();
        while (blockIterator.hasNext()) {
            Block block = blockIterator.next();
            EntranceSelection entranceSelection = EntranceSelection.fromLocation(block.getLocation());
            if (entranceSelection == null) continue;
            if (!entranceSelection.hasBreakProtection()) continue;

            blockIterator.remove();
        }
    }

    /**
     * Listens when a piston pushes a group of blocks and cancels it if an EntranceSelection is pushed.
     *
     * @param event BlockPistonExtendEvent.
     */
    @EventHandler
    public void onEntranceBlockPush(BlockPistonExtendEvent event) {
        if (event.isCancelled()) return;
        if (!plugin.getMainConfig().isBreakProtectionEnabled()) return;
        if (!containsEntranceSelection(event.getBlocks())) return;

        event.setCancelled(true);
    }

    /**
     * Listens when a piston retracts a group of blocks and cancels it if an EntranceSelection is retracted.
     *
     * @param event BlockPistonRetractEvent.
     */
    @EventHandler
    public void onEntranceBlockPull(BlockPistonRetractEvent event) {
        if (event.isCancelled()) return;
        if (!plugin.getMainConfig().isBreakProtectionEnabled()) return;
        if (!containsEntranceSelection(event.getBlocks())) return;

        event.setCancelled(true);
    }

    /**
     * Checks if the passed list of blocks contains an EntranceSelection with break protection enabled.
     *
     * @param blocks Blocks to check.
     * @return true if the list contains an EntranceSelection with break protection enabled, false otherwise.
     */
    private boolean containsEntranceSelection(Collection<Block> blocks) {
        for (Block block : blocks) {
            EntranceSelection entranceSelection = EntranceSelection.fromLocation(block.getLocation());
            if (entranceSelection == null) continue;
            if (!entranceSelection.hasBreakProtection()) continue;

            return true;
        }
        return false;
    }
}

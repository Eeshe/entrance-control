package me.eeshe.entrancecontrol.inventories;

import me.eeshe.entrancecontrol.EntranceControl;
import me.eeshe.entrancecontrol.inventories.holders.EntranceSelectionManagerMenuHolder;
import me.eeshe.entrancecontrol.models.EntranceSelection;
import me.eeshe.entrancecontrol.models.config.Menu;
import me.eeshe.penpenlib.models.config.ConfigMenu;
import me.eeshe.penpenlib.util.MenuUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.List;

public class EntranceSelectionManagerMenu {

    /**
     * Creates and returns an inventory with the EntranceSelectionManager menu.
     *
     * @param player The player to create the inventory for.
     * @param page   The page to create the inventory for.
     * @return The created inventory.
     */
    public static Inventory create(OfflinePlayer player, int page) {
        List<EntranceSelection> playerSelections = EntranceControl.getInstance().getEntranceSelectionManager()
                .getPlayerEntranceSelections(player);
        ConfigMenu configMenu = Menu.ENTRANCE_SELECTION_MANAGER.fetch();

        Inventory inventory = configMenu.createInventory(new EntranceSelectionManagerMenuHolder(page),
                playerSelections.size(), page, true, false, false, false, new HashMap<>());
        addEntranceSelectionItems(configMenu, inventory, playerSelections, page);
        MenuUtil.placeFillerItems(configMenu, inventory);

        return inventory;
    }

    private static void addEntranceSelectionItems(ConfigMenu configMenu, Inventory inventory,
                                                  List<EntranceSelection> playerSelections, int page) {
        boolean hasNextPage = false;
        for (int index = MenuUtil.computeInitialIndex(configMenu, page); index < playerSelections.size(); index++) {
            if (inventory.firstEmpty() == -1) {
                hasNextPage = true;
                break;
            }
            inventory.addItem(playerSelections.get(index).createListItemStack());
        }
        if (hasNextPage || page > 1) {
            MenuUtil.addPageItems(inventory, configMenu, page, hasNextPage);
        }
    }
}

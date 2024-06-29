package me.eeshe.entrancecontrol.inventories.holders;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public record EntranceSelectionManagerMenuHolder(int currentPage) implements InventoryHolder {

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}

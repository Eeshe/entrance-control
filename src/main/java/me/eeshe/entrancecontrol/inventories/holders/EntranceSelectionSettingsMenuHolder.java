package me.eeshe.entrancecontrol.inventories.holders;

import me.eeshe.entrancecontrol.models.EntranceSelection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public record EntranceSelectionSettingsMenuHolder(EntranceSelection entranceSelection) implements InventoryHolder {

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}

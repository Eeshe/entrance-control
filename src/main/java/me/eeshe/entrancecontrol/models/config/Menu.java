package me.eeshe.entrancecontrol.models.config;

import me.eeshe.entrancecontrol.EntranceControl;
import me.eeshe.penpenlib.files.config.ConfigWrapper;
import me.eeshe.penpenlib.models.config.MenuItem;
import me.eeshe.penpenlib.models.config.PenMenu;
import me.eeshe.penpenlib.util.ItemUtil;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Menu extends PenMenu {
    private static final List<PenMenu> MENUS = new ArrayList<>();
    private static final ConfigWrapper CONFIG_WRAPPER = new ConfigWrapper(EntranceControl.getInstance(), null, "menus.yml");

    public static final Menu ENTRANCE_SELECTION_MANAGER = new Menu("entrance-selection-manager", 0, "Entrance Selection Manager",
            Material.BLUE_STAINED_GLASS_PANE,
            List.of(-1),
            Material.BLACK_STAINED_GLASS_PANE,
            List.of(-1),
            new MenuItem("previous-page", ItemUtil.generateItemStack(
                    Material.ARROW,
                    "&7Previous Page",
                    List.of("&7Click to go to the previous page.")
            ), -9),
            new MenuItem("next-page", ItemUtil.generateItemStack(
                    Material.ARROW,
                    "&7Next Page",
                    List.of("&7Click to go to the next page.")
            ), -1),
            null,
            new ArrayList<>(),
            Map.ofEntries(
                    Map.entry("entrance-selection-item", Map.ofEntries(
                            Map.entry("item", Material.OAK_DOOR.name()),
                            Map.entry("name", "&7%display_name%"),
                            Map.entry("lore", List.of(
                                    "&3Entrances: &l%selected_entrances%/%maximum_selected_entrances%"
                            ))
                    ))
            )
    );

    public static final Menu ENTRANCE_SELECTION_SETTINGS = new Menu("entrance-selection-settings", 27, "%display_name%'s Settings",
            Material.BLUE_STAINED_GLASS_PANE,
            List.of(-1),
            Material.BLACK_STAINED_GLASS_PANE,
            List.of(-1),
            null,
            null,
            new MenuItem("back", ItemUtil.generateItemStack(
                    Material.RED_STAINED_GLASS_PANE,
                    "&cBack"
            ), 1),
            List.of(
                    new MenuItem("entrance-sync", ItemUtil.generateItemStack(
                            Material.OBSERVER,
                            "&eEntrance Sync",
                            List.of(
                                    "%entrance_sync%",
                                    "&7Click here to enable or disable entrance syncing."
                            )
                    ), 11),
                    new MenuItem("break-protection", ItemUtil.generateItemStack(
                            Material.GOLDEN_PICKAXE,
                            "&eBreak Protection",
                            List.of(
                                    "%break_protection%",
                                    "&7Click here to enable or disable break protection."
                            )
                    ), 12),
                    new MenuItem("members", ItemUtil.generateItemStack(
                            Material.PLAYER_HEAD,
                            "&eMembers",
                            List.of("&7Click here to manage members.")
                    ), 13),
                    new MenuItem("edit-selection", ItemUtil.generateItemStack(
                            Material.WRITABLE_BOOK,
                            "&eEdit Selection",
                            List.of("&7Click here to edit the selected entrances")
                    ), 14),
                    new MenuItem("delete", ItemUtil.generateItemStack(
                            Material.BARRIER,
                            "&cDelete"
                    ), -5)
            ),
            Map.ofEntries(
                    Map.entry("entrance-sync-status", Map.ofEntries(
                            Map.entry(true, "&aEnabled"),
                            Map.entry(false, "&cDisabled")
                    )),
                    Map.entry("break-protection-status", Map.ofEntries(
                            Map.entry(true, "&aEnabled"),
                            Map.entry(false, "&cDisabled")
                    ))
            )
    );

    public static final Menu ENTRANCE_SELECTION_MEMBERS = new Menu("entrance-selection-members", 0, "%display_name%'s Members",
            Material.BLUE_STAINED_GLASS_PANE,
            List.of(-1),
            Material.BLACK_STAINED_GLASS_PANE,
            List.of(-1),
            new MenuItem("previous-page", ItemUtil.generateItemStack(
                    Material.ARROW,
                    "&7Previous Page",
                    List.of("&7Click to go to the previous page.")
            ), -9),
            new MenuItem("next-page", ItemUtil.generateItemStack(
                    Material.ARROW,
                    "&7Next Page",
                    List.of("&7Click to go to the next page.")
            ), -1),
            new MenuItem("back", ItemUtil.generateItemStack(
                    Material.RED_STAINED_GLASS_PANE,
                    "&cBack"
            ), 1),
            List.of(
                    new MenuItem("add-member", ItemUtil.generateItemStack(
                            Material.PLAYER_HEAD,
                            "&eAdd Member",
                            List.of("&7Click here to add a member.")
                    ), -5)
            ),
            Map.ofEntries(
                    Map.entry("member-item", Map.ofEntries(
                            Map.entry("name", "&7%player_name%"),
                            Map.entry("lore", List.of(
                                    "&7Left click to transfer ownership of selection to player.",
                                    "&7Right click to remove player from selection."
                            ))
                    ))
            )
    );

    public Menu(String path, int defaultSize, String defaultTitle, Material defaultFrame,
                List<Integer> defaultFrameSlots, Material defaultFiller, List<Integer> defaultFillerSlots,
                MenuItem defaultPreviousPageItem, MenuItem defaultNextPageItem, MenuItem defaultBackItem,
                List<MenuItem> defaultMenuItems, Map<String, Object> additionalConfigs) {
        super(path, defaultSize, defaultTitle, defaultFrame, defaultFrameSlots, defaultFiller, defaultFillerSlots,
                defaultPreviousPageItem, defaultNextPageItem, defaultBackItem, defaultMenuItems, additionalConfigs);
    }

    public Menu() {
    }

    @Override
    public List<PenMenu> getMenus() {
        return MENUS;
    }

    @Override
    public ConfigWrapper getConfigWrapper() {
        return CONFIG_WRAPPER;
    }
}

package me.eeshe.entrancecontrol;

import me.eeshe.entrancecontrol.commands.CommandEntranceControl;
import me.eeshe.entrancecontrol.files.config.MainConfig;
import me.eeshe.entrancecontrol.listeners.EntranceSelectionHandler;
import me.eeshe.entrancecontrol.managers.EntranceSelectionManager;
import me.eeshe.entrancecontrol.models.EntranceSelection;
import me.eeshe.entrancecontrol.models.config.Menu;
import me.eeshe.entrancecontrol.models.config.Message;
import me.eeshe.entrancecontrol.models.config.Particle;
import me.eeshe.entrancecontrol.models.config.Sound;
import me.eeshe.penpenlib.PenPenLib;
import me.eeshe.penpenlib.PenPenPlugin;
import me.eeshe.penpenlib.files.config.ConfigWrapper;
import me.eeshe.penpenlib.managers.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class EntranceControl extends JavaPlugin implements PenPenPlugin {
    private final List<ConfigWrapper> configFiles = new ArrayList<>();
    private final List<DataManager> dataManagers = new ArrayList<>();

    private final Map<UUID, EntranceSelection> entranceSelectionEditors = new HashMap<>();

    private MainConfig mainConfig;

    private EntranceSelectionManager entranceSelectionManager;

    /**
     * Creates and returns a static instance of the Plugin's main class.
     *
     * @return Instance of the main class of the plugin.
     */
    public static EntranceControl getInstance() {
        return EntranceControl.getPlugin(EntranceControl.class);
    }

    @Override
    public void onEnable() {
        setupFiles();
        registerManagers();
        registerCommands();
        registerListeners();
        for (DataManager dataManager : dataManagers) {
            dataManager.onEnable();
        }
    }

    /**
     * Creates and configures all the config files of the plugin.
     */
    public void setupFiles() {
        configFiles.clear();

        this.mainConfig = new MainConfig(this);
        Message message = new Message();
        Sound sound = new Sound();
        Menu menu = new Menu();
        Particle particle = new Particle();
        configFiles.addAll(List.of(
                mainConfig,
                message.getConfigWrapper(),
                sound.getConfigWrapper(),
                menu.getConfigWrapper(),
                particle.getConfigWrapper()
        ));
        message.register();
        sound.register();
        menu.register();
        particle.register();
        for (ConfigWrapper configFile : configFiles) {
            configFile.writeDefaults();
        }
    }

    /**
     * Registers all the needed managers in order for the plugin to work.
     */
    private void registerManagers() {
        this.entranceSelectionManager = new EntranceSelectionManager(this);
        dataManagers.addAll(List.of(
                entranceSelectionManager
        ));
    }

    /**
     * Registers all the commands, subcommands, CommandExecutors and TabCompleters regarding the plugin.
     */
    private void registerCommands() {
        if (!(Bukkit.getPluginManager().getPlugin("PenPenLib") instanceof PenPenLib penPenLib)) return;

        penPenLib.registerCommands(List.of(
                new CommandEntranceControl(this)
        ));
    }

    /**
     * Registers all the event listeners the plugin might need.
     */
    private void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new EntranceSelectionHandler(this), this);
    }

    @Override
    public void onDisable() {
        for (DataManager dataManager : dataManagers) {
            dataManager.unload();
        }
    }

    @Override
    public void reload() {
        for (ConfigWrapper configFile : configFiles) {
            configFile.reloadConfig();
        }
        setupFiles();
        for (DataManager dataManager : dataManagers) {
            dataManager.reload();
        }
    }

    @Override
    public Plugin getSpigotPlugin() {
        return this;
    }

    public Map<UUID, EntranceSelection> getEntranceSelectionEditors() {
        return entranceSelectionEditors;
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }

    public EntranceSelectionManager getEntranceSelectionManager() {
        return entranceSelectionManager;
    }
}

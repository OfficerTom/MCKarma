package p.officertom.mck;

import p.officertom.mck.CustomEntity.CustomEntityRegistry;
import p.officertom.mck.CustomEntity.CustomVindicatorEntity;
import p.officertom.mck.CustomEntity.CustomVindicatorSpawnManager;
import p.officertom.mck.Managers.CommandManager;pu

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import p.officertom.mck.Managers.SettingsManager;

import java.io.File;

public class main extends JavaPlugin {

    private SettingsManager settingsManager;
    private CommandManager commandManager;

    private File configFile;
    private FileConfiguration config;

    private String[] commands = {"kreload", "ksave", "setkarma"};

    public void onEnable() {
        registerManagers();
        registerCommands();
        registerConfig();
        new JoinListener(this);
        new EntityDeathHandler(this);

        CustomEntityRegistry.registerCustomEntity(36, "Vindicator", CustomVindicatorEntity.class);

        karmaEventRunnable();
        spawnCustomVindicatorRunnable();
    }

    public void onDisable() {
        settingsManager.saveConfig();
    }

    private void registerManagers() {
        settingsManager = SettingsManager.getThisSettingsManager();
        settingsManager.setup(this);
        commandManager = new CommandManager(this);
    }

    private void registerCommands() {
        for (String thisCommand : commands)
            getCommand(thisCommand).setExecutor(commandManager);
    }

    private void registerConfig() {
        config = getConfig();
        config.options().copyDefaults(true);
        saveConfig();
        configFile = new File(getDataFolder(), "config.yml");
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public void karmaEventRunnable() {
        new BukkitRunnable() {

            @Override
            public void run() {
                KarmaEvent.doKarmaEventForAllPlayers();
            }
        }.runTaskTimer(this, 480, 240);
    }

    public void spawnCustomVindicatorRunnable() {
        new BukkitRunnable() {

            @Override
            public void run() {
                CustomVindicatorSpawnManager.trySpawnForAllPlayers();
            }
        }.runTaskTimer(this, 360, 240);
    }

}
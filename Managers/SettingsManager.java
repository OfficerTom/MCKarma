package p.officertom.mck.Managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

 /*
 Settings Manager Utility Class
 Author: OfficerTom
 Version: 1.0
 Description: Manages the config file and provides hook into plugin settings and data for entire project
 Local Dependency: none
 */

public class SettingsManager {

    static SettingsManager thisSettingsManager = new SettingsManager();
    Plugin plugin;
    File configFile;
    FileConfiguration config;
    //Config Settings
    private String pluginTag;
    private boolean debugEnabled, resetKarmaOnDeath;
    private int minKarma, maxKarma, defaultKarma;
    private int killMobKarma, killAnimalKarma, killLeashedAnimalKarma, killPlayerKarma;
    //Data
    private HashMap<UUID, Integer> playerKarmas = new HashMap<>();

    private SettingsManager() {

    }

    public static SettingsManager getThisSettingsManager() {
        return thisSettingsManager;
    }

    public String getPluginTag() {
        return pluginTag;
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public boolean isResetKarmaOnDeath() {
        return resetKarmaOnDeath;
    }

    public int getPlayerKarma(Player player) {
        UUID thisUUID = player.getUniqueId();

        if (playerKarmas.containsKey(thisUUID))
            return playerKarmas.get(thisUUID);

        playerKarmas.put(thisUUID, defaultKarma);
        config.set(getKarmaPath(thisUUID), defaultKarma);
        return defaultKarma;
    }

    public int getKillMobKarma() {
        return killMobKarma;
    }

    public int getKillAnimalKarma() {
        return killAnimalKarma;
    }

    public int getKillLeashedAnimalKarma() {
        return killLeashedAnimalKarma;
    }

    public int getDefaultKarma() {
        return defaultKarma;
    }

    public int getKillPlayerKarma() {
        return killPlayerKarma;
    }

    public int getMinKarma() {
        return minKarma;
    }

    public int getMaxKarma() {
        return maxKarma;
    }

    public void setup(Plugin p) {
        plugin = p;
        config = plugin.getConfig();
        config.options().copyDefaults(true);
        configFile = new File(plugin.getDataFolder(), "config.yml");
        loadVars(true, false);
        saveConfig();
    }

    public void reloadConfig(boolean settings, boolean playerData) {
        config = YamlConfiguration.loadConfiguration(configFile);
        loadVars(settings, playerData);
        MessageManager.debugConsoleMessageWithTag("Settings reloaded from config.yml!");
    }

    public void saveConfig() {
        config.set("settings.plugin-tag", pluginTag);
        config.set("settings.enable-debug", debugEnabled);
        config.set("settings.min-karma", minKarma);
        config.set("settings.max-karma", maxKarma);
        config.set("settings.default-karma", defaultKarma);
        config.set("settings.kill-mob-karma", killMobKarma);
        config.set("settings.kill-animal-karma", killAnimalKarma);
        config.set("settings.kill-leashedanimal-karma", killLeashedAnimalKarma);
        config.set("settings.kill-player-karma", killPlayerKarma);
        config.set("settings.reset-karma-on-death", resetKarmaOnDeath);

        if (playerKarmas != null)
            for (Map.Entry<UUID, Integer> entry : playerKarmas.entrySet()) {
                config.set(getKarmaPath(entry.getKey()), entry.getValue());
            }

        try {
            config.save(configFile);
        } catch (IOException e) {
            MessageManager.consoleMessageWithTag("Error saving config file. Try reinstalling the plugin.");
        }
    }

    private void loadVars(boolean loadSettings, boolean loadPlayerData) {
        if (loadSettings) {
            pluginTag = config.getString("settings.plugin-tag");
            debugEnabled = config.getBoolean("settings.enable-debug");

            minKarma = config.getInt("settings.min-karma");
            maxKarma = config.getInt("settings.max-karma");
            if (maxKarma <= minKarma)
                maxKarma = minKarma + 1; //if max = min, it may try to divide by zero
            defaultKarma = boundKarma(config.getInt("settings.default-karma")); //bound to mix/max in case config is written incorrectly

            killMobKarma = config.getInt("settings.kill-mob-karma");
            killAnimalKarma = config.getInt("settings.kill-animal-karma");
            killLeashedAnimalKarma = config.getInt("settings.kill-leashedanimal-karma");
            killPlayerKarma = config.getInt("settings.kill-player-karma");
            resetKarmaOnDeath = config.getBoolean("settings.reset-karma-on-death");
        }

        if (loadPlayerData) {
            playerKarmas.clear();
            plugin.getServer().getOnlinePlayers().forEach(player -> loadPlayerData(player));
        }
    }

    public boolean loadPlayerData(Player player) {
        return loadPlayerData(player, false);
    }

    public boolean loadPlayerData(Player player, boolean writeDefault) {
        UUID thisUUID = player.getUniqueId();
        if (playerKarmas == null || !playerKarmas.containsKey(thisUUID)) {
            int amount;
            if (!writeDefault && config.contains(getKarmaPath(thisUUID)))
                amount = config.getInt(getKarmaPath(thisUUID));
            else
                amount = defaultKarma;

            playerKarmas.put(thisUUID, amount);
            aScoreboardManager.sendScoreboard(player, amount);

            return true;
        }
        aScoreboardManager.sendScoreboard(player, getPlayerKarma(player));
        return false;
    }

    public boolean savePlayerData(Player player) {
        UUID thisUUID = player.getUniqueId();
        if (!playerKarmas.containsKey(thisUUID)) {
            config.set(getKarmaPath(thisUUID), defaultKarma);
            return false;
        } else
            config.set(getKarmaPath(thisUUID), boundKarma(playerKarmas.get(thisUUID)));
        return true;
    }

    public void setPlayerKarma(Player player, int amount) {
        int boundedAmount = boundKarma(amount);
        if (amount != boundedAmount)
            MessageManager.debugConsoleMessageWithTag("&4Setkarma received out of bound amount (" + amount + "), passing (" + boundedAmount + ") instead!");

        playerKarmas.put(player.getUniqueId(), boundedAmount);
        aScoreboardManager.sendScoreboard(player, boundedAmount);
    }

    public void resetPlayerKarma(Player player) {
        playerKarmas.put(player.getUniqueId(), defaultKarma);
        aScoreboardManager.sendScoreboard(player, defaultKarma);
    }

    public void addPlayerKarma(Player player, int amount) {
        setPlayerKarma(player, getPlayerKarma(player) + amount);
    }

    private String getKarmaPath(UUID thisUUID) {
        return "data.player." + thisUUID.toString() + ".karma";
    }

    private int boundKarma(int karma) {
        karma = (karma > maxKarma) ? maxKarma : karma;
        karma = (karma < minKarma) ? minKarma : karma;
        return karma;
    }
}

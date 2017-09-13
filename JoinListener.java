package p.officertom.mck;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import p.officertom.mck.Managers.MessageManager;
import p.officertom.mck.Managers.SettingsManager;
import p.officertom.mck.Managers.aScoreboardManager;

/*
 Join Listener
 Author: OfficerTom
 Description: Initializes player data and scoreboards on player join, saves data on player quit
 Local Dependency: aScoreboardManager, SettingsManager
 */

public class JoinListener implements Listener {

    private static SettingsManager settingsManager = SettingsManager.getThisSettingsManager();
    private main plugin;

    public JoinListener(main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        aScoreboardManager.sendScoreboard(player, settingsManager.getPlayerKarma(player));

        settingsManager.loadPlayerData(player,
                !(player.hasPlayedBefore())); //Load default karma if it's their first time
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!settingsManager.savePlayerData(player))
            MessageManager.debugConsoleMessageWithTag("Player karma wasn't found - saving default");
    }
}

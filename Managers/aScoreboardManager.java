package p.officertom.mck.Managers;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.HashMap;

/*
 Scoreboard Manager
 Author: OfficerTom
 Description: Creates a scoreboard and pushes it to client
 Local Dependency: SettingsManager
 */

public class aScoreboardManager {

    private static SettingsManager settingsManager = SettingsManager.getThisSettingsManager();

    private static ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
    private static Scoreboard scoreboard;
    private static Objective objective;
    private static HashMap<OfflinePlayer, Scoreboard> scoreboards = new HashMap<>();


    public static void sendScoreboard(Player player, int karma) {
        //Create new scoreboard with title
        scoreboard = scoreboardManager.getNewScoreboard();
        objective = scoreboard.registerNewObjective("test", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("Karma");

        //Set correct score and push to player
        objective.getScore(KarmaChangeManager.getKarmaLevel(karma)).setScore(karma);
        player.setScoreboard(scoreboard);
    }
}

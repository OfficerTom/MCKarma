package p.officertom.mck.Managers;

import org.bukkit.ChatColor;
import org.bukkit.entity.*;

/*
 Custom Vindicator
 Author: OfficerTom
 Description: Handles changing of karma external from SettingsManager
              Also provides other classes with the Karma Level
 Local Dependency: SettingsManager
 */

public class KarmaChangeManager {

    private static SettingsManager settingsManager = SettingsManager.getThisSettingsManager();

    private static String[] karmalevels = {ChatColor.DARK_RED + "Fugitive", ChatColor.RED + "Lawbreaker", ChatColor.YELLOW + "Citizen", ChatColor.GREEN + "Upstanding", ChatColor.DARK_GREEN + "Samaritan"};

    public static void killedEntity(Player player, LivingEntity entity) {
        if (entity instanceof Monster) {
            addKarmaWeighted(player, settingsManager.getKillMobKarma());
        } else if (entity instanceof Animals && (!(entity instanceof Wolf && ((Wolf) entity).isAngry()))) {
            //See if entity was leashed and determine owner
            try {
                if (entity.getLeashHolder().equals(player))
                    addKarmaWeighted(player, settingsManager.getKillAnimalKarma());
                else
                    addKarmaWeighted(player, settingsManager.getKillLeashedAnimalKarma());
            } catch (IllegalStateException e) {
                addKarmaWeighted(player, settingsManager.getKillAnimalKarma());
            }

        } else if (entity instanceof Player)
            if (settingsManager.getPlayerKarma(player) < settingsManager.getPlayerKarma((Player) entity)) //Only lose karma for killing players with higher karma
                addKarmaWeighted(player, settingsManager.getKillPlayerKarma());
    }

    private static void addKarmaWeighted(Player player, int amount) {
        if (amount < 0) //catch incorrect method usage
            removeKarmaWeighted(player, -amount);
        else {
            int previousKarma = settingsManager.getPlayerKarma(player);

            //Add less karma if player's karma is already high
            amount = (int) (amount * ((settingsManager.getMaxKarma() - (previousKarma / 4.0)) / settingsManager.getMaxKarma()));
            MessageManager.debugConsoleMessageWithTag(player.getName() + " killed a mob for " + amount + "karma");
            settingsManager.addPlayerKarma(player, amount);
            MessageManager.debugConsoleMessageWithTag("&fChanged &e" + player.getName() + "&f's Karma from &8" + previousKarma + " &fto &a" + (previousKarma + amount) + "!");
        }
    }

    private static void removeKarmaWeighted(Player player, int amount) {
        if (amount < 0) //catch incorrect method usage
            addKarmaWeighted(player, -amount);
        else {
            int previousKarma = settingsManager.getPlayerKarma(player);

            //Remove more karma if player's karma is already high
            amount = (int) ((-amount * ((settingsManager.getMaxKarma() - (previousKarma / 2.0)) / settingsManager.getMaxKarma())) + 2.0 * amount);
            MessageManager.debugConsoleMessageWithTag(player.getName() + " killed an animal or player for -" + amount + "karma");
            settingsManager.addPlayerKarma(player, -amount);
            MessageManager.debugConsoleMessageWithTag("&fChanged &e" + player.getName() + "&f's Karma from &8" + previousKarma + " &fto &a" + (previousKarma - amount) + "!");
        }
    }

    public static int getKarmaLevelNumber(int karma) {
        //Returns 1 - 5 based on which interval
        double range = settingsManager.getMaxKarma() - settingsManager.getMinKarma();
        double interval = range / karmalevels.length;
        return Math.min((int) Math.floor((karma - settingsManager.getMinKarma()) / interval), 4) + 1;
    }

    public static String getKarmaLevel(int karma) {
        //Returns the display name for Karma Level (for player reference)
        double range = settingsManager.getMaxKarma() - settingsManager.getMinKarma();
        double interval = range / karmalevels.length;
        int thisLevel = Math.min((int) Math.floor((karma - settingsManager.getMinKarma()) / interval), 4); //Take min so 100% karma doesn't go out of bounds

        return karmalevels[thisLevel];
    }
}

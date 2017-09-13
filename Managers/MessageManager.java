package p.officertom.mck.Managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

 /*
 Message Manager Utility Class
 Author: OfficerTom
 Version: 2.1
 Description: Provides utility methods for sending message(s) to player / console
 Dependency: SettingsManager class with methods getPluginTag() , isDebugEnabled
 Changes:
    2.0 - removed overloaded methods with incorrect orders
        - added addPluginTag method
        - added formatColor method
        - added WithTag methods for each message type
        - added settings and SettingsManager integration

    2.1 - added debugConsoleMessage which only sends if debug is enabled
        - added overloaded setup method for passing main object
        - removed setup method, now references SettingsManager directly
 */

public class MessageManager {

    private static SettingsManager settingsManager = SettingsManager.getThisSettingsManager();

    public static boolean globalMessage(String string) {
        if (Bukkit.getServer().getOnlinePlayers().size() == 0) {
            debugConsoleMessageWithTag("No players online - message not sent.");
            return false;
        }

        Bukkit.broadcastMessage(formatColor(string));
        if (settingsManager.isDebugEnabled())
            consoleMessageWithTag("Message sent globally to " + Bukkit.getServer().getOnlinePlayers().size() + " players!");
        return true;
    }

    public static void consoleMessageWithTag(String string) {
        consoleMessage(addPluginTag(string));
    }

    public static void consoleMessage(String string) {
        Bukkit.getServer().getConsoleSender().sendMessage(formatColor(string));
    }

    public static boolean debugConsoleMessage(String string) {
        if (settingsManager.isDebugEnabled())
            consoleMessage(string);
        else
            return false;
        return true;
    }

    public static boolean debugConsoleMessageWithTag(String string) {
        if (settingsManager.isDebugEnabled())
            consoleMessage(addPluginTag(string));
        else
            return false;
        return true;
    }

    public static boolean localMessageWithTag(Player player, String string) {
        return localMessage(player, addPluginTag(string));
    }

    public static boolean localMessage(Player player, String string) {
        return localMessage(player, string, false);
    }

    public static boolean localMessage(Player player, String string, boolean silent) {
        if (!Bukkit.getServer().getOnlinePlayers().contains(player)) {
            if (settingsManager.isDebugEnabled() && !silent)
                consoleMessageWithTag("Player " + player.getName() + " not found.");
            return false;
        }

        player.sendMessage(formatColor(string));
        return true;
    }

    public static boolean localMessagesWithTags(Player player, String[] strings) {
        for (int i = 0; i < strings.length; i++)
            strings[i] = addPluginTag(strings[i]);

        return localMessages(player, strings);
    }

    public static boolean localMessages(Player player, String[] strings) {
        boolean returnVal = true;

        for (String message : strings) {
            if (!localMessage(player, message, true)) {
                returnVal = false;
            }
        }

        if (!returnVal && settingsManager.isDebugEnabled())
            consoleMessageWithTag("Player " + player.getName() + " not found.");

        return returnVal;
    }

    private static String addPluginTag(String string) {
        String tag = settingsManager.getPluginTag();

        if (!string.contains(tag)) {
            if (string.isEmpty())
                string = " default message";

            if (!string.startsWith(" "))
                string = " " + string;

            string = tag.concat(string);
        }
        return string;
    }

    private static String formatColor(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

}

package p.officertom.mck.Managers;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import p.officertom.mck.main;

import java.util.Arrays;

 /*
 Command Manager
 Author: OfficerTom
 Description: Handles Commands for the plugin. Permissions are OP only.
 Local Dependency: SettingsManager, MessageManager,
 */

public class CommandManager implements CommandExecutor {
    private main plugin;

    public CommandManager(main plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("kreload")) {
            if (args.length == 1) {
                String type = args[0];
                String[] validTypes = {"all", "settings", "data"};
                if (Arrays.asList(validTypes).contains(type)) {
                    if (sender.isOp()) {
                        switch (type) {
                            case "settings":
                                plugin.getSettingsManager().reloadConfig(true, false);
                                break;
                            case "data":
                                plugin.getSettingsManager().reloadConfig(false, true);
                                break;
                            default:
                                plugin.getSettingsManager().reloadConfig(true, true);
                                break;
                        }
                        MessageManager.localMessageWithTag((Player) sender, "Reload successful!");
                    } else {
                        MessageManager.localMessageWithTag((Player) sender, "You need to be an &cOP &fto reload the config");
                        MessageManager.debugConsoleMessageWithTag("Player " + sender.getName() + " attempted to reload " + type + " but is not op.");
                    }
                }
            } else
                MessageManager.localMessageWithTag((Player) sender, "invalid arguments - use /kreload [all/settings/data]");
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("ksave")) {
            if (sender.isOp())
                doSave((Player) sender);
            else {
                MessageManager.localMessageWithTag((Player) sender, "You need to be an &cOP &fto save to config");
                MessageManager.debugConsoleMessageWithTag("Player " + sender.getName() + " attempted to save to config but is not op.");
            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("setkarma")) {
            Player player = Bukkit.getPlayer(args[0]);
            if (player != null && StringUtils.isNumericSpace(args[1])) {
                if (sender.isOp())
                    plugin.getSettingsManager().setPlayerKarma(Bukkit.getPlayer(args[0]), Integer.parseInt(args[1]));
                else {
                    MessageManager.localMessageWithTag((Player) sender, "You need to be an &cOP &fto set a player's karma");
                    MessageManager.debugConsoleMessageWithTag("Player " + sender.getName() + " attempted to set " + player + "'s karma but is not op.");
                }
            } else
                MessageManager.localMessageWithTag((Player) sender, "invalid arguments - use /setkarma [name] [amount]");
            return true;
        }

        return false;
    }

    private void doSave(Player player) {
        plugin.getSettingsManager().saveConfig();

        //Send local notice and console message if debug is enabled
        //Or force console message if local message fails
        if (MessageManager.localMessageWithTag(player, "settings were saved successfully!"))
            MessageManager.debugConsoleMessageWithTag("settings saved!");
        else
            MessageManager.consoleMessageWithTag("settings saved!");
    }
}

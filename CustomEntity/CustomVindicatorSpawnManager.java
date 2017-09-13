package p.officertom.mck.CustomEntity;

import net.minecraft.server.v1_12_R1.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;
import p.officertom.mck.Managers.MessageManager;
import p.officertom.mck.Managers.SettingsManager;

/*
 (NPC) Spawn Manager
 Author: OfficerTom
 Description: Randomly spawns a CustomVindicatorEntity within the player's view
 Local Dependency: CustomVindicatorEntity, SettingsManager
 */

public class CustomVindicatorSpawnManager {

    private static SettingsManager settingsManager = SettingsManager.getThisSettingsManager();

    public static void trySpawnForAllPlayers() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            //Check if player's karma is lawbreaker / fugitive
            int currKarmaInRange = settingsManager.getPlayerKarma(player) - settingsManager.getMinKarma();
            double unLawfulRange = (2.0 * (settingsManager.getMaxKarma() - settingsManager.getMinKarma())) / 5;

            //Determine if the player should receive a CustomVindicator spawn - lower karma = much higher chances
            if (currKarmaInRange <= unLawfulRange)
                if (randomDouble(100) <= (8.0) * (2.0 + 8.0 * ((unLawfulRange - currKarmaInRange) / unLawfulRange))) //increased chance for an event based on how far your karma is from the median value
                    spawnInPlayerPath(player, randomInt(10) + 8);
        }
    }

    private static void spawnInPlayerPath(Player player, int distance) {
        double yaw = 90 + player.getLocation().getYaw(); // Fit to middle of screen
        double x = (distance * Math.cos(Math.toRadians(yaw))) + player.getLocation().getX();
        double z = (distance * Math.sin(Math.toRadians(yaw))) + player.getLocation().getZ();

        spawnCustomVindicator(findSafeLocation(player, (int) x, (int) z));
    }

    private static void spawnCustomVindicator(Location location) {
        //Adds a CustomVindicatorEntity to world and sets its location
        World world = ((CraftWorld) location.getWorld()).getHandle();

        CustomVindicatorEntity customVindicatorEntity = new CustomVindicatorEntity(world);
        customVindicatorEntity.setPosition(location.getX(), location.getY(), location.getZ());
        world.addEntity(customVindicatorEntity);

        MessageManager.debugConsoleMessageWithTag("Spawned a CustomVindicatorEntity at (" +
                location.getX() + ", " + location.getY() + ", " + location.getZ() + ") in world " + location.getWorld().getName());
    }

    private static Location findSafeLocation(Player player, int x, int z) {
        //Finds the highest non-air block and returns the location above it
        org.bukkit.World world = player.getLocation().getWorld();
        int y = player.getLocation().getBlockY() + 60;

        while (y > 0) {
            if (world.getBlockAt(x, y, z).isEmpty())
                y--;
            else
                return new Location(world, x, y + 1, z);
        }

        return new Location(world, x, player.getLocation().getBlockY(), z);
    }

    private static double randomDouble(double max) {
        return (Math.random() * max);
    }

    private static int randomInt(int max) {
        int range = (max - 1) + 1;
        return (int) (Math.random() * range) + 1;
    }
}

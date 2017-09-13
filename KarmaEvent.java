package p.officertom.mck;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import p.officertom.mck.Managers.KarmaChangeManager;
import p.officertom.mck.Managers.MessageManager;
import p.officertom.mck.Managers.SettingsManager;

/*
 Karma Event
 Author: OfficerTom
 Description: Does some good/bad action to player depending on their karma
 Local Dependency: SettingsManager, MessageManager
 */

public class KarmaEvent {

    private static SettingsManager settingsManager = SettingsManager.getThisSettingsManager();

    public static void doKarmaEventForAllPlayers() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            int currKarma = settingsManager.getPlayerKarma(player);
            int range = settingsManager.getMaxKarma() - settingsManager.getMinKarma();
            int medium = ((range / 2) + settingsManager.getMinKarma());

            //Higher chance for event depending on how good/bad player's karma is
            //Medium karma would have 8% chance, max/min has 24% chance
            if (randomDouble(100) <= (16.0) * (0.5 + (Math.abs(medium - currKarma) / (range / 2.0)))) //increased chance for an event based on how far your karma is from the median value
            {
                doRandomEvent(player, currKarma);

                //Karma moves closer to neutral to account for event
                if(currKarma < medium)
                    settingsManager.addPlayerKarma(player, 100);
                else if(currKarma > medium)
                    settingsManager.addPlayerKarma(player, -100);
            }

            if (currKarma < settingsManager.getDefaultKarma())
                settingsManager.addPlayerKarma(player, 10);
        }
    }

    @SuppressWarnings("deprecation")
    public static void doRandomEvent(Player player, int karma) {
        int karmaLevel = KarmaChangeManager.getKarmaLevelNumber(karma);
        if ((karmaLevel < 3) || ((karmaLevel == 3) && (randomDouble(100) <= 50))) {
            switch ((int) (Math.random() * 6) + 1) {
                case 1:
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 300, 1));
                    MessageManager.localMessageWithTag(player, "Looks like you've lost your glasses. Probably deserved it.");
                    break;
                case 2:
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 600, 3));
                    MessageManager.localMessageWithTag(player, "Does your leg feel alright? Probably deserved it.");
                    break;
                case 3:
                    dropHandItem(player);
                    MessageManager.localMessageWithTag(player, "Is your hand working today? Probably deserved it.");
                    break;
                case 4:
                    player.kickPlayer("Fatal Error: 785(Loss of Power) - Is your computer turned on?");
                    break;
                case 5:
                    player.setFireTicks(randomInt(100) + 100);
                    MessageManager.localMessageWithTag(player, "Hot day today, isn't it? Probably deserved it.");
                    break;
                default:
                    player.getLocation().getWorld().playEffect(player.getLocation(), Effect.EXPLOSION_HUGE, 1);
                    player.getLocation().getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_FIREBALL_EXPLODE, 1.0F, 0.0F);
                    MessageManager.localMessageWithTag(player, "That was a landmine. Probably deserved it.");
                    break;
            }
        } else {
            switch ((int) (Math.random() * 5) + 1) {
                case 1:
                    giveFood(player);
                    MessageManager.localMessageWithTag(player, "You found some food. Couldn't be a coincidence.");
                    break;
                case 2:
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, randomInt(600) + 600, 2));
                    MessageManager.localMessageWithTag(player, "The doctor said you're healthier than ever. Couldn't be a coincidence.");
                    break;
                case 3:
                    giveDiamond(player);
                    MessageManager.localMessageWithTag(player, "Wow, a diamond! Couldn't be a coincidence.");
                    break;
                case 4:
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 900, 3));
                    MessageManager.localMessageWithTag(player, "Time seems to slow around you. Couldn't be a coincidence.");
                    break;
                default:
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1200, 1));
                    MessageManager.localMessageWithTag(player, "You suddenly feel invincible. Couldn't be a coincidence.");
                    break;
            }
        }
    }

    private static void giveFood(Player player) {
        Material item;
        int amount = 1;
        short damage = 0;
        double random = randomDouble(100);
        if (random <= 30) {
            item = Material.GOLDEN_APPLE;
            if (random <= 15)
                damage = 1;
        } else if (random <= 60)
            item = Material.GOLDEN_CARROT;
        else {
            Material[] meats = {Material.COOKED_BEEF, Material.COOKED_MUTTON, Material.GRILLED_PORK};
            item = meats[randomInt(3) - 1];
            amount = randomInt(3) + 1;
        }

        ItemStack thisFood = new ItemStack(item, amount, damage);

        PlayerInventory playerInventory = player.getInventory();

        if (player.getInventory().firstEmpty() >= 0)
            playerInventory.addItem(thisFood);
        else
            player.getWorld().dropItem(player.getLocation(), thisFood);

    }

    private static void giveDiamond(Player player) {
        ItemStack diamond = new ItemStack(Material.DIAMOND, 1);
        PlayerInventory playerInventory = player.getInventory();

        if (player.getInventory().firstEmpty() >= 0)
            playerInventory.addItem(diamond);
        else
            player.getWorld().dropItem(player.getLocation(), diamond);
    }

    private static void dropHandItem(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        ItemStack heldItem = playerInventory.getItemInMainHand();

        playerInventory.removeItem(heldItem);
        player.getWorld().dropItem(player.getLocation(), heldItem);
    }

    private static int randomInt(int max) {
        int range = (max - 1) + 1;
        return (int) (Math.random() * range) + 1;
    }

    private static double randomDouble(double max) {
        return (Math.random() * max);
    }

}

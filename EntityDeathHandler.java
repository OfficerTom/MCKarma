package p.officertom.mck;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import p.officertom.mck.Managers.KarmaChangeManager;

/*
 Death Listener
 Author: OfficerTom
 Description: Tells the karma manager to update killing player's karma
 Local Dependency: KarmaChangeManager, SettingsManager
 */

public class EntityDeathHandler implements Listener {
    private main plugin;

    public EntityDeathHandler(main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void entityDeath(EntityDeathEvent event) {

        LivingEntity deadEntity = event.getEntity();
        Player player = deadEntity.getKiller();
        if (player != null) //Make sure entity wasn't killed naturally
            KarmaChangeManager.killedEntity(player, deadEntity);

        //If death is a player death, reset that player's karma if the setting is enabled
        if (deadEntity instanceof Player && plugin.getSettingsManager().isResetKarmaOnDeath())
            plugin.getSettingsManager().resetPlayerKarma((Player) deadEntity);

    }
}

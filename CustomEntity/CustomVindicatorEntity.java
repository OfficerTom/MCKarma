package p.officertom.mck.CustomEntity;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/*
 Custom Vindicator
 Author: OfficerTom
 Description: Custom Vindicator with name 'Police Officer'
 Local Dependency: none
 */

public class CustomVindicatorEntity extends EntityVindicator {

    public CustomVindicatorEntity(World world) {
        super(world);

        org.bukkit.inventory.ItemStack helmet = new org.bukkit.inventory.ItemStack(org.bukkit.Material.LEATHER_HELMET);
        LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
        helmetMeta.setColor(Color.BLUE);
        helmet.setItemMeta(helmetMeta);

        ItemStack nmsHelmet = CraftItemStack.asNMSCopy(helmet);

        this.setEquipment(EnumItemSlot.HEAD, nmsHelmet);
        this.setCustomName(ChatColor.BLUE + "Police Officer");
        this.setCustomNameVisible(true);

        this.targetSelector.a(new PathfinderGoalNearestAttackableTarget<>(this, EntityPlayer.class, true));

        this.getWorld().addEntity(this);
    }
}

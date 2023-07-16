package betterwithmods.module.tweaks;

import betterwithmods.module.Feature;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

/**
 * Created by primetoxinz on 4/20/17.
 */
public class EquipmentDrop extends Feature {

    /*@SubscribeEvent
    public void setDropChance(EntityJoinWorldEvent e) {
        if (e.getEntity() instanceof EntityLiving) {
            EntityLiving entity = (EntityLiving) e.getEntity();
            if (entity instanceof EntityZombie) {
                for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
                    entity.setDropChance(slot, 1.1f);
                }
            }
        }
    }*/

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntityDrop(LivingDropsEvent e) {
        if (e.getEntity() instanceof EntityLiving) {
            EntityLiving entity = (EntityLiving) e.getEntity();
            if (entity instanceof EntityZombie) {
                for (EntityEquipmentSlot entityequipmentslot : EntityEquipmentSlot.values())
                {
                    boolean alreadyAdded = false;

                    ItemStack itemstack = entity.getItemStackFromSlot(entityequipmentslot);
                    for (EntityItem item : e.getDrops()) {
                        if(item.getItem() == itemstack)
                            alreadyAdded = true;
                    }

                    if(!alreadyAdded) {
                        Random rand = entity.getRNG();
                        if (!itemstack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemstack)) {
                            if (itemstack.isItemStackDamageable()) {
                                itemstack.setItemDamage(itemstack.getMaxDamage() - rand.nextInt(1 + rand.nextInt(Math.max(itemstack.getMaxDamage() - 3, 1))));
                            }

                            EntityItem item = new EntityItem(entity.getEntityWorld(), entity.posX, entity.posY, entity.posZ, itemstack);
                            item.setDefaultPickupDelay();
                            e.getDrops().add(item);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

    @Override
    public String getFeatureDescription() {
        return "Zombies have a 100% chance to drop any equipment";
    }
}

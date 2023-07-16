package betterwithmods.module.hardcore.creatures;

import betterwithmods.common.damagesource.BWDamageSource;
import betterwithmods.common.items.tools.ItemSoulforgeArmor;
import betterwithmods.util.player.PlayerHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.SoundCategory;

public class AIAttackRiding extends EntityAIBase {

    public EntityLivingBase rider;

    public AIAttackRiding(EntityLivingBase rider) {
        this.rider = rider;
    }

    @Override
    public boolean shouldExecute() {
        return rider.isRiding() && rider.getRidingEntity() instanceof EntityLivingBase;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return rider.getRidingEntity() != null && rider.getRidingEntity().isEntityAlive();
    }

    @Override
    public void updateTask() {
        EntityLivingBase mount = (EntityLivingBase) rider.getRidingEntity();
        //Attack if not wearing a SFS helmet
        if (mount == null)
            return;
        if(mount instanceof EntityPlayer) {
            if (!PlayerHelper.hasPart(mount, EntityEquipmentSlot.HEAD, ItemSoulforgeArmor.class)) {
                if (mount.world.rand.nextInt(5) == 0)
                    mount.world.playSound(null, mount.getPosition(), SoundEvents.ENTITY_SQUID_HURT, SoundCategory.HOSTILE, 0.5f, 1);
                mount.attackEntityFrom(BWDamageSource.squid, 0);
//                ((EntityPlayer) mount).addExhaustion(1f);
            }
        } else {
            mount.attackEntityFrom(BWDamageSource.squid, 0);
        }

    }
}

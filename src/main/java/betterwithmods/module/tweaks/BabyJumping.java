package betterwithmods.module.tweaks;

import betterwithmods.module.Feature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BabyJumping extends Feature {

    @SubscribeEvent
    public void onJump(LivingEvent.LivingJumpEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if(entity instanceof EntityLiving) {
            if (!entity.isChild())
                return;

            double motionY = 0.42;

            if (entity.isPotionActive(MobEffects.JUMP_BOOST)) {
                motionY += (double) ((float) (entity.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1F);
            }
            entity.motionY -= motionY;
        }
    }

    @Override
    public String getFeatureDescription() {
        return "Make it so babies can no longer jump. Adds some possibilities for automation";
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }
}

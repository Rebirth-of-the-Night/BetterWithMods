package betterwithmods.module.hardcore.creatures;

import betterwithmods.module.Feature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIFindEntityNearest;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HCSquid extends Feature {


    @Override
    public String getFeatureDescription() {
        return "Fear the squid...";
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

    @SubscribeEvent
    public void addEntityAI(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntitySquid) {
            EntitySquid squid = (EntitySquid) event.getEntity();
//            squid.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64d);
            squid.setNoGravity(true);

            squid.tasks.taskEntries.clear();
            squid.tasks.addTask(2, new AIAttackRiding(squid));
            squid.tasks.addTask(2, new AIMount(squid, 2));
            squid.tasks.addTask(3, new AIGrab(squid, 2, 10));
            //wander 7
            squid.tasks.addTask(8, new EntityAIWatchClosest(squid, EntityPlayer.class, 100f));
            squid.targetTasks.addTask(2, new EntityAIFindEntityNearest(squid, EntityPlayer.class));
//            squid.targetTasks.taskEntries.clear();
        }
    }

    @SubscribeEvent
    public void onEntityTick(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof EntitySquid) {
            EntitySquid squid = (EntitySquid) event.getEntityLiving();
            squid.setAir(300);
            if (squid.isRiding() && squid.getRidingEntity() instanceof EntityLivingBase) {
                //Keep vertical
                float f1 = MathHelper.sqrt(squid.motionX * squid.motionX + squid.motionZ * squid.motionZ);
                squid.squidPitch += (-((float) MathHelper.atan2((double) f1, squid.motionY)) * (180F / (float) Math.PI) - squid.squidPitch) * 0.5F;
            }
        }
    }

    @SubscribeEvent
    public void onClicked(PlayerInteractEvent.EntityInteractSpecific event) {

    }

    @SubscribeEvent
    public void onDismount(EntityMountEvent event) {
        if (event.getEntityMounting() instanceof EntitySquid && event.isDismounting() && event.getEntityMounting().isEntityAlive()) {
            event.setResult(Event.Result.DENY);
            event.setCanceled(true);
        }
    }
}

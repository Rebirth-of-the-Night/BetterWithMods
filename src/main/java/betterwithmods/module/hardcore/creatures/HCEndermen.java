package betterwithmods.module.hardcore.creatures;

import betterwithmods.module.Feature;
import betterwithmods.util.EntityUtils;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by primetoxinz on 4/20/17.
 */
public class HCEndermen extends Feature {
    @Override
    public String getFeatureDescription() {
        return "Makes Endermen agro in groups and make a thunder noise when they teleport";
    }

    @Override
    public boolean requiresMinecraftRestartToEnable() {
        return true;
    }

    @SubscribeEvent
    public void onTeleport(EnderTeleportEvent evt) {
        evt.getEntityLiving().getEntityWorld().playSound(null, evt.getEntityLiving().getPosition(), SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.HOSTILE, 1, 1);
    }

    @SubscribeEvent
    public void addEntityAI(EntityJoinWorldEvent evt) {
        if (evt.getEntity() instanceof EntityEnderman) {
            if (!evt.getWorld().isRemote) {
                EntityEnderman entity = (EntityEnderman) evt.getEntity();
                EntityUtils.findFirst(entity, EntityAIHurtByTarget.class).ifPresent(ai -> ((EntityAIHurtByTarget) ai).entityCallsForHelp = true);
                entity.targetTasks.addTask(2, new EntityAIHurtByTarget(entity, true));
            }
        }
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }
}

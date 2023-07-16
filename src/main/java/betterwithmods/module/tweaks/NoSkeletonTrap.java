package betterwithmods.module.tweaks;

import betterwithmods.module.Feature;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoSkeletonTrap extends Feature {

    @SubscribeEvent
    public void onEntitySpawn(EntityJoinWorldEvent event) {
        if(event.getEntity() instanceof EntitySkeletonHorse && ((EntitySkeletonHorse)event.getEntity()).isTrap()) {
            event.setCanceled(true);
        }
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

    @Override
    public String getFeatureDescription() {
        return "Remove the vanilla feature of Skeleton Traps, they are quite dumb";
    }
}

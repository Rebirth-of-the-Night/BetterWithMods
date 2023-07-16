package betterwithmods.module.tweaks;

import betterwithmods.module.Feature;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CactusSkeleton extends Feature{

	@Override
	public boolean hasSubscriptions() {
		return true;
	}

	@SubscribeEvent
	public void onDamage(LivingHurtEvent event) {
		if(event.getEntityLiving() instanceof AbstractSkeleton && event.getSource().damageType.equals(DamageSource.CACTUS.damageType)) {
			event.setCanceled(true);
		}
	}

	@Override
	public String getFeatureDescription() {
		return "Skeletons are no longer damaged by Cacti. Intended to make killing mobs in mobtraps harder.";
	}
}

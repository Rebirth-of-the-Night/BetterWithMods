package betterwithmods.module.tweaks;

import betterwithmods.common.BWMItems;
import betterwithmods.module.Feature;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static betterwithmods.util.WorldUtils.addDrop;

public class BatWings extends Feature {

    @Override
    public String getFeatureDescription() {
        return "Add a Bat Wing drop to bats";
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

    @Override
    public String[] getIncompatibleMods() {
        return new String[]{"actuallyadditions"};
    }

    @SubscribeEvent
    public void onDeath(LivingDropsEvent event) {
        World world = event.getEntityLiving().getEntityWorld();
        if (event.getEntityLiving() instanceof EntityBat) {
            int count = event.getLootingLevel() > 0 ? Math.min(2, event.getLootingLevel()) : world.rand.nextInt(2);
            addDrop(event, new ItemStack(BWMItems.BAT_WING, count));
        }
    }
}

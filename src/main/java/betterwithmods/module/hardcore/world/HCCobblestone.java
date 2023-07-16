package betterwithmods.module.hardcore.world;

import betterwithmods.common.BWMBlocks;
import betterwithmods.module.Feature;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HCCobblestone extends Feature {
    @Override
    public String getFeatureDescription() {
        return "Makes stone variants drop into cobblestone.";
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

    @SubscribeEvent
    public void dropCobble(BlockEvent.HarvestDropsEvent evt) {
        if (!this.enabled)
            return;

        if (!evt.isSilkTouching() && !evt.getWorld().isRemote) {
            if (evt.getState().getBlock() == Blocks.STONE) {
                int meta = evt.getState().getBlock().getMetaFromState(evt.getState());
                if (meta == 1 || meta == 3 || meta == 5) {
                    int harvestMeta = meta == 1 ? 0 : meta == 3 ? 1 : 2;
                    evt.getDrops().clear();
                    evt.getDrops().add(new ItemStack(BWMBlocks.COBBLE, 1, harvestMeta));
                }
            }
        }
    }
}

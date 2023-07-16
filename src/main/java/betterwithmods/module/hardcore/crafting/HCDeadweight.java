package betterwithmods.module.hardcore.crafting;

import betterwithmods.common.BWMRecipes;
import betterwithmods.module.Feature;
import betterwithmods.util.player.PlayerHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HCDeadweight extends Feature {

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        BWMRecipes.removeRecipe(new ItemStack(Blocks.ANVIL));
        Blocks.ANVIL.setTranslationKey("bwm:deadweight");
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.RightClickBlock event) {
        if(!PlayerHelper.isSurvival(event.getEntityPlayer()))
            return;

        if (event.getWorld().getBlockState(event.getPos()).getBlock().equals(Blocks.ANVIL)) {
            event.setCanceled(true);
            event.setCancellationResult(EnumActionResult.FAIL);
        }
    }

    @Override
    public String getFeatureDescription() {
        return "Disables the vanilla Anvil";
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }
}

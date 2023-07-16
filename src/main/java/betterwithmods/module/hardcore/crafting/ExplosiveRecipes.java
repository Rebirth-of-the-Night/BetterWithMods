package betterwithmods.module.hardcore.crafting;

import betterwithmods.api.tile.IHeated;
import betterwithmods.common.BWOreDictionary;
import betterwithmods.common.advancements.BWAdvancements;
import betterwithmods.common.registry.bulk.recipes.BulkCraftEvent;
import betterwithmods.common.registry.bulk.recipes.CookingPotRecipe;
import betterwithmods.module.Feature;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemStackHandler;

public class ExplosiveRecipes extends Feature {

    @Override
    public String getFeatureDescription() {
        return "Some recipes can't get too hot or they might explode.";
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

    @SubscribeEvent
    public void onBulkCraft(BulkCraftEvent event) {
        if (event.getTile() instanceof IHeated && event.getRecipe() instanceof CookingPotRecipe) {
            CookingPotRecipe recipe = (CookingPotRecipe) event.getRecipe();
            if (((IHeated) event.getTile()).getHeat(event.getWorld(), event.getTile().getPos()) > recipe.getHeat()) {
                explodeCauldron(event.getWorld(), event.getTile().getPos(), event.getInventory());
            }
        }
    }

    private void explodeCauldron(World world, BlockPos pos, ItemStackHandler inv) {
        float expSize = 0.0f;
        int blockAmt = 0;

        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack.isEmpty())
                continue;
            else if (BWOreDictionary.isOre(stack, "listAllExplosives")) {
                if (stack.getItem() instanceof ItemBlock)
                    blockAmt += stack.getCount();
                else
                    expSize += stack.getCount() / 64f;
            }
            inv.setStackInSlot(i, ItemStack.EMPTY);
        }

        expSize = blockAmt == 0 ? Math.max(expSize, 2.0f) : Math.max(expSize, 4.0f) + blockAmt;

        BWAdvancements.triggerNearby(world, new AxisAlignedBB(pos, pos.add(1, 1, 1)).grow(10.0D, 5.0D, 10.0D), BWAdvancements.EXPLOSIVE_RECIPE);

        world.createExplosion(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, expSize, true);


    }
}

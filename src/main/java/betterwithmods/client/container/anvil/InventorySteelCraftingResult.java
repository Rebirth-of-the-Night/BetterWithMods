package betterwithmods.client.container.anvil;

import betterwithmods.common.blocks.tile.TileEntitySteelAnvil;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;

public class InventorySteelCraftingResult extends InventoryCraftResult {

    private TileEntitySteelAnvil craft;

    public InventorySteelCraftingResult(TileEntitySteelAnvil te) {
        craft = te;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot == 0 ? craft.getResult() : null;
    }

    @Override
    public ItemStack decrStackSize(int slot, int decrement) {
        //return craft.decrStackSize(slot, decrement);
        craft.getWorld().playSound(null,craft.getPos(),SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS,0.5f,1.0f);
        ItemStack stack = craft.getResult();
        if (stack != null) {
            craft.setResult(null);
            return stack;
        } else {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        craft.setResult(stack);
    }
}

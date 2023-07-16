package betterwithmods.client.container.inventory;

import betterwithmods.client.container.anvil.InventorySteelCrafting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class SlotCraftingItemHandler extends Slot {
    private final InventorySteelCrafting craftingInv;
    private final IItemHandler matrix;

    public SlotCraftingItemHandler(EntityPlayer player, InventoryCrafting craftingInventory, IInventory inventoryIn, int slotIndex, int xPosition, int yPosition) {
        super(inventoryIn, slotIndex, xPosition, yPosition);
        craftingInv = (InventorySteelCrafting) craftingInventory;
        this.matrix = ((InventorySteelCrafting) craftingInventory).craft.inventory;
    }

    /**
     * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
     */
    public boolean isItemValid(@Nullable ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack onTake(EntityPlayer player, ItemStack stack) {
        for (int i = 0; i < matrix.getSlots(); i++) {
            if (!matrix.getStackInSlot(i).isEmpty()) {
                matrix.extractItem(i, 1, false);
            }
        }
        //craftingInv.craft.setResult(ExtremeCraftingManager.getInstance().findMatchingRecipe(craftingInv, craftingInv.craft.getBlockWorld()));
        craftingInv.container.onCraftMatrixChanged(craftingInv);
        return stack;
    }
}
package betterwithmods.client.container.anvil;

import betterwithmods.common.blocks.tile.TileEntitySteelAnvil;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class InventorySteelCrafting extends InventoryCrafting {

    public TileEntitySteelAnvil craft;
    public Container container;
    private IItemHandler handler;

    public InventorySteelCrafting(Container container, TileEntitySteelAnvil te) {
        super(container, 4, 4);
        craft = te;
        handler = te.inventory;
        this.container = container;
    }


    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot >= this.getSizeInventory() ? null : handler.getStackInSlot(slot);
    }

    @Override
    public ItemStack getStackInRowAndColumn(int row, int column) {
        if (row >= 0 && row < 4) {
            int x = row + column * 4;
            return this.getStackInSlot(x);
        } else {
            return null;
        }
    }

    @Override
    public ItemStack decrStackSize(int slot, int decrement) {
        ItemStack stack = handler.getStackInSlot(slot);
        this.container.onCraftMatrixChanged(this);
        if (stack != null) {
            ItemStack itemstack;
            if (stack.getCount() <= decrement) {
                itemstack = stack.copy();
                stack = ItemStack.EMPTY;
                craft.setInventorySlotContents(slot, ItemStack.EMPTY);
                this.container.onCraftMatrixChanged(this);
                return itemstack;
            } else {
                itemstack = stack.splitStack(decrement);
                if (stack.getCount() == 0) {
                    stack = ItemStack.EMPTY;
                    craft.setInventorySlotContents(slot, ItemStack.EMPTY);
                }
                this.container.onCraftMatrixChanged(this);
                return itemstack;
            }
        } else {
            return null;
        }
    }

    public void craft() {
        for (int i = 0; i < handler.getSlots(); i++) {
            if (!handler.getStackInSlot(i).isEmpty()) {
                handler.extractItem(i, 1, false);
            }
        }
        this.container.onCraftMatrixChanged(this);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack itemstack) {
        craft.setInventorySlotContents(slot, itemstack);
        this.container.onCraftMatrixChanged(this);
    }

}
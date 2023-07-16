package betterwithmods.client.container.anvil;

import betterwithmods.common.blocks.tile.TileEntitySteelAnvil;
import betterwithmods.common.registry.anvil.AnvilCraftingManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerSteelAnvil extends Container {
    private final int INV_FIRST = 17;
    private final int INV_LAST = 44;
    private final int HOT_LAST = 53;
    public InventoryCrafting craftMatrix;
    public IInventory craftResult;
    public InventoryPlayer inventoryPlayer;

    public EntityPlayer player;
    private TileEntitySteelAnvil te;

    public ContainerSteelAnvil(InventoryPlayer inventory, TileEntitySteelAnvil te) {
        this.te = te;
        this.inventoryPlayer = inventory;
        this.player = inventory.player;
        craftMatrix = new InventorySteelCrafting(this, te);
        craftResult = new InventorySteelCraftingResult(te);
        this.addSlotToContainer(new SlotSteelAnvilCrafting(player, craftMatrix, craftResult, 0, 124, 44));
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                this.addSlotToContainer(new Slot(craftMatrix, j + i * 4, 12 + j * 18, 17 + i * 18));
            }
        }
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 102 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 160));
        }
        this.onCraftMatrixChanged(craftMatrix);
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory matrix) {

        this.craftResult.setInventorySlotContents(0, AnvilCraftingManager.findMatchingResult(this.craftMatrix, te.getWorld()));
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);

    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return te.isUseableByPlayer(player);
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index == 0) {
                if (!this.mergeItemStack(itemstack1, INV_FIRST, HOT_LAST, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(itemstack1, itemstack);
            } else if (index >= INV_FIRST && index < INV_LAST) {
                if (!this.mergeItemStack(itemstack1, INV_LAST, HOT_LAST, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= INV_LAST && index < HOT_LAST) {
                if (!this.mergeItemStack(itemstack1, INV_FIRST, INV_LAST, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, INV_FIRST, HOT_LAST, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }
}
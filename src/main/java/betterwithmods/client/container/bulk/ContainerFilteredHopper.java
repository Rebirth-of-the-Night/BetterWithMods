package betterwithmods.client.container.bulk;

import betterwithmods.client.container.ContainerProgress;
import betterwithmods.common.BWRegistry;
import betterwithmods.common.blocks.mechanical.tile.TileEntityFilteredHopper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerFilteredHopper extends ContainerProgress {
    private final TileEntityFilteredHopper tile;

    public ContainerFilteredHopper(EntityPlayer player, TileEntityFilteredHopper tile) {
        super(tile);
        this.tile = tile;

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new SlotItemHandler(tile.inventory, j + i * 9, 8 + j * 18, 60 + i * 18));
            }
        }

        addSlotToContainer(new SlotItemHandler(tile.filter, 0, 80, 37));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new SlotItemHandler(player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), j + i * 9 + 9, 8 + j * 18, 111 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new SlotItemHandler(player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), i, 8 + i * 18, 169));
        }
    }
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack clickedStack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack processedStack = slot.getStack();
            clickedStack = processedStack.copy();

            if (index < 19) {
                if (!mergeItemStack(processedStack, 19, this.inventorySlots.size(), true))
                    return ItemStack.EMPTY;
            } else if (BWRegistry.HOPPER_FILTERS.isFilter(processedStack)) {
                if (!mergeItemStack(processedStack, 18, 19, false))
                    return ItemStack.EMPTY;
            } else if (!mergeItemStack(processedStack, 0, 18, false)) {
                return ItemStack.EMPTY;
            }

            if (processedStack.getCount() == 0)
                slot.putStack(ItemStack.EMPTY);
            else
                slot.onSlotChanged();
        }
        return clickedStack;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return tile.isUseableByPlayer(playerIn);
    }

}

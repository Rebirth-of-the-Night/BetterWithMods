package betterwithmods.client.container.other;

import betterwithmods.client.container.ContainerProgress;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.blocks.mechanical.tile.TileEntityPulley;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerPulley extends ContainerProgress {
    private static final int ROPE_SLOTS_END = 4;
    private final TileEntityPulley tile;


    public ContainerPulley(EntityPlayer player, TileEntityPulley tile) {
        super(tile);
        this.tile = tile;

        for (int i = 0; i < ROPE_SLOTS_END; i++) {
            addSlotToContainer(new SlotItemHandler(tile.inventory, i, 53 + i * 18, 52) {
                @Override
                public boolean isItemValid(ItemStack stack) {
                    return super.isItemValid(stack) && stack.getItem() == Item.getItemFromBlock(BWMBlocks.ROPE);
                }
            });
        }


        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new SlotItemHandler(player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), j + i * 9 + 9, 8 + j * 18, 93 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new SlotItemHandler(player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), i, 8 + i * 18, 151));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tile.isUseableByPlayer(player);
    }


    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack clickedStack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack processedStack = slot.getStack();
            clickedStack = processedStack.copy();

            if (index < ROPE_SLOTS_END) {
                if (!mergeItemStack(processedStack, ROPE_SLOTS_END, this.inventorySlots.size(), true))
                    return ItemStack.EMPTY;
            } else if (!mergeItemStack(processedStack, 0, ROPE_SLOTS_END, false)) {
                return ItemStack.EMPTY;
            }

            if (processedStack.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }
        return clickedStack;
    }
}

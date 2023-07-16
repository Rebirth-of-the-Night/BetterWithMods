package betterwithmods.client.container.bulk;

import betterwithmods.client.container.ContainerProgress;
import betterwithmods.common.blocks.mechanical.tile.TileEntityCookingPot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerCookingPot extends ContainerProgress {
    private final TileEntityCookingPot tile;
    private int heat;

    public ContainerCookingPot(EntityPlayer player, TileEntityCookingPot tile) {
        super(tile);
        IItemHandler playerInv = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        this.tile = tile;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new SlotItemHandler(tile.inventory, j + i * 9, 8 + j * 18, 43 + i * 18));
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new SlotItemHandler(playerInv, j + i * 9 + 9, 8 + j * 18, 111 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new SlotItemHandler(playerInv, i, 8 + i * 18, 169));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.tile.isUseableByPlayer(player);
    }

    @Override
    public ItemStack slotClick(int x, int dragType, ClickType type, EntityPlayer player) {
        this.tile.markDirty();
        return super.slotClick(x, dragType, type, player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack()) {
            ItemStack stack1 = slot.getStack();
            stack = stack1.copy();

            if (slotIndex < 27) {
                if (!mergeItemStack(stack1, 27, this.inventorySlots.size(), true))
                    return ItemStack.EMPTY;
            } else if (!mergeItemStack(stack1, 0, 27, false))
                return ItemStack.EMPTY;

            if (stack1.getCount() < 1)
                slot.putStack(ItemStack.EMPTY);
            else
                slot.onSlotChanged();
        }
        return stack;
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendWindowProperty(this, 2, this.tile.getHeat(tile.getWorld(), tile.getPos()));
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        int h = this.tile.getHeat(tile.getWorld(), tile.getPos());
        if (heat != h) {
            heat = h;
            for (IContainerListener craft : this.listeners) {
                craft.sendWindowProperty(this, 2, heat);
            }
        }
    }

    @Override
    public void updateProgressBar(int index, int value) {
        super.updateProgressBar(index, value);
        switch (index) {
            case 2:
                heat = value;
                break;
        }
    }

    public int getHeat() {
        return heat;
    }
}

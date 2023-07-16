package betterwithmods.util;

import betterwithmods.common.blocks.behaviors.BehaviorDefaultDispenseBlock;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;

public class DispenserBlockOverride extends BehaviorDefaultDispenseBlock {

    private final ItemStack override;

    public DispenserBlockOverride(ItemStack override) {
        this.override = override;
    }

    @Override
    public ItemStack getInputStack(IBlockSource source, ItemStack stackIn) {
        return this.override.copy();
    }
}

package betterwithmods.common.blocks.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class FilteredStackHandler extends SimpleStackHandler {

    public Predicate<ItemStack>[] predicate;

    @SuppressWarnings("all")
    public FilteredStackHandler(int size, TileEntity tile, Predicate<ItemStack>... predicate) {
        super(size, tile);
        this.predicate = predicate;
    }


    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if(predicate.length > slot) {
            Predicate<ItemStack> p = predicate[slot];
            if(p != null)
                if (!p.test(stack))
                    return stack;
        }
        return super.insertItem(slot, stack, simulate);
    }
}

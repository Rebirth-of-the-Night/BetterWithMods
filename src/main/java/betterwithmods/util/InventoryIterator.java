package betterwithmods.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class InventoryIterator implements Iterator<ItemStack> {
    public static Stream<ItemStack> stream(ItemStackHandler handler) {
        return stream(handler, 0, handler.getSlots());
    }

    public static Stream<ItemStack> stream(ItemStackHandler handler, int start, int end) {
        Iterator<ItemStack> iterator = new InventoryIterator(handler, start, end);
        Iterable<ItemStack> iterable = () -> iterator;
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    private final ItemStackHandler handler;
    private final int end;
    private int index;

    private InventoryIterator(ItemStackHandler handler, int start, int end) {
        this.handler = handler;
        this.index = start;
        this.end = end;
    }

    @Override
    public boolean hasNext() {
        return index < end;
    }

    @Override
    public ItemStack next() {
        return handler.getStackInSlot(index++);
    }
}

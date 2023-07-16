package betterwithmods.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;

public class MessageHarness extends BWMessage {
    public ItemStack stack;
    public int entity;

    public MessageHarness() {
        this(0, ItemStack.EMPTY);
    }

    public MessageHarness(int entity, ItemStack stack) {
        this.stack = stack;
        this.entity = entity;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entity = readData(buf, int.class);
        stack = readData(buf, ItemStack.class);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writeData(buf, entity);
        writeData(buf, stack);
    }
}

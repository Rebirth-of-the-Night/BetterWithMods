package betterwithmods.network.messages;

import betterwithmods.network.MessageDataHandler;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import javax.annotation.Nonnull;

public class BWMessage implements IMessage {
    protected static <T> T readData(ByteBuf buf, Class<T> type) {
        return (T) MessageDataHandler.getHandler(type).read(buf);
    }

    @SuppressWarnings("unchecked")
    protected static <T> void writeData(@Nonnull ByteBuf buf, T data) {
        MessageDataHandler.getHandler((Class<T>) data.getClass()).write(buf, data);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

}

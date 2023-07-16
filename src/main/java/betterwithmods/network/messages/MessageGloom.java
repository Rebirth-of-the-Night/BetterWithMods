package betterwithmods.network.messages;

import io.netty.buffer.ByteBuf;

public class MessageGloom extends BWMessage {
    public String uuid;
    public int gloom;

    public MessageGloom(String uuid, int gloom) {
        this.uuid = uuid;
        this.gloom = gloom;
    }

    public MessageGloom() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        uuid = readData(buf, String.class);
        gloom = readData(buf, int.class);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writeData(buf, uuid);
        writeData(buf, gloom);
    }
}

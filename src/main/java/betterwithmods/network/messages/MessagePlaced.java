package betterwithmods.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;

public class MessagePlaced extends BWMessage {
    public BlockPos[] pos;

    public MessagePlaced(BlockPos... pos) {
        this.pos = pos;
    }

    public MessagePlaced() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int length = readData(buf, int.class);
        pos = new BlockPos[length];
        for (int i = 0; i < length; i++) {
            pos[i] = readData(buf, BlockPos.class);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        if (pos != null) {
            writeData(buf, pos.length);
            for (int i = 0; i < pos.length; i++) {
                writeData(buf, pos[i]);
            }

        }
    }
}

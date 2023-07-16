package betterwithmods.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class MessageFXDynamite extends BWMessage {
    public double x, y, z;
    public float size;
    public Collection<BlockPos> affectedPositions;

    public MessageFXDynamite(Vec3d center, float size, Collection<BlockPos> include, Collection<BlockPos> exclude) {
        this.x = center.x;
        this.y = center.y;
        this.z = center.z;
        this.size = size;
        this.affectedPositions = new HashSet<>();
        this.affectedPositions.addAll(include);
        this.affectedPositions.removeAll(exclude);
    }

    public MessageFXDynamite() {
        this.affectedPositions = new ArrayList<>();
    }

    public Vec3d getCenter() {
        return new Vec3d(x,y,z);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = readData(buf, double.class);
        y = readData(buf, double.class);
        z = readData(buf, double.class);
        size = readData(buf, float.class);
        int size = readData(buf, int.class);
        for(int i = 0; i < size; i++) {
            affectedPositions.add(readData(buf, BlockPos.class));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writeData(buf, x);
        writeData(buf, y);
        writeData(buf, z);
        writeData(buf, size);
        writeData(buf, affectedPositions.size());
        for (BlockPos pos : affectedPositions) {
            writeData(buf, pos);
        }
    }
}

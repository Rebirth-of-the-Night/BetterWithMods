package betterwithmods.module.hardcore.world.strata;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.math.BlockPos;
import team.chisel.ctm.client.texture.ctx.TextureContextPosition;

import javax.annotation.Nonnull;

public class TextureContextStrata extends TextureContextPosition{
    public TextureContextStrata(@Nonnull BlockPos pos) {
        super(pos);
    }

    public int getStrata() {
        WorldClient world = Minecraft.getMinecraft().world;
        return HCStrata.getStratification(world,position, world.provider.getDimension()).ordinal();
    }


    @Override
    public long getCompressedData() {
        return getStrata();
    }
}

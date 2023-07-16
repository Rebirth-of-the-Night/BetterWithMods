package betterwithmods.module.gameplay.miniblocks.tiles;

import betterwithmods.module.gameplay.miniblocks.orientations.BaseOrientation;
import betterwithmods.module.gameplay.miniblocks.orientations.CornerOrientation;
import net.minecraft.nbt.NBTTagCompound;

public class TileCorner extends TileMini {
    @Override
    public BaseOrientation deserializeOrientation(NBTTagCompound tag) {
        int o = tag.getInteger("orientation");
        return CornerOrientation.VALUES[o];
    }
}

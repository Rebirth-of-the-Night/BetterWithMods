package betterwithmods.module.gameplay.miniblocks.tiles;

import betterwithmods.module.gameplay.miniblocks.orientations.BaseOrientation;
import betterwithmods.module.gameplay.miniblocks.orientations.SidingOrientation;
import net.minecraft.nbt.NBTTagCompound;

public class TileSiding extends TileMini {
    @Override
    public BaseOrientation deserializeOrientation(NBTTagCompound tag) {
        int o = tag.getInteger("orientation");
        return SidingOrientation.VALUES[o];
    }
}

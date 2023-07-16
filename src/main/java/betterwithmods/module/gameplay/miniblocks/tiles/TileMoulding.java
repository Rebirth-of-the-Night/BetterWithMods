package betterwithmods.module.gameplay.miniblocks.tiles;

import betterwithmods.module.gameplay.miniblocks.orientations.BaseOrientation;
import betterwithmods.module.gameplay.miniblocks.orientations.MouldingOrientation;
import net.minecraft.nbt.NBTTagCompound;

public class TileMoulding extends TileMini {
    @Override
    public BaseOrientation deserializeOrientation(NBTTagCompound tag) {
        int o = tag.getInteger("orientation");
        return MouldingOrientation.VALUES[o];
    }

}

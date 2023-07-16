package betterwithmods.module.gameplay.miniblocks.client;

import betterwithmods.client.baking.IRenderComparable;
import betterwithmods.module.gameplay.miniblocks.orientations.BaseOrientation;
import betterwithmods.module.gameplay.miniblocks.tiles.TileMini;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;

public class MiniCacheInfo implements IRenderComparable<MiniCacheInfo> {
    public final IBlockState state;
    public final BaseOrientation orientation;

    public MiniCacheInfo(IBlockState state, BaseOrientation orientation) {
        this.state = state;
        this.orientation = orientation;
    }

    public static MiniCacheInfo from(IBlockState state, BaseOrientation orientation) {
        return new MiniCacheInfo(state, orientation);
    }

    public static MiniCacheInfo from(TileMini mini) {
        return from(mini.state, mini.orientation);
    }

    public static MiniCacheInfo from(ItemStack mini) {
        NBTTagCompound tag = mini.getTagCompound();
        if (tag != null && tag.hasKey("texture")) {
            IBlockState texture = NBTUtil.readBlockState(tag.getCompoundTag("texture"));
            return from(texture, BaseOrientation.DEFAULT);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MiniCacheInfo cacheInfo = (MiniCacheInfo) o;
        if (!state.equals(cacheInfo.state)) return false;
        return orientation == cacheInfo.orientation;
    }

    @Override
    public boolean renderEquals(MiniCacheInfo other) {
        return equals(other);
    }

    @Override
    public int renderHashCode() {
        return 31 * state.hashCode() + orientation.hashCode();
    }


    public BaseOrientation getOrientation() {
        if (orientation == null)
            return BaseOrientation.DEFAULT;
        return orientation;
    }

    public IBlockState getState() {
        if(state == null)
            return Blocks.AIR.getDefaultState();
        return state;
    }
}

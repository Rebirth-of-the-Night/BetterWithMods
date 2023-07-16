package betterwithmods.api.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IRopeConnector {
    EnumFacing getFacing(IBlockState state);

    default boolean canMovePlatforms(World world, BlockPos pos) {
        return true;
    }

    default void onLand(World world, BlockPos pos, IBlockState previousState) {
        //NOOP
    }
}

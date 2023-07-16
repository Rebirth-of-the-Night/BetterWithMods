package betterwithmods.common.blocks.mini;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by primetoxinz on 6/10/17.
 */
public interface IDamageDropped {
    int damageDropped(IBlockState state, World world, BlockPos pos);
}

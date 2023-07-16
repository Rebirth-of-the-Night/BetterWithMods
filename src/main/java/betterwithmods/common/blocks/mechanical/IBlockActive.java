package betterwithmods.common.blocks.mechanical;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by primetoxinz on 7/19/17.
 */
public interface IBlockActive {
    PropertyBool ACTIVE = PropertyBool.create("active");

    default boolean isActive(IBlockState state) {
        if(state.getBlock() instanceof IBlockActive)
            return state.getValue(ACTIVE);
        return false;
    }

    default void setActive(World world, BlockPos pos, boolean active) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof IBlockActive) {
            if(state.getValue(ACTIVE) != active)
                onChangeActive(world,pos,active);
            world.setBlockState(pos, state.withProperty(ACTIVE, active));
            world.scheduleUpdate(pos, state.getBlock(),1);
        }
    }

    default void onChangeActive(World world, BlockPos pos, boolean newValue) {

    }
}

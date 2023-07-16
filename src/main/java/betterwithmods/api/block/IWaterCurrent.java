package betterwithmods.api.block;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;

public interface IWaterCurrent {
    IWaterCurrent VANILLA_LIQUID = fromLiquid();
    IWaterCurrent FORGE_LIQUID = fromForgeLiquid();
    IWaterCurrent NO_FLOW = (world, pos, state) -> Vec3d.ZERO;

    Vec3d getFlowDirection(World world, BlockPos pos, IBlockState state);

    static IWaterCurrent fromLiquid()
    {
        return (world, pos, state) -> {
            BlockLiquid block = (BlockLiquid) state.getBlock();
            return block.getFlow(world,pos,state);
        };
    }

    static IWaterCurrent fromForgeLiquid()
    {
        return (world, pos, state) -> {
            BlockFluidBase block = (BlockFluidBase) state.getBlock();
            return block.getFlowVector(world,pos);
        };
    }
}

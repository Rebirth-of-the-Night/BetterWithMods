package betterwithmods.common.blocks.tile;

import betterwithmods.common.blocks.mechanical.tile.TileEntityWaterwheel;
import betterwithmods.common.fluid.FluidTankRestricted;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class TileEntityBucket extends TileFluid /*implements ITickable*/ {
    // private int ticks;

    @Override
    public FluidTank createTank() {
        return new FluidTankRestricted(new FluidStack(FluidRegistry.WATER, 0), getCapacity());
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public int getCapacity() {
        return Fluid.BUCKET_VOLUME * 8;
    }

    @Override
    public boolean hasFluid(EnumFacing facing) {
        return facing != EnumFacing.DOWN;
    }


    public boolean isWater(IBlockState state) {
        return TileEntityWaterwheel.isWater(state);
    }

    /*@Override
    public void update() {
        if (!isFull() && ticks > 100) {
            fillFromSurrounding();
            ticks = 0;
        }
        ticks++;
    }*/

    public void fillFromSurrounding() {
        for (EnumFacing face : EnumFacing.HORIZONTALS) {
            if (isWater(world.getBlockState(pos.offset(face)))) {
                fill(new FluidStack(FluidRegistry.WATER, getCapacity() / 4), true);
            }
        }
    }
}

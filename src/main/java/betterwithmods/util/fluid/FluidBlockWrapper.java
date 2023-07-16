package betterwithmods.util.fluid;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

/**
 * Wrapper to handle {@link IFluidBlock} as an IFluidHandler
 */
public class FluidBlockWrapper implements IFluidHandler {
    protected final IFluidBlock fluidBlock;
    protected final World world;
    protected final BlockPos blockPos;

    public FluidBlockWrapper(IFluidBlock fluidBlock, World world, BlockPos blockPos) {
        this.fluidBlock = fluidBlock;
        this.world = world;
        this.blockPos = blockPos;
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        float percentFilled = fluidBlock.getFilledPercentage(world, blockPos);
        if (percentFilled < 0) {
            percentFilled *= -1;
        }
        int amountFilled = Math.round(Fluid.BUCKET_VOLUME * percentFilled);
        FluidStack fluid = amountFilled > 0 ? new FluidStack(fluidBlock.getFluid(), amountFilled) : null;
        return new FluidTankProperties[]{new FluidTankProperties(fluid, Fluid.BUCKET_VOLUME, false, true)};
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        // NOTE: "Filling" means placement in this context!
        if (resource == null) {
            return 0;
        }
        return setLiquid(world, blockPos, fluidBlock, 2);
    }

    public int setLiquid(World world, BlockPos pos, IFluidBlock fluidBlock, int level) {
        if (fluidBlock instanceof BlockFluidBase) {
            BlockFluidBase blockFluidBase = (BlockFluidBase) fluidBlock;
            IBlockState state = world.getBlockState(pos);
            Block existingBlock = state.getBlock();
            FluidUtil.destroyBlockOnFluidPlacement(world, pos);
            if ((!(existingBlock instanceof BlockFluidBase) && existingBlock.isReplaceable(world, pos)) || state.getValue(BlockFluidBase.LEVEL) > level) {
                world.setBlockState(pos, blockFluidBase.getDefaultState().withProperty(BlockFluidBase.LEVEL, level), 11);
            }
            return Fluid.BUCKET_VOLUME;
        }
        return 0;
    }


    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (resource == null || !fluidBlock.canDrain(world, blockPos)) {
            return null;
        }

        FluidStack simulatedDrain = fluidBlock.drain(world, blockPos, false);
        if (resource.containsFluid(simulatedDrain)) {
            //Only simulate the drain, as to support HCBuckets
//            if (doDrain) {
//                return fluidBlock.drain(world, blockPos, true);
//            } else {
            return simulatedDrain;
//            }
        }

        return null;
    }


    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (maxDrain <= 0 /*|| !fluidBlock.canDrain(world, blockPos)*/) {
            return null;
        }

        return new FluidStack(fluidBlock.getFluid(), Fluid.BUCKET_VOLUME);
//
//        FluidStack simulatedDrain = fluidBlock.drain(world, blockPos, false);
//        if (simulatedDrain != null && simulatedDrain.amount <= maxDrain) {
        //Only simulate the drain, as to support HCBuckets
//            if (doDrain) {
//                return fluidBlock.drain(world, blockPos, true);
//            } else {
//                return simulatedDrain;
//            }
//        }
//        return null;
    }

}
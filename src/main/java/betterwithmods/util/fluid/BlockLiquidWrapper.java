package betterwithmods.util.fluid;

import betterwithmods.util.FluidUtils;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

public class BlockLiquidWrapper implements IFluidHandler {
    protected final BlockLiquid blockLiquid;
    protected final World world;
    protected final BlockPos blockPos;

    public BlockLiquidWrapper(BlockLiquid blockLiquid, World world, BlockPos blockPos) {
        this.blockLiquid = blockLiquid;
        this.world = world;
        this.blockPos = blockPos;
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        FluidStack containedStack = null;
        IBlockState blockState = world.getBlockState(blockPos);
        if (blockState.getBlock() == blockLiquid) {
            containedStack = getStack(blockState);
        }
        return new FluidTankProperties[]{new FluidTankProperties(containedStack, Fluid.BUCKET_VOLUME, false, true)};
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {

        // NOTE: "Filling" means placement in this context!
        if (resource.amount < Fluid.BUCKET_VOLUME) {
            return 0;
        }

        if (doFill) {
            Material material = blockLiquid.getDefaultState().getMaterial();
            BlockLiquid block = BlockLiquid.getFlowingBlock(material);
            if (!world.isRemote) {
                FluidUtils.setLiquid(world, blockPos, block, 2);
                for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                    BlockPos p2 = blockPos.offset(facing);
                    FluidUtils.setLiquid(world, p2, block, 5);
                }
            }
        }
        return Fluid.BUCKET_VOLUME;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (resource == null || resource.amount < Fluid.BUCKET_VOLUME) {
            return null;
        }

        IBlockState blockState = world.getBlockState(blockPos);
        //Ignore the level of the fluid, so we can fill from flowing liquids in HCBuckets.
        if (blockState.getBlock() == blockLiquid /*&& blockState.getValue(BlockLiquid.LEVEL) == 0*/) {
            FluidStack containedStack = getStack(blockState);
            if (containedStack != null && resource.containsFluid(containedStack)) {
                //Don't remove the block ever for HCBuckets
//                if (doDrain) {
//                    world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 11);
//                }
                return containedStack;
            }

        }
        return null;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        //HCBuckets doesn't remove the block, so it doesn't matter how much the container holds
//        if (maxDrain < Fluid.BUCKET_VOLUME) {
//            return null;
//        }

        IBlockState blockState = world.getBlockState(blockPos);
        if (blockState.getBlock() == blockLiquid) {
            FluidStack containedStack = getStack(blockState);


            if (containedStack != null) {
                if (containedStack.getFluid() == FluidRegistry.LAVA && containedStack.amount <= maxDrain) {
                    if (doDrain) {
                        world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 11);
                    }
                    return null;
                }

                //Don't remove the block ever for HCBuckets
//
                containedStack.amount = maxDrain;
                return containedStack;
            }

        }
        return null;
    }

    @Nullable
    private FluidStack getStack(IBlockState blockState) {
        Material material = blockState.getMaterial();
        //Ignore level for HCBuckets only for water
        if (material == Material.WATER /*&& blockState.getValue(BlockLiquid.LEVEL) == 0*/) {
            return new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME);
        } else if (material == Material.LAVA && blockState.getValue(BlockLiquid.LEVEL) == 0) {
            return new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME);
        } else {
            return null;
        }
    }
}
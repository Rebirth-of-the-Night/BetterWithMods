package betterwithmods.util;

import betterwithmods.util.fluid.BlockLiquidWrapper;
import betterwithmods.util.fluid.FluidBlockWrapper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;

public class FluidUtils {


    public static IFluidHandler getBlockFluidHandler(Fluid fluid, World world, BlockPos pos) {
        Block block = fluid.getBlock();
        if (block instanceof IFluidBlock) {
            return new FluidBlockWrapper((IFluidBlock) block, world, pos);
        } else if (block instanceof BlockLiquid) {
            return new BlockLiquidWrapper((BlockLiquid) block, world, pos);
        }
        return null;
    }

    @Nullable
    public static IFluidHandler getBlockFluidHandler(World world, BlockPos blockPos, EnumFacing side) {
        IBlockState state = world.getBlockState(blockPos);
        Block block = state.getBlock();
        if (block instanceof IFluidBlock) {
            return new FluidBlockWrapper((IFluidBlock) block, world, blockPos);
        } else if (block instanceof BlockLiquid) {
            return new BlockLiquidWrapper((BlockLiquid) block, world, blockPos);
        }

        //this is bad hack since Item.rayTrace doesn't give not full liquids
        blockPos = blockPos.offset(side);
        state = world.getBlockState(blockPos);
        block = state.getBlock();
        if (block instanceof IFluidBlock) {
            return new FluidBlockWrapper((IFluidBlock) block, world, blockPos);
        } else if (block instanceof BlockLiquid) {
            return new BlockLiquidWrapper((BlockLiquid) block, world, blockPos);
        }

        return null;
    }

    public static Fluid getFluidFromBlock(World world, BlockPos blockPos, EnumFacing side) {
        IFluidHandler handler = getBlockFluidHandler(world, blockPos, side);
        if (handler != null)
            return Arrays.stream(handler.getTankProperties()).map(IFluidTankProperties::getContents).filter(Objects::nonNull).map(FluidStack::getFluid).findFirst().orElse(null);
        return null;
    }


    @Nonnull
    public static FluidActionResult tryPickUpFluid(@Nonnull ItemStack emptyContainer, @Nullable EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side) {
        if (emptyContainer.isEmpty() || worldIn == null || pos == null) {
            return FluidActionResult.FAILURE;
        }

        IFluidHandler targetFluidHandler = getBlockFluidHandler(worldIn, pos, side);
        if (targetFluidHandler != null) {
            return FluidUtil.tryFillContainer(emptyContainer, targetFluidHandler, Integer.MAX_VALUE, playerIn, true);
        }
        return FluidActionResult.FAILURE;
    }

    @Nonnull
    public static FluidActionResult tryPlaceFluid(@Nullable EntityPlayer player, World world, BlockPos pos, @Nonnull ItemStack container, FluidStack resource) {
        ItemStack containerCopy = ItemHandlerHelper.copyStackWithSize(container, 1); // do not modify the input
        IFluidHandlerItem containerFluidHandler = FluidUtil.getFluidHandler(containerCopy);
        if (containerFluidHandler != null && tryPlaceFluid(player, world, pos, containerFluidHandler, resource)) {
            return new FluidActionResult(containerFluidHandler.getContainer());
        }
        return FluidActionResult.FAILURE;
    }


    public static boolean tryPlaceFluid(@Nullable EntityPlayer player, World world, BlockPos pos, IFluidHandler fluidSource, FluidStack resource) {
        if (world == null || resource == null || pos == null) {
            return false;
        }

        Fluid fluid = resource.getFluid();
        if (fluid == null || !fluid.canBePlacedInWorld()) {
            return false;
        }

        if (fluidSource.drain(resource, false) == null) {
            return false;
        }

        // check that we can place the fluid at the destination
        IBlockState destBlockState = world.getBlockState(pos);
        Material destMaterial = destBlockState.getMaterial();
        boolean isDestNonSolid = !destMaterial.isSolid();
        boolean isDestReplaceable = destBlockState.getBlock().isReplaceable(world, pos);
        if (!world.isAirBlock(pos) && !isDestNonSolid && !isDestReplaceable) {
            return false; // Non-air, solid, unreplacable block. We can't put fluid here.
        }

        if (world.provider.doesWaterVaporize() && fluid.doesVaporize(resource)) {
            FluidStack result = fluidSource.drain(resource, true);
            if (result != null) {
                result.getFluid().vaporize(player, world, pos, result);
                return true;
            }
        } else {
            // This fluid handler places the fluid block when filled
            IFluidHandler handler = getBlockFluidHandler(fluid, world, pos);
            if (handler != null) {
                FluidStack result = FluidUtil.tryFluidTransfer(handler, fluidSource, resource, true);
                if (result != null) {
                    SoundEvent soundevent = resource.getFluid().getEmptySound(resource);
                    world.playSound(player, pos, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    return true;
                }
            }
        }
        return false;
    }

    public static void setLiquid(World world, BlockPos pos, Block block, int level) {
        setLiquid(world, pos, block, level, false);
    }

    public static void setLiquid(World world, BlockPos pos, Block block, int level, boolean force) {
        IBlockState state = world.getBlockState(pos);
        Block existingBlock = state.getBlock();
        if (force || ((existingBlock instanceof BlockLiquid && state.getValue(BlockLiquid.LEVEL) > level) || (!state.getMaterial().isLiquid() && existingBlock.isReplaceable(world, pos)))) {
            world.setBlockState(pos, block.getDefaultState().withProperty(BlockLiquid.LEVEL, level), 11);
        }
    }
}

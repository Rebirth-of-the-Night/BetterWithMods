package betterwithmods.util;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class DispenserBehaviorFiniteWater extends BehaviorDefaultDispenseItem {
    private final BehaviorDefaultDispenseItem dispenseBehavior = new BehaviorDefaultDispenseItem();

    /**
     * Dispense the specified stack, play the dispense sound and spawn particles.
     */
    @Override
    public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
        if (FluidUtil.getFluidContained(stack) != null) {
            return dumpContainer(source, stack);
        } else {
            return fillContainer(source, stack);
        }
    }

    /**
     * Picks up fluid in front of a Dispenser and fills a container with it.
     */
    private ItemStack fillContainer(IBlockSource source, ItemStack stack) {
        World world = source.getWorld();
        EnumFacing dispenserFacing = source.getBlockState().getValue(BlockDispenser.FACING);
        BlockPos blockpos = source.getBlockPos().offset(dispenserFacing);

        ItemStack result = FluidUtil.tryPickUpFluid(stack, null, world, blockpos, dispenserFacing.getOpposite()).getResult();
        if (result.isEmpty()) {
            return super.dispenseStack(source, stack);
        }

        if (stack.getCount() == 0) {
            stack.deserializeNBT(result.serializeNBT());
        } else if (((TileEntityDispenser) source.getBlockTileEntity()).addItemStack(result) < 0) {
            this.dispenseBehavior.dispense(source, result);
        }
        stack.shrink(1);
        return stack;
    }

    /**
     * Drains a filled container and places the fluid in front of the Dispenser.
     */
    private ItemStack dumpContainer(IBlockSource source, ItemStack stack) {
        ItemStack dispensedStack = stack.copy();
        dispensedStack.setCount(1);
        IFluidHandler fluidHandler = FluidUtil.getFluidHandler(dispensedStack);
        if (fluidHandler == null) {
            return super.dispenseStack(source, stack);
        }

        FluidStack fluidStack = fluidHandler.drain(Fluid.BUCKET_VOLUME, false);
        EnumFacing dispenserFacing = source.getBlockState().getValue(BlockDispenser.FACING);
        BlockPos blockpos = source.getBlockPos().offset(dispenserFacing);

        if (fluidStack != null && fluidStack.amount == Fluid.BUCKET_VOLUME && FluidUtil.tryPlaceFluid(null, source.getWorld(), blockpos, stack.getItem().getContainerItem(stack), fluidStack) != FluidActionResult.FAILURE) {
            if (fluidStack.getFluid() == FluidRegistry.WATER) {
                source.getWorld().setBlockState(blockpos, Blocks.FLOWING_WATER.getStateFromMeta(2));
                for (EnumFacing face : EnumFacing.HORIZONTALS) {
                    BlockPos off = blockpos.offset(face);
                    if (source.getWorld().isAirBlock(off) || source.getWorld().getBlockState(off).getBlock()
                            .isReplaceable(source.getWorld(), off))
                        source.getWorld().setBlockState(off, Blocks.FLOWING_WATER.getStateFromMeta(5));
                }
            }

            fluidHandler.drain(Fluid.BUCKET_VOLUME, true);

            stack.shrink(1);
            if (stack.getCount() == 0) {
                stack.deserializeNBT(dispensedStack.serializeNBT());
            } else if (((TileEntityDispenser) source.getBlockTileEntity()).addItemStack(dispensedStack) < 0) {
                this.dispenseBehavior.dispense(source, dispensedStack);
            }

            return stack;
        } else {
            return this.dispenseBehavior.dispense(source, stack);
        }
    }
}
